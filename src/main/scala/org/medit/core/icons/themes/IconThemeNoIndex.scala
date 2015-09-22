package org.medit.core.icons.themes

import java.io.File
import java.nio.file.{Paths, Files}

import org.apache.commons.io.FilenameUtils
import org.medit.core.icons.{IconLibrary}

import scala.collection.mutable

/**
 * Created by nico on 21/09/15.
 */
class IconThemeNoIndex(file: File) extends IconTheme {
  name = file.getName
  val categoriesList = mutable.HashSet("mimetypes","animations","code","actions","cursors","web","media","stock","text","mimes","categories","form","symbolic","navigation","icons","status-extra","filesystems","apps-extra","image","sources","devices","special","actions-extra","places","data","table","io","object","status","places-extra","chart","emotes","net","intl","emblems","search","apps")

  def explorePath(curFile: File, size: Int, category: String) : Unit = {
    var curSize = size
    var curCategory = category
    val fileName = curFile.getName
    if (curFile.isDirectory) {
      if(categoriesList.contains(fileName)) { curCategory = fileName }
      try {
        curSize = (if(fileName.contains("x")) fileName.split("x")(0) else fileName).toInt
      } catch { case e : NumberFormatException => {}}
      curFile.listFiles().foreach(f => explorePath(f, curSize, curCategory))
    } else if(curFile.exists()) {
      val ext = FilenameUtils.getExtension(fileName).toLowerCase
      if (ext == "png" || ext == "jpg" || ext == "svg" || ext == "xpm") {
        val iconName = FilenameUtils.getBaseName(fileName).toLowerCase
        icons(iconName) = (size, Some(category), ext == "svg", curFile.getAbsolutePath) :: icons.getOrElse(iconName, List[(Int, Option[String], Boolean, String)]())
      }
    }
  }
  explorePath(file, -1, "")

  buildPreferredIcons()
}
