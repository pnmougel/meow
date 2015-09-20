package org.medit.core.entries

import java.io.File
import scala.io.Source
import scala.collection.mutable.HashMap
import scala.Vector
import sys.process.stringSeqToProcess
import sys.process.stringToProcess
import java.io.PrintWriter
import org.medit.core.{RootAccess, Folders, Folder}
import org.medit.gui.folders.FolderView

class DesktopEntry(val file: File) {
  var categories = Vector[Category]()
  var categoryMap = HashMap[String, Category]()
  
  // This is a hack to avoid being considered has a drag copy when it is a drag move
  var sourceFolder: Option[FolderView] = None
  
  // Parse the file
  var curCategory = new Category()
  categories :+= curCategory
  categoryMap("") = curCategory
  val sourceFile = Source.fromFile(file).getLines().map(line => {
    if (line.startsWith("[") && line.endsWith("]")) {
      val categoryName = line.replaceAll("\\[", "").replaceAll("\\]", "")
      curCategory = new Category(categoryName)
      categoryMap(categoryName) = curCategory
      categories :+= curCategory
    } else {
      curCategory.addProperty(new Property(line))
    }
  }).mkString("\n")

  def getName = {
    if(!getValue("Name").isDefined) {
      println(file.getCanonicalPath)
    }
    getValue("Name").getOrElse("Missing name entry")
  }
  def getIcon = getValue("Icon")

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
          if (exec.split("\"").size == 1) { "" } else exec.split("\"")(1)
        } else {
          exec.split(" ")(0)
        }
        if (progName.contains("/")) {
          isValid = new File(progName).isFile()
        } else {
          try {
            val res = Seq("which", progName).!!
            isValid = res.startsWith("/")
          } catch { case e: Throwable => {} }
        }
      }
    }
    isValid
  }

  def isDisplayedIn(environments: List[String]): Boolean = {
    val environmentsSet = environments.toSet
    val onlyShowIn = getListValue("OnlyShowIn")

    // Check if entry is in required list
    var shouldBeDisplayed = if (!onlyShowIn.isDefined || onlyShowIn.get.isEmpty) {
      true
    } else {
      false
    }
    for (
      desktops <- getListValue("OnlyShowIn");
      desktopName <- desktops
    ) {
      if (environmentsSet.contains(desktopName)) {
        shouldBeDisplayed = true
      }
    }
    for (
      desktops <- getListValue("NotShownIn");
      desktopName <- desktops
    ) {
      if (environmentsSet.contains(desktopName)) {
        shouldBeDisplayed = false
      }
    }
    shouldBeDisplayed
  }

  /**
   * Categories
   */
  def getCategories: List[String] = getListValue("Categories").getOrElse(List[String]())
  def addCategory(category: String) = addToList("Categories", category)
  def removeCategory(category: String) = removeFromList("Categories", category)

  def hasProperty(name: String, category: String = "Desktop Entry"): Boolean = {
    categoryMap.get(category).map(_.propertyMap.contains(name)).getOrElse(false)
  }

  /**
   * Setters
   */
  def setValue(name: String, value: String, category: String = "Desktop Entry", createIfNotExists: Boolean = true): Unit = {
    if (hasProperty(name, category) || createIfNotExists) {
      // Create the category if it does not exists
      val cat = categoryMap.getOrElseUpdate(category, {
        val newCategory = new Category(category)
        categories :+= newCategory
        newCategory
      })
      if (!cat.propertyMap.contains(name)) {
        cat.addProperty(new Property(name + "=" + value))
      } else {
        cat.propertyMap(name).setValue(value)
      }
    }
  }

  def isInCategory() : Boolean = {
    val allCategories = Folders.getAllCategories()
    getCategories.exists( c => allCategories.contains(c))
  }

  def addToList(name: String, value: String, category: String = "Desktop Entry", createIfNotExists: Boolean = true): Unit = {
    var curList = getListValue(name, category).getOrElse(List[String]())
    if (!curList.contains(value)) {
      curList = value :: curList
      setValue(name, curList.mkString("", ";", ";"), category, createIfNotExists)
    }
  }

  def removeFromList(name: String, value: String, category: String = "Desktop Entry", createIfNotExists: Boolean = true): Unit = {
    val curList = getListValue(name, category).getOrElse(List[String]()).filter(!_.equals(value))
    setValue(name, curList.mkString("", ";", ";"), category, createIfNotExists)
  }

  def isEditable = file.canWrite || RootAccess.isRootEnabled

  /**
   * Getters
   */
  def getValue(name: String, category: String = "Desktop Entry"): Option[String] = {
    if (!hasProperty(name, category)) {
      None
    } else {
      categoryMap(category).propertyMap(name).getValue
    }
  }

  def getBooleanValue(name: String, category: String = "Desktop Entry"): Option[Boolean] = {
    getValue(name, category).flatMap(v => {
      if (v.equalsIgnoreCase("true")) { Option(true) }
      else if (v.equalsIgnoreCase("false")) { Option(false) }
      else None
    })
  }

  def getListValue(name: String, category: String = "Desktop Entry"): Option[List[String]] = {
    getValue(name, category).map(_.split(";").toList)
  }

  def save() : Unit = {
    println("try save")
    if(file.canWrite) {
      val printWriter = new PrintWriter(file)
      try {
        printWriter.write(toString())
        printWriter.flush()
        printWriter.close()
      } catch {
        case e : Throwable => {
          printWriter.write(sourceFile)
          printWriter.flush()
          printWriter.close()
        }
      }
    } else if(RootAccess.isRootEnabled) {
      println("Root write")
      RootAccess.writeFile(file, toString())
    }
  }
  
  override def toString(): String = {
     categories.filter(!_.toString.isEmpty()).mkString("\n")
  }
}