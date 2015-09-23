package org.medit.gui.utils

import scala.collection.mutable

/**
 * Created by nico on 23/09/15.
 *
 * A very basic event system
 */
object EventsSystem {
  val eventHandlers = mutable.HashMap[String, List[(String) => Unit]]()

  val entriesListUpdated = "entries list updated"
  val foldersListUpdated = "folders updated"
  val rootUnlocked = "root unlocked"
  val rootLocked = "root locked"
  /**
   * Register a callback for an event
   * @param eventName Name of the event
   * @param f function to call
   */
  def on(eventName: String, f: (String) => Unit) = {
    eventHandlers(eventName) = f :: eventHandlers.getOrElse(eventName, List[(String) => Unit]())
  }

  /**
   * Trigger an event and run all handlers registered for this event
   * @param eventName Name of the event
   */
  def triggerEvent(eventName: String) = {
    for(handlers <- eventHandlers.get(eventName); handler <- handlers) {
      handler(eventName)
    }
  }
}
