//package org.medit.core
//
//import java.io._
//import javax.swing.JFrame
//import Folders._
//import org.medit.core.entries.DesktopEntry
//import org.medit.core.entries.DesktopEntries
//import org.medit.core.icons.{SVGRasterize, IconFinder}
//import java.awt.GraphicsEnvironment
//import java.awt.Font
//import org.medit.gui.Main
//
//import scala.sys.process.{ProcessIO, Process}
//
//object Run {
//  //	"""gsettings reset org.gnome.desktop.app-folders.folder:/org/gnome/desktop/app-folders/folders/Game/ categories""" !
//  //    val home = System.getProperty("user.home")
//
//  //    def makeBackup(src: File): Unit = {
//  //        val backup = new File(s"${home}/.local/share/applications.backup")
//  //        if (!backup.exists()) {
//  //            backup.mkdir()
//  //        }
//  //        val dest = new FileOutputStream(backup.getAbsolutePath() + "/" + src.getName())
//  //        dest.getChannel().transferFrom(new FileInputStream(src) getChannel, 0, Long.MaxValue)
//  //    }
//
//  def main(args: Array[String]): Unit = {
////    val pw = new PrintWriter("tmp")
////    val process = Process ("gksu -u root echo \"\"")
////    val io = new ProcessIO(in => pw, out => System.out, err => System.err)
////
////    val p = process.run(io)
////
////    pw.println("touch /etc/aaaaaaaaaaaaaaa")
////    pw.println("touch /bin/a")
////
////    pw.flush()
////    io.writeInput
//
////    val f = new JFrame()
////    f.setSize(200, 200)
////    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
////    f.setVisible(true)
//
//    val in = new File("tmpXXX")
//    val in2 = new File("/tmp/bla")
//    val writer = new PrintWriter(in)
//    val writer2 = new PrintWriter(in2)
//
//    // val p = new ProcessBuilder("/usr/bin/pkexec", "/bin/sh")
//    // val p = new ProcessBuilder("echo", "fwRise | sudo -S /bin/sh")
//    val p = new ProcessBuilder("/bin/sh", "-c", "/etc/bla")
//    p.redirectInput(in)
//    writer.println("pkexec /bin/sh")
//    writer.println("touch /bin/aaa")
//    writer.println("pkexec /bin/sh")
//    writer.flush()
////    writer.println("touch /bin/dddd")
////    writer.flush()
////    writer.println("touch /bin/bbbb")
////    writer.flush()
//
//    writer2.println("touch /etc/a")
//    writer2.flush()
//
//
//    p.start()
//
//    //      val p = Runtime.getRuntime.exec("echo \"\"")
////      p.getOutputStream.write(("touch /home/nico/aaaa\n").getBytes)
////      p.getOutputStream.flush()
//
////    val exe = new Exe("echo \"\"", out => println("o: " + out), err => println("e: " + err))
////    exe.write("echo $PATH")
////    exe.write("touch /etc/aaaaaaaaaaaaaaa")
////    exe.write("touch /bin/a")
//
////    process.writeInput("gksu -u root echo \"\"")
//
////    val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
////    val font = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Roboto-Bold.ttf"))
////    println(font.getFamily)
////    println(font.getFontName)
////    ge.registerFont(font)
//
////     val z = Main.getClass.getResource("/Roboto-Black.ttf")
////    val z = Main.getClass.getResource("missing.png")
////    println(z)
////    val font2 = Font.createFont(Font.TRUETYPE_FONT, new File(z.toURI()))
////    println(font2.getFamily)
////    println(font2.getFontName)
////    ge.registerFont(font2)
//
//    // var bi = SVGRasterize.rasterize(new File("/usr/share/icons/hicolor/scalable/apps/libreoffice-startcenter.svg"))
//    //    var bi = SVGRasterize.rasterize(new File("/usr/share/yelp-xsl/icons/hicolor/scalable/status/yelp-note.svg"))
//    //    println(bi)
//    //    IconFinder.icons.contains("test")
//    // val e = DesktopEntries.getEntriesWithCategories(List("GNOME"))
//
//    //    	var z = List("fsdfsd")
//    //    	println(z.mkString("['", "','", "']"))
//
//    // e.foreach(println)
//
//  }
//}
//
//class Exe(command: String, out: String => Unit, err: String => Unit) {
//
//  import scala.sys.process._
//  import scala.io._
//  import java.io._
//  import scala.concurrent._
//
//
//  // val inputStream = new SyncVar[OutputStream]
//  val inputStream = new PrintWriter("tmp")
//
//  val process = Process(command).run(
//    new ProcessIO(
//      stdin => inputStream,
//      stdout => Source.fromInputStream(stdout).getLines.foreach(out),
//      stderr => Source.fromInputStream(stderr).getLines.foreach(err)))
//
//  def write(s: String): Unit = synchronized {
//    println(s)
//    inputStream.println(s)
//    inputStream.flush()
////    inputStream.get.write((s + "\n").getBytes)
////    inputStream.get.flush()
//  }
//
//  def close(): Unit = {
//    inputStream.close()
////    inputStream.get.close
//  }
//}