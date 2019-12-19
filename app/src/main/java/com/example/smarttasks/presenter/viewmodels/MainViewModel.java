package com.example.smarttasks.presenter.viewmodels;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import com.example.smarttasks.MainActivity;
import com.example.smarttasks.business.tasks.TasksOperationsUseCase;

import java.util.ArrayList;
import java.util.HashMap;

public class MainViewModel extends ViewModel{

    //Vars
    private final String TAG = getClass().toString();
    private MutableLiveData<ArrayList<HashMap>> allTasksList = new MutableLiveData<>();
    private MutableLiveData<ArrayList<String>> allTableNamesList = new MutableLiveData<>();
    private MutableLiveData<HashMap<String,String>> newTask = new MutableLiveData<>();

    //Business
    private TasksOperationsUseCase tasksOperationsUseCase;

    //Rx
    private CompositeDisposable compositeDisposable;

    public MainViewModel(TasksOperationsUseCase tasksOperationsUseCase) {
        this.tasksOperationsUseCase = tasksOperationsUseCase;
        compositeDisposable = new CompositeDisposable();
    }

    //TODO add rxJava to this class

    public void addTasksList(String taskListRealName, ArrayList<String> tasksList) {
        tasksOperationsUseCase.addTasksList(taskListRealName, tasksList);
    }

    public void removeTasksList(String listName) {
        tasksOperationsUseCase.removeTasksList(listName);
    }

    public void addTasks(String taskListRealName, String taskListName, ArrayList<HashMap<String, String>> tasksList) {
        tasksOperationsUseCase.addTasks(taskListRealName, taskListName, tasksList);
    }

    public void removeTasks(String taskListName, ArrayList<Integer> indexList) {
        tasksOperationsUseCase.removeTasks(taskListName, indexList);
    }

    public void removeTask(String taskListName, Integer taskId) {
        tasksOperationsUseCase.removeTask(taskListName, taskId);
    }

    public void updateTasks(String taskListName, int rowId, String taskName, String taskFinished) {
        tasksOperationsUseCase.updateTasks(taskListName, rowId, taskName, taskFinished);
    }

    public void changeTaskListRealName(String taskListTableName, String taskListRealName) {
        tasksOperationsUseCase.changeTaskListRealName(taskListTableName, taskListRealName);
    }


    public void setNewTask(HashMap<String,String> newAddedTask) {
        newTask.setValue(newAddedTask);
    }

    public void clearTasks() {
        newTask.setValue(new HashMap<>());
    }

    public MutableLiveData<HashMap<String,String>> getNewTask() {
        return newTask;
    }

    public void getAllTasks(String tasksListTableName) {

        /*
        compositeDisposable.add(Single.fromCallable(() -> tasksOperationsUseCase.getAllTasks(tasksListTableName))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( hashMaps -> {
                            allTasksList.setValue(hashMaps);
                            Log.d(TAG, "Tasklist added to LiveData");
                          }));

         */

        allTasksList.setValue(tasksOperationsUseCase.getAllTasks(tasksListTableName));
    }

    public LiveData<ArrayList<HashMap>> get() {
        return allTasksList;
    }

    public void getAllTableNames() {

        /*
        compositeDisposable.add(Single.fromCallable(() -> tasksOperationsUseCase.getAllTableNames())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(arrayList -> {
                    allTableNamesList.setValue(arrayList);
                    Log.d(TAG, "TableNameList added to LiveData");
                }));

         */

        allTableNamesList.setValue(tasksOperationsUseCase.getAllTableNames());
    }

    public LiveData<ArrayList<String>> getNames() {
        return allTableNamesList;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

}
