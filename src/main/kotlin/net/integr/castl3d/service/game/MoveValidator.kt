/*
 * Copyright © 2024 Integr
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
            Constants.Piece.PAWN -> getValidMovesPawn(x, y).filter { !willHitFriendly(x, y, it.first, it.second) }
            Constants.Piece.ROOK -> getValidMovesRook(x, y).filter { !willHitFriendly(x, y, it.first, it.second) }
            Constants.Piece.KNIGHT -> getValidMovesKnight(x, y).filter { !willHitFriendly(x, y, it.first, it.second) }
            Constants.Piece.BISHOP -> getValidMovesBishop(x, y).filter { !willHitFriendly(x, y, it.first, it.second) }
            Constants.Piece.QUEEN -> getValidMovesQueen(x, y).filter { !willHitFriendly(x, y, it.first, it.second) }
            Constants.Piece.KING -> getValidMovesKing(x, y).filter { !willHitFriendly(x, y, it.first, it.second) }
            else -> listOf()
        }
    }

    private fun willHitFriendly(x: Int, y: Int, i: Int, f: Int): Boolean {
        return boardContext.get(x, y).color == boardContext.get(i, f).color
    }

    private fun getValidMovesPawn(x: Int, y: Int): List<Pair<Int, Int>> {
        val moves: MutableList<Pair<Int, Int>> = mutableListOf()

        if (y == 6) {
            if (boardContext.get(x, y-1).piece == Constants.Piece.NONE) moves += x to y-1
            if (boardContext.get(x, y-2).piece == Constants.Piece.NONE) moves += x to y-2
        } else if (y > 0){
            if (boardContext.get(x, y-1).piece == Constants.Piece.NONE) moves += x to y-1
        }

        if (x > 0 && y > 0 && boardContext.get(x-1, y-1).piece != Constants.Piece.NONE) moves += x-1 to y-1
        if (x < 7  && y > 0 && boardContext.get(x+1, y-1).piece != Constants.Piece.NONE) moves += x+1 to y-1

        if (y == 1) {
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
                moves += i to j
            }
        }

        if (boardContext.get(x, y).moveCount == 0) {
            // Castle
            val leftRook = boardContext.get(0, y)
            val rightRook = boardContext.get(7, y)

            if (leftRook.piece == Constants.Piece.ROOK && leftRook.color == boardContext.get(x, y).color && leftRook.moveCount == 0) {
                var canCastle = true
                for (i in (1..<x)) {
                    if (boardContext.get(i, y).piece != Constants.Piece.NONE) {
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
                    }
                }

                if (canCastle) {
                    moves += x+2 to y
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
}