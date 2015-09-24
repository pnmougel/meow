package org.medit.gui.utils.dnd

import java.awt.Point
import java.awt.dnd.{DragSourceDropEvent, DragSourceDragEvent, DragSource}
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

import com.alee.managers.drag.DragViewHandler
import org.medit.core.entries.DesktopEntry
import org.medit.core.icons.IconFinder

import scala.collection.mutable

/**
 * Created by nico on 22/09/15.
 */
class EntryDragViewHandler {
  val cache = mutable.HashMap[DesktopEntry, BufferedImage]()

  def getObjectFlavor = TransferDataFlavor.entryFlavor
  def getView(entry: DesktopEntry, event: DragSourceDragEvent) : BufferedImage = {
    val img = IconFinder.getIcon(entry.getIcon, 30).getImage
    val bi = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB)
    bi.getGraphics.drawImage(img, 0, 0, null)
    bi.getGraphics.dispose()
    bi
  }

  def getViewRelativeLocation(entry: DesktopEntry, event: DragSourceDragEvent) = {
    new Point(-15, -15)
  }

  def dragEnded(entry: DesktopEntry, event: DragSourceDropEvent) = {
  }
}
