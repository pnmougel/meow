package org.medit.gui.entries.properties

import java.awt.{BorderLayout, FlowLayout}
import javax.swing.JPanel

import com.alee.laf.panel.WebPanel
import com.alee.laf.text.WebTextField
import org.medit.gui.components.{BsHiddenInput, BsLabel}
import org.medit.gui.entries.{EntriesPanel, EntryView}
import org.medit.gui.utils.SwingEvents._
import org.medit.gui.utils.{InputBorder, WrapLayout}

/**
 * Created by nico on 16/09/15.
 */
class ExecProperty extends JPanel(new FlowLayout(FlowLayout.LEADING)) with EntryProperty {
  val execInput = new BsHiddenInput()
  execInput.setInputPrompt("Program or url")
  execInput.onKeyRelease(e => {
    val newExec = execInput.getText
    for (e <- entry; if (!newExec.trim.isEmpty && isEditable)) {
      if(newExec.startsWith("http")) {
//        e.entry.setValue("Exec", "xdg-open \"" + newExec + "\"")
      } else {
//        e.entry.setValue("Exec", newExec)
      }
      e.entry.save()
    }
  })

//  add(typeField, 0)
  add(execInput, 0)

  override def setEntry(newEntry: EntryView) = {
    isEditable = newEntry.entry.isEditable
    entry = Some(newEntry)
    for(prop <- newEntry.entry.executable) {
//      execInput.setColumns(prop.size)

      // Compute maximum text size
      execInput.setColumns((EntriesPanel.getWidth * 0.5 / 12).toInt)

      execInput.setText(prop)
      execInput.setEditable(isEditable)
    }
  }
}
