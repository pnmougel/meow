package org.medit.gui.entries

import java.awt._
import java.awt.dnd.{DnDConstants, DragSource}
import javax.swing.{JRootPane, JPanel}

import com.alee.extended.window.{PopOverDirection, WebPopOver}
import com.alee.laf.label.WebLabel
import com.alee.managers.language.data.TooltipWay
import com.alee.managers.tooltip.TooltipManager
import org.medit.core.entries.DesktopEntry
import org.medit.core.icons.IconFinder
import org.medit.gui.folders.FolderView
import org.medit.gui.utils.MultilineLabelAutoFit
import org.medit.gui.utils.dnd.DragEntryGesture
import org.medit.gui.utils.SwingEvents._

class EntryView(val entry: DesktopEntry, iconSize: Int = 140, showLabel: Boolean = true, val folder: Option[FolderView] = None) extends WebLabel {
  setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
  val ds = new DragSource()
  ds.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, new DragEntryGesture())

  val maxTextWidth = iconSize - 5

  val dimension = if (showLabel) {
    new Dimension(iconSize, iconSize - 5)
  } else { new Dimension(iconSize, iconSize) }
  setMinimumSize(dimension)
  setPreferredSize(dimension)
  setMaximumSize(dimension)
  setBackground(null)
  val iconName = entry.getIcon.getOrElse("NOICON")

  val textLabel = new MultilineLabelAutoFit(entry.getName, iconSize, 45)
  textLabel.setName("entry-name-label")

  var imageLabel : Option[WebLabel] = None

  if (showLabel) {
    val imageLabel = new WebLabel(IconFinder.getIcon(iconName, iconSize - 70))
    this.imageLabel = Some(imageLabel)
    val labelPanel = new JPanel()
    labelPanel.setBackground(null)
    labelPanel.add(textLabel)
    labelPanel.setBounds(0, iconSize - 65, iconSize, iconSize + 30)
    add(labelPanel)

    imageLabel.setBounds(0, -30, iconSize, iconSize)
    setBounds(0, -20, iconSize, iconSize)
    add(imageLabel)
  } else {
    // TOCHANGE
    setIcon(IconFinder.getIconLoader(iconName, iconSize, this))
    TooltipManager.addTooltip(this, entry.getName, TooltipWay.down, 0)
  }
}

