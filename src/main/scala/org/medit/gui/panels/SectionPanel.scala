package org.medit.gui.panels

import javax.swing.JPanel

import com.alee.laf.label.WebLabel

class SectionPanel(name: String, panel: JPanel) extends VerticalPanel {
  val tasksLabel = new WebLabel(name)
  tasksLabel.setName("header-label")

  addComponent(tasksLabel)
  addComponent(panel)
  addFiller()
}
