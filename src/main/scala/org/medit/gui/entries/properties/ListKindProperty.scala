package org.medit.gui.entries.properties

import java.awt.{BorderLayout, Color, FlowLayout}
import javax.swing.JPanel

import com.alee.laf.label.WebLabel
import com.alee.laf.panel.WebPanel
import com.alee.managers.language.data.TooltipWay
import com.alee.managers.tooltip.TooltipManager
import org.medit.gui.Main
import org.medit.gui.components.{BsHiddenInput, BsLabel}
import org.medit.gui.entries.EntryView
import org.medit.gui.panels.VerticalPanel
import org.medit.gui.utils.SwingEvents._
import org.medit.gui.utils.WrapLayout

/**
 * Created by nico on 16/09/15.
 */
class ListKindProperty(label: String, fieldKey: String, inputPrompt: String, helpMessage: String = "") extends WebPanel with EntryProperty {
  val leftContainer = new VerticalPanel()
  val items = new JPanel(new WrapLayout(FlowLayout.LEADING))
  items.setBackground(null)

  // Update the list of items
  def updateItems() : Unit = {
    // Remove all components except the input field
    val componentsToRemove = for(c <- items.getComponents; if(c != addInput)) yield { c }
    for(c <- componentsToRemove) { items.remove(c) }

    for(e <- entry; itemList <- e.entry.getListValue(fieldKey); item <- itemList) {
      val label = new BsLabel(item, isEditable)
      label.setName("keyword")
      label.onRemove( _ => {
        e.entry.removeFromList(fieldKey, item)
        e.entry.save()
        updateItems()
      })
      if(isEditable) {
        items.add(label, 0)
      }
    }
    items.doLayout()
    Main.styles.updateComponents()
  }

  // Field to add a new item
  val addInput = new BsHiddenInput()
  addInput.setColumns(10)
  addInput.setName("small-hidden-input")
  addInput.setInputPrompt(inputPrompt)
  addInput.onKeyKeyEnter(event => {
    for(e <- entry; if(!addInput.getText.isEmpty)) {
      e.entry.addToList(fieldKey, addInput.getText)
      e.entry.save()
      addInput.setText("")
      updateItems()
    }
  })
  items.add(addInput)

  val titleLabel = new WebLabel(label)
  titleLabel.setMinimumWidth(75)
  titleLabel.setName("property-name")
  if(!helpMessage.isEmpty) {
    TooltipManager.addTooltip(titleLabel, helpMessage, TooltipWay.down, 0)
  }
  leftContainer.addComponent(titleLabel)
  leftContainer.addFiller()

  setLayout(new BorderLayout())
  add(leftContainer, BorderLayout.WEST)
  add(items, BorderLayout.CENTER)

  override def setEntry(newEntry: EntryView) = {
    isEditable = newEntry.entry.isEditable
    addInput.setVisible(isEditable)
    entry = Some(newEntry)
    updateItems()
  }
}
