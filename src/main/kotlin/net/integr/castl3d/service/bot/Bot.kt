package net.integr.castl3d.service.bot

import net.integr.castl3d.service.game.ChessBoard

/**
 * Base class for all bots.
 * @param id the id of the bot
 * @param name the name of the bot
 * @constructor creates a new bot with the given id and name
 */
open class Bot(val id: String, val name: String) {
    /**
     * Make a move on the board.
     * @param board the board to make a move on
     */
    open fun move(board: ChessBoard) {
    }
}
