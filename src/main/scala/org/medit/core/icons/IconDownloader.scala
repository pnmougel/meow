package org.medit.core.icons

import java.awt.image.BufferedImage
import java.io.{File, FileOutputStream, FileWriter}
import java.nio.file.{Paths, Files}
import javax.imageio.ImageIO
import org.apache.commons.io.FilenameUtils
import org.jsoup.Jsoup
import org.medit.core.utils.{GlobalPaths, FileUtils}

import scala.collection.JavaConversions._
import net.sf.image4j.codec.ico.ICODecoder

import scalaj.http.Http

/**
 * Created by nico on 22/09/15.
 */
object IconDownloader {
  def getIconUrl(url : String) = {
    val urlParts = url.split("/")
    val baseUrl = urlParts.take(3).mkString("/")
    var iconUrl = baseUrl + "/favicon.ico"
    try {
      val body = Http(url).asString.body
      val doc = Jsoup.parse(body)
      val urls = (for(elem <- doc.head().getElementsByTag("link"); if(elem.attr("rel").contains("icon") && !elem.attr("href").isEmpty)) yield {
        val rel = elem.attr("rel")
        var size = 0
        if(elem.attr("sizes").contains("x")) {
          try {
            size = elem.attr("sizes").split("x")(0).toInt
          } catch { case e: NumberFormatException => {} }
        }
        // Build the link for the image
        var href = elem.attr("href")
        if(!href.startsWith("http")) {
          if(href.startsWith("//")) { href = "http:" + href }
          else {
            if(!href.startsWith("/")) { href = "/" + href }
            href = baseUrl + href
          }
        }
        val matchScore = if(size >= 60) size
        else if(rel.contains("fluid-icon")) 30
        else if(rel.contains("apple-touch-icon")) 20
        else if(rel.contains("shortcut icon")) 10
        else 5
//        println((matchScore, href))
        (matchScore, href)
      }).sortBy(-1 * _._1).map(_._2)
      iconUrl = if(urls.isEmpty) { baseUrl + "/favicon.ico" } else urls(0)
    } catch { case e: Throwable => }
    iconUrl
  }

  def saveFavicon(url: String, appName: String) = {
    val iconUrl = getIconUrl(url)
    val ext = FilenameUtils.getExtension(iconUrl)
    val tmpImagePath = "/tmp/favicon." + ext
    val fw = new FileOutputStream(tmpImagePath)
    fw.write(Http(iconUrl).asBytes.body)
    fw.flush()
    fw.close()
    var outIconPath = ""
    for(sourceImage <- ImageLoader.get(new File(tmpImagePath))) {
      val bi = new BufferedImage(sourceImage.getWidth(null), sourceImage.getHeight(null), BufferedImage.TYPE_INT_ARGB)
      bi.getGraphics.drawImage(sourceImage, 0, 0, null)
      bi.getGraphics.dispose()
      outIconPath = FileUtils.saveIcon(appName.toLowerCase(), bi)
    }
    outIconPath
  }
}
