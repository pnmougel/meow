package org.medit.gui.entries.properties

import java.awt.{Color, BorderLayout, FlowLayout}
import javax.swing.JPanel

import com.alee.laf.label.WebLabel
import com.alee.laf.panel.WebPanel
import org.medit.gui.Main
import org.medit.gui.components.{BsHiddenInput, BsLabel, BsToggle}
import org.medit.gui.entries.EntryView
import org.medit.gui.panels.VerticalPanel
import org.medit.gui.utils.SwingEvents._
import org.medit.gui.utils.WrapLayout

/**
 * Created by nico on 16/09/15.
 */
class KeywordsProperty extends WebPanel with EntryProperty {
  setLayout(new BorderLayout())

  val rightContainer = new VerticalPanel()
  val leftContainer = new VerticalPanel()
  val items = new WebPanel(new WrapLayout(FlowLayout.LEADING))
  items.setName("no-border-container")
  items.setMargin(0)

  def updateContainer() : Unit = Main.updateContainer(rightContainer, () => {
    updateItems()
    rightContainer.addComponent(items)

    rightContainer.addComponent(inputContainer)
    rightContainer.addFiller()
  })

  def updateItems() : Unit = Main.updateContainer(items, () => {
    for(e <- entry; itemList <- e.entry.getListValue("Keywords"); item <- itemList) {
      val label = new BsLabel(item, isEditable)
      label.setName("keyword")
      label.onRemove( _ => {
        e.entry.removeFromList("Keywords", item)
        e.entry.save()
        updateContainer()
      })
      items.add(label, 0)
    }
    items.setVisible(items.getComponentCount != 0)
    if(items.getComponentCount == 0) {
      inputContainer.setMargin(3, 2, 0, 0)
    } else {
      inputContainer.setMargin(0, 2, 0, 0)
    }
  })

  val inputContainer = new WebPanel(new BorderLayout())
  val addInput = new BsHiddenInput()
  addInput.setMargin(0)
  addInput.setName("small-hidden-input")
  addInput.setInputPrompt("Add a keyword")
  addInput.onKeyKeyEnter(event => {
    for(e <- entry; if(!addInput.getText.isEmpty)) {
      e.entry.addToList("Keywords", addInput.getText)
      e.entry.save()
      addInput.setText("")
      updateItems()
      event.consume()
    }
  })
  inputContainer.add(addInput, BorderLayout.CENTER)


  val titleLabel = new WebLabel("Keywords: ")
  titleLabel.setName("property-name")
  leftContainer.addComponent(titleLabel)
  leftContainer.addFiller()

  add(leftContainer, BorderLayout.WEST)
  add(rightContainer, BorderLayout.CENTER)

  override def setEntry(newEntry: EntryView) = {
    isEditable = newEntry.entry.isEditable
    addInput.setVisible(isEditable)
    entry = Some(newEntry)
    updateContainer()
  }
}
