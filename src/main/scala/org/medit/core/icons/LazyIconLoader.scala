package org.medit.core.icons

import java.awt.Image
import javax.swing.ImageIcon

/**
 * Created by nico on 20/09/15.
 */
class LazyIconLoader(val fileName: String, size : Int) extends Runnable {
  var onImageLoaded : Option[Option[ImageIcon] => Unit] = None

  def onLoaded(f: Option[ImageIcon] => Unit) = {
    onImageLoaded = Some(f)
  }

  override def run() : Unit = {
    val img = ImageLoader.get(fileName)
    for(f <- onImageLoaded) {
      f(for(image <- img) yield {
        new ImageIcon(image.getScaledInstance(size, size, Image.SCALE_SMOOTH))
      })
    }
  }
}
