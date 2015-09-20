package org.medit.core.icons

import java.awt.Image
import java.io.File
import java.nio.file.{Files, Paths}
import javax.imageio.ImageIO

import org.apache.commons.io.FilenameUtils
import org.medit.gui.utils.Timer2

import scala.collection.mutable._
import scala.io.Source

/**
 * Created by nico on 17/09/15.
 */
object ImageLoader {

  def get(fileName: String): Option[Image] = get(new File(fileName))

  def get(file: File): Option[Image] = {
    val ext = FilenameUtils.getExtension(file.getName).toLowerCase()
    Timer2.startTimer(ext)
    val img = (for(f <- extToImage.get(ext)) yield { f(file) }).getOrElse(None)
    Timer2.stopTimer(ext)
//    Timer2.printTimers()
    img
  }

  def getFileNameWithoutExtension(fileName: String): String = {
    val idx = fileName.lastIndexOf(".")
    if (idx == -1 || idx == 0) {
      fileName
    } else {
      fileName.splitAt(idx)._1
    }
  }

  def readXpm(file: File): Option[Image] = {
    if (Files.size(Paths.get(file.getAbsolutePath())) == 0) {
      None
    } else {
      try {
        val firstLine = Source.fromFile(file).bufferedReader().readLine()
        if (firstLine.startsWith("<?xml")) {
          readSvg(file)
        } else {
          Some(Xpm.XpmToImage(file.getCanonicalPath))
        }
      } catch {
        case e: Throwable => None
      }
    }
  }

  def readSvg(file: File): Option[Image] = {
    try {
      SVGRasterize.rasterize(file)
    } catch {
      case e: Throwable => None
    }
  }

  def readRaster(file: File): Option[Image] = {
    try {
      val img = ImageIO.read(file)
      if (img == null) None else Some(img)
    } catch {
      case e: Throwable => None
    }
  }

  val extToImage = HashMap[String, File => Option[Image]]()
  extToImage("xpm") = readXpm
  extToImage("png") = readRaster
  extToImage("jpg") = readRaster
  extToImage("svg") = readSvg
}
