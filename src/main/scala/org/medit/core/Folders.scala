package org.medit.core

import scala.collection.mutable
import sys.process.stringSeqToProcess
import sys.process.stringToProcess

object Folders {


  val gConfPath = "/org/gnome/shell/app-folder-categories"
  val gConfPathNew = "/org/gnome/desktop/app-folders"
  val foldersStr = s"dconf read $gConfPath".!!
  val folders = mutable.HashMap[String, Folder]( stringToList(foldersStr).filterNot(_.isEmpty).map(e => (e, new Folder(e))) :_* )



  def updateFolders() = {
    val value = folders.keySet.mkString("['", "', '", "']")
    println(value)
    Seq("dconf", "write", s"$gConfPath",  value).!!
    Seq("dconf", "write", s"$gConfPathNew/folder-children",  value).!!
  }

  def addFolder(name: String): Folder = {
    println(name)
    Seq("dconf", "write", s"$gConfPathNew/folders/$name/categories", s"['$name']").!!
    Seq("dconf", "write", s"$gConfPathNew/folders/$name/translate", "false").!!
    Seq("dconf", "write", s"$gConfPathNew/folders/$name/name", "'" + name + "'").!!
    val newFolder = folders.getOrElseUpdate(name, new Folder(name))
    updateFolders()
    newFolder
  }


  def removeFolder(folder: Folder) = {
    folders.remove(folder.name)
    Seq("dconf", "reset", "-f",  s"$gConfPathNew/folders/${folder.name}/").!!
    updateFolders()
  }

  def stringToList(str: String): List[String] = {
    str.replaceAll("[\\[\\]']", "").split(",").toList.map(s => s.trim())
  }

  /**
   * Returns the list of Gnome menu folders
   */
  def getFolders(): List[Folder] = {
    folders.values.toList.sortBy(_.name.toLowerCase())
  }
}