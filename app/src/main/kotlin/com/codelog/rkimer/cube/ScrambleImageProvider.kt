package com.codelog.rkimer.cube

import javafx.scene.image.Image
import com.codelog.rkimer.Reference
import com.codelog.rkimer.util.KLoggerContext as Logger

class ScrambleImageProvider(val scramble: Scramble, private val width: Double, private val height: Double,
                            private val smooth: Boolean = true, private val backgroundLoading: Boolean = true): ImageProvider {

    override fun provide(): Image {
        val scrambleStr = scramble.toString().replace(" ", "")
        val img = Image(
            Reference.SCRAMBLE_IMAGE_WEBSITE + scrambleStr,
            width, height,
            false, smooth, backgroundLoading
        )

        if (img.isError) {
            Logger.error(img.exception.message ?: "ERROR")
            Logger.exception(img.exception)
        }

        return img
    }
}