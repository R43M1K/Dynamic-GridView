package com.example.smarttasks.business.tasks;

import com.example.smarttasks.repository.ProvideTasksOperationsInter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

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
    public Completable addTasks(String taskListRealName, String taskListName, ArrayList<HashMap<String, String>> tasksList) {
        return Completable.fromCallable((Callable<Void>)() -> {
            provideTasksOperationsInter.addTasks(taskListRealName, taskListName, tasksList);
            return null;
        });
    }

    @Override
    public Completable removeTasks(String taskListName, ArrayList<Integer> indexList) {
        return Completable.fromCallable((Callable<Void>) () -> {
            provideTasksOperationsInter.removeTasks(taskListName, indexList);
            return null;
        });
    }

    @Override
    public Completable removeTask(String taskListName, Integer taskId) {
        return Completable.fromCallable((Callable<Void>) () -> {
            provideTasksOperationsInter.removeTask(taskListName, taskId);
            return null;
        });
    }

    @Override
    public Completable updateTasks(String taskListName, int rowId, String taskName, String taskFinished) {
        return Completable.fromCallable((Callable<Void>) () -> {
            provideTasksOperationsInter.updateTasks(taskListName, rowId, taskName, taskFinished);
            return null;
        });
    }

    @Override
    public Completable changeTaskListRealName(String taskListTableName, String taskListRealName) {
        return Completable.fromCallable((Callable<Void>) () -> {
            provideTasksOperationsInter.changeTaskListRealName(taskListTableName, taskListRealName);
            return null;
        });
    }

    @Override
    public Single<ArrayList<HashMap>> getAllTasks(String taskListTableName) {
        return provideTasksOperationsInter
                .checkTaskListExists(taskListTableName)
                .flatMap(exist -> {
                    if (exist) return provideTasksOperationsInter.getAllTasks(taskListTableName);
                    else return Single.just(new ArrayList<>());
                });
    }

    @Override
    public ArrayList<String> getAllTableNames() {
        return provideTasksOperationsInter.getAllTableNames();
    }
}
