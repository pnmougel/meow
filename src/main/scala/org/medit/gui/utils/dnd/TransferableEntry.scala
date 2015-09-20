package org.medit.gui.utils.dnd

import org.medit.core.entries.DesktopEntry
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.datatransfer.DataFlavor

/**
 * @author nico
 */
class TransferableEntry(entry: DesktopEntry) extends Transferable {
  override def getTransferDataFlavors(): Array[DataFlavor] = {
    Array[DataFlavor](Flavor.dataFlavor)
  }

  override def isDataFlavorSupported(flavor: DataFlavor): Boolean = {
    return flavor.equals(Flavor.dataFlavor);
  }

  override def getTransferData(flavor: DataFlavor): Object = {
    entry
  }
}