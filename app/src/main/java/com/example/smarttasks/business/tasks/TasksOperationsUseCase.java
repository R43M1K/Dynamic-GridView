package com.example.smarttasks.business.tasks;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface TasksOperationsUseCase {

    Completable addTasksList(String taskListRealName, ArrayList<String> tasksList);

    Completable removeTasksList(String listName);

    Single<Boolean> checkTaskListExists(String listName);

    Completable addTasks(String taskListRealName, String taskListName, ArrayList<HashMap<String, String>> tasksList);

    Completable removeTasks(String taskListName, ArrayList<Integer> indexList);

    Completable removeTask(String taskListName, Integer taskId);

    Completable updateTasks(String taskListName, int rowId, String taskName, String taskFinished);

    Completable changeTaskListRealName(String taskListTableName, String taskListRealName);

    Completable updatePoJoWithGetAllTasks(String taskListTableName);

    Single<ArrayList<HashMap>> getAllTasks(String taskListTableName);

    ArrayList<String> getAllTableNames();

}
