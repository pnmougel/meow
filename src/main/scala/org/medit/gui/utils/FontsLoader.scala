package org.medit.gui.utils

import java.awt.GraphicsEnvironment
import java.io.File
import java.awt.Font
import org.medit.gui.Main
import scala.io.Source

/**
 * @author nico
 */
trait FontsLoader {
  val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
  val fonts = List("HelveticaNeue", "Roboto-Regular", "Roboto-Thin", "Roboto-Light", "Roboto-Black", "Roboto-Bold", "Roboto-Medium")
  for (fontName <- fonts) {
    val fontFile = Main.getClass.getResourceAsStream(s"/fonts/${fontName}.ttf")
    if (fontFile != null) {
      val font = Font.createFont(Font.TRUETYPE_FONT, fontFile)
      if (font != null) {
        ge.registerFont(font)
      }
    }
  }
}