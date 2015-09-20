package org.medit.gui.components

import java.awt.Cursor

import com.alee.laf.label.WebLabel
import org.medit.gui.utils.Icon
import org.medit.gui.utils.SwingEvents._

/**
 * Created by nico on 15/09/15.
 */
class BsToggle(label: String, help: String = "", isDefaultToggle : Boolean = false) extends WebLabel(label, Icon.get("radio_button")) {
  var isSelected = !isDefaultToggle
  setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))

  this.onClick( e => {
    toggle
  })

  def setSelected(v: Boolean) = {
    isSelected = !v
    toggle
  }

  var isEditable = true
  def setEditable(isEditable: Boolean) = {
    this.isEditable = isEditable
  }

  def toggle = {
    if(isEditable) {
      isSelected = !isSelected
      setIcon(Icon.get(if(isSelected) "accept_button" else "radio_button", 13))
    }
  }
  toggle

  setName("bs-toggle")
}
