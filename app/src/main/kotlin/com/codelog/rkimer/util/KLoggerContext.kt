package com.codelog.rkimer.util

import com.codelog.clogg.Logger

/**
 * A Kotlin wrapper for the CLogg Logger class
 */
object KLoggerContext {
    private val instance: Logger = Logger.getInstance()

    /**
     * Logs an exception
     */
    fun exception(e: Exception) {
        instance.exception(e)
    }

    /**
     * Logs a message with INFO severity
     */
    fun info(message: String) {
        instance.info(message)
    }

    /**
     * Logs a message with DEBUG severity
     */
    fun debug(message: String) {
        instance.debug(message)
    }

    /**
     * Logs a message with ERROR severity
     */
    fun error(message: String) {
        instance.error(message)
    }

    /**
     * Logs a message with WARNING severity
     */
    fun warn(message: String) {
        instance.warn(message)
    }
}