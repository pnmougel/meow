package org.medit.gui.utils

import java.awt.geom.GeneralPath
import java.awt._
import javax.swing.border.Border

class CornerBorder(cornerX: Int, isTop: Boolean, marginRight: Int) extends Border {
  val cornerHeight = 10
  val cornerWidth = 10
  val leftPadding = 30
  val rightPadding = marginRight
  val insets = 10
  var r = 3
  val paddingBottom = 30

  def getBorderInsets(c : Component) : Insets = {
    val top = cornerHeight + insets
    val right = insets + marginRight
    val left = leftPadding + insets
    val bottom = insets + paddingBottom
    new Insets(top, left, bottom, right)
  }

  def isBorderOpaque = true

  def paintBorder(c: Component, g: Graphics, x: Int, y: Int, w: Int, h: Int): Unit = {
    val shape = new GeneralPath()
    val left = x + leftPadding
    val right = x + w - 1 - rightPadding
    val top1 = y
    val top2 = y + cornerHeight
    val bottom = if(isTop) y + h - paddingBottom else y + h - paddingBottom - cornerHeight
    val bottom2 = y + h - paddingBottom

    shape.moveTo(left, top2 + r)
    shape.lineTo(left, bottom - r)
    shape.quadTo(left, bottom, left + r, bottom)
    if(!isTop) {
      shape.lineTo(cornerX - cornerWidth, bottom)
      shape.lineTo(cornerX, bottom2)
      shape.lineTo(cornerX + cornerWidth, bottom)
    }
    shape.lineTo(right - r, bottom)
    shape.quadTo(right, bottom, right, bottom - r)
    shape.lineTo(right, top2 + r)

    shape.quadTo(right, top2, right - r, top2)
    if(isTop) {
      shape.lineTo(cornerX + cornerWidth, top2)
      shape.lineTo(cornerX, top1)
      shape.lineTo(cornerX - cornerWidth, top2)
    }
    shape.lineTo(left + r, top2)
    shape.quadTo(left, top2, left, top2 + r)
    val g2d = g.asInstanceOf[Graphics2D]
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g2d.setColor(Colors.gray)
    g2d.draw(shape)

  }
}
