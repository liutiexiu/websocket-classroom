package me.tiezhu.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MsgInClassroom {

    private String user;
    private String classroom;
    private long time;
}
