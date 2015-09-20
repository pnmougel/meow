package org.medit.gui.utils

import java.awt._
import java.awt.geom.GeneralPath
import javax.swing.border.Border

class InputBorder(pad: Int = 4) extends Border {

  def getBorderInsets(c : Component) : Insets = {
    val top = 0
    val right = 3
    val left = 4
    val bottom = 1
    new Insets(top, left, bottom, right)
  }

  def isBorderOpaque = true

  def paintBorder(c: Component, g: Graphics, x: Int, y: Int, w: Int, h: Int): Unit = {
    g.setColor(Colors.gray)
    g.drawLine(x, y + h - pad, x, y + h)
    g.drawLine(x, y + h - 1, x + w - 1, y + h - 1)
    g.drawLine(x + w - 1, y + h - 1, x + w - 1, y + h - pad)
  }
}
