package edu.vt.codewaveservice.utils;

import java.util.Random;

public class TaskIdUtil {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LENGTH = 16;
    private static final Random RANDOM = new Random();

    public static Long generateTaskID() {
//        StringBuilder builder = new StringBuilder(LENGTH);
//        for (int i = 0; i < LENGTH; i++) {
//            builder.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
//        }
//        return builder.toString();
        return RANDOM.nextLong();
    }
}
