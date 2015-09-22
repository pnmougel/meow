package org.medit.core.icons.themes

import java.awt.Image
import java.io.File

import org.apache.commons.io.FilenameUtils
import org.medit.core.icons.{IconLibrary, ImageLoader}
import org.medit.core.properties.PropertyFile

import scala.collection.mutable

/**
 * Created by nico on 20/09/15.
 */
class IconThemeWithIndex(themeFolder: File, themeFile: File) extends PropertyFile(themeFile, "Icon Theme") with IconTheme {
  val basePath = themeFolder.getAbsolutePath

  name = getValue("Name").getOrElse(themeFolder.getName)
  inheritedThemes = (for(d <- getValue("Inherits")) yield d.split(",")).getOrElse(Array())
  private val directories = (for(d <- getValue("Directories")) yield d.split(",")).getOrElse(Array())

  // Parse the directory
  directories.foreach( dir => {
    val path = new File(basePath + "/" + dir)

    val iconSizeStr = getValue("Size", dir)
    val iconSize : Int = (for(a <- iconSizeStr) yield {
      try { a.toInt } catch { case e : NumberFormatException => 0 }
    }).getOrElse(0)
//    val isScalable = getValue("Type", dir).getOrElse("None") == "Scalable"
    val iconContext = getValue("Context", dir)

    if(path.isDirectory) {
      for(iconFile <- path.listFiles()) {
        val ext = FilenameUtils.getExtension(iconFile.getName).toLowerCase
        if (ext == "png" || ext == "jpg" || ext == "svg" || ext == "xpm") {
          val iconPath = iconFile.getAbsolutePath
          val iconName = FilenameUtils.getBaseName(iconPath).toLowerCase
          icons(iconName) = (iconSize, iconContext, ext == "svg", iconPath) :: icons.getOrElse(iconName, List[(Int, Option[String], Boolean, String)]())
        }
      }
    }
  })

  buildPreferredIcons()
}
