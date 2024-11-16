package net.integr.castl3d.service.game

import net.integr.castl3d.Constants

class ChessFieldPieceData(var piece: Int, var color: Int, var moveCount: Int, var hasJustMoved: Boolean) {
    fun isWhite(): Boolean {
        return color == Constants.Color.WHITE
    }

    fun isBlack(): Boolean {
        return color == Constants.Color.BLACK
    }

    fun isPawn(): Boolean {
        return piece == Constants.Piece.PAWN
    }

    fun isRook(): Boolean {
        return piece == Constants.Piece.ROOK
    }

    fun isKnight(): Boolean {
        return piece == Constants.Piece.KNIGHT
    }

    fun isBishop(): Boolean {
        return piece == Constants.Piece.BISHOP
    }

    fun isQueen(): Boolean {
        return piece == Constants.Piece.QUEEN
    }

    fun isKing(): Boolean {
        return piece == Constants.Piece.KING
    }

    fun isEmpty(): Boolean {
        return piece == Constants.Piece.NONE
    }

    fun isPiece(piece: Int): Boolean {
        return this.piece == piece
    }
}