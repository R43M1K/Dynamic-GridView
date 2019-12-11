package com.example.smarttasks.presenter.viewmodels;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import com.example.smarttasks.business.tasks.TasksOperationsUseCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Callable;

public class MainViewModel extends ViewModel implements Parcelable {

    //Vars
    private final String TAG = getClass().toString();
    private MutableLiveData<ArrayList<HashMap>> allTasksList = new MutableLiveData<>();
    private MutableLiveData<ArrayList<String>> allTableNamesList = new MutableLiveData<>();

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
