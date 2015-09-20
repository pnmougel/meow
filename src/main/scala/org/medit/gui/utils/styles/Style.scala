package org.medit.gui.utils.styles

import java.awt.Component
import java.awt.Color
import org.medit.gui.utils.styles.styleelements.StyleElement


class Style() {
    var styleElements = Vector[StyleElement]()
    
    def applyStyle(component: Component) {
        for(styleElement <- styleElements) {
            styleElement.doApply(component)
        }
    }
    
    def addStyleElement(styleElement : StyleElement) {
        styleElements :+= styleElement
    }
}