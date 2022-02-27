package com.codelog.rkimer.util

import javafx.scene.control.ButtonType
import javafx.scene.control.TextInputDialog

class TextInputDialogFactory {
    companion object {
        fun makeTextBoxDialog(): TextInputDialog {
            val dialog = TextInputDialog()
            dialog.headerText = "Please input a time in the format (mm:ss.xx, e.g. 00:30.04)"
            dialog.title = "Time Input"

            return dialog
        }
    }
}