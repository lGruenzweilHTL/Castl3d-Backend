package net.integr.castl3d.service.bot.management

import net.integr.castl3d.service.bot.Bot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AssignableTypeFilter
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Service
import java.lang.reflect.Constructor
import java.security.Principal

@Service
class BotManager @Autowired constructor(private val messagingTemplate: SimpMessageSendingOperations) {
    private val botCache = mutableMapOf<String, Constructor<*>>()

    init {
        loadBotsToCache()
    }

    private final fun loadBotsToCache() {
        val scanner = ClassPathScanningCandidateComponentProvider(false)
        scanner.addIncludeFilter(AssignableTypeFilter(Bot::class.java))

        val botClasses = scanner.findCandidateComponents("net.integr.castl3d.service.bot.impl")
            .map { Class.forName(it.beanClassName) }

        botClasses.forEach {
            val bot = it.constructors.first().newInstance() as Bot
            botCache[bot.id] = it.constructors.first()
        }
    }

    fun bootInstance(botId: String, user: Principal): Bot {
        val botConstructor = botCache[botId] ?: throw IllegalArgumentException("Bot with id $botId not found")
        val bootedBot = botConstructor.newInstance() as Bot
        bootedBot.messagingTemplate = messagingTemplate
        bootedBot.user = user
        return bootedBot
    }
}