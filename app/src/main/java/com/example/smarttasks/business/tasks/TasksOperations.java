package com.example.smarttasks.business.tasks;

import com.example.smarttasks.repository.ProvideTasksOperationsInter;

import java.util.ArrayList;
import java.util.HashMap;

public class TasksOperations implements TasksOperationsUseCase {

    private ProvideTasksOperationsInter provideTasksOperationsInter;

    public TasksOperations(ProvideTasksOperationsInter provideTasksOperationsInter) {
        this.provideTasksOperationsInter = provideTasksOperationsInter;
    }


    @Override
    public void addTasksList(String tasksListRealName, ArrayList<String> tasksList) {
        provideTasksOperationsInter.addTasksList(tasksListRealName, tasksList);
    }

    @Override
    public void removeTasksList(String listName) {
        provideTasksOperationsInter.removeTasksList(listName);
    }

    @Override
    public void addTasks(String taskListRealName, String taskListName, ArrayList<HashMap<String, String>> tasksList) {
        provideTasksOperationsInter.addTasks(taskListRealName, taskListName, tasksList);
    }

    @Override
    public void removeTasks(String taskListName, ArrayList<Integer> indexList) {
        provideTasksOperationsInter.removeTasks(taskListName, indexList);
    }

    @Override
    public void removeTask(String taskListName, Integer taskId) {
        provideTasksOperationsInter.removeTask(taskListName, taskId);
    }

    @Override
    public void updateTasks(String taskListName, int rowId, String taskName, String taskFinished) {
        provideTasksOperationsInter.updateTasks(taskListName, rowId, taskName, taskFinished);
    }

    @Override
    public ArrayList<HashMap> getAllTasks(String taskListTableName) {
        return provideTasksOperationsInter.getAllTasks(taskListTableName);
    }

    @Override
    public ArrayList<String> getAllTableNames() {
        return provideTasksOperationsInter.getAllTableNames();
    }
}
