/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.nigjo.kll.important.nodes;

import java.awt.Image;

import javax.swing.Action;

import org.netbeans.api.project.Project;

import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 * A Node for displaying Important Files.
 *
 * @author breebo
 */
public class ImportantFilesNode extends FilterNode
{
  private static final String KEY_DISPLAY_NAME = "ImportantFilesNode.displayName";

  public ImportantFilesNode(Node original, Project context)
  {
    super(original, Children.create(new ImportantFileChildFactory(context), true),
        Lookups.singleton(context));
  }

  @Override
  public Image getIcon(int type)
  {
    Image originalIcon = getOriginal().getIcon(type);
    Image spanner = ImageUtilities.loadImage("de/nigjo/kll/important/images/spanner.png");
    int x = 8;
    int y = 8;
    Image merged = ImageUtilities.mergeImages(originalIcon, spanner, x, y);
    return merged;
  }

  @Override
  public Image getOpenedIcon(int type)
  {
    //Take the same Icon for opened and closed state
    return getIcon(type);
  }

  @Override
  public String getDisplayName()
  {
    return NbBundle.getMessage(ImportantFilesNode.class, KEY_DISPLAY_NAME);
  }

  @Override
  public Action[] getActions(boolean context)
  {
    return null;
  }
}
