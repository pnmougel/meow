package org.medit.core.icons

import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

import org.apache.commons.io.{FileUtils, FilenameUtils}
import org.medit.core.icons.themes.{IconTheme, IconThemeNoIndex, IconThemeWithIndex}
import org.medit.core.utils.GlobalPaths

import scala.collection.mutable
import scala.sys.process.stringSeqToProcess

/**
 * Created by nico on 17/09/15.
 */
object IconLibrary {

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

  def getSingleIconPath(appName: String) : Option[String] = {
    val name = appName.toLowerCase.trim
    var iconPathMatch : Option[String] = None
    val themesToSearch = IconLibrary.iconThemes(IconLibrary.iconTheme) ::: IconLibrary.iconThemes(IconLibrary.fallbackTheme)
    // Try to find an icon available in the current theme
    themesToSearch.find( theme => {
      if(theme.preferredIcons.contains(name)) {
        iconPathMatch = Some(name)
      }
      theme.preferredIcons.contains(name)
    })

    // Otherwise search an icon in another theme and return the full path
    iconPathMatch.orElse({
      allThemes.find( theme => {
        if(theme.preferredIcons.contains(name)) {
          iconPathMatch = Some(theme.preferredIcons(name))
        }
        theme.preferredIcons.contains(name)
      })
      iconPathMatch
    })
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
  for (path <- GlobalPaths.pathsForIcons) { explorePath(new File(path)) }
  for (theme <- allThemes) { theme.buildInheritedThemes}
}
