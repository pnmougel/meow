package org.medit.core.icons

import java.awt.event.{ActionEvent, ActionListener}
import java.io.{FileInputStream, File}
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.{Files, Paths}
import java.security.MessageDigest

import org.apache.commons.io.{IOUtils, FilenameUtils, FileUtils}
import org.medit.core.icons.themes.{IconThemeNoIndex, IconTheme, IconThemeWithIndex}
import org.medit.gui.utils.Timer2
import sun.security.provider.MD5

import scala.collection.mutable
import scala.sys.process.stringSeqToProcess

/**
 * Created by nico on 17/09/15.
 */
object IconLibrary {
  val home = System.getProperty("user.home")
  val pathsForIcons = Seq("/usr/share/icons", "/usr/local/share/icons", s"${home}/.local/share/icons", "/usr/share/pixmaps")
  val iconTheme = Seq("gsettings", "get", "org.gnome.desktop.interface", "icon-theme").!!.trim().replaceAllLiterally("'", "")
  val fallbackTheme = "hicolor"

  var iconThemes = mutable.HashMap[String, List[IconTheme]]()
  val allThemes = mutable.HashSet[IconTheme]()
  val missingIcons = mutable.HashMap[String, String]()
  val thumbnailSize = 40

  def explorePath(file: File) : Unit = {
    if(file.exists()) {
      for(theme <- file.listFiles()) {
        val absolutePath = theme.getAbsolutePath
        val themeName = theme.getName
        if(theme.isDirectory) {
          val themeFile = new File(absolutePath + "/index.theme")
          if(themeFile.isFile) {
            val newTheme = new IconThemeWithIndex(theme, themeFile)
            allThemes.add(newTheme)
            iconThemes(themeName) = newTheme :: iconThemes.getOrElse(themeName, List[IconTheme]())
          } else {
            val newTheme = new IconThemeNoIndex(theme)
            allThemes.add(newTheme)
            iconThemes(themeName) = newTheme :: iconThemes.getOrElse(themeName, List[IconTheme]())
          }
        } else {
          for(n <- List(themeName, FilenameUtils.getBaseName(themeName))) {
            missingIcons(n) = absolutePath
          }
        }
      }
    }
  }


  val digests = new mutable.HashSet[String]
  def searchIcon(appName: String, curIconName: String) : List[String] = {
    digests.clear()
    val iconNameMatch = curIconName.toLowerCase.trim()
    val nameLower = appName.toLowerCase.trim()
    val nameParts = nameLower.split(" ")

    var iconNameMatchs = List[String]()
    var appNameMatchs = List[String]()
    var appNameStartsWithMatchs = List[String]()
    var appNameContainsMatchs = List[String]()
    var appNamePartsMatchs = List[String]()
    for(theme <- allThemes; (iconName, iconPath) <- theme.preferredIcons) yield {
      if(iconNameMatch == iconName) { iconNameMatchs = iconPath :: iconNameMatchs }
      else if (iconName == nameLower) { appNameMatchs = iconPath :: appNameMatchs }
      else if (iconName.startsWith(nameLower)) { appNameStartsWithMatchs = iconPath :: appNameStartsWithMatchs }
      else if (iconName.contains(nameLower)) { appNameContainsMatchs = iconPath :: appNameContainsMatchs }
      else if (nameParts.exists(n => iconName.contains(n))) { appNamePartsMatchs = iconPath :: appNamePartsMatchs }
    }
    iconNameMatchs.sorted ::: appNameMatchs.sorted ::: appNameStartsWithMatchs.sorted ::: appNameContainsMatchs.sorted ::: appNamePartsMatchs.sorted
  }

  def loadNextImages(icons: List[String], n: Int) : (List[LazyIconLoader], List[String]) = {
    val (l1, l2) = icons.splitAt(n)
    (l1.map( e => {
      val file = new File(e)
      val md = MessageDigest.getInstance("MD5")
      val hash = new BigInteger(md.digest(FileUtils.readFileToByteArray(file))).toString
      val isAlreadyFound = digests.contains(hash)
      digests.add(hash)
      (e, isAlreadyFound)
    }).filterNot(_._2).map(e => {
      new LazyIconLoader(e._1, thumbnailSize)
    }), l2)
  }

  // Perform the icon lookup
  for (path <- pathsForIcons) { explorePath(new File(path)) }
  for (theme <- allThemes) { theme.buildInheritedThemes}

  /*
  def explorePath(curFile: File, level: Int, size: Int, category: String) : Unit = {
    var file = curFile
    var curSize = size
    var curCategory = category
    var fileName = file.getName
    if (file.isDirectory) {
      if (level == 1) {
        curTheme = fileName
      }
      if(level > 1) {
        if(categoriesList.contains(fileName)) { curCategory = fileName }
        try {
          curSize = (if(fileName.contains("x")) fileName.split("x")(0) else fileName).toInt
        } catch { case e : NumberFormatException => {}}
      }

      file.listFiles().foreach(f => explorePath(f, level + 1, curSize, curCategory))
    } else if(file.exists()) {
      if(Files.isSymbolicLink(Paths.get(file.toURI))) {
        val targetFile = new File(file.getParent + "/" + Files.readSymbolicLink(Paths.get(file.toURI)))
        if(targetFile.exists()) {
          file = targetFile
          fileName = targetFile.getName
        }
      }
      val ext = FilenameUtils.getExtension(fileName).toLowerCase
      if (ext == "png" || ext == "jpg" || ext == "svg" || ext == "xpm") {
        val fileNameWithoutExtension = FilenameUtils.getBaseName(fileName).toLowerCase

        val (prevSize, prevPath, prevIsSvg) = allIconsPath.getOrElseUpdate((fileNameWithoutExtension, curTheme, curCategory), {
          (curSize, file.getAbsolutePath, ext == "svg")
        })

        if(prevIsSvg && curSize > 48 || (!prevIsSvg && prevSize < curSize) || (ext == "svg" && prevSize < 30)) {
          allIconsPath((fileNameWithoutExtension, curTheme, curCategory)) = (curSize, file.getAbsolutePath, ext == "svg")
        }
        val curIcon = icons.getOrElseUpdate(fileNameWithoutExtension, new AppIcon(fileName))
        if(!curCategory.isEmpty) {
          categories(curCategory).add(curIcon)
          curIcon.addCategory(curCategory)
        }
        curIcon.addPath(file.getAbsolutePath, curTheme, curSize)
      }
    }
    if(level == 3) { curCategory = ""}
    if(level == 2) { curSize = -1}
  }
  */

}
