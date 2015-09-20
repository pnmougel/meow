package org.medit.gui.utils

import scala.collection.mutable.HashMap
import javax.swing.ImageIcon
import javax.imageio.ImageIO
import java.io.File
import java.awt.Image
import org.medit.gui.Main

object Icon {
  val iconsCache = new HashMap[String, HashMap[Int, ImageIcon]]()

  def get(path: String, size: Int = 18): ImageIcon = {
    iconsCache.getOrElseUpdate(path, {
      new HashMap[Int, ImageIcon]()
    }).getOrElseUpdate(size, {
      val img = ImageIO.read(Main.getFile("/icons/" + path + ".png")).getScaledInstance(size, size, Image.SCALE_SMOOTH)
      new ImageIcon(img)
    })
  }
}