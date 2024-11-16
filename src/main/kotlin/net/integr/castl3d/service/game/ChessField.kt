@file:Suppress("unused")

package net.integr.castl3d.service.game

import net.integr.castl3d.Constants

/**
 * Represents a chess field piece data.
 * @param piece The piece.
 * @param color The color.
 * @param moveCount The move count.
 * @param hasJustMoved True if the piece has just moved, false otherwise.
 * @constructor Creates a new chess field piece data.
 * @property piece The piece.
 * @property color The color.
 * @property moveCount The move count.
 * @property hasJustMoved True if the piece has just moved, false otherwise.
 */
class ChessFieldPieceData(var piece: Int, var color: Int, var moveCount: Int, var hasJustMoved: Boolean) {
    /**
     * Returns true if the piece is white, false otherwise.
     */
    fun isWhite(): Boolean {
        return color == Constants.Color.WHITE
    }

    /**
     * Returns true if the piece is black, false otherwise.
     */
    fun isBlack(): Boolean {
        return color == Constants.Color.BLACK
    }

    /**
     * Returns true if the piece is a pawn, false otherwise.
     */
    fun isPawn(): Boolean {
        return piece == Constants.Piece.PAWN
    }

    /**
     * Returns true if the piece is a rook, false otherwise.
     */
    fun isRook(): Boolean {
        return piece == Constants.Piece.ROOK
    }

    /**
     * Returns true if the piece is a knight, false otherwise.
     */
    fun isKnight(): Boolean {
        return piece == Constants.Piece.KNIGHT
    }

    /**
     * Returns true if the piece is a bishop, false otherwise.
     */
    fun isBishop(): Boolean {
        return piece == Constants.Piece.BISHOP
    }

    /**
     * Returns true if the piece is a queen, false otherwise.
     */
    fun isQueen(): Boolean {
        return piece == Constants.Piece.QUEEN
    }

    /**
     * Returns true if the piece is a king, false otherwise.
     */
    fun isKing(): Boolean {
        return piece == Constants.Piece.KING
    }

    /**
     * Returns true if the piece is empty, false otherwise.
     */
    fun isEmpty(): Boolean {
        return piece == Constants.Piece.NONE
    }

    /**
     * Returns true if the piece is the same as the given piece, false otherwise.
     * @param piece The piece to compare with.
     */
    fun isPiece(piece: Int): Boolean {
        return this.piece == piece
    }
}