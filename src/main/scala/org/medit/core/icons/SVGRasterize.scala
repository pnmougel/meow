package org.medit.core.icons

import java.awt.image.BufferedImage
import java.io.{File, FileInputStream, IOException, PrintStream}
import java.security.MessageDigest
import javax.imageio.ImageIO

import com.alee.utils.FileUtils
import org.apache.batik.anim.dom.SVGDOMImplementation
import org.apache.batik.transcoder.image.ImageTranscoder
import org.apache.batik.transcoder.{SVGAbstractTranscoder, TranscoderInput, TranscoderOutput, TranscodingHints, XMLAbstractTranscoder}
import org.apache.batik.util.SVGConstants
import org.medit.core.utils.GlobalPaths

/**
 * @author nico
 */
object SVGRasterize {
//  val svgRasterCacheName = "svg-raster"
//  val svgRasterCache = new File(svgRasterCacheName)
//  if(svgRasterCache.exists()) {
//    if(!svgRasterCache.isDirectory) {
//      svgRasterCache.delete()
//      svgRasterCache.mkdir()
//    }
//  } else {
//    svgRasterCache.mkdir()
//  }

  val messageDigest = MessageDigest.getInstance("MD5")
  def md5Hash(text: String) : String = messageDigest.digest(text.getBytes()).map(0xFF & _).map { "%02x".format(_) }.foldLeft(""){_ + _}


  def rasterize(svgFile: File): Option[BufferedImage] = {
    val rasterName = md5Hash(svgFile.getCanonicalPath + svgFile.lastModified()) + ".png"
    val rasterFile = GlobalPaths.cacheFile.listFiles().find(f => f.getName == rasterName)
    if(rasterFile.isDefined) {
      Some(ImageIO.read(rasterFile.get))
    } else {
      val prevErrStream = System.err
      System.setErr(new PrintStream("/dev/null"))

      var bufferedImage : Option[BufferedImage] = None
//      var bufferedImage = new Array[BufferedImage](1)

      val css = "svg {" +
        "shape-rendering: geometricPrecision;" +
        "text-rendering:  geometricPrecision;" +
        "color-rendering: optimizeQuality;" +
        "image-rendering: optimizeQuality;" +
        "}"
      val cssFile = File.createTempFile("batik-default-override-", ".css")
      FileUtils.writeStringToFile(css, cssFile)

      val transcoderHints = new TranscodingHints()
      //    transcoderHints.put(XMLAbstractTranscoder.KEY_XML_PARSER_VALIDATING, System.getProperty("isrelease", "false") != "true")
      transcoderHints.put(XMLAbstractTranscoder.KEY_XML_PARSER_VALIDATING, false)
      transcoderHints.put(SVGAbstractTranscoder.KEY_WIDTH, 256f)
      transcoderHints.put(XMLAbstractTranscoder.KEY_DOM_IMPLEMENTATION, SVGDOMImplementation.getDOMImplementation)
      transcoderHints.put(XMLAbstractTranscoder.KEY_DOCUMENT_ELEMENT_NAMESPACE_URI, SVGConstants.SVG_NAMESPACE_URI)
      transcoderHints.put(XMLAbstractTranscoder.KEY_DOCUMENT_ELEMENT, "svg")
      transcoderHints.put(SVGAbstractTranscoder.KEY_USER_STYLESHEET_URI, cssFile.toURI.toString)

      try {
        val input = new TranscoderInput(new FileInputStream(svgFile))

        val t = new ImageTranscoder() {
          override def createImage(w: Int, h: Int): BufferedImage = { new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB) }
          override def writeImage(image: BufferedImage, out: TranscoderOutput): Unit = { bufferedImage = Some(image) }
        }
        t.setTranscodingHints(transcoderHints)
        t.transcode(input, null)
      } catch { case ex: Throwable => throw new IOException("Couldn't convert " + svgFile)
      } finally {
        cssFile.delete()
        System.setErr(prevErrStream)
      }
      for(image <- bufferedImage) { ImageIO.write(image, "png", new File(GlobalPaths.cacheFolder + "/" + rasterName)) }
      bufferedImage
    }
  }
}