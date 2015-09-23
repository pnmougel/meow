package org.medit.gui.folders

import java.awt._
import com.alee.laf.panel.WebPanel
import com.alee.managers.language.data.TooltipWay
import com.alee.managers.tooltip.TooltipManager
import org.medit.core.Folder
import org.medit.gui.Main
import org.medit.gui.components.BsInput
import org.medit.gui.panels._
import org.medit.gui.entries._
import org.medit.gui.utils.SwingEvents.convertComponent
import org.medit.gui.utils.{FontAwesome, Colors, WrapLayout, Icon}
import org.medit.gui.utils.dnd._
import javax.swing.{Box, BoxLayout, JLabel, JPanel}
import com.alee.laf.label.WebLabel

class FolderView(val folder: Folder) extends VerticalPanel {
  setName("folder-panel")

  val label = new JLabel(folder.name)
  label.setName("task-name")

  val topPanel = new WebPanel()
  topPanel.setMargin(5, 0, 5, 5)
  topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS))
  topPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
  topPanel.onClick( e => {
    toggleDisplayEntries()
  })

  val hideButton = new WebLabel(FontAwesome.faTimes)
  hideButton.setFont(FontAwesome.font(16))
  hideButton.setForeground(Colors.red)
  hideButton.setMargin(0, 12, 0, 12)
  hideButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
  TooltipManager.addTooltip(hideButton, "Remove the folder", TooltipWay.down, 0)
  hideButton.setVisible(false)
  hideButton.onClick(e => {
    folder.hide()
    FolderPanel.updateGroups()
  })

  val categoriesPanel = new JPanel(new WrapLayout(FlowLayout.LEFT))
  categoriesPanel.setVisible(false)
  categoriesPanel.setBackground(null)
  val addCategoryField = new BsInput()
  addCategoryField.onAdd(newCategory => {
    if (!newCategory.isEmpty) {
      updateCategories()
    }
  })


  val smallAppContainer = new JPanel(new FlowLayout(FlowLayout.TRAILING))
  smallAppContainer.setBackground(null)
  def updateTopPanel() = Main.updateContainer(smallAppContainer, () => {
    for (entry <- folder.getDesktopEntries().take(5)) {
      val entryView = new EntryView(entry, 25, false, Some(this))
      val transferHandler = new EntryDragHandler[EntryWithinFolder](entryView.imageLabel, new EntryWithinFolder(entry, this), TransferDataFlavor.entryInFolderFlavor)
      entryView.imageLabel.setTransferHandler(transferHandler)
      entryView.setVisible(entry.isVisible)
      smallAppContainer.add(entryView)
    }
  })
  topPanel.add(smallAppContainer, 0)
  topPanel.add(hideButton, 0)
  topPanel.add(Box.createHorizontalGlue(), 0)
  topPanel.add(label, 0)

  def updateCategories(): Unit = {
    Main.updateContainer(categoriesPanel, () => {
      categoriesPanel.add(addCategoryField)
      updateEntries()
      updateTopPanel()
    })
  }

  def toggleDisplayEntries() = {
    val isVisible = entriesPanel.isVisible()
    smallAppContainer.setVisible(isVisible)
    entriesPanel.setVisible(!isVisible)
    categoriesPanel.setVisible(!isVisible)
    hideButton.setVisible(!isVisible)
    repaint()
  }

  val entriesPanel = new JPanel(new WrapLayout(FlowLayout.LEFT))
  entriesPanel.setVisible(false)
  entriesPanel.setBackground(null)
  def updateEntries(): Unit = {
    Main.updateContainer(entriesPanel, () => {
      for (entry <- folder.getDesktopEntries()) {
        val entryView = new EntryView(entry, 40, false, Some(this))
        val transferHandler = new EntryDragHandler[EntryWithinFolder](entryView.imageLabel, new EntryWithinFolder(entry, this), TransferDataFlavor.entryInFolderFlavor)
        entryView.imageLabel.setTransferHandler(transferHandler)
        entryView.setVisible(entry.isVisible)
        entriesPanel.add(entryView)
      }
    })
  }
  updateCategories()

  topPanel.onEnter(e => {
      setBackground(Colors.lightGray)
  })
  topPanel.onExit(e => {
    setBackground(Colors.white)
  })

  // Layout the components
  addComponent(topPanel)
  addComponent(entriesPanel)
  addFiller()

  setTransferHandler(new EntryDropHandler(Some(this)))
}