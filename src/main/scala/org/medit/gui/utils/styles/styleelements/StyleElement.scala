package org.medit.gui.utils.styles.styleelements

import javax.swing.JComponent
import java.awt.Component
import java.awt.Color

abstract class StyleElement(val name: String) {
    def doApply(component: Component) = {
        if(isValid) {
            applyStyleComponent(component)
        }
    }
    def build() : StyleElement
    def applyStyleComponent(component: Component)
    var isValid = false
    def parse(value: String) : Boolean
    def doParse(value: String) : Boolean = {
        isValid = parse(value)
        isValid
    }
    
    def tryCallWithTypes(component: Component, functionName: String, params: (Object, Class[_])*) {
         val paramsClasses = new Array[Class[_]](params.length)
         val paramsValues = new Array[Object](params.length)
         for((v, i) <- params.zipWithIndex) {
             paramsValues(i) = v._1 
             paramsClasses(i) = v._2
         }
        try {
            val m = component.getClass().getMethod(functionName, paramsClasses : _*)
             m.invoke(component, paramsValues:_*)
        } catch{ case e : Throwable => {} }
    }
    
    def tryCall(component: Component, functionName: String, params: Object*) {
         val paramsClasses = new Array[Class[_]](params.length)
         val paramsValues = new Array[Object](params.length)
         for((v, i) <- params.zipWithIndex) {
             paramsValues(i) = v 
             paramsClasses(i) = v.getClass()
         }
        try {
            val m = component.getClass().getMethod(functionName, paramsClasses : _*)
             m.invoke(component, paramsValues:_*)
        } catch{ case e : Throwable => {} }
    }
}