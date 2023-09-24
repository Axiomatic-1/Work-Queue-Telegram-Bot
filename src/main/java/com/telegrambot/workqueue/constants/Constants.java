package com.telegrambot.workqueue.constants;

import java.util.HashSet;
import java.util.Set;


public class Constants {
    public static final Set<String> commands = new HashSet<>();

    private Constants() {
        commands.add("/add");
        commands.add("/delete");
        commands.add("/close");
        commands.add("/report");
    }
}
