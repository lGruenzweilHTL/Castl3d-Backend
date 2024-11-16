package net.integr.castl3d.service.game.management

import net.integr.castl3d.service.bot.management.BotManager
import net.integr.castl3d.service.game.Game
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Service
import java.security.Principal

@Service
class GameManager @Autowired constructor(private val botManager: BotManager, private val messagingTemplate: SimpMessageSendingOperations) {
    val games = mutableMapOf<String, Game>()

    fun startGame(id: String, user: Principal): Game {
        val bot = botManager.bootInstance(id, user)
        val game = Game(bot)
        game.messagingTemplate = messagingTemplate
        game.user = user

        games[user.name] = game
        return game
    }

    fun getGame(user: Principal): Game? {
        return games[user.name]
    }
}