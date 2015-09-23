package org.medit.gui.utils.dnd

/**
 * Created by nico on 22/09/15.
 */

import com.alee.managers.glasspane.GlassPaneManager
import com.alee.managers.glasspane.WebGlassPane
import com.alee.utils.SwingUtils
import java.awt._
import java.awt.dnd._
import java.awt.image.BufferedImage

import org.medit.core.entries.DesktopEntry
import org.medit.gui.utils.Colors


object DragManager {

  val noCursor = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "null")
  val entryDragViewHandler = new EntryDragViewHandler
  protected var glassPane: WebGlassPane = _
  protected var data: DesktopEntry = _
  protected var view: BufferedImage = _
  protected var viewDisabled: BufferedImage = _
  protected var dropLocation: Component = _

  def initialize() {
    synchronized {
      val dsa = new DragSourceAdapter() {

        override def dragEnter(dsde: DragSourceDragEvent) {
          actualDragEnter(dsde)
        }

        protected def actualDragEnter(dsde: DragSourceDragEvent) {
          val dsc = dsde.getDragSourceContext
          dsc.setCursor(noCursor)
          dropLocation = dsc.getComponent
          val transferable = dsc.getTransferable
          data = if(transferable.isDataFlavorSupported(TransferDataFlavor.entryInFolderFlavor)) {
            transferable.getTransferData(TransferDataFlavor.entryInFolderFlavor).asInstanceOf[EntryWithinFolder].entry
          } else {
            transferable.getTransferData(TransferDataFlavor.entryFlavor).asInstanceOf[DesktopEntry]
          }
//          data = transferable.getTransferData(entryDragViewHandler.getObjectFlavor).asInstanceOf[DesktopEntry]
          val viewEnabled = entryDragViewHandler.getView(data, dsde)
          view = viewEnabled
          viewDisabled = new BufferedImage(viewEnabled.getWidth, viewEnabled.getHeight, BufferedImage.TYPE_INT_ARGB)
          val g = viewDisabled.getGraphics.asInstanceOf[Graphics2D]
          val ac = AlphaComposite.getInstance(AlphaComposite.DST_IN)
          g.drawImage(viewEnabled, 0, 0, null)
          g.setComposite(ac)
          g.setPaint(Colors.transparentWhite)
          g.fillRect(0, 0, viewEnabled.getWidth, viewEnabled.getHeight)
          g.dispose()

          glassPane = GlassPaneManager.getGlassPane(dsc.getComponent)
          glassPane.setPaintedImage(view, getLocation(glassPane, dsde))
        }

        override def dragMouseMoved(dsde: DragSourceDragEvent) {
          val dsc = dsde.getDragSourceContext
          if (dsc.getComponent != dropLocation) {
            actualDragEnter(dsde)
          }
          if (view != null) {
            val gp = GlassPaneManager.getGlassPane(dsde.getDragSourceContext.getComponent)
            if (gp != glassPane) {
              glassPane.clearPaintedImage()
              glassPane = gp
            }
            if(dsde.getDropAction == 0) {
              glassPane.setPaintedImage(viewDisabled, getLocation(glassPane, dsde))
            } else {
              glassPane.setPaintedImage(view, getLocation(glassPane, dsde))
            }

          }
        }

        def getLocation(gp: WebGlassPane, dsde: DragSourceDragEvent): Point = {
          val mp = SwingUtils.getMousePoint(gp)
          val vp = entryDragViewHandler.getViewRelativeLocation(data, dsde)
          new Point(mp.x + vp.x, mp.y + vp.y)
        }

        override def dragDropEnd(dsde: DragSourceDropEvent) {
          dropLocation = null
          if (view != null) {
            entryDragViewHandler.dragEnded(data, dsde)
            glassPane.clearPaintedImage()
            glassPane = null
            data = null
            view = null
          }
        }
      }
      DragSource.getDefaultDragSource.addDragSourceListener(dsa)
      DragSource.getDefaultDragSource.addDragSourceMotionListener(dsa)
    }
  }
}
