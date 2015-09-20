package org.medit.gui.utils.styles.styleelements

import javax.swing.JComponent
import javax.swing.border.EmptyBorder
import java.awt.Color
import java.awt.Component
import org.medit.gui.utils.Colors
import org.medit.gui.utils.styles.helpers.ColorParser

class StyleBackgroundColor extends StyleElement("background") {
  var color = Colors.red

  def build(): StyleElement = {
    new StyleBackgroundColor()
  }

  def applyStyleComponent(component: Component) {
    component.setBackground(color)
  }

  def parse(value: String): Boolean = {
    var res = ColorParser.parseColor(value)
    for (c <- res) {
      color = c
    }
    res.isDefined
  }
}