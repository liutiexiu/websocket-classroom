package me.tiezhu.utils;

import me.tiezhu.websocket.MainController;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public abstract class Utils {

    public static String findUserClassroom(String user) {
        if (StringUtils.isNotEmpty(user)) {
            if (user.startsWith("t")) {
                return findTeacherClassroom(user);
            } else if (user.startsWith("s")) {
                return findStudentClassroom(user);
            }
        }

        return "no-classroom";
    }

    public static String findTeacherClassroom(String teacher) {
        int index = Arrays.binarySearch(MainController.teachers, teacher);
        if (index < 0) {
            return "no-classroom";
        } else {
            return MainController.classrooms[index];
        }
    }

    public static String findStudentClassroom(String student) {
        int index = Arrays.binarySearch(MainController.students, student);
        if (index < 0) {
            return "no-classroom";
        } else {
            int carry = MainController.students.length % MainController.classrooms.length == 0 ? 0 : 1;
            int studentCountPerClassroom = MainController.students.length / MainController.classrooms.length + carry;
            return MainController.classrooms[index / studentCountPerClassroom];
        }
    }
}
