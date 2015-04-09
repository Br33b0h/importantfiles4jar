/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.nigjo.kll.important.nodes;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.openide.nodes.ChildFactory;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Parameters;

/**
 *
 * @author kll
 */
public class ImportantFileChildFactory extends ChildFactory<String>
{

  private Project currentProject;
  private Map<String, Node> nodes;
  private ProjectFileListener fileListener;

  public ImportantFileChildFactory(Project currentProject)
  {
    this.currentProject = currentProject;
    fileListener = new ProjectFileListener();
    nodes = new HashMap<>();

    //Register Listener to immediatly refresh the node tree if something changes.
    FileObject projDir = this.currentProject.getProjectDirectory();
    projDir.addFileChangeListener(fileListener);
    FileObject nbproj = projDir.getFileObject("nbproject");
    nbproj.addFileChangeListener(fileListener);
    Enumeration<? extends FileObject> folders = nbproj.getFolders(true);
    while(folders.hasMoreElements())
    {
      folders.nextElement().addFileChangeListener(fileListener);
    }
  }

  @Override
  protected boolean createKeys(List<String> toPopulate)
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
        break;
      }
      FileObject projFile =
          currentProject.getProjectDirectory().getFileObject(attr_projectFile.toString());
      if(projFile == null || projFile.isVirtual())
      {
        Logger.getLogger(ImportantFileChildFactory.class.getName()).log(Level.SEVERE,
            "File \"{0}\" can't be found in Project \"{1}\"",
            new Object[]
            {
              attr_projectFile.toString(),
              ProjectUtils.getInformation(currentProject).getDisplayName()
            });
        break;
      }
      try
      {
        DataObject do_file = DataObject.find(projFile);
        Node clone = new ImportantFileFilterNode(do_file.getNodeDelegate(), file);
        nodes.put(file.getName(), clone);
      }
      catch(DataObjectNotFoundException ex)
      {
        Exceptions.printStackTrace(ex);
      }
    }
    toPopulate.addAll(nodes.keySet());
    return true;
  }

  @Override
  protected Node createNodeForKey(String key)
  {
    Node resultNode = nodes.get(key);
    return resultNode;
  }

  private class ProjectFileListener implements FileChangeListener
  {

    @Override
    public void fileFolderCreated(FileEvent fe)
    {
      fe.getFile().addFileChangeListener(this);
      refresh(true);
    }

    @Override
    public void fileDataCreated(FileEvent fe)
    {
      refresh(true);
    }

    @Override
    public void fileChanged(FileEvent fe)
    {
    }

    @Override
    public void fileDeleted(FileEvent fe)
    {
      refresh(true);
    }

    @Override
    public void fileRenamed(FileRenameEvent fe)
    {
      refresh(true);
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

      if(displName == null)
      {
        disableDelegation(DELEGATE_GET_DISPLAY_NAME | DELEGATE_SET_DISPLAY_NAME);
      }
      else
      {
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
