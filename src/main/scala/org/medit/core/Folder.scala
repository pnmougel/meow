package org.medit.core

import sys.process.stringSeqToProcess
import sys.process.stringToProcess
import org.medit.core.entries.DesktopEntry
import org.medit.core.entries.DesktopEntries

class Folder(val name: String) {
  var categories = getCategories()

  def getDesktopEntries(): List[DesktopEntry] = {
    DesktopEntries.getEntriesWithCategories(categories)
  }

  def addCategory(category: String) = {
    var curCategories = getCategories()
    if (!curCategories.contains(category)) {
      categories = category :: categories
      setCategories(categories)
    }
  }

  def getCategories(): List[String] = {
    if (!name.isEmpty()) {
      val categoriesStr = (Seq("gsettings", "get", s"org.gnome.desktop.app-folders.folder:/org/gnome/desktop/app-folders/folders/${name}/", "categories").!!).trim()
      if (categoriesStr.startsWith("[") && categoriesStr.endsWith("]")) {
        Folders.stringToList(categoriesStr)
      } else List()
    } else List()
  }

  def setCategories(categories: List[String]) = {
    this.categories = categories
    val categoriesStr = if (categories.isEmpty) "[]" else categories.mkString("['", "', '", "']")
    val path = s"org.gnome.desktop.app-folders.folder:/org/gnome/desktop/app-folders/folders/${name}/"
    Seq("gsettings", "set", path, "categories", categoriesStr).!
  }

  def removeCategory(category: String) = {
    categories = categories.filter(!_.equals(category))
    setCategories(categories)
  }

  /**
   * Show the folder
   */
  def show(): Unit = {
    val curFolders = Folders.listFolders()
    val newFolders = if (curFolders.contains(name)) {
      curFolders
    } else {
      name :: curFolders
    }
    Seq("gsettings", "set", "org.gnome.desktop.app-folders", "folder-children", newFolders.mkString("['", "', '", "']")).!
  }

  /**
   * Hide the folder but keep the entry
   */
  def hide(): Unit = {
    val newFolders = Folders.listFolders().filter(folderName => !folderName.equalsIgnoreCase(name))
    Folders.removeFolder(this)
    Seq("gsettings", "set", "org.gnome.desktop.app-folders", "folder-children", newFolders.mkString("['", "', '", "']")).!
  }

  /**
   * Remove completely the folder
   */
  def remove(): Unit = {
    Seq("dconf", "reset", "-f", s"/org/gnome/desktop/app-folders/folders/${name}/").!
  }

}