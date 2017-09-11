package me.tiezhu.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.tiezhu.model.MsgInClassroom;
import me.tiezhu.model.MsgTimeSpend;
import me.tiezhu.model.QueueMsgInClassroom;
import me.tiezhu.utils.Utils;
import me.tiezhu.websocket.Constants.SubscribePath;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

import static me.tiezhu.queue.QueueConfig.Q_NAME_CLASSROOM;
import static me.tiezhu.queue.QueueConfig.Q_NAME_USER;

/**
 * Created by liushuai on 2017/9/6.
 */
@Component
public class QueueReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueReceiver.class);

    @Autowired
    private SimpMessagingTemplate template;

    private static ObjectMapper mapper = new ObjectMapper();

    @RabbitHandler
    @RabbitListener(queues = Q_NAME_CLASSROOM)
    public void consumeClassroomMessage(QueueMsgInClassroom msg) throws IOException {
        LOGGER.debug("msg from classroom queue, msg:{}", msg);
        String user = msg.getUser();
        String classroom = Utils.findUserClassroom(user);
        template.convertAndSendToUser(classroom, SubscribePath.CLASSROOM_INFO, new MsgInClassroom(user, classroom, System.currentTimeMillis()));
    }

    @RabbitHandler
    @RabbitListener(queues = Q_NAME_USER)
    public void consumeUserMessage(String msg) {
        LOGGER.debug("msg from user queue, msg:{}", msg);
        JSONObject json = new JSONObject(msg);
        String type = json.optString("type", "");
        if ("msgTimeSpend".equals(type)) {
            String user = json.optString("user");
            String destination = json.optString("destination");
            template.convertAndSendToUser(
                    user, destination, new MsgTimeSpend(json.optLong("enterSince"), System.currentTimeMillis()), Collections.<String, Object>singletonMap("type", "MsgTimeSpend"));
        }
    }
}
