package org.medit.gui.utils.styles.styleelements

import javax.swing.JComponent
import java.awt.Component
import java.awt.Color
import java.awt.Font
import java.io.File

class StyleFont extends StyleElement("font") {
    var font = new Font("Verdana", Font.BOLD, 12)
    
    def build(): StyleElement = {
        new StyleFont()
    }
    def applyStyleComponent(component: Component) {
        component.setFont(font)
    }
    
    def parse(value : String) : Boolean = {
      var params = value.split(" ")
      try {
          val fontSize = params(0).toInt
          val fontStyle = if(params.length == 1) {
              Font.PLAIN
          } else { 
              if(params(1).equalsIgnoreCase("bold")) { Font.BOLD }
              else if(params(1).equalsIgnoreCase("italic")) { Font.ITALIC }
              else { Font.PLAIN }
          }
          val fontFamily = if(params.length < 2) {
              "Verdana"
          } else { params(2).replace("-", " ") }
          font = new Font(fontFamily, fontStyle, fontSize)
          true
      } catch { case e : Throwable => false }
      true
    }
}