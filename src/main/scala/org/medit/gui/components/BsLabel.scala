package org.medit.gui.components

import javax.swing.JLabel
import java.awt.{Cursor, Color, Font, BorderLayout}
import javax.swing.JButton
import javax.swing.border.EmptyBorder

import org.medit.gui.utils.Colors
import org.medit.gui.utils.SwingEvents._

class BsLabel(label: String, isRemovable: Boolean = true) extends RoundedPanel {
  setName("round-panel")
  val l = new JLabel(label)
  l.setName("BsLabelContent")
  add(l, BorderLayout.CENTER)

  def setText(text: String): Unit = {
    l.setText(text)
  }

  val removeButton = new JLabel("x")
  removeButton.setForeground(Color.decode("#777777"))
  removeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
  removeButton.setFont(removeButton.getFont().deriveFont(Font.BOLD, 11))
  removeButton.setBorder(new EmptyBorder(0, 0, 0, 4))
  removeButton.onEnter(e => {
    removeButton.setForeground(Colors.red)
  })
  removeButton.onExit(e => {
    removeButton.setForeground(Color.decode("#777777"))
  })

  if (isRemovable) {
    add(removeButton, BorderLayout.EAST)
  }

  def onRemove(f: ((String) => Unit)) = {
    removeButton.onClick(e => {
      f(label)
    })
  }
}