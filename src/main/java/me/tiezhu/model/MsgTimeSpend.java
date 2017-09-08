package me.tiezhu.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MsgTimeSpend {
    private long enterClassroomSince;
    private long now;
}
