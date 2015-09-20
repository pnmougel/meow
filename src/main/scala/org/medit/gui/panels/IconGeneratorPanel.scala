package org.medit.gui.panels

import java.awt._
import java.io.File
import javax.imageio.ImageIO
import javax.swing.border.LineBorder
import javax.swing.{BoxLayout, ImageIcon, JPanel}

import com.alee.laf.label.WebLabel
import com.alee.laf.panel.WebPanel
import com.alee.managers.popup.WebPopup
import org.medit.core.icons.IconGenerator
import org.medit.gui.components.BsHiddenInput
import org.medit.gui.utils.Colors
import org.medit.gui.utils.SwingEvents._

class IconGeneratorPanel(appName: String, chooser: IconChooser, f: (String) => Unit) extends WebPanel(new BorderLayout()) {
  setBackground(Colors.white)

  def saveIcon() : String = {
    val baseName = if(iconText.getText().isEmpty) "empty" else iconText.getText().replaceAll(" ", "-").replaceAll("/", "_")
    val home = System.getProperty("user.home")
    val basePath = s"${home}/.local/share/icons/meow/"
    val baseDirFile = new File(basePath)
    if(!baseDirFile.exists()) {
      baseDirFile.mkdirs()
    }
    var curPath = basePath + baseName + ".png"
    var i = 0
    while(new File(curPath).exists()) {
      i += 1
      curPath = basePath + baseName + "-" + i + ".png"
    }
    ImageIO.write(curImage, "png", new File(curPath))
    curPath
  }

  val iconItem = new WebLabel()
  iconItem.setMargin(0, 4, 0, 0)
  iconItem.onClick( e => {
    for(popup <- chooser.popOver) {
      popup.dispose()
    }
    f(saveIcon())
  })
  val nbColors = 16
  val colors = Colors.getColors(nbColors, 75, 200)
  var curColor = colors(0)

  val iconText = new BsHiddenInput()
  iconText.setText(appName)
  iconText.setInputPrompt("Text for the icon")
  iconText.onKeyRelease( _ => {
    updateIcon()
  })
  var curImage = IconGenerator.generateIcon(iconText.getText, curColor)


  def updateIcon() = {
    curImage = IconGenerator.generateIcon(iconText.getText, curColor)
    val image = curImage.getScaledInstance(45, 45, Image.SCALE_SMOOTH)
    iconItem.setIcon(new ImageIcon(image))
    repaint()
  }
  updateIcon()

  val gridLayout = new GridLayout(2, nbColors / 2)
  gridLayout.setHgap(5)
  gridLayout.setVgap(5)
  val colorsPanel = new JPanel(gridLayout)
  for(color <- colors) {
    val colorComponent = new ColorComponent(color)
    colorComponent.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
    colorComponent.onClick(_ => {
      curColor = color
      updateIcon
    })
    colorsPanel.add(colorComponent)
  }


  val rightPanel = new JPanel()
  iconText.setColumns(17)
//  rightPanel.add(iconText)
  rightPanel.add(colorsPanel)
  iconText.setBackground(Colors.white)
  rightPanel.setBackground(Colors.white)
  iconItem.setBackground(Colors.white)

  add(iconItem, BorderLayout.WEST)
  add(rightPanel, BorderLayout.CENTER)
}

class ColorComponent(color: Color) extends JPanel(new FlowLayout()) {
  val roundSize = 4

  val dimension = new Dimension(20, 20)
  setPreferredSize(dimension)
  setMinimumSize(dimension)
  setMaximumSize(dimension)

  override def paint(g2: Graphics) = {
    val g = g2.asInstanceOf[Graphics2D]
    val s = Math.min(getWidth, getHeight)
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
    g.setPaint(color)
    g.fillRoundRect((getWidth - s) / 2, 0, s - 1, s - 1, roundSize, roundSize)
    g.setColor(color.darker())
    g.drawRoundRect((getWidth - s) / 2, 0, s - 1, s - 1, roundSize, roundSize)
  }
}
