package org.medit.gui.entries.properties

import org.medit.core.entries.DesktopEntry
import org.medit.gui.entries.EntryView

/**
 * Created by nico on 16/09/15.
 */
trait EntryProperty {
  var entry : Option[EntryView] = None

  def setEntry(newEntry: EntryView): Unit = {
    isEditable = newEntry.entry.file.canWrite
    entry = Some(newEntry)
  }

  var isEditable = false
}
