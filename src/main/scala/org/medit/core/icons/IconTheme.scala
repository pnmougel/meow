package org.medit.core.icons

import java.io.File

import org.medit.core.properties.PropertyFile

/**
 * Created by nico on 20/09/15.
 */
class IconTheme(file: File) extends PropertyFile(file, "Icon Theme") {
  val basePath = file.getAbsolutePath

  val name = getValue("Name").getOrElse(file.getName)
  private val inheritedThemes = getListValue("Inherits").getOrElse(List())
  private val directories = getListValue("Directories").getOrElse(List())

  directories.foreach( dir => {
    val path = new File(basePath + "/" + dir)
    val iconSize = getValue("Size", dir)
    val iconType = getValue("Type", dir)
    val iconContext = getValue("Context", dir)

    if(path.isDirectory) {
      for(iconFile <- path.listFiles()) {

      }
    }
  })
}
