package com.codelog.rkimer.util

import javafx.scene.control.Alert

class AlertFactory {
    companion object {
        fun showAlert(message: String, type: Alert.AlertType = Alert.AlertType.INFORMATION) {
            val alert = Alert(type, message)
            alert.show()
        }

        fun showAndWait(message: String, type: Alert.AlertType = Alert.AlertType.INFORMATION) {
            val alert = Alert(type, message)
            alert.showAndWait()
        }
    }
}