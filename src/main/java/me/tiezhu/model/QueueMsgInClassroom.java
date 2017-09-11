package me.tiezhu.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueueMsgInClassroom {
    private String user;
    private String sessionId;
    private String msgName;
    private long time;
}
