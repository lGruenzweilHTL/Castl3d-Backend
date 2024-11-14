package net.integr.castl3d.socket.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableScheduling
@EnableWebSocketMessageBroker
class SocketConfiguration : WebSocketMessageBrokerConfigurer {
    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/private", "/public")
        config.setApplicationDestinationPrefixes("/app")
        config.setUserDestinationPrefix("/user")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/bot_socket").setAllowedOrigins("*").setHandshakeHandler(AnonymousHandshakeHandler())
        registry.addEndpoint("/bot_socket").setAllowedOrigins("*").setHandshakeHandler(AnonymousHandshakeHandler()).withSockJS()
    }
}