package org.medit.gui.utils.styles.styleelements

import javax.swing.border.EmptyBorder
import java.awt.Color
import java.awt.Component
import org.medit.gui.utils.Colors
import org.medit.gui.utils.styles.helpers.ColorParser

class StyleColor extends StyleElement("color") {
	var color = Colors.red
	var defaultColor : Option[Color] = None
    def build(): StyleElement = {
        new StyleColor()
    }
    def applyStyleComponent(component: Component) {
        if(!defaultColor.isDefined) {
            defaultColor = Some(component.getForeground())
        }
        if(color == null && defaultColor.isDefined) {
            component.setForeground(defaultColor.get)
        } else {
            component.setForeground(color)
        }
    }
    
    def parse(value : String) : Boolean = {
        if(value.equals("none")) {
            color = null
        }
        var res = ColorParser.parseColor(value)
        for(c <- res) { color = c }
        res.isDefined
    }
}