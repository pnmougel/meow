package org.medit.core.icons

import java.awt._
import java.awt.font.TextLayout
import java.awt.geom.{AffineTransform, GeneralPath}
import java.awt.image.BufferedImage
import java.io.{FileWriter, File}
import javax.imageio.ImageIO
import javax.swing.JFrame

import com.alee.extended.image.WebDecoratedImage
import org.apache.batik.dom.GenericDOMImplementation
import org.apache.batik.svggen.SVGGraphics2D
import org.medit.gui.utils.Colors

/**
 * Created by nico on 17/09/15.
 */
object IconGenerator {
  val iconSizes = Array(16, 22, 24, 32, 48, 64, 96, 128, 256)

  def generateIcon(text: String, color: Color) = {
    val size = 256
    val image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
    paint(image.getGraphics.asInstanceOf[Graphics2D], color, text)
    image
  }

  def generateSVG(out: String, color: Color, text: String) = {
    val domImpl = GenericDOMImplementation.getDOMImplementation()
    val document = domImpl.createDocument("http://www.w3.org/2000/svg", "svg", null)
    val svgGenerator = new SVGGraphics2D(document)
    svgGenerator.setSVGCanvasSize(new Dimension(512, 512))
    paint(svgGenerator, color, text)
    svgGenerator.stream(new FileWriter(out), false)
  }

  def paint(g : Graphics2D, color: Color, text: String): Unit = {
    val size = 256
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
    val roundSize = 32

    g.setPaint(color)
    g.fillRoundRect(20, 20, size - 41, size - 41, roundSize, roundSize)
    val stringToWrite = text.charAt(0).toUpper.toString
    val curFont = g.getFont.deriveFont(Font.BOLD, 128)
    g.setFont(curFont)
//    val metrics = g.getFontMetrics(g.getFont.deriveFont(Font.PLAIN, 128))
    val textTl = new TextLayout(stringToWrite, curFont, g.getFontRenderContext)

    val w = textTl.getBounds.getWidth
    val h = textTl.getBounds.getHeight
//    val x = (size - metrics.stringWidth(stringToWrite)) / 2
//    val y = curFont.getSize + (size - curFont.getSize) / 2 - 20
    val x = ((size - w) / 2).toInt - 5
    val y = (h + (size - h) / 2).toInt
    val transform = new AffineTransform()
    transform.translate(x, y)
    val textShape = textTl.getOutline(transform)
    g.setColor(Colors.white)
    g.setPaint(Colors.white)
    g.fill(textShape)
    g.setPaint(color.darker())
    g.setColor(color.darker())
    g.draw(textShape)

    //    g.drawString(stringToWrite, x, y)
    /*
    val r = roundSize / 2
    val shadowShape = new GeneralPath()

    val angleHeight = 20

    //    shadowShape.moveTo(0, (size / 2) - angleHeight)
    //    shadowShape.lineTo(size / 2, (size / 2) + angleHeight)
    //    shadowShape.lineTo(size, (size / 2) - angleHeight)
    shadowShape.moveTo(0, size / 2 - angleHeight)
    shadowShape.quadTo(size / 2, size / 2 + 2*angleHeight, size, size / 2 - angleHeight)
    shadowShape.lineTo(size, size - r)
    shadowShape.quadTo(size, size, size - r, size)
    shadowShape.lineTo(r, size)
    shadowShape.quadTo(0, size, 0, size - r)
    shadowShape.closePath()

    val paintBack = new GradientPaint(0, 0, color, 0, size, color.darker())
    g.setPaint(paintBack)
    g.fillRoundRect(0, 0, size - 1, size - 1, roundSize, roundSize)
    val alphaDark = new Color(0, 0, 0, 20)
    g.setPaint(alphaDark)
    g.fill(shadowShape)

    g.setColor(color)
    g.setStroke(new BasicStroke(4))
    g.drawRoundRect(2, 2, size - 4, size - 4, roundSize, roundSize)

    g.setColor(color.brighter())
    g.drawRoundRect(6, 6, size - 12, size - 12, roundSize - 6, roundSize - 6)

    val lines = text.split(" ").map(_.capitalize)
    var fontSize = 60
    var isFit = false
    val maxWidth = size * 0.75
    while(!isFit) {
      fontSize -= 1
      isFit = true
      val metrics = g.getFontMetrics(g.getFont.deriveFont(Font.BOLD, fontSize))
      for(line <- lines) {
        isFit = isFit & metrics.stringWidth(line) < maxWidth
      }
    }

    val font = g.getFont.deriveFont(Font.BOLD, fontSize)
    g.setFont(font)
    g.setStroke(new BasicStroke(2))
    val textHeight = font.getSize * 1.2
    val top = 0.5 * (size - (lines.size * textHeight))
    for((line, i) <- lines.zipWithIndex) {
      if(!line.isEmpty) {
        val textTl = new TextLayout(line, font, g.getFontRenderContext)
        val transform = new AffineTransform()
        transform.translate(20, top + font.getSize + i * textHeight - 1)
        val textShape = textTl.getOutline(transform)
        g.setColor(color.darker())
        g.draw(textShape)
        g.setColor(color.brighter())
        g.fill(textShape)
      }
    }
    */
    g.dispose()
  }
}
