package org.medit.core.entries

import java.io.File
import scala.io.Source
import scala.collection.mutable.HashMap
import scala.Vector

class Property(line: String) {
    val trimedLine = line.trim()
    val isKeyValue = line.contains("=") && !trimedLine.startsWith("#")
    val elems = line.split("=")
    val isComment = trimedLine.startsWith("#")
    val isEmpty = trimedLine.isEmpty()
    val key = if(isKeyValue) Option(elems(0)) else None
    var value = if(isKeyValue && elems.length >= 2) {
        Option(line.splitAt(line.indexOf("=") + 1)._2) 
    } else Option("")
    
    def getKey() : Option[String] = key
    
    def getValue() = value
    
    def setValue(newValue: String) = {
        if(isKeyValue) {
            value = Option(newValue)
        }
    }
    
    override def toString() : String = {
        if(isKeyValue) { key.get + "=" + value.get } else { line }
    }
}
