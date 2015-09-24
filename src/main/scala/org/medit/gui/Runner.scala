package org.medit.gui

import org.medit.core.root.RootAccessServer

import scala.io.Source

/**
 * Created by nico on 21/09/15.
 */
object Runner {
  def main(args: Array[String]): Unit = {
    if(args.length == 2 && args(0) == "admin") {
      new RootAccessServer(args(1))
    } else {
      Main.initGUI()
    }
  }
}
