package org.medit.core.icons

import java.awt.Image
import java.awt.image.BufferedImage
import javax.swing.ImageIcon

import scala.collection.mutable

/**
 * Created by nico on 17/09/15.
 */
class AppIcon(val name: String) {
  val paths = mutable.HashMap[String, Vector[(String, Int)]]()

  val favoriteThemes = List[String](IconLibrary.iconTheme, IconLibrary.gtkTheme, "hicolor")
  val themeToImage = mutable.HashMap[String, Option[Image]]()

  def getIconFromTheme(theme: String) : Option[Image] = {
    var svgImage = List[String]()
    var curImage : Option[Image] = None
    themeToImage.getOrElseUpdate(theme, {
      if(paths.contains(theme)) {
        paths(theme).sortBy(-1 * _._2).find(path => {
          if (path._1.endsWith(".svg")) {
            svgImage = path._1 :: svgImage
            false
          } else {
            curImage = ImageLoader.get(path._1)
            curImage.isDefined
          }
        })
        if (!curImage.isDefined) {
          svgImage.find(path => {
            curImage = ImageLoader.get(path)
            curImage.isDefined
          })
        }
        curImage
      } else { None }
    })
  }

  // Check if the icon is only available in svg
  lazy val isOnlySvg = {
    var onlySvg = true
    for(theme <- paths; path <- theme._2) {
      onlySvg = onlySvg || path._1.endsWith(".svg")
    }
    onlySvg
  }

  def getIcon : Option[Image] = {
    if(paths.size == 0) { None }
    else {
      val curTheme = favoriteThemes.find( theme => getIconFromTheme(theme).isDefined).getOrElse( { paths.find( _ => true).get._1 })
      getIconFromTheme(curTheme)
    }
  }

  val categories = mutable.HashSet[String]()
  def addCategory(category: String) = categories.add(category)

  def addPath(path: String, theme: String, size: Int) {
    paths(theme) = paths.getOrElse(theme, { Vector[(String, Int)]() }) :+ (path, size)
  }
}

class LazyIconLoader(val fileName: String, size : Int) extends Runnable {
  var onImageLoaded : Option[Option[ImageIcon] => Unit] = None

  def onLoaded(f: Option[ImageIcon] => Unit) = {
    onImageLoaded = Some(f)
//    val th = new Thread(this)
//    th.setPriority(Thread.MIN_PRIORITY)
//    th.start()
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
