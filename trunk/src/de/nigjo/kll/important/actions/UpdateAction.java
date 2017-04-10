/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.nigjo.kll.important.actions;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

import de.nigjo.kll.important.ImportantFilesActionConnector;

@ActionID(
    category = "File",
    id = "de.nigjo.kll.important.actions.UpdateAction"
)
@ActionRegistration(
    displayName = "#CTL_UpdateAction"
)
@ActionReference(path = "de/nigjo/kll/ImportantFilesNode/Actions")
@Messages("CTL_UpdateAction=Update")
public final class UpdateAction implements ActionListener
{
  private final ImportantFilesActionConnector context;

  public UpdateAction(ImportantFilesActionConnector context)
  {
    this.context = context;
  }

  @Override
  public void actionPerformed(ActionEvent ev)
  {
    Logger.getLogger(UpdateAction.class.getName()).log(Level.INFO, "Updating Important Files");
    context.actionPerformed(ev);
    Logger.getLogger(UpdateAction.class.getName()).log(Level.INFO, "Updating Important Files finished");
  }
}
