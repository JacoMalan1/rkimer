package com.codelog.rkimer.util

import javafx.scene.control.Alert

/**
 * A utility class for displaying alerts to the user.
 */
class AlertFactory {
    companion object {
        /**
         * Shows an alert message to the user.
         * @param message The message to display
         * @param type The type of alert to display
         */
        fun showAlert(message: String, type: Alert.AlertType = Alert.AlertType.INFORMATION) {
            val alert = Alert(type, message)
            alert.show()
        }

        /**
         * Shows an alert message to the user and waits for them to
         * click on 'OK'
         * @param message The message to display
         * @param type The type of alert to display
         */
        fun showAndWait(message: String, type: Alert.AlertType = Alert.AlertType.INFORMATION) {
            val alert = Alert(type, message)
            alert.showAndWait()
        }
    }
}