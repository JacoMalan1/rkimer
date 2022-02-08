package com.codelog.rkimer.util

import com.codelog.clogg.Logger

object KLoggerContext {
    private val instance: Logger = Logger.getInstance()

    fun exception(e: Exception) {
        instance.exception(e)
    }

    fun info(message: String) {
        instance.info(message)
    }

    fun debug(message: String) {
        instance.debug(message)
    }

    fun error(message: String) {
        instance.error(message)
    }

    fun warn(message: String) {
        instance.warn(message)
    }
}