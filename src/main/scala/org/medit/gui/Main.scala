package org.medit.gui

import java.awt.{Toolkit, BorderLayout, Color, Container}
import java.awt.event.{WindowAdapter, WindowEvent}
import java.io.{File, InputStream}
import java.lang.System
import javax.swing.{JDialog, JFrame, JSplitPane, UIManager}

import com.alee.laf.WebLookAndFeel
import com.alee.laf.splitpane.WebSplitPane
import org.medit.core.RootAccess
import org.medit.gui.folders.{FolderPanel}
import org.medit.gui.entries.{EntriesPanel, EntryDetailsPanel}
import org.medit.gui.panels.LeftMenu
import org.medit.gui.utils._
import org.medit.gui.utils.styles.Styles

object Main extends SinglePID with FontsLoader {
  val frame = new JFrame("Meow")

  val styleFile = new File("./src/main/resources/styles/app")
  val styles = new Styles(frame, getFile("/styles/app"), if(styleFile.exists()) Some(styleFile) else None)
  
  def getFile(path: String): InputStream = {
    getClass.getResourceAsStream(path)
  }

  def updateContainer(container: Container, f: (() => _)) = {
    container.removeAll()
    f()
    container.doLayout()
    styles.updateComponents
  }

  var splitPane : WebSplitPane = null 
  
  def createAndShowGUI() = {
//    frame.setIconImage(Icon.get("large_tiles", 16).getImage)
    val width = AppPreferences.get[Int]("window/width", 800)
    val height = AppPreferences.get[Int]("window/height", 600)
    val posX = AppPreferences.get[Int]("window/posX", 10)
    val posY = AppPreferences.get[Int]("window/posY", 10)
    val splitPane2Pos = AppPreferences.get[Int]("splitPane2", 450)

    val splitPane2 = new WebSplitPane(JSplitPane.HORIZONTAL_SPLIT, LeftMenu, EntriesPanel)
    splitPane2.setDividerLocation(splitPane2Pos)
    splitPane2.setDividerSize(3)
    splitPane2.setDividerBorderColor(Color.WHITE)
    splitPane2.setBackground(Color.white)
    splitPane2.setForeground(Color.white)
    splitPane2.setOpaque(true)

    frame.setBounds(posX, posY, width, height)
    frame.setLocationRelativeTo(null)
    frame.addWindowListener(new WindowAdapter {
      override def windowClosing(e: WindowEvent) = {
        RootAccess.stopServer()
        val window = e.getWindow
        AppPreferences.setInt("window/width", window.getWidth)
        AppPreferences.setInt("window/height", window.getHeight)
        AppPreferences.setInt("window/posX", window.getX)
        AppPreferences.setInt("splitPane2", splitPane2.getDividerLocation)
        AppPreferences.setInt("window/posY", window.getY)
        System.exit(0)
      }
    })

    val rootPanel = frame.getContentPane
    rootPanel.setLayout(new BorderLayout())
    rootPanel.add(splitPane2)

    frame.setVisible(true)
    styles.start()
  }

  def main(args: Array[String]): Unit = {
    RootAccess.stopServer()
    WebLookAndFeel.install(true)
    System.setProperty("awt.useSystemAAFontSettings", "on")
    System.setProperty("swing.aatext", "true")
    UIManager.put("Panel.background", Color.white)

    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      def run() {
        UIManager.put("Panel.background", Color.white)
        UIManager.getInstalledLookAndFeels().foreach { laf =>
          if (laf.getName() == "Nimbus") {
            UIManager.put("Panel.background", Color.white)
            UIManager.setLookAndFeel(laf.getClassName());
          }
        }
        JFrame.setDefaultLookAndFeelDecorated(true)
        JDialog.setDefaultLookAndFeelDecorated(true)

        createAndShowGUI()
//        Timer.printTimers()
      }
    })
  }
}

