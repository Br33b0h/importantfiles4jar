package de.nigjo.kll.important;

import java.util.LinkedList;
import java.util.List;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author daniel.koll
 */
public class ImportantFilesActionConnector
{
  private List<ActionListener> listener;

  public void addActionListener(ActionListener l)
  {
    if(listener == null)
    {
      listener = new LinkedList<>();
    }
    listener.add(l);
  }

  public void removeActionListener(ActionListener l)
  {
    if(listener != null)
    {
      listener.remove(l);
    }
  }

  private void notifyListener(final ActionEvent e)
  {
    if(listener != null)
    {
      listener.forEach(l -> l.actionPerformed(e));
    }
  }

  public void actionPerformed(final ActionEvent e)
  {
    notifyListener(e);
  }
}
