package com.telegrambot.workqueue.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackerQueueService {
    private static final String CLEAR_SUCCESS = "Таски были удалены из очереди. До завтра!";
    public static final String NOTHING_TO_REPORT = "Мне не о чем докладывать. Вы бездельник.";
    private final Map<Long, Map<Integer, String>> channels = new ConcurrentHashMap<>();
    public static final String TASK_IS_ADDED_IN_QUEUE = "Таска добавлена в очередь \n " +
            "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -\n";
    public static final String END_TITLE = "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -\n";
    private static final String GIF_TOPH_REPORT = "https://tenor.com/ru/view/toph-smug-avatar-the-last-airbender-yeah-at-la-gif-11607641";
    private static final String GIF_TOPH_NOTHING_TO_REPORT = "https://tenor.com/ru/view/toph-gif-18056949";
    //add
    //delete
    //clear
    //close
    //report - составить отчет из всех закрытых тасок

    public String addTask(String task, String name, Long chatId) {

        Map<Integer, String> taskMap = chooseChannelMap(chatId);
        int counter = taskMap.keySet().size() + 1;
        taskMap.put(counter, task + " @" + name);

        return TASK_IS_ADDED_IN_QUEUE + buildTaskList(taskMap) + END_TITLE;
    }

    public String getReport(Long chatId) {
        Map<Integer, String> taskList = chooseChannelMap(chatId);
        String time = LocalDate.now() + "\n";
        if (taskList.isEmpty()) {
            return NOTHING_TO_REPORT + "\n" + GIF_TOPH_NOTHING_TO_REPORT;
        }
        return time + buildTaskList(taskList) +"\n" + GIF_TOPH_REPORT;
    }

    public String clearTasks(long chatId) {
        var taskMap = chooseChannelMap(chatId);
        taskMap.clear();
        return CLEAR_SUCCESS;
    }

    private Map<Integer, String> chooseChannelMap(Long chatId) {
        channels.computeIfAbsent(chatId, task -> new LinkedHashMap<>());
        return channels.get(chatId);
    }

    private String buildTaskList(Map<Integer, String> taskMap) {
        StringBuilder taskList = new StringBuilder();
        AtomicInteger count = new AtomicInteger(1);

        taskMap.values().forEach(taskDescription ->
                taskList.append(count.getAndIncrement())
                        .append(". ")
                        .append(taskDescription)
                        .append("\n")
        );
        return taskList.toString();
    }
}
