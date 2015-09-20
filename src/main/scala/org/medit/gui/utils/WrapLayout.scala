package org.medit.gui.utils

import java.awt._
import javax.swing.JScrollPane
import javax.swing.SwingUtilities
//remove if not needed
import scala.collection.JavaConversions._

@SerialVersionUID(9075785607557180684L)
class WrapLayout(align: Int) extends FlowLayout(align) {


  override def preferredLayoutSize(target: Container): Dimension = layoutSize(target, true)

  override def minimumLayoutSize(target: Container): Dimension = {
    val minimum = layoutSize(target, false)
    minimum.width -= (getHgap + 1)
    minimum
  }

  private def layoutSize(target: Container, preferred: Boolean): Dimension = {
      var targetWidth = target.getSize.width
      var container = target
      while (container.getSize.width == 0 && container.getParent != null) {
        container = container.getParent
      }
      targetWidth = container.getSize.width
      if (targetWidth == 0) targetWidth = java.lang.Integer.MAX_VALUE
      val hgap = getHgap
      val vgap = getVgap
      val insets = target.getInsets
      val horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2)
      val maxWidth = targetWidth - horizontalInsetsAndGap
      val dim = new Dimension(0, 0)
      var rowWidth = 0
      var rowHeight = 0
      val nmembers = target.getComponentCount
      for (i <- 0 until nmembers) {
        val m = target.getComponent(i)
        if (m.isVisible) {
          val d = if (preferred) m.getPreferredSize else m.getMinimumSize
          if (rowWidth + d.width > maxWidth) {
            addRow(dim, rowWidth, rowHeight)
            rowWidth = 0
            rowHeight = 0
          }
          if (rowWidth != 0) {
            rowWidth += hgap
          }
          rowWidth += d.width
          rowHeight = Math.max(rowHeight, d.height)
        }
      }
      addRow(dim, rowWidth, rowHeight)
      dim.width += horizontalInsetsAndGap
      dim.height += insets.top + insets.bottom + vgap * 2
      val scrollPane = SwingUtilities.getAncestorOfClass(classOf[JScrollPane], target)
      if (scrollPane != null && target.isValid) {
        dim.width -= (hgap + 1)
      }
      dim
  }

  private def addRow(dim: Dimension, rowWidth: Int, rowHeight: Int) {
    dim.width = Math.max(dim.width, rowWidth)
    if (dim.height > 0) {
      dim.height += getVgap
    }
    dim.height += rowHeight
  }
}
