package com.telegrambot.workqueue.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackerQueueService {
    private final Map<Long, Map<Integer, String>> channels = new ConcurrentHashMap<>();
    //add
    //delete
    //clear
    //close
    //report - составить отчет из всех закрытых тасок

    public Map<Integer, String> addTask(String task, String name, Long chatId) {

        Map<Integer, String> taskMap = chooseChannellMap(chatId);
        int counter = taskMap.keySet().size() + 1;
        taskMap.put(counter, task + " @" + name);
        return taskMap;
    }

    private Map<Integer, String> chooseChannellMap(Long chatId) {
        channels.computeIfAbsent(chatId, k -> new HashMap<>());
        return channels.get(chatId);
    }

}
