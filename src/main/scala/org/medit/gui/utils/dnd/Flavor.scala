package org.medit.gui.utils.dnd

import org.medit.core.entries.DesktopEntry
import java.awt.datatransfer.DataFlavor

/**
 * @author nico
 */
object Flavor {
  val dataFlavor = new DataFlavor(classOf[DesktopEntry], classOf[DesktopEntry].getSimpleName())
}