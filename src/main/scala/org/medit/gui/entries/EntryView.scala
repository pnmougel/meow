package org.medit.gui.entries

import java.awt._
import java.awt.dnd.{DnDConstants, DragSource}
import java.awt.event.{ActionEvent, MouseEvent}
import javax.swing.{JRootPane, JPanel}

import com.alee.extended.window.{PopOverDirection, WebPopOver}
import com.alee.laf.label.WebLabel
import com.alee.managers.language.data.TooltipWay
import com.alee.managers.tooltip.TooltipManager
import org.medit.core.entries.DesktopEntry
import org.medit.core.icons.IconFinder
import org.medit.gui.folders.FolderView
import org.medit.gui.utils.{EventsSystem, Colors, MultilineLabelAutoFit}
import org.medit.gui.utils.dnd.{EntryDropHandler, EntryDragHandler}
import org.medit.gui.utils.SwingEvents._

class EntryView(val entry: DesktopEntry, iconSize: Int = 140, showLabel: Boolean = true, val folder: Option[FolderView] = None) extends JPanel(new BorderLayout()) {
  val maxTextWidth = iconSize - 5

  val dimension = if (showLabel) {
    new Dimension(iconSize, iconSize - 5)
  } else { new Dimension(iconSize, iconSize) }
  setMinimumSize(dimension)
  setPreferredSize(dimension)
  setMaximumSize(dimension)
  setBackground(null)
  val iconName = entry.getIcon.getOrElse("NOICON")

  val textLabel = new MultilineLabelAutoFit(entry, iconSize, 45)
  textLabel.setName("entry-name-label")

  val imageLabel = new WebLabel()
  imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))

  val image = if (showLabel) {
    val imagePanel = new JPanel()
    imagePanel.setBackground(Colors.white)
    imagePanel.add(imageLabel)
    add(imagePanel)

    imageLabel.onClick(e => {
      if((e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK && entry.isEditable) {
        entry.setVisible(entry.isVisible)
        entry.save()
        EventsSystem.triggerEvent(EventsSystem.entriesListUpdated)
      }
    })

    add(textLabel, BorderLayout.SOUTH)
    IconFinder.getIconLoader(iconName, iconSize - 70, imageLabel)
  } else {
    add(imageLabel)
    TooltipManager.addTooltip(imageLabel, entry.getName, TooltipWay.down, 0)
    IconFinder.getIconLoader(iconName, iconSize, imageLabel)
  }
  imageLabel.setIcon(image)

}

