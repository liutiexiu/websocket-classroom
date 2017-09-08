package me.tiezhu.websocket;

import com.sun.net.httpserver.HttpPrincipal;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import sun.security.acl.PrincipalImpl;

import java.security.Principal;

/**
 * Created by liushuai on 2017/9/6.
 *
 * 优先级很高，可以在websocket里做passport校验
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class ConnectSecurityInterceptor extends ChannelInterceptorAdapter {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        System.out.println("in websocket, command: " + accessor.getCommand() + ", accessor:" + accessor);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Principal user = new HttpPrincipal("user1channel", "default");
            accessor.setUser(user);
        }

        return message;
    }
}
