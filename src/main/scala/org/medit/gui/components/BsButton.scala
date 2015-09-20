package org.medit.gui.components

import javax.swing.JTextField
import javax.swing.JButton
import java.awt.BorderLayout
import java.awt.Font
import java.awt.Color
import javax.swing.border.EmptyBorder
import javax.swing.JLabel
import org.medit.gui.utils.SwingEvents._
import java.awt.event.KeyEvent
import com.alee.laf.button.WebButton
import javax.swing.ImageIcon

class BsButton(label: String) extends WebButton(label) {

  def setIcon(icon: ImageIcon) = {
    super.setIcon(icon)
  }

  setName("bs-button")
  setBackground(Color.WHITE)
  setOpaque(false)
  setShineColor(Color.WHITE)
  setShadeColor(Color.WHITE)
  setBottomBgColor(Color.WHITE)
  setTopBgColor(Color.WHITE)
}