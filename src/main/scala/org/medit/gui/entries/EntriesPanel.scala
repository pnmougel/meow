package org.medit.gui.entries

import java.awt._
import java.awt.event.ActionEvent
import javax.swing.JPanel

import com.alee.laf.label.WebLabel
import com.alee.laf.scroll.{WebScrollBar, WebScrollPane}
import org.medit.core.entries.{DesktopEntry, DesktopEntries}
import org.medit.gui.components.{HorizontalScrollPane}
import org.medit.gui.panels.VerticalPanel
import org.medit.gui.tasks.FiltersPanel
import org.medit.gui.utils.SwingEvents._
import org.medit.gui.utils.{EventsSystem, WrapLayout}
import org.medit.gui.utils.dnd.{TransferDataFlavor, EntryDragHandler, EntryDropHandler}
import scala.collection.JavaConversions._
import scala.sys.process.stringToProcess

object EntriesPanel extends JPanel(new BorderLayout) {
  val noApplicationLabel = new WebLabel("No applications to display")
  noApplicationLabel.setName("no-applications-to-display")
  noApplicationLabel.setVisible(false)

  setBackground(null)

  val entryList = new JPanel(new WrapLayout(FlowLayout.LEADING))
  val entryList2 = new JPanel(new WrapLayout(FlowLayout.LEADING))
  entryList2.setBackground(Color.white)
  entryList2.setVisible(true)

  var isSplitted = false
  var splittedAt : Option[EntryView] = None

  def splitInTwo(entryView : EntryView) = {
    isSplitted = true
    splittedAt = Some(entryView)
    val point : Point = entryView.getLocation()
    EntryDetailsPanel.setVisible(true)
    var componentsToRemove = scala.collection.immutable.List[Component]()
    for(comp <- entryList.getComponents) {
      val p2 : Point = comp.getLocation
      if(p2.y > point.y) {
        componentsToRemove = comp :: componentsToRemove
        entryList2.add(comp)
      }
    }
    for(comp <- componentsToRemove) {
      entryList.remove(comp)
    }

    container.doLayout()
    container.repaint()
  }

  def merge() = {
    isSplitted = false
    splittedAt = None
    EntryDetailsPanel.setVisible(false)
    for(comp <- entryList2.getComponents) {
      entryList.add(comp)
    }
    entryList2.removeAll()
    container.doLayout()
    container.repaint()
  }

  this.onResize( _ => merge())

  val allEntries = DesktopEntries.getDesktopEntries()
  val allEntryViews = allEntries.map(entry => {
    val entryView = new EntryView(entry)
    val transferHandler = new EntryDragHandler[DesktopEntry](entryView.imageLabel, entry, TransferDataFlavor.entryFlavor)
    entryView.imageLabel.setTransferHandler(transferHandler)
    entryView.imageLabel.onClick(e => {
      if((e.getModifiers() & ActionEvent.CTRL_MASK) != ActionEvent.CTRL_MASK) {
        if(e.getClickCount == 1) {
          println(entryView.entry)
          if(isSplitted) {
            merge()
          } else {
            splitInTwo(entryView)
            EntryDetailsPanel.setEntry(entryView)
          }
        } else if(e.getClickCount == 2) {
          for(cmd <- entry.executable) {
            cmd.run()
          }
        }
      }
    })
    entryList.add(entryView)
    entryView
  })
  entryList.add(noApplicationLabel)

  val container = new VerticalPanel
  container.addComponent(entryList)
  container.addComponent(EntryDetailsPanel)
  container.addComponent(entryList2)
  container.addFiller()

  val scrollPane = new WebScrollPane(container, false, true)
  val scrollBar = scrollPane.getVerticalScrollBar.asInstanceOf[WebScrollBar]
  scrollPane.setPaintButtons(false)
  scrollBar.setUnitIncrement(64)
  scrollBar.setMargin(0)
  scrollBar.setPaintTrack(false)
  scrollBar.setPreferredWidth(6)
  scrollBar.setBackground(null)
  scrollBar.setForeground(null)
  scrollBar.setOpaque(true)
  scrollBar.setMargin(0, 0, 0, 0)

  updateDesktopEntries()
  add(EntriesHeader, BorderLayout.NORTH)
  add(scrollPane)

  EventsSystem.on(EventsSystem.entriesListUpdated, _ => {
    updateDesktopEntries()
  })
  def updateDesktopEntries() = {
    merge()
    var atLeastOneApplicationToDisplay = false
    allEntryViews.foreach(entry => {
      val isAppVisible = FiltersPanel.isAppVisible(entry)
      entry.setVisible(isAppVisible)
      atLeastOneApplicationToDisplay |= isAppVisible
    })
    noApplicationLabel.setVisible(!atLeastOneApplicationToDisplay)
  }

  setTransferHandler(new EntryDropHandler())
}