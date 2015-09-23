package org.medit.gui

import org.medit.core.root.RootAccessServer

import scala.io.Source

/**
 * Created by nico on 21/09/15.
 */
object Runner {

  def readFa() = {
    var curCode = ""
    for(line <- Source.fromFile("fa").getLines()) {
      if(line.startsWith("fa-")) {
        curCode = line.split("-").zipWithIndex.map(e => {if(e._2== 0) e._1 else e._1.capitalize}).mkString("")
      }
      else if(line.startsWith("[&#")) {
        val code = line.replaceAllLiterally("[&#x", "\\u").replaceAllLiterally(";]", "")
        println("val " + curCode + "=\"" + code + "\"")
      }
    }
  }

  def main(args: Array[String]): Unit = {
    if(args.length == 2 && args(0) == "admin") {
      new RootAccessServer(args(1))
    } else {
      Main.initGUI()
    }
  }
}
