package org.medit.gui.utils.styles

import java.io.File
import scala.io.Source
import scala.collection.mutable.HashMap
import javax.swing.border.EmptyBorder
import java.awt.Component
import javax.swing.JComponent
import org.medit.gui.utils.styles.styleelements._
import java.io.InputStream

case class ParseError(line: String, i: Int, messages: String*)

class StyleParser() {
  val styleElements = HashMap[String, () => StyleElement]()
  def registerStyleElements(styleElement: StyleElement) {
    styleElements(styleElement.name) = styleElement.build
  }
  registerStyleElements(new StyleBackgroundColor())
  registerStyleElements(new StylePadding())
  registerStyleElements(new StyleColor())
  registerStyleElements(new StyleFont())

  def outputErrors(errors: Seq[ParseError]) {
    System.err.println("Error parsing style file")
    errors.foreach(error => {
      System.err.println(s"  Line ${error.i}: ${error.line}")
      for (message <- error.messages) {
        System.err.println(s"  ${message}")
      }
    })
  }

  def parseKeyValue(elem: String, curStyle: Style, line: String, i: Int) = {
    val elems = elem.split(":")
    if (elems.length < 2) {
      errors :+= ParseError(line, i, "Syntax error, missing :")
    } else {
      val key = elems(0)
      val value = elem.splitAt(elem.indexOf(":") + 1)._2.trim()
      if (!styleElements.contains(key)) {
        errors :+= ParseError(line, i, s"Syntax error, invalid property ${key}")
      } else {
        val styleElement = styleElements(key)()
        if (!styleElement.doParse(value)) {
          errors :+= ParseError(line, i, s"Syntax error, unable to parse ${value} for property ${key}")
        } else {
          curStyle.addStyleElement(styleElement)
        }
      }
    }
  }

  var errors = Vector[ParseError]()
  def parse(file: InputStream): HashMap[String, Style] = {
    val styles = HashMap[String, Style]()
    var curStyle = new Style()
    errors = Vector[ParseError]()
    var inClass = false
    for ((line, i) <- Source.fromInputStream(file).getLines.zipWithIndex) {
      val l = line.trim()
      if (l.startsWith(".") && l.endsWith("{") && !inClass) {
        curStyle = new Style()
        val className = l.substring(1).split(" ")(0)
        styles(className) = curStyle
        inClass = true
      } else if (!l.isEmpty() && !l.startsWith("//")) {
        if (!inClass) {
          errors :+= ParseError(line, i, "No matching class")
        } else {
          if (l.endsWith("}")) {
            inClass = false
          } else {
            for (elem <- l.split(";")) {
              parseKeyValue(elem.trim(), curStyle, line, i)
            }
          }

        }
      }
      if (l.endsWith("}")) {
        inClass = false
      }
    }

    if (!errors.isEmpty) {
      outputErrors(errors)
    }
    styles
  }
}


