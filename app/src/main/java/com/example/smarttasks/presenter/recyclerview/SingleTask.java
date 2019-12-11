package com.example.smarttasks.presenter.recyclerview;

import java.util.ArrayList;

public class SingleTask {

    private String task;
    private String taskState;

    public SingleTask(String task, String taskState){
        this.task = task;
        this.taskState = taskState;
    }

    public String getTask() {return task;}

    public String getTaskState() {return taskState;}

}
