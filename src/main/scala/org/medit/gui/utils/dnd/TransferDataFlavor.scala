package org.medit.gui.utils.dnd

import java.io.File

import org.medit.core.entries.DesktopEntry
import java.awt.datatransfer.DataFlavor

import org.medit.gui.folders.FolderView

/**
 * @author nico
 */
object TransferDataFlavor {
  val entryFlavor = new DataFlavor(classOf[DesktopEntry], classOf[DesktopEntry].getSimpleName())

  val entryInFolderFlavor = new DataFlavor(classOf[EntryWithinFolder], classOf[EntryWithinFolder].getSimpleName())

  val stringFlavor = new DataFlavor(classOf[String], classOf[String].getSimpleName())

  val fileFlavor = new DataFlavor(classOf[File], classOf[File].getSimpleName())
}

class EntryWithinFolder(val entry: DesktopEntry, val folder: FolderView)