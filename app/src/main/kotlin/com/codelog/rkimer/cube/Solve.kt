package com.codelog.rkimer.cube

import org.json.JSONException
import org.json.JSONObject
import kotlin.math.truncate

fun timeToStr(time: Int): String {
    val millis = time % 100
    val seconds = truncate(time.toDouble() / 100.0).toInt() % 60
    val minutes = truncate(time.toDouble() / 6000.0).toInt() % 60

    val millisStr = if (millis < 10) "0$millis" else "$millis"
    val secondsStr = if (seconds < 10) "0$seconds" else "$seconds"
    val minutesStr = if (minutes < 10) "0$minutes" else "$minutes"

    return "$minutesStr:$secondsStr.$millisStr"
}

data class Solve(val time: Int, var dnf: Boolean, var plusTwo: Boolean, val scramble: Scramble? = null,
                 val cubeType: CubeType = CubeType.c33) {

    companion object {
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

            return Solve(time, dnf, plusTwo, scramble, cubeType ?: CubeType.c33)
        }
    }

    fun actualTime(): Int =
        if (plusTwo)
            time + 200
        else
            time

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