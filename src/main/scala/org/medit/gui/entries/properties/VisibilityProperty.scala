package org.medit.gui.entries.properties

import com.alee.laf.panel.WebPanel
import com.alee.laf.text.WebTextField
import org.medit.gui.components.BsToggle
import org.medit.gui.entries.EntryView
import org.medit.gui.utils.SwingEvents._

/**
  * Created by nico on 16/09/15.
  */
class VisibilityProperty extends WebPanel with EntryProperty {
   val field = new BsToggle("Visible")
  field.onClick( e => {
     for(e <- entry) {
       if(isEditable) {
         e.entry.setValue("NoDisplay", (!field.isSelected).toString)
         e.entry.save()
       }
     }
   })
   add(field, 0)

   override def setEntry(newEntry: EntryView) = {
     isEditable = newEntry.entry.isEditable
     entry = Some(newEntry)
     field.setEditable(isEditable)
     field.setSelected(!newEntry.entry.getBooleanValue("NoDisplay").getOrElse(false))
   }
 }
