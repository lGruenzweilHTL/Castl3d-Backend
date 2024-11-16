@file:Suppress("DuplicatedCode")

package net.integr.castl3d.service.game

import net.integr.castl3d.Constants

class MoveValidator(private val boardContext: ChessBoard) {
    fun getValidMoves(x: Int, y: Int): List<Pair<Int, Int>> {
        val data = boardContext.get(x, y)
        if (data.piece == Constants.Piece.NONE) {
            return listOf()
        }

        return when (data.piece) {
            Constants.Piece.PAWN -> getValidMovesPawn(x, y)
            Constants.Piece.ROOK -> getValidMovesRook(x, y)
            Constants.Piece.KNIGHT -> getValidMovesKnight(x, y)
            Constants.Piece.BISHOP -> getValidMovesBishop(x, y)
            Constants.Piece.QUEEN -> getValidMovesQueen(x, y)
            Constants.Piece.KING -> getValidMovesKing(x, y)
            else -> listOf()
        }
    }

    private fun getValidMovesPawn(x: Int, y: Int): List<Pair<Int, Int>> {
        val moves: MutableList<Pair<Int, Int>> = mutableListOf()

        if (y == 7) {
            if (boardContext.get(x, y-1).piece == Constants.Piece.NONE) moves += x to y-1
            if (boardContext.get(x, y-2).piece == Constants.Piece.NONE) moves += x to y-2
        } else {
            if (boardContext.get(x, y-1).piece == Constants.Piece.NONE) moves += x to y-1
        }

        if (x > 0 && boardContext.get(x-1, y-1).piece == Constants.Piece.NONE) moves += x-1 to y-1
        if (x < 7 && boardContext.get(x+1, y-1).piece == Constants.Piece.NONE) moves += x+1 to y-1

        if (y == 2) {
            if (x > 0 && boardContext.get(x-1, y).piece == Constants.Piece.PAWN && boardContext.get(x-1, y+1).piece == Constants.Piece.NONE && boardContext.get(x-1, y).moveCount == 1 && boardContext.get(x-1, y).hasJustMoved) moves += x-1 to y-1
            if (x < 7 && boardContext.get(x+1, y).piece == Constants.Piece.PAWN && boardContext.get(x+1, y+1).piece == Constants.Piece.NONE && boardContext.get(x+1, y).moveCount == 1 && boardContext.get(x+1, y).hasJustMoved) moves += x+1 to y-1
        }

        return moves
    }

    private fun getValidMovesRook(x: Int, y: Int): List<Pair<Int, Int>> {
        val moves: MutableList<Pair<Int, Int>> = mutableListOf()

        val directions: List<Pair<Int, Int>> = listOf(
            -1 to 0, 1 to 0, 0 to -1, 0 to 1
        )

        for ((dx, dy) in directions) {
            var i = x + dx
            var j = y + dy
            while (i in 0..7 && j >= 0 && j < 8) {
                moves += i to j
                if (boardContext.get(i, j).piece != Constants.Piece.NONE) break
                i += dx
                j += dy
            }
        }

        return moves
    }

    private fun getValidMovesKing(x: Int, y: Int): List<Pair<Int, Int>> {
        val moves: MutableList<Pair<Int, Int>> = mutableListOf()

        val directions: List<Pair<Int, Int>> = listOf(
            -1 to -1, 1 to -1, -1 to 1, 1 to 1,
            -1 to 0, 1 to 0, 0 to -1, 0 to 1
        )

        for ((dx, dy) in directions) {
            val i = x + dx
            val j = y + dy
            if (i in 0..7 && j >= 0 && j < 8) {
                if (coordinateCanBeHit(i, j, boardContext.get(x, y).color)) continue
                moves += i to j
            }
        }

        if (boardContext.get(x, y).moveCount == 0) {
            if (!coordinateCanBeHit(x, y, boardContext.get(x, y).color)) {
                // Castle
                val leftRook = boardContext.get(0, y)
                val rightRook = boardContext.get(7, y)

                if (leftRook.piece == Constants.Piece.ROOK && leftRook.color == boardContext.get(x, y).color && leftRook.moveCount == 0) {
                    var canCastle = true
                    for (i in (1..<x)) {
                        if (boardContext.get(i, y).piece != Constants.Piece.NONE) {
                            canCastle = false
                            break
                        } else if (coordinateCanBeHit(i, y, boardContext.get(x, y).color)) {
                            canCastle = false
                            break
                        }
                    }

                    if (canCastle) {
                        moves += x-2 to y
                    }
                }

                if (rightRook.piece == Constants.Piece.ROOK && rightRook.color == boardContext.get(x, y).color && rightRook.moveCount == 0) {
                    var canCastle = true
                    for (i in (x+1..<7)) {
                        if (boardContext.get(i, y).piece != Constants.Piece.NONE) {
                            canCastle = false
                            break
                        } else if (coordinateCanBeHit(i, y, boardContext.get(x, y).color)) {
                            canCastle = false
                            break
                        }
                    }

                    if (canCastle) {
                        moves += x+2 to y
                    }
                }
            }
        }

        return moves
    }

