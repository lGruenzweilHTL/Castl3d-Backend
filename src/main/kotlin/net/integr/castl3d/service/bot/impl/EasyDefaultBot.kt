package net.integr.castl3d.service.bot.impl

import net.integr.castl3d.service.bot.Bot
import net.integr.castl3d.service.game.ChessBoard
import net.integr.castl3d.service.game.management.Castl3dBot

@Castl3dBot
class EasyDefaultBot : Bot("easy_default", "Easy") {
    override fun move(board: ChessBoard) {

    }
}