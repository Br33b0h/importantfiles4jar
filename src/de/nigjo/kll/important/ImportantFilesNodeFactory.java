/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.nigjo.kll.important;

import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.netbeans.spi.project.ui.support.NodeList;

import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

import de.nigjo.kll.important.nodes.ImportantFilesNode;

/**
 *
 * @author breebo
 */
@NodeFactory.Registration(projectType = {
  "org-netbeans-modules-java-j2seproject"
},
    position = 250)
public class ImportantFilesNodeFactory implements NodeFactory {

  @Override
  public NodeList<?> createNodes(Project p) {
    try {
      DataObject found = DataObject.find(p.getProjectDirectory().getFileObject("nbproject"));
      Node original = found.getNodeDelegate();
      return NodeFactorySupport.fixedNodeList(new ImportantFilesNode(original, p));
    }
    catch(DataObjectNotFoundException ex) {
      Exceptions.printStackTrace(ex);
    }
    return NodeFactorySupport.fixedNodeList();
  }

}
