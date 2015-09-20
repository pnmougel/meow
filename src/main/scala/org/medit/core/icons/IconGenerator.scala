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

  def main(args: Array[String]): Unit = {
    for(color <- Colors.getColors(8, 75, 200)) {
      val baseImage = generateIcon("Chromium web browser", color)
      ImageIO.write(baseImage, "png", new File("icon" + color.toString + ".png"))
    }

//    val baseImage = generateIcon("Gimp", color)
//    generateSVG("icon.svg", color, "Gimp")
//    for(size <- iconSizes) {
//      val scaledImage = baseImage.getScaledInstance(size, size, Image.SCALE_SMOOTH)
//      val newImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
//      val g = newImage.createGraphics()
//      g.drawImage(scaledImage, 0, 0, null)
//      g.dispose
//      ImageIO.write(newImage, "png", new File("icon" + size + ".png"))
//    }
  }

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
    svgGenerator.setSVGCanvasSize(new Dimension(256, 256))
    paint(svgGenerator, color, text)
    svgGenerator.stream(new FileWriter(out), false)
  }

  def paint(g : Graphics2D, color: Color, text: String): Unit = {
    val size = 256
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
    val roundSize = 32

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
    g.dispose()
  }
}
