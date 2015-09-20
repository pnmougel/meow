package org.medit.gui.tasks

import java.awt.BorderLayout
import java.awt.event.KeyEvent

import com.alee.laf.label.WebLabel
import com.alee.laf.panel.WebPanel
import com.alee.laf.text.WebTextField
import org.medit.core.Folders
import org.medit.gui.folders.FolderPanel
import org.medit.gui.utils.Icon
import org.medit.gui.utils.SwingEvents._

/**
  * Created by nico on 15/09/15.
  */
object AddFolderPanel extends WebPanel(new BorderLayout()) {

  val addFolderInput = new WebTextField()
  addFolderInput.setName("add-folder-input")
  addFolderInput.setDrawFocus(false)
  addFolderInput.setDrawShade(false)
  addFolderInput.setHideInputPromptOnFocus(false)
  addFolderInput.setInputPrompt("New folder name")

  def createNewGroup() = {
    val newFolderName = addFolderInput.getText()
    if (!newFolderName.isEmpty()) {
      Folders.addFolder(newFolderName)
      FolderPanel.updateGroups()
      addFolderInput.setText("")
    }
  }

  val addFolderButton = new WebLabel(Icon.get("add"))
  addFolderButton.onClick(e => {
    createNewGroup()
  })
  addFolderInput.onKeyPress { e =>
    if (e.getKeyCode == KeyEvent.VK_ENTER) {
      createNewGroup()
    }
  }

  setMargin(0, 5, 0, 0)
  setBackground(null)
  add(addFolderInput, BorderLayout.CENTER)
  addFolderButton.setMargin(0, 5, 0, 10)
  add(addFolderButton, BorderLayout.EAST)
 }
