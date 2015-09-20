package org.medit.gui.utils.dnd

import java.awt.dnd.DragSource
import java.awt.dnd.DragGestureListener
import java.awt.dnd.DragGestureEvent
import java.awt.Cursor
import java.awt.dnd.DnDConstants

import org.medit.gui.entries.EntryView

/**
 * @author nico
 */
class DragEntryGesture extends DragGestureListener {
    
    override def dragGestureRecognized(event: DragGestureEvent) : Unit =  {
      var cursor : Cursor = DragSource.DefaultMoveDrop
      val entryView = event.getComponent.asInstanceOf[EntryView]
      
      if (event.getDragAction == DnDConstants.ACTION_MOVE) {
        cursor = DragSource.DefaultCopyDrop;
        for(folder <- entryView.folder) {
            entryView.entry.sourceFolder = Some(folder)
        }
        event.startDrag(cursor, new TransferableEntry(entryView.entry))
      } else if (event.getDragAction == DnDConstants.ACTION_COPY) {
        cursor = DragSource.DefaultMoveDrop
        try {
          event.startDrag(cursor, new TransferableEntry(entryView.entry))
        } catch { case e : Throwable => {} }

      }
    }
}