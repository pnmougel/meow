package org.medit.gui.panels

import java.awt.{Color, Component, GridBagConstraints, GridBagLayout}
import javax.swing.Box

import com.alee.laf.panel.WebPanel
import com.alee.laf.scroll.WebScrollPane
import org.medit.gui.components.HorizontalScrollPane

/**
 * Created by nico on 15/09/15.
 */
class VerticalPanel extends WebPanel {
  setLayout(new GridBagLayout())
  setBackground(Color.white)
  val gbc = new GridBagConstraints()
  gbc.gridwidth = GridBagConstraints.REMAINDER
  gbc.weightx = 1.0
  gbc.fill = GridBagConstraints.HORIZONTAL
  gbc.anchor = GridBagConstraints.NORTHEAST

  val horizontalFill = new GridBagConstraints()
  horizontalFill.weighty = 1
  val glue = Box.createVerticalGlue()
  glue.setBackground(null)

  def addComponent(component : Component) : Unit = {
    component.setBackground(null)
    add(component, gbc)
  }

  def addFiller() = {
    horizontalFill.gridy = this.getComponentCount
    add(glue, horizontalFill)
  }

  def inVerticalScroll : WebScrollPane = {
    new HorizontalScrollPane(this)
  }
}
