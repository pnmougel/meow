package org.medit.core.entries

import java.io.File
import scala.io.Source
import scala.collection.mutable.HashMap
import scala.Vector

class Category(name : String = "") {
    var properties = Vector[Property]()
    var propertyMap = HashMap[String, Property]()
    
    def addProperty(property : Property) = {
        properties :+= property
        if(property.isKeyValue) {
            propertyMap(property.getKey.get) = property
        }
    }
    
    override def toString() : String = {
        val title = if(name.isEmpty()) "" else "[" + name + "]\n"
        properties.map(p => p).mkString(title, "\n", "")
    }
}
