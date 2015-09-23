package org.medit.gui.entries.filters

import org.medit.gui.components.BsToggle
import org.medit.gui.entries.EntryView
import org.medit.gui.utils.EventsSystem
import org.medit.gui.utils.SwingEvents._

/**
 * Created by nico on 23/09/15.
 */
abstract class EntryFilter(label: String, default: Boolean = false) {
  var unSelect = List[EntryFilter]()

  val toggle = new BsToggle(label)
  toggle.setSelected(default)
  toggle.onClick(_ => {
    if(toggle.isSelected) {
      for(toggleToUnselect <- unSelect) {
        toggleToUnselect.toggle.setSelected(false)
      }
    }
    EventsSystem.triggerEvent(EventsSystem.entriesListUpdated)
  })

  def filter(e: EntryView): Boolean

  def isVisible(e: EntryView) = {
    if(toggle.isSelected) {
      filter(e)
    } else true
  }
}

class ShowAllAppFilter extends EntryFilter("Show all applications") {
  def filter(e: EntryView) = {
    true
  }
}

class ShowHiddenAppFilter extends EntryFilter("Show hidden applications") {
  def filter(e: EntryView) = {
    !e.entry.isVisible || !e.entry.isDisplayedIn(List("GNOME"))
  }
}

class HideAppInFolderFilter extends EntryFilter("Hide app in a folder") {
  def filter(e: EntryView) = {
    !e.entry.isInCategory()
  }
}

class DesktopFilter(desktopName: String, default: Boolean = false) extends EntryFilter("Visible in " + desktopName, default) {
  def filter(e: EntryView) = {
    e.entry.isVisible && e.entry.isDisplayedIn(List(desktopName))
  }
}
