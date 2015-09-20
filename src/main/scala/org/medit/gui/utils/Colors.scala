package org.medit.gui.utils

import java.awt.Color

/**
 * Created by nico on 17/09/15.
 */
object Colors {
  val red = Color.decode("#D9534F")
  val gray = Color.decode("#777777")
  val lightGray = Color.decode("#f1f1f1")
  val white = Color.white
  val transparent = new Color(0, 0, 0, 0)

  def getColors(nbColors: Int, saturation: Int, value: Int) = {
    for(i <- 0 until nbColors) yield {
      val hue = (256f / nbColors) * i
      Color.getHSBColor(hue / 256f, saturation / 256f, value / 256f)
    }
  }
}
