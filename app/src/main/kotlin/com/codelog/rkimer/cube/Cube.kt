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
    C55,

    /**
     * Enum value representing a 2x2 Rubik's cube
     */
    C22;

    /**
     * Returns a string representing the name of the puzzle described
     * by this enum value
     */
    fun cubeName(): String =
        when (this) {
            C22 -> "2x2"
            C33 -> "3x3"
            C44 -> "4x4"
            C55 -> "5x5"
        }

    /**
     * Returns the dimensions of the cube in question
     * (e.g. 2x2 = 2, 3x3 = 3, etc.)
     */
    fun cubeSize(): Int =
        when (this) {
            C22 -> 2
            C33 -> 3
            C44 -> 4
            C55 -> 5
        }

    /**
     * Returns an integer describing the order in which cubes are considered with 2x2 being 0
     */
    fun cubeRank(): Int =
        when (this) {
            C22 -> 0
            C33 -> 1
            C44 -> 2
            C55 -> 3
        }
}