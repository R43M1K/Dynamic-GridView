package com.example.smarttasks.repository;

import java.util.ArrayList;
import java.util.HashMap;

public interface ProvideTasksOperationsInter {

    void addTasksList(String taskListRealName, ArrayList<String> tasksList);

    void removeTasksList(String listName);

    void addTasks(String taskListRealName, String taskListName, ArrayList<HashMap<String, String>> tasksList);

    void removeTasks(String taskListName, ArrayList<Integer> indexList);

    void removeTask(String taskListName, Integer taskId);

    void updateTasks(String taskListName, int rowId, String taskName, String taskFinished);

    void changeTaskListRealName(String taskListTableName, String taskListRealName);

    ArrayList<HashMap> getAllTasks(String taskListTableName);

    ArrayList<String> getAllTableNames();
}
