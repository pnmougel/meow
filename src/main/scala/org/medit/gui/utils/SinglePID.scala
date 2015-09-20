package org.medit.gui.utils

import java.lang.management.ManagementFactory
import java.io.PrintWriter
import scala.io.Source
import sys.process.stringSeqToProcess
import sys.process.stringToProcess
import java.io.File
/**
 * @author nico
 * Ensures that only one instance of the app will exists
 */
trait SinglePID {
  if(System.getProperty("isrelease", "false") != "true") {
    if(new File("pid").exists()) {
      val prevPid = Source.fromFile("pid").getLines().mkString

      Seq("kill", "-9", prevPid).!
    }

    val pid = ManagementFactory.getRuntimeMXBean().getName().split("@")(0)
    val bw = new PrintWriter("pid")
    bw.write(pid)
    bw.flush()
    bw.close()
  }
}