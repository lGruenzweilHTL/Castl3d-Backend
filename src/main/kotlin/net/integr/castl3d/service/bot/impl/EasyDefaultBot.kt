package net.integr.castl3d.service.bot.impl

import net.integr.castl3d.Constants
import net.integr.castl3d.service.bot.Bot
import net.integr.castl3d.service.game.ChessBoard

class EasyDefaultBot : Bot("easy_default", "Easy") {
    override fun init() {

    }

    override fun move(board: ChessBoard) {
        if (board.get(0, 0).piece == Constants.Piece.KING) {
        }
    }
}