package org.medit.gui.utils.styles.helpers

import java.awt.Color

object ColorParser {
	def parseColor(value: String) : Option[Color] = {
	    try {
	        Option(Color.decode(value))
	    } catch {
	        case e : NumberFormatException => None 
	    }
	}
}