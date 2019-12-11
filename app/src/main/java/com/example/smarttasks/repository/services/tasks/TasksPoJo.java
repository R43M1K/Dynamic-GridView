package com.example.smarttasks.repository.services.tasks;

import com.example.smarttasks.presenter.recyclerview.SingleTask;

import java.util.ArrayList;

public class TasksPoJo {

    private ArrayList<SingleTask> tasks = new ArrayList<>();
    private String taskListName;

    private static TasksPoJo INSTANCE = null;

    private TasksPoJo() {}

    public static TasksPoJo getInstance() {
        if(INSTANCE == null) {
            synchronized (TasksPoJo.class) {
                if(INSTANCE == null) {
                    INSTANCE = new TasksPoJo();
                }
            }
        }
        return INSTANCE;
    }

    public void clear() {
        tasks.clear();
        taskListName = "";
    }

    public ArrayList<SingleTask> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<SingleTask> tasks) {
        this.tasks = tasks;
    }

    public String getTaskListName() {
        return taskListName;
    }

    public void setTaskListName(String taskListName) {
        this.taskListName = taskListName;
    }
}
