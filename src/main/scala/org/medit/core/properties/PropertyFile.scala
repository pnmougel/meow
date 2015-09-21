package org.medit.core.properties

import java.io.File

import scala.collection._
import scala.io.Source

/**
 * Created by nico on 20/09/15.
 */
class PropertyFile(file: File, defaultGroup: String = "") {
  var groups = Vector[Group]()
  var groupMap = mutable.HashMap[String, Group]()

  // Parse the file
  var curGroup = new Group()
  groups :+= curGroup
  groupMap("") = curGroup
  val sourceFile = Source.fromFile(file).getLines().map(line => {
    if (line.startsWith("[") && line.endsWith("]")) {
      val groupName = line.replaceAll("\\[", "").replaceAll("\\]", "")
      curGroup = new Group(groupName)
      groupMap(groupName) = curGroup
      groups :+= curGroup
    } else {
      curGroup.addProperty(new Property(line))
    }
  }).mkString("\n")

  def hasProperty(name: String, group: String = defaultGroup): Boolean = {
    groupMap.get(group).map(_.propertyMap.contains(name)).getOrElse(false)
  }

  def setValue(name: String, value: String, group: String = defaultGroup, createIfNotExists: Boolean = true): Unit = {
    if (hasProperty(name, group) || createIfNotExists) {
      // Create the category if it does not exists
      val cat = groupMap.getOrElseUpdate(group, {
        val newCategory = new Group(group)
        groups :+= newCategory
        newCategory
      })
      if (!cat.propertyMap.contains(name)) {
        cat.addProperty(new Property(name + "=" + value))
      } else {
        cat.propertyMap(name).setValue(value)
      }
    }
  }


  def addToList(name: String, value: String, group: String = defaultGroup, createIfNotExists: Boolean = true): Unit = {
    var curList = getListValue(name, group).getOrElse(List[String]())
    if (!curList.contains(value)) {
      curList = value :: curList
      setValue(name, curList.mkString("", ";", ";"), group, createIfNotExists)
    }
  }

  def removeFromList(name: String, value: String, group: String = defaultGroup, createIfNotExists: Boolean = true): Unit = {
    val curList = getListValue(name, group).getOrElse(List[String]()).filter(!_.equals(value))
    setValue(name, curList.mkString("", ";", ";"), group, createIfNotExists)
  }

  def getValue(name: String, group: String = defaultGroup): Option[String] = {
    if (!hasProperty(name, group)) {
      None
    } else {
      groupMap(group).propertyMap(name).getValue
    }
  }

  def getBooleanValue(name: String, group: String = defaultGroup): Option[Boolean] = {
    getValue(name, group).flatMap(v => {
      if (v.equalsIgnoreCase("true")) { Option(true) }
      else if (v.equalsIgnoreCase("false")) { Option(false) }
      else None
    })
  }

  def getListValue(name: String, group: String = defaultGroup): Option[List[String]] = {
    getValue(name, group).map(_.split(";").toList)
  }


  override def toString(): String = {
    groups.filter(!_.toString.isEmpty()).mkString("\n")
  }
}
