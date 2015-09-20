package org.medit.gui.utils.styles.styleelements

import javax.swing.JComponent
import javax.swing.border.EmptyBorder
import java.awt.Component
import javax.swing.border.Border

class StylePadding extends StyleElement("padding") {
    var top = 0
    var bottom = 0
    var left = 0
    var right = 0

    def build(): StyleElement = {
        new StylePadding()
    }
    def applyStyleComponent(component: Component) {
        val border = new EmptyBorder(top, left, bottom, right)
        tryCallWithTypes(component, "setBorder", (border, classOf[Border]))
    }
    def parse(value: String): Boolean = {
        val elems = value.split(" ").toList.map(_.trim())
        try {
            val values = for(e <- elems) yield { e.toInt }
	        if (values.length == 1) {
	            top = values(0)
	            bottom = values(0)
	            left = values(0)
	            right = values(0)
	        } else if (values.length == 2) {
	            top = values(0)
	            bottom = values(0)
	            left = values(1)
	            right = values(1)
	        } else if(values.length == 4) {
	            top = values(0)
	            right = values(1)
	            bottom = values(2)
	            left = values(3)
	        } else {
	            false
	        }
	        true
        } catch { case e : NumberFormatException => false }
    }
}