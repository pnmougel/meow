package org.medit.core.root

import java.io.{File, PrintWriter}
import java.net.InetSocketAddress

import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}
import org.jasypt.util.text.StrongTextEncryptor

import scala.io.Source
import scala.sys.process.stringToProcess

/**
 * Created by nico on 21/09/15.
 */
class RootAccessServer(password: String) {
  def decypher(req: HttpExchange, f: (Array[String]) => Boolean) = {
    val ret = try {
      val bodyEncrypted = Source.fromInputStream(req.getRequestBody).getLines().mkString("\n")
      val lines = textDecryptor.decrypt(bodyEncrypted).split("\n")
      val data = if (lines.size > 2 && lines(0) == "UWP1SY2LPXK60GMXXYB5PZ2GEP3QR7KF9A3BDA8S") lines.splitAt(1)._2 else new Array[String](0)
      data
    } catch {
      case e: Throwable => {
        new Array[String](0)
      }
    }
    val status = if (f(ret)) 200 else 401
    req.sendResponseHeaders(status, -1)
  }


  val blackListUpdateRequest = new HttpHandler {
    override def handle(req: HttpExchange): Unit = {
      decypher(req, lines => {
        val pw = new PrintWriter("/etc/gnome/menus.blacklist")
        try {
          pw.write(lines.mkString("\n"))
          true
        } catch {
          case e: Throwable => { false }
        } finally { pw.flush(); pw.close() }
      })
    }
  }

  val writeRequest = new HttpHandler {
    override def handle(req: HttpExchange): Unit = {
      decypher(req, lines => {
        var isOk = false
        if (lines.size   > 2) {
          val path = "/usr/share/applications/" + lines(0) + ".desktop"
          val file = new File(path)
          if (!path.contains("..") && file.exists() && file.isFile && !file.canExecute && !file.isHidden) {
            val content = lines.splitAt(1)._2.mkString("\n")
            val pw = new PrintWriter(file)
            isOk = try {
              pw.write(content)
              true
            } catch {
              case e: Throwable => { false }
            } finally { pw.flush(); pw.close() }
          }
        }
        isOk
      })
//      var lines = Array[String]()
//      var message = "Ok"
//      try {
//        val bodyEncrypted = Source.fromInputStream(req.getRequestBody).getLines().mkString("\n")
//        lines = textDecryptor.decrypt(bodyEncrypted).split("\n")
//      } catch {
//        case e: Throwable => {
//          message = "Unable to read request data "
//        }
//      }
//      if (lines.size > 3) {
//        if (lines(0) == "UWP1SY2LPXK60GMXXYB5PZ2GEP3QR7KF9A3BDA8S") {
//          val path = "/usr/share/applications/" + lines(1) + ".desktop"
//          val file = new File(path)
//          if (!path.contains("..") && file.exists() && file.isFile && !file.canExecute && !file.isHidden) {
//            val content = lines.splitAt(2)._2.mkString("\n")
//            val pw = new PrintWriter(file)
//            try {
//              pw.write(content)
//              pw.flush()
//            } catch {
//              case e: Throwable => {
//                message = "Unable to write the file"
//              }
//            } finally {
//              pw.close()
//            }
//          } else {
//            message = "Invalid file"
//          }
//        } else {
//          message = "Invalid query"
//        }
//      } else {
//        message = "Invalid query"
//      }
//      val status = if (message == "Ok") 200 else 401
//      val out = message.getBytes()
//      req.sendResponseHeaders(status, out.length)
//      req.getResponseBody.write(out)
//      req.getResponseBody.flush()
//      req.getResponseBody.close()
    }
  }


  val killRequest = new HttpHandler {
    override def handle(req: HttpExchange): Unit = {
      req.sendResponseHeaders(200, -1)
      server.stop(1)
      Thread.sleep(2)
      System.exit(0)
    }
  }

  val gnomeReloadRequest = new HttpHandler {
    override def handle(req: HttpExchange): Unit = {
      try {
        "gnome-shell --replace &".!
      } catch {
        case e: Throwable => {}
      }
      req.sendResponseHeaders(200, -1)
    }
  }

  val isAliveRequest = new HttpHandler {
    override def handle(req: HttpExchange): Unit = {
      req.sendResponseHeaders(200, -1)
    }
  }
  //  var portFound = false
  //  var server: HttpServer = _
  //  while(!portFound) {
  //    try {
  //      port += 1
  //      println(port)
  //      val address = new InetSocketAddress(port)
  //      server = HttpServer.create(address, 0)
  //      portFound = true
  //    } catch { case e: Throwable => {}}
  //  }
  //  println(port)
  var port = 4328
  val address = new InetSocketAddress(port)
  val server = HttpServer.create(address, 0)
  val textDecryptor = new StrongTextEncryptor()
  textDecryptor.setPassword(password)
  server.createContext("/write", writeRequest)
  server.createContext("/kill", killRequest)
  server.createContext("/alive", isAliveRequest)
  server.createContext("/blacklist", blackListUpdateRequest)
  // server.createContext("/reloadGnomeMenu", gnomeReloadRequest)
  server.start()
}
