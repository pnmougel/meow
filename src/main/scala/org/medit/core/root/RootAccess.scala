package org.medit.core.root

import java.awt.event.{ActionEvent, ActionListener}
import java.io.File
import javax.swing.Timer

import org.jasypt.util.text.StrongTextEncryptor
import org.medit.core.GnomeBlackList
import org.medit.core.entries.{DesktopEntry, MyJsonProtocol}
import org.medit.gui.entries.EntriesHeader
import org.medit.gui.utils.EventsSystem

import scala.sys.process.stringToProcess
import scala.sys.process.stringSeqToProcess
import scala.util.Random
import scalaj.http.Http

/**
 * Created by nico on 18/09/15.
 */
object RootAccess {
  private val key = Random.nextDouble().toString
  private val textDecryptor = new StrongTextEncryptor()
  textDecryptor.setPassword(key)
  val code = "UWP1SY2LPXK60GMXXYB5PZ2GEP3QR7KF9A3BDA8S"

  val port = 4328

  def getRootAccess = {
    s"pkexec meow admin ${key}".run
  }

  def writeFile(file: File, fileContent: String): Unit = {
    val fileToChange = file.getCanonicalPath.replaceAllLiterally("/usr/share/applications/", "").replaceAllLiterally(".desktop", "")
    val rawData = List(code, fileToChange, fileContent).mkString("\n")
    val cypheredData = textDecryptor.encrypt(rawData)
    try {
      Http(s"http://localhost:${port}/write").postData(cypheredData).header("content-type", "text/plain").asString
    } catch { case e: Throwable => {}}
  }

  def saveBlackList() : Unit = {
    val rawData = List(code, GnomeBlackList).mkString("\n")
    val cypheredData = textDecryptor.encrypt(rawData)
    try {
      Http(s"http://localhost:${port}/blacklist").postData(cypheredData).header("content-type", "text/plain").asString
    } catch { case e: Throwable => {}}
  }

  def stopServer() = {
    try {
      Http(s"http://localhost:${port}/kill").header("content-type", "text/plain").asString
    } catch { case e: Throwable => {}}
  }

  var isRootEnabled = false

  new Timer(1000, new ActionListener {
    override def actionPerformed(p1: ActionEvent): Unit = {
      val prevStatus = isRootEnabled
      try {
        val res = Http(s"http://localhost:${port}/alive").header("content-type", "text/plain").asString
        isRootEnabled = res.code == 200
        if(prevStatus != isRootEnabled) {
          EventsSystem.triggerEvent(EventsSystem.rootUnlocked)
        }
      } catch { case e: Throwable => {
        isRootEnabled = false
        if(prevStatus == true) {
          EventsSystem.triggerEvent(EventsSystem.rootLocked)
        }
      }}
    }
  }).start()
}
