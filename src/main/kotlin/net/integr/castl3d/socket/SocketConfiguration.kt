package net.integr.castl3d.socket

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import java.util.logging.Logger


@Configuration
@EnableWebSocketMessageBroker
class SocketConfiguration : WebSocketMessageBrokerConfigurer {
    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/private", "/public")
        config.setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/bot_socket").setAllowedOrigins("*")
        registry.addEndpoint("/bot_socket").setAllowedOrigins("*").withSockJS()
    }
}

@Component
class WebSocketEventListener @Autowired constructor(private val messagingTemplate: SimpMessageSendingOperations) {

    @EventListener
    fun handleWebSocketConnectListener(event: SessionConnectedEvent) {
        Logger.getLogger("WebSocketHandler").info("Received a new web socket connection")

        val headerAccessor = StompHeaderAccessor.wrap(event.message)

        Logger.getLogger("WebSocketHandler").info("user connected")
        messagingTemplate.convertAndSend("/public/abc", "Connect")
    }

    @EventListener
    fun handleWebSocketDisconnectListener(event: SessionDisconnectEvent) {
        val headerAccessor = StompHeaderAccessor.wrap(event.message)

        Logger.getLogger("WebSocketHandler").info("user disconnected")
        Thread.sleep(2000)
        messagingTemplate.convertAndSend("/public/abc", "Disconnect")

    }
}