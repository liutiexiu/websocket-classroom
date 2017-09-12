package me.tiezhu.config;

import me.tiezhu.websocket.MyWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 *
 * 这个handler不能生效
 * 可能是因为这里注册的东西跟WebSocketChannelConfig重复了
 *
 *
 */
@Deprecated
// @Configuration
// @EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // registry class: org.springframework.web.socket.config.annotation.ServletWebSocketHandlerRegistry
        registry.addHandler(myWebSocketHandler(), "/msg/websocket").setAllowedOrigins("*");
    }

    @Bean
    WebSocketHandler myWebSocketHandler() {
        System.out.println("handler created");
        return new MyWebSocketHandler();
    }
}
