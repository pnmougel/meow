package org.medit.gui.entries.properties

import java.awt.BorderLayout
import javax.swing.JPanel

import com.alee.laf.panel.WebPanel
import com.alee.laf.text.WebTextField
import org.medit.gui.components.{BsLabel, BsHiddenInput}
import org.medit.gui.entries.EntryView
import org.medit.gui.utils.SwingEvents._

/**
 * Created by nico on 16/09/15.
 */
class NameProperty extends JPanel(new BorderLayout()) with EntryProperty {
  val nameInput = new BsHiddenInput()
  nameInput.setEditable(isEditable)
  nameInput.setName("hidden-input application-name")
  nameInput.setDrawBorder(false)
  nameInput.setInputPrompt("Application name")
  nameInput.setHideInputPromptOnFocus(false)
  nameInput.onKeyRelease( e => {
    val newName = nameInput.getText
    for(e <- entry; if(!newName.trim.isEmpty)) {
      e.textLabel.setText(newName)
      if(e.entry.isEditable) {
        e.entry.setValue("Name", newName)
        e.repaint()
        e.entry.save()
      }
    }
  })
  val appTypeLabel = new BsLabel("", false)
  val leftPanel = new JPanel()
  leftPanel.setBackground(null)
  leftPanel.setName("margin-top")
//  leftPanel.setMargin(4, 0, 0, 0)
  leftPanel.add(appTypeLabel, 0)
  add(leftPanel, BorderLayout.WEST)
  add(nameInput)

  override def setEntry(newEntry: EntryView) = {
    isEditable = newEntry.entry.isEditable
    entry = Some(newEntry)
    appTypeLabel.setText(newEntry.entry.getAppType())
    nameInput.setText(newEntry.entry.getName)
    nameInput.setEditable(isEditable)
  }
}
