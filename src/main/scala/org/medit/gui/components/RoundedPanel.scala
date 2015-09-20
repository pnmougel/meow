package org.medit.gui.components

import javax.swing.JPanel
import javax.swing.border.TitledBorder
import java.awt.BorderLayout
import javax.swing.JLabel
import javax.swing.border.Border
import javax.swing.border.LineBorder
import java.awt.Color
import com.alee.laf.label.WebLabel
import com.alee.extended.label.WebHotkeyLabel
import java.awt.Graphics2D
import java.awt.Graphics
import java.awt.RenderingHints
import javax.swing.border.EmptyBorder

class RoundedPanel extends JPanel {
  setLayout(new BorderLayout)
  setBackground(null)

  override def paintComponent(g: Graphics): Unit = {
    super.paintComponent(g)
    val g2d = g.asInstanceOf[Graphics2D]
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g2d.setColor(getForeground())
    g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 4, 4)
    g2d.setColor(Color.BLACK)
  }
}