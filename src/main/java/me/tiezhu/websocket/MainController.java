package me.tiezhu.websocket;

import me.tiezhu.model.MsgGreeting;
import me.tiezhu.model.HelloMessage;
import me.tiezhu.model.QueueMsgInClassroom;
import me.tiezhu.queue.QueueReceiver;
import me.tiezhu.queue.QueueSender;
import me.tiezhu.utils.Utils;
import me.tiezhu.websocket.Constants.SubscribePath;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Arrays;

@Controller
public class MainController {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private QueueSender queueSender;

    @Autowired
    private QueueReceiver queueReceiver;

    public static final String[] classrooms = new String[]{"classroom1", "classroom2"};
    public static final String[] teachers = new String[]{"teacher1", "teacher2"};
    public static final String[] students = new String[]{"student1", "student2", "student3", "student4"};

    @GetMapping("/roles")
    @ResponseBody
    public String getRoles() {
        return new JSONObject().put("students", Arrays.asList(students))
                               .put("teachers", Arrays.asList(teachers)).toString();
    }

    // to /app/online
    @MessageMapping("/online")
    @SendToUser(value = SubscribePath.PERSONAL_MSG)
    public MsgGreeting userOnline(HelloMessage message, @Header("simpSessionId") String sessionId, final Principal principal) throws Exception {
        // send to class queue, to notify teacher and all classmates
//        queueSender.sendToClassroomQueue(new JSONObject().put("user", principal.getName())
//                                                         .put("sessionId", sessionId)
//                                                         .put("msg.name", message.getName()));
        queueSender.sendToClassroomQueue(new QueueMsgInClassroom(principal.getName(), sessionId, message.getName(), System.currentTimeMillis()));
        return new MsgGreeting("Hello " + principal.getName() + ", welcome to " + Utils.findUserClassroom(principal.getName()));
    }
}
