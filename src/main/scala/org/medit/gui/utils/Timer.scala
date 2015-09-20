package org.medit.gui.utils

import scala.collection._

object Timer2 {
	private val deltas = mutable.HashMap[String, Double]()
	private val timers = mutable.HashMap[String, Double]()
	
	def startTimer(timerName : String) = {
		deltas(timerName) = System.currentTimeMillis()
	}
	
	def stopTimer(timerName : String) = {
		val curTimer = System.currentTimeMillis() - deltas(timerName)
		timers(timerName) = timers.getOrElse(timerName, 0D) + curTimer
	}

  def stopAllTimers() = {
    for((timerName, _) <- deltas) {
      if(!timers.contains(timerName)) {
        stopTimer(timerName)
      }
    }
  }
	
	def getTimer(timerName : String) = timers(timerName)
	def clearTimer(timerName : String) = {
	    timers(timerName) = 0D
	}
	
	def clearAllTimers() = {
	    timers.clear()
	}
	
	def printTimers() = {
		println("# \n# Timers")
		timers.toList.sortBy({_._2}).foreach { a =>
		  	println("# " + a._1 + ":\t" + a._2 / 1000)
		}
	}
	
	def time[R](timerName : String)(block: => R) : R = {
	    deltas(timerName) = System.currentTimeMillis()
	    val result = block
	    val curTimer = System.currentTimeMillis() - deltas(timerName)
		timers(timerName) = timers.getOrElse(timerName, 0D) + curTimer
		result
	}
}