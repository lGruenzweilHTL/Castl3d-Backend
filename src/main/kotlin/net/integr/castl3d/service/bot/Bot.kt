package net.integr.castl3d.service.bot

import net.integr.castl3d.service.game.ChessBoard

open class Bot(val id: String, val name: String) {
    open fun move(board: ChessBoard) {
    }
}
