package org.medit.core.properties

import scala.collection.mutable.HashMap

class Group(name : String = "") {
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
