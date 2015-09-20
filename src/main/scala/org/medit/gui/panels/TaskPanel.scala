package org.medit.gui.panels

import java.awt.Cursor
import javax.swing.JPanel

import com.alee.laf.label.WebLabel
import org.medit.gui.utils.SwingEvents._

/**
 * Created by nico on 15/09/15.
 */
class TaskPanel(taskName: String, panel: JPanel) extends VerticalPanel {
  val taskLabel = new WebLabel(taskName)
  taskLabel.setName("task-name")
  taskLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))

  taskLabel.onClick(e => {
    panel.setVisible(!panel.isVisible)
  })

  panel.setVisible(false)

  addComponent(taskLabel)
  addComponent(panel)
  addFiller()
}
