package com.example.smarttasks.business.tasks;

import com.example.smarttasks.repository.ProvideTasksOperationsInter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;

public class TasksOperations implements TasksOperationsUseCase {

    private ProvideTasksOperationsInter provideTasksOperationsInter;

    public TasksOperations(ProvideTasksOperationsInter provideTasksOperationsInter) {
        this.provideTasksOperationsInter = provideTasksOperationsInter;
    }


    @Override
    public Completable addTasksList(String tasksListRealName, ArrayList<String> tasksList) {
        return Completable.fromCallable((Callable<Void>) () -> {
            provideTasksOperationsInter.addTasksList(tasksListRealName, tasksList);
            return null;
        });
    }

    @Override
    public Completable removeTasksList(String listName) {
        return Completable.fromCallable((Callable<Void>)() -> {
            provideTasksOperationsInter.removeTasksList(listName);
            return null;
        });
    }

    @Override
    public Single<Boolean> checkTaskListExists(String listName) {
        return provideTasksOperationsInter.checkTaskListExists(listName);
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
    public void changeTaskListRealName(String taskListTableName, String taskListRealName) {
        provideTasksOperationsInter.changeTaskListRealName(taskListTableName, taskListRealName);
    }

    @Override
    public Single<ArrayList<HashMap>> getAllTasks(String taskListTableName) {
        return provideTasksOperationsInter
                .checkTaskListExists(taskListTableName)
                .flatMap(existst -> {
                    if (existst) return provideTasksOperationsInter.getAllTasks(taskListTableName);
                    else return Single.just(new ArrayList<>());
                });
    }

    @Override
    public ArrayList<String> getAllTableNames() {
        return provideTasksOperationsInter.getAllTableNames();
    }
}
