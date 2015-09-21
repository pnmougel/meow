package org.medit.gui.folders

import org.medit.core.Folders
import org.medit.gui.Main
import org.medit.gui.panels.VerticalPanel
import org.medit.gui.tasks.AddFolderPanel

object FolderPanel extends VerticalPanel {
  val addFolderPanel = new AddFolderPanel()
  def updateGroups() = {
    Main.updateContainer(this, () => {
      addComponent(addFolderPanel)
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