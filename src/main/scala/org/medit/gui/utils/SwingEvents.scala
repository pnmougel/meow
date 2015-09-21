package org.medit.gui.utils

import java.awt.event._
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.event.{DocumentEvent, ChangeListener, ChangeEvent}
import javax.swing.JSlider
import javax.swing.JTextField
import com.alee.utils.swing.DocumentChangeListener
import org.medit.gui.components.BsButton

/**
 * This class automatically add methods to swing components to handle the most common events
 */
object SwingEvents {
    implicit def convertToActionable(c: JButton) = new SActionable(c)
    implicit def convertToActionable(c: JTextField) = new SActionable(c)
    implicit def convertToActionable(c: BsButton) = new SActionable(c)
//    implicit def convertToActionable(c: RoundedSwitch) = new SActionable(c)
//    implicit def convertToActionable(c: WebComboBox) = new SActionable(c)
    implicit def convertToActionable(c: JCheckBox) = new SActionable(c)
    class SActionable(c : JComponent) {
        val actionHandler = (c.getClass().getMethods().toList ::: c.getClass().getDeclaredMethods().toList).find(e => e.getName() == "addActionListener")
        def onAction(f : ActionEvent => Unit) = {
            actionHandler.foreach { a =>
                val actionListener = new Array[ActionListener](1)
                actionListener(0) = new ActionListener() { def actionPerformed(e : ActionEvent) : Unit = { f(e) } }
                a.invoke(c, actionListener : _*)
            }
        }
	}


    implicit def convertToChangeable(c: JSlider) = new SChangeable(c)
    class SChangeable(c : JComponent) {
        val actionHandler = (c.getClass().getMethods().toList ::: c.getClass().getDeclaredMethods().toList).find(e => e.getName() == "addChangeListener")
        def onChange(f : ChangeEvent => Unit) = {
            actionHandler.foreach { a =>
                val changeListener = new Array[ChangeListener](1)
                changeListener(0) = new ChangeListener() { def stateChanged(e : ChangeEvent) : Unit = { f(e) } }
                a.invoke(c, changeListener : _*)
            }
        }
	  }


  implicit def convertToDocument(c: JTextField) = new SJTextField(c)
  class SJTextField(c : JTextField) {
    def onTextChange(f : DocumentEvent => Unit) = {
      c.getDocument.addDocumentListener(new DocumentChangeListener {
        override def documentChanged(p1: DocumentEvent): Unit = { f(p1) }
      })
    }
  }

  implicit def convertComponent(c : JComponent) = new SComponent(c)
    class SComponent(c : JComponent) {
        // Mouse click
	    def onClick(f : MouseEvent => Unit) = { c.addMouseListener(new MouseAdapter() { override def mouseClicked(e : MouseEvent) : Unit = { f(e) } }) }
	    def onRelease(f : MouseEvent => Unit) = { c.addMouseListener(new MouseAdapter() { override def mouseReleased(e : MouseEvent) : Unit = { f(e) } }) }
	    def onPress(f : MouseEvent => Unit) = { c.addMouseListener(new MouseAdapter() { override def mousePressed(e : MouseEvent) : Unit = { f(e) } }) }

	    def onExit(f : MouseEvent => Unit) = { c.addMouseListener(new MouseAdapter() { override def mouseExited(e : MouseEvent) : Unit = { f(e) } }) }
	    def onEnter(f : MouseEvent => Unit) = { c.addMouseListener(new MouseAdapter() { override def mouseEntered(e : MouseEvent) : Unit = { f(e) } }) }

      def onFocusLost(f : FocusEvent => Unit) = { c.addFocusListener(new FocusAdapter { override def focusLost(e : FocusEvent) : Unit = { f(e) } }) }
      def onFocusGained(f : FocusEvent => Unit) = { c.addFocusListener(new FocusAdapter { override def focusGained(e : FocusEvent) : Unit = { f(e) } }) }

      def onResize(f : ComponentEvent => Unit) = { c.addComponentListener(new ComponentAdapter() { override def componentResized(e : ComponentEvent) : Unit = { f(e) } }) }
      def onComponentMoved(f : ComponentEvent => Unit) = { c.addComponentListener(new ComponentAdapter() { override def componentMoved(e : ComponentEvent) : Unit = { f(e) } }) }

	    // Mouse drag
	    def onDrag(f : MouseEvent => Unit) = { c.addMouseMotionListener(new MouseMotionAdapter() { override def mouseDragged(e : MouseEvent) : Unit = { f(e) } }) }
	    def onMove(f : MouseEvent => Unit) = { c.addMouseMotionListener(new MouseMotionAdapter() { override def mouseMoved(e : MouseEvent) : Unit = { f(e) } }) }

	    // Mouse wheel
	    def onMouseWheelMove(f : MouseWheelEvent => Unit) = {c.addMouseWheelListener(new MouseWheelListener() { def mouseWheelMoved(e : MouseWheelEvent) : Unit = {f(e)} } )}
	    // Keys
	    def onKeyPress(f : KeyEvent => Unit) = {c.addKeyListener(new KeyAdapter() { override def keyPressed(e : KeyEvent) : Unit = {f(e)} } )}
	    def onKeyRelease(f : KeyEvent => Unit) = {c.addKeyListener(new KeyAdapter() { override def keyReleased(e : KeyEvent) : Unit = {f(e)} } )}
	    def onKeyType(f : KeyEvent => Unit) = {c.addKeyListener(new KeyAdapter() { override def keyTyped(e : KeyEvent) : Unit = {f(e)} } )}
      def onKeyKeyEnter(f : KeyEvent => Unit) = {c.addKeyListener(new KeyAdapter() { override def keyPressed(e : KeyEvent) : Unit = {
        if(e.getKeyCode == KeyEvent.VK_ENTER) f(e)
      } } )}

	}
}