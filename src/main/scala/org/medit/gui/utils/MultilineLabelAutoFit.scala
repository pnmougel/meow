package org.medit.gui.utils

import javax.swing.JComponent
import java.awt.{Color, Dimension, Font, FontMetrics, Graphics, Graphics2D, RenderingHints}

/**
 * Created by nico on 15/09/15.
 */
class MultilineLabelAutoFit(baseText: String, width: Int, height: Int) extends JComponent {
  var text = baseText
  var metrics: Option[FontMetrics] = None
  var fittingText: Option[List[String]] = None

  val dimension = new Dimension(width, height)
  setMinimumSize(dimension)
  setPreferredSize(dimension)
  setMaximumSize(dimension)

  def getFittingText(): List[String] = {
    val curMetrics = metrics.getOrElse(getFontMetrics(getFont()))
    metrics = Some(curMetrics)
    val maxWidth = 135

    if (curMetrics.stringWidth(text) <= maxWidth) {
      List(text)
    } else {
      // Try by splitting words
      var isSplitOk = false
      var curRes = List[String]()
      var prevIndex = 0
      var curIndex = text.indexOf(" ", prevIndex)
      while (curIndex != -1 && !isSplitOk) {
        val parts = text.splitAt(curIndex)
        if (curMetrics.stringWidth(parts._1) <= maxWidth && curMetrics.stringWidth(parts._2) <= maxWidth) {
          curRes = List(parts._1, parts._2)
          isSplitOk = true
        }
        prevIndex = curIndex + 1
        curIndex = text.indexOf(" ", prevIndex)
      }

      if (!isSplitOk) {
        var curStr = ""
        var i = 0
        while (curMetrics.stringWidth(curStr + "...") <= maxWidth) {
          curStr += text.charAt(i)
          i += 1
        }
        curRes = List(curStr + "...")
      }
      curRes
    }
  }

  def setText(newText: String) = {
    text = newText
    fittingText = Some(getFittingText())
  }

  override def setFont(f: Font): Unit = {
    metrics = Some(getFontMetrics(f))
    fittingText = Some(getFittingText())
    super.setFont(f)
  }

  override def paint(g: Graphics): Unit = {
    val curMetrics = metrics.getOrElse(getFontMetrics(getFont()))
    val curFittingText = fittingText.getOrElse(getFittingText())
    fittingText = Some(curFittingText)
    val g2d = g.asInstanceOf[Graphics2D]
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    val textHeight = curMetrics.getHeight
    g2d.setColor(Color.BLACK)
    for ((line, i) <- curFittingText.zipWithIndex) {
      val x = (140 - curMetrics.stringWidth(line)) / 2
      val y = if(curFittingText.length == 1) {
        (textHeight * 1.5).toInt
      } else textHeight * (i + 1)

      g2d.drawString(line, x, y - 5)
    }
  }
}
