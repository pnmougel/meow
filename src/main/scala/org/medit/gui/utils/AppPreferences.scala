
package org.medit.gui.utils

import java.util.prefs.Preferences
import org.medit.gui.Main

object AppPreferences {
	val prefs = Preferences.userNodeForPackage(Main.getClass())
	
	def getNode(path : String) : (Preferences, String) = {
	    var curNode = prefs
	    val elems = path.split("/")
	    0 until (elems.size - 1) foreach { i =>
	        curNode = curNode.node(elems(i))
	    }
	    (curNode, elems(elems.size - 1))
	}
	
	def setInt(path : String, value : Int) = {
		val (node, key) = getNode(path)
		node.putInt(key, value)
	}
	
	def setFloat(path : String, value : Float) = {
		val (node, key) = getNode(path)
		node.putFloat(key, value)
	}
	
	def setString(path : String, value : String) = {
		val (node, key) = getNode(path)
		node.put(key, value)
	}
	
	def getInt(path : String) : Option[Int] = {
	    val (node, key) = getNode(path)
	    val value = node.getInt(key, Int.MaxValue)
	    if(value == Int.MaxValue) None else Option(value)
	}
	
	def get[T](path : String, default : T) : T = {
	    val (node, key) = getNode(path)
	    default match  {
	    case v : Int => node.getInt(key, v).asInstanceOf[T]
	    case v : Float => node.getFloat(key, v).asInstanceOf[T]
	    case v : String => node.get(key, v).asInstanceOf[T]
	    }
	}
	
	def getInt(path : String, default : Int) : Int = {
	    val (node, key) = getNode(path)
	    node.getInt(key, default)
	}
	
	def getString(path : String, default : String) : String = {
	    val (node, key) = getNode(path)
	    node.get(key, default)
	}
	
	def getString(path : String) : Option[String] = {
	    val (node, key) = getNode(path)
	    val value = node.get(key, "fsdkfhuskqjfdsqklj")
	    if(value == "fsdkfhuskqjfdsqklj") None else Option(value)
	}
	
	def getFloat(path : String) : Option[Float] = {
	    val (node, key) = getNode(path)
	    val default : Float = 0.04156498412158f
	    val value = node.getFloat(key, default)
	    if(value == default) None else Option(value)
	}
}