package org.medit.gui.entries

import java.awt.BorderLayout
import java.awt.Point

import com.alee.extended.label.WebMultiLineLabel
import com.alee.laf.label.WebLabel
import com.alee.laf.panel.WebPanel
import org.medit.gui.entries.properties._
import org.medit.gui.panels.VerticalPanel
import org.medit.gui.utils.CornerBorder

/*
object EntryDetailsPanel extends JPanel(new BorderLayout) {
  val curDropTarget = new MyDropTargetListener(this)
  
  setVisible(false)
  setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))

  setBackground(Color.white)
  val detailPanel = new JPanel(new BorderLayout)
  detailPanel.setBackground(Color.white)

  val scrollPane = new WebScrollPane(detailPanel, false, true)
  scrollPane.setPaintButtons(false)
  scrollPane.getVerticalScrollBar().setUnitIncrement(40)
  add(scrollPane)

  def buildTitlePanel(entry: DesktopEntry): JPanel = {
    val titlePanel = new JPanel(new WrapLayout(FlowLayout.LEADING))
    titlePanel.setBackground(Color.white)
    for (icon <- entry.getIcon) {
      titlePanel.add(new WebLabel(IconFinder.getIcon(icon, 40)))
    }
    val entryNameLabel = new WebLabel(entry.getName)
    entryNameLabel.setName("entry-name-title")

    val saveButton = new WebButton("Save")
    saveButton.onClick { e =>
      entry.save()
    }

    titlePanel.add(entryNameLabel)
    titlePanel.add(saveButton)
    titlePanel
  }

  def buildCategoryPanel(entry: DesktopEntry): JPanel = {
    val categoriesPanel = new JPanel(new WrapLayout(FlowLayout.LEADING))
    categoriesPanel.setBackground(Color.white)
    val addCategoryField = new BsInput()
    addCategoryField.onAdd(newCategory => {
      if (!newCategory.isEmpty()) {
        entry.addCategory(newCategory)
        updateCategories()
      }
    })

    def updateCategories(): Unit = {
      Main.updateContainer(categoriesPanel, () => {
        for (category <- entry.getCategories) {
          val categoryLabel = new BsLabel(category)
          categoryLabel.onRemove(category => {
            entry.removeCategory(category)
            updateCategories()
          })
          categoriesPanel.add(categoryLabel)
        }
        categoriesPanel.add(addCategoryField)
      })
    }
    updateCategories()
    categoriesPanel
  }

  def setEntry(entry: DesktopEntry) = {
    setVisible(true)
    Main.splitPane.setDividerLocation(Main.frame.getHeight - 150)
    Main.updateContainer(detailPanel, () => {
      detailPanel.add(buildTitlePanel(entry), BorderLayout.NORTH)
      detailPanel.add(buildCategoryPanel(entry))
    })
  }
}
*/

object EntryDetailsPanel extends WebPanel(new BorderLayout) {
  setVisible(false)

  val nameProperty = new NameProperty()
  val iconProperty = new IconProperty()
  val execProperty = new ExecProperty()
  val visibleProperty = new VisibilityProperty()
  val keywordsProperty = new ListKindProperty("Keywords: ", "Keywords", "Add a keyword", "Keywords are used when searching an application in the main menu")
  val categoriesProperty = new ListKindProperty("Categories: ", "Categories", "Add a category", "Categories are used to associate an application to a folder")
  var properties = List[EntryProperty](nameProperty, iconProperty, execProperty, visibleProperty, keywordsProperty, categoriesProperty)

  val contentPanel = new VerticalPanel()
  contentPanel.addComponent(nameProperty)
  contentPanel.addComponent(execProperty)
  contentPanel.addComponent(categoriesProperty)
  contentPanel.addComponent(keywordsProperty)
  contentPanel.addComponent(visibleProperty)
  contentPanel.addFiller()

  val rightPanel = new VerticalPanel()
  rightPanel.addComponent(iconProperty)
  rightPanel.addFiller()

  val entryContent = new WebMultiLineLabel()
  add(contentPanel, BorderLayout.CENTER)
  add(rightPanel, BorderLayout.EAST)

  def setEntry(entry: EntryView) = {
    val p: Point = entry.getLocation
    for(p <- properties) { p.setEntry(entry)}
    setBorder(new CornerBorder((p.getX + entry.getWidth / 2).toInt, true, (EntriesPanel.getWidth - Math.floor(EntriesPanel.getWidth / 145) * 145).toInt))
    setVisible(true)
  }
}