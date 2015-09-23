package org.medit.gui.tasks

import org.medit.core.Folders
import org.medit.core.entries.DesktopEntries
import org.medit.gui.components.{BsToggle}
import org.medit.gui.entries.filters._
import org.medit.gui.entries.{EntryView, EntriesHeader, EntriesPanel}
import org.medit.gui.panels.VerticalPanel
import org.medit.gui.utils.SwingEvents._

/**
 * Created by nico on 15/09/15.
 */
object FiltersPanel extends VerticalPanel {
  val gnomeFilter = new DesktopFilter("GNOME", true)
  val unityFilter = new DesktopFilter("Unity")
  val kdeFilter = new DesktopFilter("KDE")
  val showAllAppFilter = new ShowAllAppFilter
  val showHiddenAppFilter = new ShowHiddenAppFilter
  val hideAppInFolderFilter = new HideAppInFolderFilter
  val filters = List[EntryFilter](gnomeFilter, unityFilter, kdeFilter, showAllAppFilter, showHiddenAppFilter, hideAppInFolderFilter)
  for(filter <- filters; if(filter != hideAppInFolderFilter)) {
    filter.unSelect = filters.filter(_ != filter)
  }

  def isAppVisible(entry: EntryView, useNameFilter : Boolean = true): Boolean = {
    var isVisible = true
    for(filter <- filters) {
      isVisible = isVisible & filter.isVisible(entry)
    }

    if(useNameFilter) {
      val filterText = EntriesHeader.appNameFilterInput.getText.toLowerCase()
      isVisible = isVisible && entry.entry.getName.toLowerCase().contains(filterText)
    }
    isVisible
  }

  for(filter <- filters) {
    addComponent(filter.toggle)
  }
  addFiller()
}