    private fun getValidMovesKnight(x: Int, y: Int): List<Pair<Int, Int>> {
        val moves: MutableList<Pair<Int, Int>> = mutableListOf()

        if (x > 1 && y > 0) moves += x-2 to y-1
        if (x > 1 && y < 7) moves += x-2 to y+1
        if (x < 6 && y > 0) moves += x+2 to y-1
        if (x < 6 && y < 7) moves += x+2 to y+1
        if (x > 0 && y > 1) moves += x-1 to y-2
        if (x > 0 && y < 6) moves += x-1 to y+2
        if (x < 7 && y > 1) moves += x+1 to y-2
        if (x < 7 && y < 6) moves += x+1 to y+2

        return moves
    }

    private fun getValidMovesBishop(x: Int, y: Int): List<Pair<Int, Int>> {
        val moves: MutableList<Pair<Int, Int>> = mutableListOf()

        val directions: List<Pair<Int, Int>> = listOf(
            -1 to -1, 1 to -1, -1 to 1, 1 to 1
        )

        for ((dx, dy) in directions) {
            var  i = x + dx
            var j = y + dy
            while (i in 0..7 && j >= 0 && j < 8) {
                moves += i to j
                if (boardContext.get(i, j).piece != Constants.Piece.NONE) break
                i += dx
                j += dy
            }
        }

        return moves
    }

    private fun getValidMovesQueen(x: Int, y: Int): List<Pair<Int, Int>> {
        val moves: MutableList<Pair<Int, Int>> = mutableListOf()

        val directions: List<Pair<Int, Int>> = listOf(
            -1 to -1, 1 to -1, -1 to 1, 1 to 1,
            -1 to 0, 1 to 0, 0 to -1, 0 to 1
        )

        for ((dx, dy) in directions) {
            var i = x + dx
            var j = y + dy
            while (i in 0..7 && j >= 0 && j < 8) {
                moves += i to j
                if (boardContext.get(i, j).piece != Constants.Piece.NONE) break
                i += dx
                j += dy
            }
        }

        return moves
    }

    private fun coordinateCanBeHit(x: Int, y: Int, color: Int): Boolean {
        val directions: List<Pair<Int, Int>> = listOf(
            -1 to -1, 1 to -1, -1 to 1, 1 to 1,
            -1 to 0, 1 to 0, 0 to -1, 0 to 1
        )

        for ((dx, dy) in directions) {
            var i = x + dx
            var j = y + dy
            while (i in 0..7 && j >= 0 && j < 8) {
                val data = boardContext.get(i, j)
                if (data.piece != Constants.Piece.NONE) {
                    if (data.color == color) break
                    return true
                }

                i += dx
                j += dy
            }
        }

        val knightMoves: List<Pair<Int, Int>> = listOf(
            -2 to -1, -2 to 1, 2 to -1, 2 to 1,
            -1 to -2, -1 to 2, 1 to -2, 1 to 2
        )

        for ((dx, dy) in knightMoves) {
            val i = x + dx
            val j = y + dy
            if (i in 0..7 && j >= 0 && j < 8) {
                val data = boardContext.get(i, j)
                if (data.piece == Constants.Piece.KNIGHT && data.color != color) {
                    return true
                }
            }
        }

        val pawnMoves: List<Pair<Int, Int>> = listOf(
            -1 to -1, 1 to -1, -1 to 1, 1 to 1
        )

        for ((dx, dy) in pawnMoves) {
            val i = x + dx
            val j = y + dy
            if (i in 0..7 && j >= 0 && j < 8) {
                val data = boardContext.get(i, j)
                if (data.piece == Constants.Piece.PAWN && data.color != color) {
                    return true
                }
            }
        }

        return false
    }
}