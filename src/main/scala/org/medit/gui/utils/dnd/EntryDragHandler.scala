package org.medit.gui.utils.dnd

/**
 * Created by nico on 22/09/15.
 */

import java.awt.{Cursor, Point, Image}
import java.awt.datatransfer.{DataFlavor, Transferable}
import java.awt.event.{MouseMotionListener, MouseAdapter, MouseEvent}
import javax.imageio.ImageIO
import javax.swing._

import org.medit.core.entries.DesktopEntry

class EntryDragHandler[T <: AnyRef](component: JComponent, entry: T, entryFlavor: DataFlavor) extends TransferHandler() {
  private val entryFlavors = Array(entryFlavor)

  def onMouseEvent(e: MouseEvent): Unit = {
    val draggedEntry = entry match {
      case x: DesktopEntry => Some(x)
      case z: EntryWithinFolder => Some(z.entry)
      case _ => None
    }
    for(ll <- draggedEntry; if(ll.isEditable)) {
      if (SwingUtilities.isLeftMouseButton(e) && component.isEnabled) {
        exportAsDrag(component, e, getSourceActions(component))
      }
    }
  }

  component.addMouseMotionListener(new MouseMotionListener {
    override def mouseMoved(e: MouseEvent) = onMouseEvent(e)
    override def mouseDragged(e: MouseEvent) = onMouseEvent(e)
  })

  override def getSourceActions(c: JComponent): Int = {
    TransferHandler.COPY
  }

  protected override def createTransferable(c: JComponent) = new Transferable() {
    override def getTransferDataFlavors = entryFlavors

    override def isDataFlavorSupported(flavor: DataFlavor) = flavor == entryFlavor

    override def getTransferData(flavor: DataFlavor): AnyRef = {
      if (isDataFlavorSupported(flavor)) entry else null
    }
  }
}