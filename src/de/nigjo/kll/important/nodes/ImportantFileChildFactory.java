/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.nigjo.kll.important.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.awt.Image;

import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;

import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Parameters;

/**
 *
 * @author kll
 */
public class ImportantFileChildFactory extends Children.Keys<String>
{

  private Project currentProject;
  private java.util.Map<String, Node> nodeMap;
  private ProjectFileListener fileListener;

  public ImportantFileChildFactory(Project currentProject)
  {
    this.currentProject = currentProject;
    fileListener = new ProjectFileListener();
    nodeMap = new HashMap<>();

  }

  @Override
  @SuppressWarnings("unchecked")
  protected void addNotify()
  {
    setKeys(createKeys());
    //Register Listener to immediatly refresh the node tree if something changes.
    updateFileListener(true);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void removeNotify()
  {
    updateFileListener(false);
    setKeys(Collections.EMPTY_LIST);
  }

  private void updateFileListener(boolean add)
  {
    FileObject folder_listener =
        FileUtil.getConfigRoot().getFileObject("de-nigjo-kll-important/FileListener");
    Enumeration<? extends FileObject> listener = folder_listener.getChildren(true);
    while(listener.hasMoreElements())
    {
      FileObject nextElement = listener.nextElement();
      Boolean relative = (Boolean)nextElement.getAttribute("relativePath");
      String path = nextElement.getAttribute("path").toString();
      Boolean recursive = (Boolean)nextElement.getAttribute("recursive");
      if(relative)
      {
        doFileListener(this.currentProject.getProjectDirectory().getFileObject(path), add,
            recursive);
      }
    }
  }

  private void doFileListener(FileObject f, boolean add, boolean recursive)
  {
    if(f == null)
    {
      return;
    }
    if(!recursive)
    {
      if(add)
      {
        f.addFileChangeListener(fileListener);
      }
      else
      {
        f.removeFileChangeListener(fileListener);
      }
    }
    else
    {
      if(add)
      {
        f.addRecursiveListener(fileListener);
      }
      else
      {
        f.removeRecursiveListener(fileListener);
      }
    }
  }


  protected List<String> createKeys()
  {
    FileObject files =
        FileUtil.getConfigRoot().getFileObject("de-nigjo-kll-important/Files");
    FileObject[] children = files.getChildren();
    for(FileObject file : children)
    {
      Object attr_projectFile = file.getAttribute("projectFile");
      if(attr_projectFile == null)
      {
        Logger.getLogger(ImportantFileChildFactory.class.getName()).log(Level.SEVERE,
            "File named \"{0}\" had no attribute \"projectFile\"", file.getName());
        continue;
      }
      FileObject projFile =
          currentProject.getProjectDirectory().getFileObject(attr_projectFile.toString());
      if(projFile == null || projFile.isVirtual())
      {
        Logger.getLogger(ImportantFileChildFactory.class.getName()).log(Level.WARNING,
            "File \"{0}\" can't be found in Project \"{1}\"",
            new String[]
            {
              attr_projectFile.toString(),
              ProjectUtils.getInformation(currentProject).getDisplayName()
            });
        continue;
      }
      if(nodeMap.get(file.getName()) == null)
      {
        try
        {
          DataObject do_file = DataObject.find(projFile);
          Node clone = new ImportantFileFilterNode(do_file.getNodeDelegate(), file);
          nodeMap.put(file.getName(), clone);
        }
        catch(DataObjectNotFoundException ex)
        {
          Exceptions.printStackTrace(ex);
        }
      }
    }
    List<String> toPopulate = new ArrayList<>(nodeMap.keySet());
    return toPopulate;
  }

  @Override
  protected Node[] createNodes(String key)
  {
    return new Node[]
    {
      createNodeForKey(key)
    };
  }

  protected Node createNodeForKey(String key)
  {
    Node resultNode = nodeMap.get(key);
    return resultNode;
  }

  private class ProjectFileListener implements FileChangeListener
  {

    @Override
    public void fileFolderCreated(FileEvent fe)
    {
      fe.getFile().addFileChangeListener(this);
      setKeys(createKeys());
    }

    @Override
    public void fileDataCreated(FileEvent fe)
    {
      setKeys(createKeys());
    }

    @Override
    public void fileChanged(FileEvent fe)
    {
    }

    @Override
    public void fileDeleted(FileEvent fe)
    {
      nodeMap.remove(fe.getFile().getName());
      setKeys(createKeys());
    }

    @Override
    public void fileRenamed(FileRenameEvent fe)
    {
      nodeMap.remove(fe.getName());
      setKeys(createKeys());
    }

    @Override
    public void fileAttributeChanged(FileAttributeEvent fe)
    {
    }

  }

  private class ImportantFileFilterNode extends FilterNode
  {
    private Object iconBase;

    public ImportantFileFilterNode(Node original, FileObject layerEntry)
    {
      super(original);
      Parameters.notNull("layerEntry", layerEntry);
      Object displName = layerEntry.getAttribute("displayName");
      if(displName != null)
      {
        disableDelegation(DELEGATE_GET_DISPLAY_NAME | DELEGATE_SET_DISPLAY_NAME);
        setDisplayName(displName.toString());
      }
      iconBase = layerEntry.getAttribute("iconBase");
    }

    @Override
    public Image getIcon(int type)
    {
      if(iconBase != null)
      {
        return ImageUtilities.loadImage(iconBase.toString());
      }
      else
      {
        return super.getIcon(type);
      }
    }

    @Override
    public Image getOpenedIcon(int type)
    {
      if(iconBase != null)
      {
        return ImageUtilities.loadImage(iconBase.toString());
      }
      else
      {
        return super.getOpenedIcon(type);
      }
    }

  }
}
