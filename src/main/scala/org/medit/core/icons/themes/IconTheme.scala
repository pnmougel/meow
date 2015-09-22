package org.medit.core.icons.themes

import java.awt.Image

import org.medit.core.icons.{IconLibrary, ImageLoader}

import scala.collection.mutable

/**
 * Created by nico on 21/09/15.
 */
trait IconTheme {
  val icons = mutable.HashMap[String, List[(Int, Option[String], Boolean, String)]]()
  val preferredIcons = mutable.HashMap[String, String]()
  var inheritedThemes = Array[String]()
  var name: String = ""

  def buildPreferredIcons() = {
    for((iconName, iconInfos) <- icons) {
      //    val isOnlySvg = !iconInfos.exists(e => !e._3)
      //    val isOnlySmall = !iconInfos.exists(e => e._1 > 30)
      //    iconInfos.filter(e => (!e._3 || isOnlySvg || isOnlySmall) || (e._1 < 30 || isOnlySmall))
      preferredIcons(iconName.toLowerCase) = (if(iconInfos.exists(e => !e._3 && e._1 > 30)) {
        iconInfos.filter(e => !e._3)
      } else if(iconInfos.exists(e => e._3)) {
        iconInfos.filter(e => e._3)
      } else {
        iconInfos
      }).maxBy(e => e._1)._4
    }
  }

  var inherited = List[IconTheme]()
  def buildInheritedThemes = {
    for(theme <- inheritedThemes) {
      inherited = IconLibrary.iconThemes.getOrElse(theme, List[IconTheme]()) ++ inherited
    }
  }
  def getIcon(iconName: String) : Option[Image] = {
    var iconDefined = false
    var imageOpt : Option[Image] = None

    if(preferredIcons.contains(iconName)) {
      imageOpt = ImageLoader.get(preferredIcons(iconName))
      iconDefined = imageOpt.isDefined
    }
    if(!iconDefined) {
      inherited.find( theme => {
        theme.getIcon(iconName).isDefined
      }).map(matchingInheritedTheme => matchingInheritedTheme.getIcon(iconName).get)
    } else {
      imageOpt
    }
  }
}
