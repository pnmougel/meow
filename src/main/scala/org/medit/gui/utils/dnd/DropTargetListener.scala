package org.medit.gui.utils.dnd

import java.awt.datatransfer.Transferable
import java.awt.dnd.DropTargetDropEvent
import java.awt.dnd.DropTargetAdapter
import java.awt.dnd.DnDConstants
import javax.swing.JPanel
import java.awt.dnd.DropTargetListener
import java.awt.dnd.DropTarget
import org.medit.core.entries.DesktopEntry

/**
 * @author nico
 */
class MyDropTargetListener(panel: JPanel) extends DropTargetAdapter with DropTargetListener {
  val dropTarget = new DropTarget(panel, DnDConstants.ACTION_COPY, this, true, null);
  def drop(event: DropTargetDropEvent): Unit = {
    try {
      val tr = event.getTransferable();
      val entry = tr.getTransferData(Flavor.dataFlavor).asInstanceOf[DesktopEntry];
      
      if (event.isDataFlavorSupported(Flavor.dataFlavor)) {
        event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
        event.dropComplete(true);
        for(action <- dropEntryAction) {
          action(entry)
        }
      }
      event.rejectDrop();
    } catch {
      case e: Throwable => {
        // event.rejectDrop();
      }
    }
  }
  
  var dropEntryAction : Option[DesktopEntry => Unit] = None
  
  def onDropEntry(f : DesktopEntry => Unit) = {
    dropEntryAction = Some(f)
  }
}