package org.medit.gui.folders

import org.medit.core.Folders
import org.medit.gui.Main
import org.medit.gui.panels.VerticalPanel

object FolderPanel extends VerticalPanel {
  def updateGroups() = {
    Main.updateContainer(this, () => {
      val allFolders = Folders.getFolders
      for ((folder, i) <- allFolders.zipWithIndex) {
        val folderView = new FolderView(folder)
        addComponent(folderView)
      }
      addFiller()
    })
  }
  updateGroups()

  setBackground(null)
}