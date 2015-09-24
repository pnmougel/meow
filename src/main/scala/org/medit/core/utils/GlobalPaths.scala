package org.medit.core.utils

import java.io.File

/**
 * Created by nico on 22/09/15.
 */
object GlobalPaths {
  val home = System.getProperty("user.home")
  val pathsForIcons = Seq("/usr/share/icons", "/usr/local/share/icons", s"${home}/.local/share/icons", "/usr/share/pixmaps")
  val pathForNewAppIcons = s"${home}/.local/share/icons/meow/"

  val cacheFolder = s"$home/.meow/cache/"
  val cacheFile = new File(cacheFolder)
  if(cacheFile.exists() && !cacheFile.isDirectory) {
    cacheFile.delete()
  } else if(!cacheFile.exists()) {
    cacheFile.mkdirs()
  }
}
