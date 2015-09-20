package org.medit.core.icons

import javax.swing.{ImageIcon, JLabel}
import java.awt.Image

/**
 * Created by nico on 18/09/15.
 */
class ImageScaler(image: Image, size: Int, label: JLabel) extends Thread {
  override def run(): Unit = {
    val newImage = image.getScaledInstance(size, size, Image.SCALE_SMOOTH)
    label.setIcon(new ImageIcon(newImage))
    label.repaint()
  }
}
