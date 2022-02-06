package com.codelog.rkimer.cube

import kotlin.random.Random

enum class Move {
    R, L, U, D, B, F,
    Rp, Lp, Up, Dp, Bp, Fp,
    R2, L2, U2, D2, B2, F2
}

data class Scramble(val moves: Array<Move>) {
    override fun toString(): String {
        val builder = StringBuilder()
        for (move in moves) {
            builder.append(move.toString().replace('p', '\''))
            builder.append(' ')
        }
        val result = builder.toString()
        return result.substring(0, result.length - 1)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Scramble

        if (!moves.contentEquals(other.moves)) return false

        return true
    }

    override fun hashCode(): Int {
        return moves.contentHashCode()
    }
}

class ScrambleFactory {
    companion object {
        fun generateScramble(length: Int): Scramble {
            val moves = Array(length) { _ -> Move.R }
            val rand = Random(System.nanoTime())

            val validMoves: MutableList<Move> = ArrayList()
            validMoves.addAll(Move.values())
            for (i in 0 until length) {
                val selectedMove = validMoves[rand.nextInt(0, validMoves.size)]
                moves[i] = selectedMove

                validMoves.clear()
                for (move in Move.values()) {
                    if (move.ordinal % 6 != selectedMove.ordinal % 6) {
                        val invalidMove = if (selectedMove.ordinal % 2 == 0)
                            selectedMove.ordinal + 1 else selectedMove.ordinal - 1
                        if (move.ordinal % 6 != invalidMove % 6)
                            validMoves.add(move)
                    }
                }
            }

            return Scramble(moves)
        }
    }
}