package com.codelog.rkimer.util

import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog

/**
 * A utility class for displaying Confirmation Dialogs
 */
class ConfirmationDialogFactory {
    companion object {
        /**
         * Displays a dialog with "Yes", "No" and (optionally) "Cancel" buttons
         * @param message The prompt text
         * @param title The title for the prompt window
         * @param hasCancel Whether or not the prompt should have a "Cancel" button
         */
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