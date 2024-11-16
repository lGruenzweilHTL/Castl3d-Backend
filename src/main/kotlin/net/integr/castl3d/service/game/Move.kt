@file:Suppress("MemberVisibilityCanBePrivate")

package net.integr.castl3d.service.game

/**
 * Represents a move in a game.
 * @param from the coordinate of the piece to move
 * @param to the coordinate to move the piece to
 * @param isCapture true if the move is a capture, false otherwise
 * @see Coordinate
 */
class Move(val from: Coordinate, val to: Coordinate, val isCapture: Boolean) {
    /**
     * Returns a string representation of the move.
     */
    override fun toString(): String {
        return "Move(from=$from, to=$to)"
    }
}