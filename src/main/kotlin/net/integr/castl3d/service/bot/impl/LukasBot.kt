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

package net.integr.castl3d.service.bot.impl

import net.integr.castl3d.service.bot.Bot
import net.integr.castl3d.service.game.ChessBoard
import net.integr.castl3d.service.game.management.Castl3dBot

/*
    * TODO:
    * - Implement endgame strategy in eval (force king to corner)
    * - Transposition table
    * - Piece-square tables
    * - Fix search function using board.testFor
    * - Iterative deepening
    * - King safety
    * - Pawn structure
 */


@Castl3dBot("lukas", "Lukas")
class EasyDefaultBot : Bot {
    override fun move(board: ChessBoard) {
        val moves = board.getAllValidBotMoves()

        if (moves.isNotEmpty()) {
            for (move in moves) {
                if (move.isCapture) {
                    board.move(move)
                    return
                }
            }

            val move = moves.random()
            board.move(move)
        }
    }

    fun search(board: ChessBoard, depth: Int, alpha: Int, beta: Int): Int {
        if (depth == 0) {
            return searchCaptures(board, alpha, beta)
        }

        val moves = board.getAllValidBotMoves()
        if (moves.isEmpty()) {
            return if (board.isCheck(board.currentPlayer)) Int.MIN_VALUE else 0
        }

        for (move in moves) {
            board.move(move)
            val score = -search(board, depth - 1, -beta, -alpha)
            board.undoMove()

            // The last move was too good, so the opponent will never allow it
            if (score >= beta) {
                return beta
            }

            // The last move was good, so we will play it
            if (score > alpha) {
                alpha = score
            }
        }

        return alpha
    }

    fun searchCaptures(board: ChessBoard, alpha: Int, beta: Int): Int {
        int eval = evaluate (board)

        if (eval >= beta) {
            return beta
        }
        alpha = max(alpha, eval)

        val moves = with(board.getAllValidBotMoves()) {
            filter { it.isCapture }
        }
        moves = moves.sortedByDescending { guessMoveScore(it) }

        for (move in moves) {
            board.move(move)
            eval = -searchCaptures(board, -beta, -alpha)
            board.undoMove()

            if (score >= beta) {
                return beta
            }

            if (score > alpha) {
                alpha = score
            }
        }
        return alpha
    }

    fun guessMoveScore(move: Move): Int {
        var scoreGuess = 0
        var movedPiece = Piece.PieceType(Board.Square[move.from])
        var capturedPiece = Piece.PieceType(Board.Square[move.to])

        // Prioritize capturing opponent's valuable pieces with less valuable pieces
        if (capturedPiece != Piece.PieceType.NONE) {
            scoreGuess = 10 * capturedPiece.pieceValue() - movedPiece.pieceValue()
        }

        // Promoting pawns is also often a good move
        if (move.isPromotion) {
            scoreGuess += move.PromotionPieceType.pieceValue();
        }

        // Penalize moving our pieces to a square where they can be captured
        // TODO: Implement this
        /*
        if (Board.OppentPawnAttacks[move.to] != 0) {
            scoreGuess -= movedPiece.pieceValue();
         */

        return scoreGuess
    }

    fun forceKingToCornerEndgame(board: ChessBoard, endgameWeight: Float): Int {
        var eval = 0

        // Favour positions, where the opponent's king is in the corner
        // This is a common endgame strategy
        val oppenentKingRank = board.getBotKingPosition().rank
        val oppenentKingFile = board.getBotKingPosition().file

        var oppenentKingCenterDistanceFile = max(3 - oppenentKingFile, oppenentKingFile - 4)
        var oppenentKingCenterDistanceRank = max(3 - oppenentKingRank, oppenentKingRank - 4)
        var oppenentKingCenterDistance = oppenentKingCenterDistanceFile + oppenentKingCenterDistanceRank
        eval += oppenentKingCenterDistance


        // Move our king closer to the opponent's king to help cut off its movement
        val botKingRank = board.getBotKingPosition().rank
        val botKingFile = board.getBotKingPosition().file

        var kingDistanceFile = abs(oppententKingFile - botKingFile)
        var kingDistanceRank = abs(oppententKingRank - botKingRank)
        var kingDistance = kingDistanceFile + kingDistanceRank
        eval += 14 - kingDistance

        return (eval * 10 * endgameWeight).toInt()
    }

    fun evaluate(board: ChessBoard): Int {
        var whiteScore = countMaterial(board, Constants.Color.WHITE)
        var blackScore = countMaterial(board, Constants.Color.BLACK)

        var eval whiteScore-blackScore

        var perspective = if (board.currentPlayer == Constant.Color.WHITE) 1 else -1
        return eval * perspective
    }

    fun countMaterial(board: ChessBoard, int color): Int {
        var score = 0
        for (piece in board.pieces) {
            if (piece.color == color) {
                score += piece.value
            }
        }
        return score
    }
}