@file:Suppress("unused")

package net.integr.castl3d.service.game

/**
 * Represents a coordinate on the board.
 * @param x The x coordinate.
 * @param y The y coordinate.
 * @constructor Creates a new coordinate.
 */
class Coordinate(val x: Int, val y: Int) {
    /**
     * Returns true if the coordinate is within the bounds of the board.
     */
    fun isValid(): Boolean {
        return x in 0..7 && y in 0..7
    }

    /**
     * Returns a string representation of the coordinate.
     */
    override fun toString(): String {
        return "(x=$x, y=$y)"
    }
}