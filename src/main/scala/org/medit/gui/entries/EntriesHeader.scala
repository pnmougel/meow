package org.medit.gui.entries

import java.awt._

import com.alee.extended.image.WebImage
import com.alee.laf.label.WebLabel
import com.alee.laf.panel.WebPanel
import com.alee.laf.text.WebTextField
import org.medit.core.RootAccess
import org.medit.gui.utils.SwingEvents._
import org.medit.gui.utils.{Icon, WrapLayout}

/**
 * @author nico
 */
object EntriesHeader extends WebPanel(new BorderLayout()) {
  val appNameFilterInput = new WebTextField(25)
  appNameFilterInput.onKeyRelease(_ => {
    EntriesPanel.updateDesktopEntries()
  })
  appNameFilterInput.setDrawFocus(false)
  appNameFilterInput.setName("app-name-filter")
  appNameFilterInput.setTrailingComponent(new WebImage(Icon.get("magnifier")))
  appNameFilterInput.setMargin(3, 10, 3, 5)
  appNameFilterInput.setInputPrompt("Search an application")
  appNameFilterInput.setHideInputPromptOnFocus(false)

  val lockIcon = Icon.get("lock")
  val unlockIcon = Icon.get("lock_open")
  val rootAccess = new WebLabel(lockIcon)
  rootAccess.onClick(e => {
    if(RootAccess.isRootEnabled) {
      RootAccess.stopServer()
      rootAccess.setIcon(lockIcon)
    } else {
      RootAccess.getRootAccess
      rootAccess.setIcon(unlockIcon)
    }
  })
  rootAccess.setMargin(0, 0, 0, 10)


  setBackground(Color.white)
  setMargin(20, 25, 15, 10)
  add(appNameFilterInput, BorderLayout.WEST)
  add(rootAccess, BorderLayout.EAST)
}
