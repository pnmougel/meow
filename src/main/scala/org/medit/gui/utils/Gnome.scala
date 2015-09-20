package org.medit.gui.utils
import sys.process.stringToProcess

/**
 * Created by nico on 17/09/15.
 */
object Gnome {
  def reload() = {
    try {
      "gnome-shell --replace &".!
    } catch { case e : Throwable => {} }
  }
}
