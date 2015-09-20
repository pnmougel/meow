package org.medit.gui.components

import javax.swing.JTextField
import javax.swing.JButton
import java.awt.BorderLayout
import java.awt.Font
import java.awt.Color
import javax.swing.border.EmptyBorder
import javax.swing.JLabel
import org.medit.gui.utils.SwingEvents._
import java.awt.event.KeyEvent

class BsInput extends RoundedPanel {
    setName("round-panel round-panel-input")
	val input = new JTextField()
	input.setColumns(10)
	input.setName("round-panel-input-field")
	add(input, BorderLayout.CENTER)
	
	val addLabel = new JLabel("+")
	addLabel.setForeground(Color.WHITE);
	addLabel.setFont(addLabel.getFont().deriveFont(Font.BOLD, 13))
	addLabel.setBorder(new EmptyBorder(0, 5, 0, 4))
	
	add(addLabel, BorderLayout.EAST)
	

	addLabel.onClick({ e =>
	    for(f <- functionOnAdd) { f(input.getText()) }
	    input.setText("")
    })
	input.onKeyPress(e => {
	    if(e.getKeyCode() == KeyEvent.VK_ENTER) {
	        for(f <- functionOnAdd) { f(input.getText()) }
	        input.setText("")
	    }
	})
	var functionOnAdd : Option[(String => Unit)] = None
	
	def onAdd(f : (String => Unit)) = {
	    functionOnAdd = Some(f)
	}
}