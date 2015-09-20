package org.medit.gui.utils.styles

import javax.swing.JFrame
import java.awt.Component
import java.awt.Container
import java.awt.Color
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.StandardWatchEventKinds
import java.nio.file.Path
import scala.collection.JavaConversions._
import scala.collection.mutable.HashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.io.InputStream
import java.io.FileInputStream

class Styles(frame: JFrame, inputStream: InputStream, watchFile: Option[File] = None) extends Thread {
  var parser = new StyleParser()
  var styles = HashMap[String, Style]()

  val isRunning = new AtomicBoolean(true)
  override def run(): Unit = {
    styles = parser.parse(inputStream)
    updateComponents()

    for (path <- watchFile) {
      val pathToWatch = path.getParentFile().toPath()
      val watchService = FileSystems.getDefault().newWatchService()
      val watchKey = pathToWatch.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY)

      while (isRunning.get) {
        val wk = watchService.take()
        for (event <- wk.pollEvents()) {
          val changed = event.context().asInstanceOf[Path]
          if (changed.toFile().getName().startsWith(path.getName())) {
            Thread.sleep(100)
             styles = parser.parse(new FileInputStream(path))
            updateComponents()
          }
        }
        val valid = wk.reset()
        Thread.`yield`()
      }
    }
  }

  def updateComponents() = updateChildComponents(frame.getRootPane(), "")

  def updateChildComponents(component: Component, padding: String): Unit = {
    val name = component.getName()
    if (name != null) {
      name.split(" ").foreach(componentClass => {
        for (style <- styles.get(componentClass)) {
          style.applyStyle(component)
        }
      })
    }
    component match {
      case panel: Container => {
        panel.getComponents().foreach(updateChildComponents(_, padding + "  "))
      }
      case _ => {}
    }
  }
}
