package com.codelog.rkimer.cube

import javafx.scene.image.Image
import com.codelog.rkimer.Reference
import com.codelog.rkimer.util.KLoggerContext as Logger

/**
 * An ImageProvider to provide an Image representation of a
 * Scramble object from a predefined URL.
 */
class ScrambleImageProvider(val scramble: Scramble, private val width: Double, private val height: Double,
                            private val smooth: Boolean = true, private val backgroundLoading: Boolean = true): ImageProvider {

    /**
     * Synchronously fetches the image associated with the ScrambleImageProvider
     */
    override fun provide(): Image {
        val scrambleStr = scramble.toString().replace(" ", "")
        val cubeTypeStr = (scramble.cubeType.ordinal + 3).toString()
        val img = Image(
            Reference.SCRAMBLE_IMAGE_URL + "&pzl=$cubeTypeStr&alg=" + scrambleStr,
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