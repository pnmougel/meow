package org.medit.core

import java.io.File
import javax.swing.JFrame

import org.jasypt.util.text.StrongTextEncryptor

import scala.io.Source
import scala.util.Random
import scalaj.http.Http
import scala.sys.process.{ProcessIO, stringToProcess}

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
    val ret = s"pkexec java -jar /home/nico/workspace/Meow-Admin/target/scala-2.10/Meow-Admin-assembly-1.0.jar test ${key}".run
  }

  def writeFile(file: File, fileContent: String): Unit = {
    val fileToChange = file.getCanonicalPath.replaceAllLiterally("/usr/share/applications/", "").replaceAllLiterally(".desktop", "")
    val rawData = List(code, fileToChange, fileContent).mkString("\n")
    val cypheredData = textDecryptor.encrypt(rawData)
    try {
      Http(s"http://localhost:${port}/write").postData(cypheredData).header("content-type", "text/plain").asString
    } catch { case e: Throwable => {}}
  }

  def stopServer() = {
    println("Stop server")
    try {
      val res = Http(s"http://localhost:${port}/kill").header("content-type", "text/plain").asString
      println(res.code)
      println(res.body)
    } catch { case e: Throwable => {}}
  }

  def isRootEnabled : Boolean = {
    try {
      val res = Http(s"http://localhost:${port}/alive").header("content-type", "text/plain").asString
      res.code == 200
    } catch { case e: Throwable => { false }}
  }

  def main(args: Array[String]) : Unit = {
    stopServer()
    getRootAccess
    Thread.sleep(4000)
    writeFile(new File("/usr/share/applications/ubuntu-amazon-default.desktop"), "[Desktop Entry]\nName=Amazon Changed\nType=Application\nIcon=amazon-store\nExec=unity-webapps-runner --amazon --app-id=ubuntu-amazon-default\nCategories=Internet;%%       ")
  }
}
