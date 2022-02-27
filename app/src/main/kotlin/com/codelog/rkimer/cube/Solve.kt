package com.codelog.rkimer.cube

import org.json.JSONException
import org.json.JSONObject
import kotlin.math.truncate

/**
 * Returns a String representation of a delta-time
 * @param time The time in centi-seconds (Hundredths of a second)
 */
fun timeToStr(time: Int): String {
    val millis = time % 100
    val seconds = truncate(time.toDouble() / 100.0).toInt() % 60
    val minutes = truncate(time.toDouble() / 6000.0).toInt() % 60

    val millisStr = if (millis < 10) "0$millis" else "$millis"
    val secondsStr = if (seconds < 10) "0$seconds" else "$seconds"
    val minutesStr = if (minutes < 10) "0$minutes" else "$minutes"

    return "$minutesStr:$secondsStr.$millisStr"
}

fun strToTime(str: String): Int {
    if (!str.matches("\\d\\d:\\d\\d\\.\\d\\d".toRegex()))
        return -1

    val colon = str.indexOf(':')
    val point = str.indexOf('.')


    val mm = str.substring(0, colon).toIntOrNull()
    val ss = str.substring(colon + 1, point).toIntOrNull()
    val ms = str.substring(point + 1).toIntOrNull()

    if (mm == null || ss == null || ms == null)
        return -1

    return (mm * 60 + ss) * 100 + ms
}

data class Solve(val time: Int, var dnf: Boolean, var plusTwo: Boolean, val scramble: Scramble? = null,
                 val cubeType: CubeType = CubeType.C33) {

    companion object {
        /**
         * Converts a JSON object into a Solve object
         */
        fun fromJSONObject(json: JSONObject): Solve {
            if (!json.has("time") || !json.has("dnf") || !json.has("plusTwo"))
                throw JSONException("Missing values")

            val time = json.getInt("time")
            val dnf = json.getBoolean("dnf")
            val plusTwo = json.getBoolean("plusTwo")

            var scramble: Scramble? = null
            if (json.has("scramble"))
                scramble = Scramble.fromJSONArray(json.getJSONArray("scramble"))

            var cubeType: CubeType? = null
            if (json.has("cubeType"))
                cubeType = CubeType.values()[json.getInt("cubeType")]

            return Solve(time, dnf, plusTwo, scramble, cubeType ?: CubeType.C33)
        }
    }

    /**
     * Returns the solve time (includes +2 flag)
     */
    fun actualTime(): Int =
        if (plusTwo)
            time + 200
        else
            time

    /**
     * Returns a JSON representation of a Solve object
     */
    fun serialize(): JSONObject {
        val result = JSONObject()

        result.put("time", time)
        result.put("dnf", dnf)
        result.put("plusTwo", plusTwo)
        result.put("cubeType", cubeType.ordinal)

        if (scramble != null)
            result.put("scramble", scramble.toJSONArray())

        return result
    }

    /**
     * Returns a String representing a Solve Object
     */
    override fun toString(): String {
        var actualTime = time
        if (plusTwo)
            actualTime += 200
        var result = timeToStr(actualTime)
        if (dnf)
            result += " (DNF)"

        return result
    }
}