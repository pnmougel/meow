package org.medit.core

import sys.process.stringSeqToProcess
import sys.process.stringToProcess
import org.medit.core.entries.DesktopEntry
import org.medit.core.entries.DesktopEntries

class Folder(val name: String) {
//  var categories = getCategories()

  def getDesktopEntries(): List[DesktopEntry] = {
    DesktopEntries.getEntriesWithCategories(name)
  }

  /**
   * Hide the folder but keep the entry
   */
  def hide(): Unit = {
//    val newFolders = Folders.listFolders().filter(folderName => !folderName.equalsIgnoreCase(name))
    Folders.removeFolder(this)
//    Seq("gsettings", "set", "org.gnome.desktop.app-folders", "folder-children", newFolders.mkString("['", "', '", "']")).!
  }

  /**
   * Remove completely the folder
   */
  def remove(): Unit = {
    Folders.removeFolder(this)
//    Seq("dconf", "reset", "-f", s"/org/gnome/desktop/app-folders/folders/${name}/").!
  }

}