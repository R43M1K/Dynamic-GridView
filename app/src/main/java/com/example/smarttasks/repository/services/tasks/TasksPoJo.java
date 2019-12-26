package com.example.smarttasks.repository.services.tasks;

import com.example.smarttasks.presenter.adapter.recycler.SingleTask;

import java.util.ArrayList;

public class TasksPoJo {

    private ArrayList<SingleTask> tasks = new ArrayList<>();
    private ArrayList<Integer> tasksIds = new ArrayList<>();
    private String taskListRealName;
    private String taskListName;
    private ArrayList<String> taskCondition = new ArrayList<>();

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
        tasksIds.clear();
        taskListRealName = "";
        taskListName = "";
        taskCondition.clear();
    }

    public ArrayList<SingleTask> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<SingleTask> tasks) {
        this.tasks = tasks;
    }

    public String getTaskListRealName() {
        return taskListRealName;
    }

    public void setTaskListRealName(String taskListRealName) {
        this.taskListRealName = taskListRealName;
    }

    public String getTaskListName() {
        return taskListName;
    }

    public void setTaskListName(String taskListName) {
        this.taskListName = taskListName;
    }

    public ArrayList<Integer> getTasksIds() {
        return tasksIds;
    }

    public void setTasksIds(ArrayList<Integer> tasksIds) {
        this.tasksIds = tasksIds;
    }

    public ArrayList<String> getTaskCondition() {
        return taskCondition;
    }

    public void setTaskCondition(ArrayList<String> taskCondition) {
        this.taskCondition = taskCondition;
    }
}
