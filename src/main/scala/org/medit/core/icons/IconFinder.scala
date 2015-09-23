package org.medit.core.icons

import java.awt.Image
import java.io.File
import javax.imageio.ImageIO
import javax.swing.{SwingUtilities, ImageIcon, JLabel}

import org.medit.gui.utils.{Timer, Colors}

import scala.collection._
import sys.process.stringToProcess
import sys.process.stringSeqToProcess


object IconFinder {
  val missing = IconGenerator.generateIcon("No icon found", Colors.lightGray)
  val iconCache = mutable.HashMap[String, Image]()
  val iconCacheWithSize = mutable.HashMap[(String, Int), ImageIcon]()

  def getIcon(nameBase: String, size: Int = 64) = {
    // Build the scaled image icon
    iconCacheWithSize.getOrElseUpdate((nameBase, size), {
      new ImageIcon(loadIcon(nameBase, size).getScaledInstance(size, size, Image.SCALE_SMOOTH))
    })
  }

  def loadIcon(nameBase: String, size: Int = 64) : Image = {
    val iconName = nameBase.toLowerCase
    val iconFile = new File(nameBase)

    // Seems to be a path to a file, try to read the file
    if (iconFile.isFile) {
      for(img <- ImageLoader.get(nameBase)) { iconCache(iconName) = img }
    }
    iconCache.getOrElseUpdate(iconName, {
      val cacheFileName = s"svg-raster/$nameBase.png"
      val cacheFile = new File(cacheFileName)
      if(cacheFile.exists) {
        ImageIO.read(cacheFile)
      } else {
        val res = Seq("/home/nico/workspace/Meow/src/python/iconLookup.py", nameBase.toLowerCase, cacheFileName).!!
        if (res.trim == "ok") {
          ImageIO.read(new File(cacheFileName))
        } else {
          val themesToSearch = IconLibrary.iconThemes(IconLibrary.iconTheme) ::: IconLibrary.iconThemes(IconLibrary.fallbackTheme)
          val theme = themesToSearch.find(theme => {
            theme.getIcon(iconName).isDefined
          })
          (for(m <- theme) yield {
            m.getIcon(iconName).get
          }).orElse(
              for(missingName <- IconLibrary.missingIcons.get(nameBase); img <- ImageLoader.get(missingName)) yield {
                img
              }
          ).getOrElse(missing)
        }
      }
    })
  }

  val loadingIcon = new ImageIcon(IconGenerator.generateIcon("-", Colors.gray))

  def getIconLoader(nameBase: String, size: Int = 64, label : JLabel) : ImageIcon = {
    SwingUtilities.invokeLater(new ImageScaler(nameBase, size, label))
    loadingIcon
  }
}

