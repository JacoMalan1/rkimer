package com.codelog.rkimer.util

import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog

class ConfirmationDialogFactory {
    companion object {
        fun yesNoDialog(message: String, title: String, hasCancel: Boolean = true): Dialog<Boolean> {
            val dialog = Dialog<Boolean>()
            dialog.title = title
            dialog.headerText = message
            dialog.dialogPane.buttonTypes.addAll(ButtonType.NO, ButtonType.YES)
            if (hasCancel)
                dialog.dialogPane.buttonTypes.addAll(ButtonType.CANCEL)

            dialog.setResultConverter {
                when (it) {
                    ButtonType.CANCEL -> null
                    ButtonType.YES -> true
                    ButtonType.NO -> false
                    else -> null
                }
            }

            return dialog
        }
    }
}