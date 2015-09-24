package org.medit.gui.entries.properties

import java.awt.Color
import javax.swing.ImageIcon
import javax.swing.border.LineBorder

import com.alee.laf.label.WebLabel
import org.medit.core.icons.IconFinder
import org.medit.gui.entries.EntryView
import org.medit.gui.panels.IconChooser
import org.medit.gui.utils.SwingEvents._

/**
 * Created by nico on 16/09/15.
 */
class IconProperty extends WebLabel with EntryProperty {
  setMargin(5)
  override def setEntry(newEntry: EntryView) = {
    entry = Some(newEntry)
    isEditable = newEntry.entry.isEditable
    setIcon(IconFinder.getIcon(newEntry.entry.getIcon.getOrElse("NOICON"), 50))
  }

  this.onClick(event => {
    for(e <- entry; if isEditable) {
      val iconChooser = new IconChooser(e.entry, newIcon => {
        e.entry.setValue("Icon", newIcon)
        e.entry.save()
        val newIconImage = IconFinder.getIcon(e.entry.getIcon.getOrElse("NOICON"), 50)
        setIcon(newIconImage)
        e.imageLabel.setIcon(IconFinder.getIcon(e.entry.getIcon.getOrElse("NOICON"), 70))
      })
      iconChooser.setVisible(this)
    }
  })
}
