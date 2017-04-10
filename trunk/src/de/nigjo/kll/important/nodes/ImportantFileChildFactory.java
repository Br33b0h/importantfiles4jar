/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.nigjo.kll.important.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Parameters;

import de.nigjo.kll.important.ImportantFilesActionConnector;

/**
 *
 * @author kll
 */
public class ImportantFileChildFactory extends Children.Keys<String>
{

  private Project currentProject;
  private java.util.Map<String, Node> nodeMap;
  private ImportantFilesActionConnector connector;
  private ActionListener al;
  public ImportantFileChildFactory(Project currentProject, ImportantFilesActionConnector con)
  {
    this.currentProject = currentProject;
    nodeMap = new HashMap<>();
    al = this::actionPerformed;
    connector = con;
  }

  private void actionPerformed(ActionEvent evt){
    setKeys(createKeys());
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void addNotify()
  {
    setKeys(createKeys());
    //Register Listener to immediatly refresh the node tree if something changes.
    connector.addActionListener(al);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void removeNotify()
  {
    setKeys(Collections.EMPTY_LIST);
    connector.removeActionListener(al);
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
