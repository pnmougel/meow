package org.medit.gui.folders

import java.awt._
import java.awt.dnd.DnDConstants
import java.awt.dnd.DragSource
import com.alee.laf.panel.WebPanel
import org.medit.core.Folder
import org.medit.gui.Main
import org.medit.gui.components.BsInput
import org.medit.gui.components.BsLabel
import org.medit.gui.panels._
import org.medit.gui.entries._
import org.medit.gui.utils.SwingEvents.convertComponent
import org.medit.gui.utils.{Colors, WrapLayout, Icon}
import org.medit.gui.utils.dnd.DragEntryGesture
import javax.swing.{Box, BoxLayout, JLabel, JPanel}
import org.medit.gui.utils.dnd.MyDropTargetListener
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

  val hideButton = new WebLabel("Remove the folder", Icon.get("cross", 12))
  hideButton.setMargin(5)
  hideButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
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
//      folder.addCategory(newCategory)
      updateCategories()
    }
  })

  def updateTopPanel() = Main.updateContainer(topPanel, () => {
    for (entry <- folder.getDesktopEntries().take(5)) {
      val entryView = new EntryView(entry, 25, false, Some(this))
      entryView.setVisible(entry.isVisible)
      topPanel.add(Box.createHorizontalStrut(5), 0)
      topPanel.add(entryView, 0)
    }
    topPanel.add(Box.createHorizontalGlue(), 0)
    topPanel.add(label, 0)
  })

  def updateCategories(): Unit = {
    Main.updateContainer(categoriesPanel, () => {
//      for (category <- folder.getCategories) {
//        val categoryLabel = new BsLabel(category, category != folder.name)
//        categoryLabel.onRemove(category => {
//          folder.removeCategory(category)
//          updateCategories()
//        })
//        categoriesPanel.add(categoryLabel)
//      }
      categoriesPanel.add(addCategoryField)
      categoriesPanel.add(hideButton)
      updateEntries()
      updateTopPanel()
    })
  }

  def toggleDisplayEntries() = {
    val isVisible = entriesPanel.isVisible()
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
        entryView.setVisible(entry.isVisible)

        val ds = new DragSource()
        ds.createDefaultDragGestureRecognizer(entryView, DnDConstants.ACTION_COPY, new DragEntryGesture())

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
//  addComponent(categoriesPanel)
  addComponent(hideButton)
  addComponent(entriesPanel)
  addFiller()

  // The drop target should be added at the end
  val dropTargetListener = new MyDropTargetListener(this)
  dropTargetListener.onDropEntry { entry =>
    // If it is from another folder, remove the entry from the previous folder
    for(from <- entry.sourceFolder) {
      entry.removeCategory(from.folder.name)
      from.updateCategories()
    }
    entry.addCategory(folder.name)
    entry.save()
    updateCategories()
    entry.sourceFolder = None
  }
}