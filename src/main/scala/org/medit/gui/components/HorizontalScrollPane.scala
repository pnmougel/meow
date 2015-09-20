package org.medit.gui.components

import java.awt.Color
import javax.swing.JPanel

import com.alee.laf.scroll.{WebScrollBar, WebScrollPane}

/**
 * Created by nico on 15/09/15.
 */
class HorizontalScrollPane(panel: JPanel) extends WebScrollPane(panel, false, true) {
  setBackground(null)
  setPaintButtons(false)
  val scrollBar = getVerticalScrollBar.asInstanceOf[WebScrollBar]
  scrollBar.setUnitIncrement(64)
  scrollBar.setMargin(0)
  scrollBar.setPaintTrack(false)
  scrollBar.setBackground(Color.white)
  scrollBar.setForeground(Color.white)
  scrollBar.setPreferredWidth(8)
  scrollBar.setMargin(0, 0, 0, 0)
}
