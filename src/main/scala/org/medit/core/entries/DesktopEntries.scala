package org.medit.core.entries

import java.io.File
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
}