package org.medit.gui.utils

import java.awt.Color

/**
 * Created by nico on 17/09/15.
 */
object Colors {
  val red = Color.decode("#D9534F")
  val black = Color.decode("#000000")
  val gray = Color.decode("#777777")
  val lightGray = Color.decode("#f1f1f1")
  val darkGray = Color.decode("#333333")
  val white = Color.white
  val transparent = new Color(0, 0, 0, 0)
  val transparentWhite = new Color(1f, 1f, 1f, 0.6f)

  def getColors(nbColors: Int, saturation: Int, value: Int) = {
    for(i <- 0 until nbColors) yield {
      val hue = (256f / nbColors) * i
      Color.getHSBColor(hue / 256f, saturation / 256f, value / 256f)
    }
  }

  val flatDesignColorsHex = Array("#41A85F", "#00A885", "#3D8EB9", "#2969B0", "#553982", "#28324E", "#F37934", "#D14841", "#B8312F", "#75706B")
  val flatDesignColors = flatDesignColorsHex.map(Color.decode(_))

  def getFlatColors(n: Int) = flatDesignColors.take(n)
}
