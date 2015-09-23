package org.medit.gui.utils.dnd

import javax.swing.TransferHandler
import com.alee.utils.DragUtils
import javax.swing._
import java.awt.datatransfer.Transferable
import java.util.ArrayList
import java.util.List
import org.medit.core.Folder
import org.medit.core.entries.{DesktopEntry, DesktopEntries}
import org.medit.gui.folders.FolderView
import org.medit.gui.utils.EventsSystem

import scala.collection.JavaConversions._

class EntryDropHandler(folder: Option[FolderView] = None) extends TransferHandler {

  override def canImport(info: TransferHandler.TransferSupport) = isDropEnabled

  override def importData(info: TransferHandler.TransferSupport): Boolean = {
    info.isDrop && importData(info.getTransferable)
  }

  def importData(t: Transferable): Boolean = {
    if(t.isDataFlavorSupported(TransferDataFlavor.stringFlavor)) {
      var data = t.getTransferData(TransferDataFlavor.stringFlavor).toString
      if(!data.startsWith("file://") && !data.startsWith("http") && data.contains("/")) {
        // It looks like an url
        // TODO: improve the url detection
        data = "http://" + data
      }
      DesktopEntries.createDesktopEntry(data)
    } else if(t.isDataFlavorSupported(TransferDataFlavor.entryFlavor)) {
      // Adding an entry from the main list of apps
      for(f <- folder) {
        val entry = t.getTransferData(TransferDataFlavor.entryFlavor).asInstanceOf[DesktopEntry]
        entry.addCategory(f.folder.name)
        entry.save()
        f.updateCategories()
      }
    } else if(t.isDataFlavorSupported(TransferDataFlavor.entryInFolderFlavor)) {
      val data = t.getTransferData(TransferDataFlavor.entryInFolderFlavor).asInstanceOf[EntryWithinFolder]
      val entry = data.entry
      val fromFolder = data.folder
      entry.removeCategory(fromFolder.folder.name)
      fromFolder.updateCategories()
      for(f <- folder) {
        entry.addCategory(f.folder.name)
        f.updateCategories()
      }
      entry.save()
    }
    EventsSystem.triggerEvent(EventsSystem.entriesListUpdated)
    isDropEnabled
  }

  protected def isDropEnabled = true
}