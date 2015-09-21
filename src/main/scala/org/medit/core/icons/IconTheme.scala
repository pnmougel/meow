package org.medit.core.icons

import java.awt.Image
import java.io.File

import org.apache.commons.io.FilenameUtils
import org.medit.core.properties.PropertyFile

import scala.collection.mutable

/**
 * Created by nico on 20/09/15.
 */
class IconTheme(themeFolder: File, themeFile: File) extends PropertyFile(themeFile, "Icon Theme") {
  val basePath = themeFolder.getAbsolutePath

  val name = getValue("Name").getOrElse(themeFolder.getName)
  private val inheritedThemes = (for(d <- getValue("Inherits")) yield d.split(",")).getOrElse(Array())
  private val directories = (for(d <- getValue("Directories")) yield d.split(",")).getOrElse(Array())

  val icons = mutable.HashMap[String, List[(Int, Option[String], Boolean, String)]]()
  val preferredIcons = mutable.HashMap[String, String]()

  // Parse the directory
  directories.foreach( dir => {
    val path = new File(basePath + "/" + dir)

    val iconSizeStr = getValue("Size", dir)
    val iconSize : Int = (for(a <- iconSizeStr) yield {
      try { a.toInt } catch { case e : NumberFormatException => 0 }
    }).getOrElse(0)
    val isScalable = getValue("Type", dir).getOrElse("None") == "Scalable"
    val iconContext = getValue("Context", dir)

    if(path.isDirectory) {
      for(iconFile <- path.listFiles()) {
        val iconPath = iconFile.getAbsolutePath
        val iconName = FilenameUtils.getBaseName(iconPath).toLowerCase
        icons(iconName) = (iconSize, iconContext, isScalable, iconPath) :: icons.getOrElse(iconName, List[(Int, Option[String], Boolean, String)]())
      }
    }
  })

  for((iconName, iconInfos) <- icons) {
    preferredIcons(iconName) = if(iconInfos.exists(e => !e._3 && e._1 > 30)) {
      iconInfos.maxBy(e => e._1)._4
    } else if(iconInfos.exists(e => e._3)) {
        iconInfos.filter(e => e._3).maxBy(e => e._1)._4
    } else {
        iconInfos.maxBy(e => e._1)._4
    }
//    val isOnlySvg = !iconInfos.exists(e => !e._3)
//    val isOnlySmall = !iconInfos.exists(e => e._1 > 30)
//    iconInfos.filter(e => (!e._3 || isOnlySvg || isOnlySmall) || (e._1 < 30 || isOnlySmall))
    preferredIcons(iconName) = (if(iconInfos.exists(e => !e._3 && e._1 > 30)) {
      iconInfos.filter(e => !e._3)
    } else if(iconInfos.exists(e => e._3)) {
      iconInfos.filter(e => e._3)
    } else {
      iconInfos
    }).maxBy(e => e._1)._4
  }

  def getIcon(iconName: String) : Option[Image] = {
    if(preferredIcons.contains(iconName)) {
      ImageLoader.get(preferredIcons(iconName))
    } else {
      inheritedThemes.find( inheritedTheme => {
        if(iconName == "libreoffice-draw") {
          println(inheritedTheme)
        }
        (for(t <- IconLibrary.iconThemes.get(inheritedTheme)) yield {
          t.getIcon(iconName).isDefined
        }).getOrElse(false)
      }).map(matchingInheritedTheme => IconLibrary.iconThemes(matchingInheritedTheme).getIcon(iconName).get)
    }
  }
}
