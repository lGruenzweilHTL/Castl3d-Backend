package net.integr.castl3d.service.game

import net.integr.castl3d.service.bot.Bot

class Game(val bot: Bot) {
    fun start() {
        bot.init()
    }

    fun move() {
        bot.move()
    }
}