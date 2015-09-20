package org.medit.gui.tasks

import org.medit.core.Folders
import org.medit.core.entries.DesktopEntries
import org.medit.gui.components.{BsToggle}
import org.medit.gui.entries.{EntryView, EntriesHeader, EntriesPanel}
import org.medit.gui.panels.VerticalPanel
import org.medit.gui.utils.SwingEvents._

/**
 * Created by nico on 15/09/15.
 */
object FiltersPanel extends VerticalPanel {
  val showHiddenEntries = new BsToggle("Show hidden applications")
  showHiddenEntries.onClick(_ => updateEntries)

  val showOnlyAppInFolder = new BsToggle("Hide app in a folder")
  showOnlyAppInFolder.onClick(_ => updateEntries)

  def updateEntries = {
    EntriesPanel.updateDesktopEntries()
  }

  var desktopNames = List("KDE", "GNOME", "Unity")
  val desktopToggles = for(desktopName <- desktopNames) yield {
    val desktopToggle = new BsToggle("Visible in " + desktopName)
    desktopToggle.onClick(_ => updateEntries)
    addComponent(desktopToggle)
    (desktopToggle, desktopName)
  }

  def isAppVisible(entry: EntryView): Boolean = {
    val e = entry.entry

    val filterText = EntriesHeader.appNameFilterInput.getText.toLowerCase()
    val containsName = e.getName.toLowerCase().contains(filterText)

    val entryVisible = e.isVisible || showHiddenEntries.isSelected
    val entryInCategory = !e.isInCategory() || !showOnlyAppInFolder.isSelected

    val filterdDesktops = (for(desktopToggle <- desktopToggles; if(desktopToggle._1.isSelected)) yield { desktopToggle._2 }).toList
    val isInEnvironment = if(!filterdDesktops.isEmpty) {
      e.isDisplayedIn(filterdDesktops)
    } else { true }

    entryVisible && containsName && entryInCategory && isInEnvironment
  }

  addComponent(showHiddenEntries)
  addComponent(showOnlyAppInFolder)
  addFiller()
}
