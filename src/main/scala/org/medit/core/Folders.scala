package org.medit.core

import scala.collection.mutable
import sys.process.stringSeqToProcess
import sys.process.stringToProcess

object Folders {
  var folders = List[Folder]()

  def addFolder(name: String, categories: Seq[String] = List()): Unit = {
    val curFolders = listFolders()
    if(!curFolders.contains(name)) {
      val categoriesStr = if (categories.length == 0) s"['${name}']"
      else {
        categories.mkString("['", "', '", "']")
      }

      val path = s"org.gnome.desktop.app-folders.folder:/org/gnome/desktop/app-folders/folders/${name}/"
      Seq("gsettings", "set", path, "categories", categoriesStr).!

      Seq("gsettings", "set", path, "translate", "true").!

      Seq("gsettings", "set", path, "name", name).!

      val newFolders = name :: curFolders
      val newFolder = new Folder(name)
      folders = (newFolder :: folders).sortBy(_.name.toLowerCase())
      Seq("gsettings", "set", "org.gnome.desktop.app-folders", "folder-children", newFolders.mkString("['", "', '", "']")).!

    }
  }

  def removeFolder(folder: Folder) = {
    folders = folders.filter(f => f != folder)
  }

  /**
   * Returns the list of Gnome menu folders
   */
  def listFolders(): List[String] = {
    val folders = "gsettings get org.gnome.desktop.app-folders folder-children".!!
    stringToList(folders)
  }

  def stringToList(str: String): List[String] = {
    str.replaceAll("[\\[\\]']", "").split(",").toList.map(s => s.trim())
  }

  def getAllCategories(): mutable.HashSet[String] = {
    mutable.HashSet[String]((for (f <- getFolders(); category <- f.categories) yield {
      category
    }): _*)
  }

  /**
   * Returns the list of Gnome menu folders
   */
  def getFolders(): List[Folder] = {
    if (folders.isEmpty) {
      val folderNamesStr = "gsettings get org.gnome.desktop.app-folders folder-children".!!
      val folderNames = stringToList(folderNamesStr)
      folders = folderNames.filter(!_.isEmpty).map(name => {
        new Folder(name)
      })
    }
    folders.sortBy(_.name.toLowerCase())
  }
}