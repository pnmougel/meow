package org.medit.gui.panels

import java.awt.BorderLayout
import javax.swing.{JLabel, JPanel}

import com.alee.laf.label.WebLabel
import com.alee.laf.panel.WebPanel
import org.medit.gui.folders.FolderPanel
import org.medit.gui.tasks._
import org.medit.gui.utils.{Gnome, Icon}
import org.medit.gui.utils.SwingEvents._

object LeftMenu extends WebPanel(new BorderLayout) {
  setBackground(null)
  val titlePanel = new WebPanel(new BorderLayout())
  titlePanel.setBackground(null)
  val appTitle = new WebLabel(" Meow", Icon.get("cat", 30))
  appTitle.setName("appname")
  appTitle.onClick(_ => Gnome.reload)
  val appDescription = new WebLabel("Application manager for GNOME")
  appDescription.setName("app-description")
  titlePanel.add(appTitle, BorderLayout.CENTER)
  titlePanel.add(appDescription, BorderLayout.SOUTH)

  val menuContent = new VerticalPanel()

  val addAppLabel = new WebLabel("Create an application launcher")
  addAppLabel.setName("task-name")

  val tasksLabel = new WebLabel("Tasks")
  tasksLabel.setName("header-label")

  val foldersLabel = new WebLabel("Folders")
  foldersLabel.setName("header-label")

  menuContent.setMargin(0, 10, 0, 10)
  menuContent.addComponent(tasksLabel)
  menuContent.addComponent(addAppLabel)
//  menuContent.addComponent(new TaskPanel("Create an application folder", new AddFolderPanel()))
  menuContent.addComponent(new TaskPanel("Filter the applications", FiltersPanel))

  menuContent.addComponent(foldersLabel)
  menuContent.addComponent(FolderPanel)
  menuContent.addFiller()

  add(titlePanel, BorderLayout.NORTH)
  add(menuContent.inVerticalScroll, BorderLayout.CENTER)
}
