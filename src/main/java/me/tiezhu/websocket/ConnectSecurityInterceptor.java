package me.tiezhu.websocket;

import me.tiezhu.queue.QueueSender;
import me.tiezhu.utils.Utils;
import me.tiezhu.websocket.Constants.Header;
import me.tiezhu.websocket.Constants.SubscribePath;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.auth.BasicUserPrincipal;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.security.Principal;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by liushuai on 2017/9/6.
 *
 * 优先级很高，可以在websocket里做passport校验
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class ConnectSecurityInterceptor extends ChannelInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectSecurityInterceptor.class);

    public static final long NOTIFY_INTERVAL = TimeUnit.SECONDS.toSeconds(5);

    private ScheduledExecutorService timeExecutor = Executors.newScheduledThreadPool(4);

    private Map<String, Pair<Long, ScheduledFuture<?>>> userMap = new TreeMap<>();

    @Autowired
    QueueSender queueSender;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        LOGGER.debug("command: {}, accessor:{}", accessor.getCommand(), accessor);
        MessageHeaders messageHeaders = accessor.getMessageHeaders();
        Principal user = messageHeaders.get(Header.WEBSOCKET_USER, Principal.class);
        final String userName = user.getName();

        String destination = accessor.getDestination();
        if (StringUtils.contains(destination, "/classroom/")) {
            String classroom = Utils.findUserClassroom(userName);
            LOGGER.debug("set classroom[{}] for user {}", userName, classroom);
            user = new BasicUserPrincipal(classroom);
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            LOGGER.debug("user {} connecting", user);
            ScheduledFuture<?> future = timeExecutor.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    long enterSince = userMap.containsKey(userName) ? userMap.get(userName).getLeft() : -1;
                    queueSender.sendToUserQueue(new JSONObject().put("user", userName)
                                                                .put("destination", SubscribePath.PERSONAL_MSG)
                                                                .put("type", "msgTimeSpend")
                                                                .put("enterSince", enterSince));
                }
            }, NOTIFY_INTERVAL, NOTIFY_INTERVAL, TimeUnit.SECONDS);
            userMap.put(userName, Pair.<Long, ScheduledFuture<?>>of(System.currentTimeMillis(), future));
            LOGGER.debug("userMap:{}", userMap);
        }

        if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            LOGGER.debug("user {} disconnect", user);
            if (userMap.containsKey(userName)) {
                userMap.get(userName).getRight().cancel(false);
                userMap.remove(userName);
            }
        }

        accessor.setUser(user);

        return message;
    }
}
