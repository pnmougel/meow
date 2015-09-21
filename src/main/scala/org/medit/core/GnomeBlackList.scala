package org.medit.core

import java.io.File

import org.medit.core.entries.DesktopEntry
import org.medit.core.root.RootAccess

import scala.collection._
import scala.io.Source

/**
 * Created by nico on 21/09/15.
 *
 * Allows to edit and get the gnome menu black list
 */
object GnomeBlackList {
  val file = new File("/etc/gnome/menus.blacklist")
  var lines = if(file.exists()) Source.fromFile(file).getLines().toList else List[String]()

  var blackListedEntries = mutable.HashSet[String](lines.filterNot(l => l.isEmpty && l.startsWith("#")): _*)

  def removeFromBlackList(entry: DesktopEntry) = {
    val matchPath = entry.file.getAbsolutePath.replaceAllLiterally("/usr/share/applications/", "")
    if(blackListedEntries.contains(matchPath) && RootAccess.isRootEnabled) {
      blackListedEntries.remove(matchPath)
      lines = lines.filter(line => line != matchPath)
      RootAccess.saveBlackList()
    }
  }

  override def toString = lines.mkString("\n")

  def isInBlackList(entry: DesktopEntry) = {
    val matchPath = entry.file.getAbsolutePath.replaceAllLiterally("/usr/share/applications/", "")
    blackListedEntries.contains(matchPath)
  }
}
