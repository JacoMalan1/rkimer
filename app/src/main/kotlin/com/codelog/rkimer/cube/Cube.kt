package com.codelog.rkimer.cube

/**
 * An enum class representing the supported puzzle types in RKimer
 */
enum class CubeType {
    /**
     * Enum value representing a 3x3 Rubik's cube
     */
    C33,

    /**
     * Enum value representing a 4x4 Rubik's cube
     */
    C44,

    /**
     * Enum value representing a 5x5 Rubik's cube
     */
    C55;

    /**
     * Returns a string representing the name of the puzzle described
     * by this enum value
     */
    fun cubeName(): String =
        when (this) {
            C33 -> "3x3"
            C44 -> "4x4"
            C55 -> "5x5"
        }
}