package org.medit.core.utils

/**
 * Created by nico on 22/09/15.
 */
object GlobalPaths {
  val home = System.getProperty("user.home")
  val pathsForIcons = Seq("/usr/share/icons", "/usr/local/share/icons", s"${home}/.local/share/icons", "/usr/share/pixmaps")
  val pathForNewAppIcons = s"${home}/.local/share/icons/meow/"
}
