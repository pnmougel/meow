package org.medit.core.icons

import java.awt.Image
import java.io.File
import java.nio.file.{Files, Paths}
import javax.imageio.ImageIO

import net.sf.image4j.codec.bmp.BMPDecoder
import net.sf.image4j.codec.ico.ICODecoder
import org.apache.commons.io.FilenameUtils

import scala.collection.mutable._
import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.io.Source

/**
 * Created by nico on 17/09/15.
 */
object ImageLoader {

  def get(fileName: String): Option[Image] = get(new File(fileName))

  def get(file: File): Option[Image] = {
    val mimetype = Files.probeContentType(Paths.get(file.toURI))
    if(mimeTypeToImage.contains(mimetype)) {
      mimeTypeToImage(mimetype)(file)
    } else {
      val ext = FilenameUtils.getExtension(file.getName).toLowerCase()
      (for(f <- extToImage.get(ext)) yield { f(file) }).getOrElse(None)
    }
  }

  def readIco(file: File): Option[Image] = {
    try {
      val images = ICODecoder.read(file)
      if(!images.isEmpty) {
        Some(images.maxBy(_.getWidth))
      } else {
        None
      }
    } catch { case e : Throwable => None }
  }

  def readBmp(file: File): Option[Image] = {
    try {
      Some(BMPDecoder.read(file))
    } catch { case e : Throwable => None }
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

  val extToImage = mutable.HashMap[String, File => Option[Image]]()
  extToImage("xpm") = readXpm
  extToImage("png") = readRaster
  extToImage("jpg") = readRaster
  extToImage("svg") = readSvg
  extToImage("bmp") = readBmp
  extToImage("ico") = readIco

  val mimeTypeToImage = mutable.HashMap[String, File => Option[Image]]()
  mimeTypeToImage("image/xpm") = readXpm
  mimeTypeToImage("image/png") = readRaster
  mimeTypeToImage("image/jpg") = readRaster
  mimeTypeToImage("image/svg") = readSvg
  mimeTypeToImage("image/bmp") = readBmp
  mimeTypeToImage("image/ico") = readIco
}
