package org.medit.core.entries

import java.io.{PrintWriter, File}
import java.nio.file.{Path, Paths}
import org.apache.commons.io.FilenameUtils
import org.medit.core.icons.{IconDownloader, IconLibrary}
import org.medit.core.utils.FileUtils

import scala.collection.mutable
import scala.collection.mutable.HashSet

object DesktopEntries {
  val home = System.getProperty("user.home")
  val desktopEntriePaths = List(s"$home/.local/share/applications/", "/usr/share/applications", "/usr/local/share/applications")

  // List of all desktop entries
  var desktopEntries = List[DesktopEntry]()
  for(path <- desktopEntriePaths) { findDesktopEntries(path) }

  // Find all properties
  val propertiesCount = mutable.HashMap[String, Int]()
  val types = mutable.HashSet[String]()
  for(desktopEntry <- desktopEntries; category <- desktopEntry.groups; property <- category.properties; key <- property.getKey()) {
    if(!key.endsWith("]")) {
      propertiesCount(key) = propertiesCount.getOrElseUpdate(key, 0) + 1
      if(key == "Type") {
        types.add(property.getValue().get)
      }
    }
  }
//  println(propertiesCount.toList.sortBy(_._2).mkString("\n"))
//  println(types)

  var desktopNames = HashSet[String]()

  desktopEntries.foreach(entry => {
    for (
      desktops <- entry.getListValue("OnlyShowIn");
      desktopName <- desktops
    ) {
      desktopNames.add(desktopName)
    }
    for (
      desktops <- entry.getListValue("NotShowIn");
      desktopName <- desktops
    ) {
      desktopNames.add(desktopName)
    }
  })

  def findDesktopEntries(path: String): Unit = {
    val f = new File(path)
    if(f.isDirectory) {
      f.listFiles().foreach(file => {
        if (file.isDirectory) {
          findDesktopEntries(file.getCanonicalPath)
        } else {
          if (file.getName.endsWith(".desktop")) {
            val desktopEntry = new DesktopEntry(file)
            desktopEntries = desktopEntry :: desktopEntries
            // makeBackup(file)
          }
        }
      })
    }
  }

  def getDesktopEntries() = {
    desktopEntries.sortBy(a => a.getName.replaceAllLiterally(" ", "z").toLowerCase())
  }

  /**
   * Return the list of entries having the category
   */
  def getEntriesWithCategories(category: String): List[DesktopEntry] = {
    desktopEntries.filter(entry => {
      entry.getCategories.contains(category)
    })
  }

  def getSiteNameFromUrl(url: String) = {
    val urlElems = url.split("/")
    var domainName = ""
    if(url.contains("//") && urlElems.length > 2) {
      val domainNameParts = urlElems(2).split("\\.").toList.filter(e => e.toLowerCase != "www")
      domainName = domainNameParts.take(domainNameParts.length - 1).mkString(" ")
    }
    domainName
  }

  // Try to find the program run from the command
  def getExecFromCommand(command: String) : String = {
    println(command)
    val exec = if(command.startsWith("\"") && command.length > 1) {
      command.split("\"")(1)
    } else if(command.startsWith("'") && command.length > 1) {
      command.split("'")(1)
    } else command
    exec.split("/")(exec.split("/").length - 1).split(" ")(0)
  }

  def createDesktopEntry(appCommand: String, appName: Option[String] = None, appIcon: Option[String] = None, appCategories : List[String] = List()) = {
    var command = appCommand
    var categories = appCategories
    val exec = getExecFromCommand(command)
    var name = appName.getOrElse(FilenameUtils.getBaseName(exec))
    var icon = appIcon
    // Create an URL if the command starts with http
    if(command.startsWith("http://") || command.startsWith("https://")) {
      categories = "Website" :: categories
      name = getSiteNameFromUrl(command).capitalize
      command = "xdg-open \"" + appCommand + "\""

      // Get the icon
      // first try in the library if there is an icon matching the name
      // Then try to get an icon directly from the website
      icon = IconLibrary.getSingleIconPath(name).orElse(Some(IconDownloader.saveFavicon(appCommand, name)))
//      println(appCommand)
//      icon = Some(IconDownloader.saveFavicon(appCommand, name))
//      println(icon)
    }
    //
    if(command.startsWith("file://")) {
      val filePath = command.replaceFirst("file:/", "")
      val file = new File(filePath)
      if(file.canExecute) {
        command = "\"" + file + "\""
      } else {
        categories = "Document" :: categories
        command = "xdg-open \"" + file + "\""
        if(icon.isEmpty) {
          // Find an icon using the mimetype
          val mimeType = java.nio.file.Files.probeContentType(Paths.get(filePath)).replaceAllLiterally("/", "-")
          categories = mimeType :: categories
          if(mimeType == "image-png") {
            icon = Some(filePath)
          } else {
            icon = IconLibrary.getSingleIconPath(mimeType)
          }
        }
      }
      name = FilenameUtils.getBaseName(getExecFromCommand(filePath)).capitalize
    }

    // Try to find an icon
    val iconName = icon.getOrElse {
      IconLibrary.getSingleIconPath(name)
        .orElse(IconLibrary.getSingleIconPath(command))
        .getOrElse("")
    }

    val basePath = s"$home/.local/share/applications/"
    val fileContent = List(
      "[Desktop Entry]",
      s"Name=$name",
      s"Exec=$command",
      s"Icon=$iconName",
      s"Categories=${categories.mkString(",")}",
      "Terminal=false",
      "Type=Application").mkString("\n")
    val fileName = FileUtils.getNonExistingFileName(basePath, name.toLowerCase.replaceAllLiterally(" ", "-") + ".desktop")
    val outFile = new File(basePath + fileName)
    val out = new PrintWriter(outFile)
    try {
      out.println(fileContent)
      out.flush()
      val newDesktopEntry = new DesktopEntry(outFile)
      desktopEntries = newDesktopEntry :: desktopEntries
    } catch {
      case e: Throwable => {}
    } finally { out.close() }
  }
}