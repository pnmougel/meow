package org.medit.core.entries

import java.io.File
import org.medit.core.properties.{PropertyFile, Group, Property}
import org.medit.core.root.RootAccess

import scala.io.Source
import scala.collection.mutable.HashMap
import scala.Vector
import sys.process.stringSeqToProcess
import sys.process.stringToProcess
import java.io.PrintWriter
import org.medit.core.{GnomeBlackList, Folders, Folder}
import org.medit.gui.folders.FolderView

class DesktopEntry(val file: File) extends PropertyFile(file, "Desktop Entry") {
  // This is a hack to avoid being considered has a drag copy when it is a drag move
//  var sourceFolder: Option[FolderView] = None

  def getName = getValue("Name").getOrElse("Missing name entry")
  def getIcon = getValue("Icon")

  def getAppType() = {
    val cmd = getValue("Exec").getOrElse("")
    if(cmd.startsWith("xdg-open")) {
      if(cmd.contains("http")) "URL" else "Doc"
    } else if(cmd.isEmpty) "Missing exec"
    else "App"
  }

  /**
   * Entry Visibility
   */
  def isVisible = !getBooleanValue("NoDisplay").getOrElse(false)
  def setVisible(isVisible: Boolean) = setValue("NoDisplay", isVisible.toString)
  def getType = getValue("Type")

  def isValidExec: Boolean = {
    var isValid = false
    for (exec <- getValue("Exec"); entryType <- getType) {
      if (!exec.isEmpty()) {
        val progName = if (exec.startsWith("\"")) {
          if (exec.split("\"").size == 1) {
            ""
          } else exec.split("\"")(1)
        } else {
          exec.split(" ")(0)
        }
        if (progName.contains("/")) {
          isValid = new File(progName).isFile()
        } else {
          try {
            val res = Seq("which", progName).!!
            isValid = res.startsWith("/")
          } catch {
            case e: Throwable => {}
          }
        }
      } else {
        isValid = true
      }
    }
    isValid
  }

  def isDisplayedIn(environments: List[String]): Boolean = {
    val environmentsSet = environments.toSet
    val onlyShowIn = getListValue("OnlyShowIn")

    val displayInGnome = environmentsSet.contains("GNOME")

    // Check if entry is in required list
    var shouldBeDisplayed = !onlyShowIn.isDefined || onlyShowIn.get.isEmpty
    for (desktops <- getListValue("OnlyShowIn"); desktopName <- desktops) {
      if (environmentsSet.contains(desktopName)) {
        shouldBeDisplayed = true
      }
    }
    for (desktops <- getListValue("NotShowIn"); desktopName <- desktops) {
      if (environmentsSet.contains(desktopName)) {
        shouldBeDisplayed = false
      }
    }
    if(displayInGnome) {
      shouldBeDisplayed &= !GnomeBlackList.isInBlackList(this) && isValidExec
    }
    shouldBeDisplayed
  }


  /**
   * Categories
   */
  def getCategories: List[String] = getListValue("Categories").getOrElse(List[String]())
  def addCategory(category: String) = addToList("Categories", category)
  def removeCategory(category: String) = removeFromList("Categories", category)
  def isInCategory() = getCategories.exists( c => Folders.folders.contains(c))

  def isEditable = file.canWrite || RootAccess.isRootEnabled
  def isWriteable = file.canWrite

  def save() : Unit = {
    if(file.canWrite) {
      val printWriter = new PrintWriter(file)
      try { printWriter.write(toString())
      } catch { case e : Throwable => { printWriter.write(sourceFile) }
      } finally {
        printWriter.flush()
        printWriter.close()
      }
    } else if(RootAccess.isRootEnabled) {
      RootAccess.writeFile(file, toString())
    }
  }

}