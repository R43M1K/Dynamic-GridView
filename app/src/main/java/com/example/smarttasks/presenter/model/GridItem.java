package com.example.smarttasks.presenter.model;

import java.util.ArrayList;

public class GridItem {

    private ArrayList<String> activeTasksText;
    private String taskListRealName;
    private String taskListName;

    public ArrayList<String> getActiveTasksText() {
        return activeTasksText;
    }

    public void setActiveTasksText(ArrayList<String> activeTasksText) {
        this.activeTasksText = activeTasksText;
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
}
