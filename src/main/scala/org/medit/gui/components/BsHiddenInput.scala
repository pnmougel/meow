package org.medit.gui.components

import com.alee.laf.text.WebTextField
import org.medit.gui.utils.SwingEvents._

/**
 * Created by nico on 17/09/15.
 */
class BsHiddenInput extends WebTextField {
  setDrawBorder(false)
  setName("hidden-input")
  setHideInputPromptOnFocus(false)

  this.onEnter(_ => {
    if(isEditable) {
      setDrawBorder(true)
    }
  })
  this.onExit(_ => {
    if((!hasFocus || (hasFocus && getText().isEmpty)) && isEditable) {
      setDrawBorder(false)
    }
  })
  this.onFocusLost(_ => {
    if(isEditable) {
      setDrawBorder(false)
    }
  })
}
