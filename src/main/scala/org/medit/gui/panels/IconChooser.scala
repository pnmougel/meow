package org.medit.gui.panels

import java.awt._
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing._
import javax.swing.border.EmptyBorder
import javax.swing.event.{ChangeEvent, ChangeListener}

import com.alee.extended.window.{PopOverDirection, WebPopOver}
import com.alee.laf.label.WebLabel
import com.alee.laf.panel.WebPanel
import com.alee.laf.text.WebTextField
import org.medit.core.entries.DesktopEntry
import org.medit.core.icons.{IconGenerator, IconLibrary}
import org.medit.gui.components.HorizontalScrollPane
import org.medit.gui.utils.SwingEvents._
import org.medit.gui.utils.{Colors, WrapLayout}

/**
 * Created by nico on 17/09/15.
 */
class IconChooser(entry: DesktopEntry, f: (String) => Unit) extends WebPanel(new BorderLayout()) {

  val loadingImage = new ImageIcon(IconGenerator.generateIcon("Loading", Colors.gray).getScaledInstance(40, 40, Image.SCALE_SMOOTH))
  val searchField = new WebTextField()
  val topPanel = new WebPanel(new BorderLayout())
  val iconsPanel = new JPanel(new WrapLayout(FlowLayout.LEADING))
  searchField.setText(entry.getName)
  searchField.setInputPrompt("Search an icon")
  searchField.setHideInputPromptOnFocus(false)
  searchField.setDrawFocus(false)
  searchField.setShadeWidth(0)
  searchField.setBackground(Colors.white)
  searchField.onTextChange(_ => {
    timer.restart()
  })
  val timer = new Timer(400, new ActionListener {
    override def actionPerformed(p1: ActionEvent): Unit = {
      updateIcons()
    }
  })
  topPanel.add(searchField, BorderLayout.CENTER)
  topPanel.setBackground(null)
  val scrollPanel = new HorizontalScrollPane(iconsPanel)
  iconsPanel.setBorder(new EmptyBorder(0, 0, 0, 0))
  iconsPanel.setBackground(null)
  val dimension = new Dimension(280, 230)
  timer.setRepeats(false)
  val b = new EmptyBorder(8, 0, 8, 0)
  var iconsList = scala.collection.immutable.List[String]()

  var popOver: Option[WebPopOver] = None
  def setVisible(component: JComponent) = {
    val popOver = new WebPopOver(component)
    this.popOver = Some(popOver)
    popOver.setMargin(10)
    popOver.add(this)
    popOver.setModal(false)
    popOver.setMovable(true)
    popOver.setShadeWidth(1)
    popOver.setBackground(Colors.white)
    popOver.setCloseOnFocusLoss ( true )
    makeWhiteBackground(popOver)
    popOver.getComponent(0).setBackground(Colors.transparent)
    popOver.setCornerWidth(0)
    popOver.show(component, PopOverDirection.left)
  }

  def makeWhiteBackground(component: Component): Unit = {
    component.setBackground(Colors.white)
    component match {
      case panel: Container => {
        for(c <- panel.getComponents) { makeWhiteBackground(c) }
      }
      case _ => {}
    }
  }

  scrollPanel.getViewport.addChangeListener(new ChangeListener {
    override def stateChanged(p1: ChangeEvent): Unit = {
      val scrollBar = scrollPanel.getVerticalScrollBar
      if (scrollBar.getValue + scrollBar.getVisibleAmount >= scrollBar.getMaximum - 60) {
        loadMoreIcons()
      }
    }
  })

  val noIconLabel = new WebLabel("No icons found with this name.")
  noIconLabel.setName("no-icon-label")
  def updateIcons() = {
    val searchText = searchField.getText
    if (!searchText.isEmpty) {
      iconsPanel.removeAll()
      iconsList = IconLibrary.searchIcon(searchText, entry.getIcon)
      loadMoreIcons()

      if (iconsPanel.getComponentCount == 0) {
        iconsPanel.add(noIconLabel)
      }
    }
    if (iconsPanel.getComponentCount == 0) {
      iconsPanel.add(noIconLabel)
    }
  }
  updateIcons()

  setPreferredSize(dimension)
  setMaximumSize(dimension)
  setMinimumSize(dimension)

  def loadMoreIcons() = {
    val (images, nextList) = IconLibrary.loadNextImages(iconsList, 60)
    iconsList = nextList
    val threads = for (imageLoader <- images) yield {
      val imageComponent = new JLabel(loadingImage)
      imageComponent.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
      imageComponent.setBackground(Colors.white)
      imageComponent.onClick(_ => {
        f(imageLoader.fileName)
        for(p <- popOver) { p.dispose() }
      })
      iconsPanel.add(imageComponent)

      imageLoader.onLoaded(optImage => {
        for (image <- optImage) {
          imageComponent.setIcon(image)
        }
        if (!optImage.isDefined) {
          imageComponent.setVisible(false)
        }
      })

      val th = new Thread(imageLoader)
      th.setPriority(Thread.MIN_PRIORITY)
      th
    }
    iconsPanel.doLayout()
    iconsPanel.repaint()
    scrollPanel.doLayout()
    scrollPanel.repaint()

    threads.map(_.start())
  }

  scrollPanel.setMargin(8, 0, 8, 0)
  add(scrollPanel, BorderLayout.CENTER)
  add(new IconGeneratorPanel(entry.getName, this, f), BorderLayout.SOUTH)
  add(topPanel, BorderLayout.NORTH)
  topPanel.setBackground(Colors.white)
  iconsPanel.setBackground(Colors.white)
  scrollPanel.setBackground(Colors.white)
  setForeground(Colors.white)
}
