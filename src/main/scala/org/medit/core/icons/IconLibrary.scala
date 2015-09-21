package org.medit.core.icons

import java.awt.event.{ActionEvent, ActionListener}
import java.io.{FileInputStream, File}
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.{Files, Paths}
import java.security.MessageDigest

import org.apache.commons.io.{IOUtils, FilenameUtils, FileUtils}
import org.medit.gui.utils.Timer2
import sun.security.provider.MD5

import scala.collection.mutable
import scala.sys.process.stringSeqToProcess

/**
 * Created by nico on 17/09/15.
 */
object IconLibrary {
  val home = System.getProperty("user.home")
  val pathsForIcons = Seq("/usr/share/icons", s"${home}/.local/share/icons", "/usr/share/pixmaps")
  val icons = mutable.HashMap[String, AppIcon]()
//  val gtkTheme = Seq("gsettings", "get", "org.gnome.desktop.interface", "gtk-theme").!!.trim().replaceAllLiterally("'", "")
  val iconTheme = Seq("gsettings", "get", "org.gnome.desktop.interface", "icon-theme").!!.trim().replaceAllLiterally("'", "")
  val gtkTheme = "Faenza"
  println(gtkTheme)
  println(iconTheme)

  val categories = mutable.HashMap[String, mutable.HashSet[AppIcon]]()
  val categoriesList = mutable.HashSet("mimetypes","animations","code","actions","cursors","web","media","stock","text","mimes","categories","form","symbolic","navigation","icons","status-extra","filesystems","apps-extra","image","sources","devices","special","actions-extra","places","data","table","io","object","status","places-extra","chart","emotes","net","intl","emblems","search","apps")
  val allIconsPath = mutable.HashMap[(String, String, String), (Int, String, Boolean)]()
  val timer = new javax.swing.Timer(1000, new ActionListener {
    override def actionPerformed(p1: ActionEvent): Unit = {
      println("Key pressed")
    }
  })

  categories("") = mutable.HashSet[AppIcon]()
  for(c <- categoriesList) { categories(c) = mutable.HashSet[AppIcon]()}
  val thumbnailSize = 40
  var curTheme = ""
  var curCategory = ""

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
//        val sizeImprove = (Math.abs(prevSize - thumbnailSize) < Math.abs(curSize - thumbnailSize))
//        if(prevIsSvg && curSize >= thumbnailSize || (!prevIsSvg && sizeImprove) || (ext == "svg" && prevSize < thumbnailSize)) {
//          allIconsPath((fileNameWithoutExtension, curTheme, curCategory)) = (curSize, file.getAbsolutePath, ext == "svg")
//        }

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

  val digests = new mutable.HashSet[String]
  def searchIcon(name: String, categorySearched: Option[String] = None, themeSearched: Option[String] = None) : List[(Int, String)] = {
    digests.clear()
    val nameLower = name.toLowerCase.trim()
    val nameParts = nameLower.split(" ")

    (for (((iconName, iconTheme, iconCategory)) <- allIconsPath.keySet) yield {
      val (iconSize, iconPath, _) = allIconsPath((iconName, iconTheme, iconCategory))
      var matchScore = if (iconName == nameLower) 0
      else if (iconName.startsWith(nameLower)) 1
      else if (iconName.contains(nameLower)) 2
      else if (nameParts.exists(n => iconName.contains(n))) 3
      else -1
      if(iconSize < 32) matchScore = -1
      for (c <- categorySearched; if c != iconCategory) matchScore = -1
      for (c <- themeSearched; if c != iconTheme) matchScore = -1
      (matchScore, iconPath)
    }).filter(_._1 != -1).toList.sortWith( (a, b) => {
      if(a._1 > b._1) { false }
      else if(a._1 == b._1) { a._2 < b._2 }
      else true
    })
  }

  def loadNextImages(icons: List[(Int, String)], n: Int) : (List[LazyIconLoader], List[(Int, String)]) = {
    val (l1, l2) = icons.splitAt(n)
    (l1.map( e => {
      val file = new File(e._2)
      val md = MessageDigest.getInstance("MD5")
      val hash = new BigInteger(md.digest(FileUtils.readFileToByteArray(file))).toString
      val isAlreadyFound = digests.contains(hash)
      digests.add(hash)
      (e._1, e._2, isAlreadyFound)
    }).filterNot(_._3).map(e => {
      new LazyIconLoader(e._2, thumbnailSize)
    }), l2)
  }


  for (path <- pathsForIcons) { explorePath(new File(path), 0, -1, "") }
}
