package org.medit.core.utils

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

import org.apache.commons.io.FilenameUtils

/**
 * Created by nico on 22/09/15.
 */
object FileUtils {
  def getNonExistingFileName(path: String, fileName: String) : String = {
    var i = 0
    val ext = FilenameUtils.getExtension(fileName)
    val baseFileName = FilenameUtils.getBaseName(fileName)
    var curFileName = baseFileName
    while(new File(path + "/" + curFileName + ext).exists()) {
      i += 1
      curFileName = baseFileName + "_" + i
    }
    curFileName + "." + ext
  }

  def saveIcon(fileName: String, image: BufferedImage) : String = {
    val baseName = fileName.replaceAll(" ", "-").replaceAll("/", "_")
    val home = System.getProperty("user.home")
//    val basePath = s"${home}/.local/share/icons/meow/"

    val baseDirFile = new File(GlobalPaths.pathForNewAppIcons)
    if(!baseDirFile.exists()) {
      baseDirFile.mkdirs()
    }
    val iconFileName = getNonExistingFileName(GlobalPaths.pathForNewAppIcons, baseName + ".png")
    ImageIO.write(image, "png", new File(GlobalPaths.pathForNewAppIcons + iconFileName))
    GlobalPaths.pathForNewAppIcons + iconFileName
  }
}
