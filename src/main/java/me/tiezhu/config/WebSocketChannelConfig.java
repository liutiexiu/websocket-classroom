package me.tiezhu.config;

import com.sun.net.httpserver.HttpPrincipal;
import me.tiezhu.websocket.ConnectSecurityInterceptor;
import me.tiezhu.websocket.Constants.Attribute;
import me.tiezhu.websocket.Constants.SubscribePath;
import me.tiezhu.websocket.MyWebSocketHandler;
import me.tiezhu.websocket.WebSocketHandshakeInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketChannelConfig extends AbstractWebSocketMessageBrokerConfigurer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketChannelConfig.class);

    @Autowired
    private WebSocketHandshakeInterceptor webSocketHandshakeInterceptor;

    @Bean
    public DefaultHandshakeHandler handshakeHandler() {
        return new DefaultHandshakeHandler() {
            @Override
            protected Principal determineUser(
                    ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                LOGGER.debug("handshake attributes:{}", attributes);
                Object user = attributes.get(Attribute.PRINCIPAL);
                if (user == null) {
                    return new HttpPrincipal("no-user", "default");
                } else {
                    return (Principal) user;
                }
            }
        };
    }

    @Bean
    public StompSubProtocolErrorHandler errorHandler() {
        return new StompSubProtocolErrorHandler();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        LOGGER.debug("it is {}", registry.getClass());
        registry.enableSimpleBroker(SubscribePath.PREFIX_PERSONAL, SubscribePath.PREFIX_CLASSROOM, SubscribePath.PREFIX_PUBLIC);
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        LOGGER.debug("StompEndpointRegistry:{}", registry.getClass().getName());
        registry.setErrorHandler(errorHandler())
                .addEndpoint("/msg/websocket", "/msg/bad/websocket")
                .setHandshakeHandler(handshakeHandler())
                .addInterceptors(webSocketHandshakeInterceptor)
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.setInterceptors(channelInterceptor());
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        // TODO set send time limit and message size limit
    }

    @Bean
    ConnectSecurityInterceptor channelInterceptor() {
        return new ConnectSecurityInterceptor();
    }
}
