package org.medit.gui.tasks

import java.awt.BorderLayout
import java.awt.event.KeyEvent

import com.alee.laf.label.WebLabel
import com.alee.laf.panel.WebPanel
import com.alee.laf.text.WebTextField
import org.medit.core.Folders
import org.medit.gui.components.BsHiddenInput
import org.medit.gui.folders.FolderPanel
import org.medit.gui.utils.Icon
import org.medit.gui.utils.SwingEvents._

/**
  * Created by nico on 15/09/15.
  */
class AddFolderPanel extends WebPanel(new BorderLayout()) {
  val addFolderInput = new BsHiddenInput()
  addFolderInput.setMargin(0)
  addFolderInput.setInputPrompt("Create an application folder")

  def createNewGroup() = {
    val newFolderName = addFolderInput.getText()
    if (!newFolderName.isEmpty()) {
      Folders.addFolder(newFolderName)
      FolderPanel.updateGroups()
      addFolderInput.setText("")
    }
  }

  addFolderInput.onKeyKeyEnter(_ => createNewGroup())

  setMargin(0, 5, 0, 0)
  setBackground(null)
  add(addFolderInput, BorderLayout.CENTER)
 }
