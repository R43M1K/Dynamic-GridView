package com.example.smarttasks.presenter.viewmodels;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
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
    private MutableLiveData<Boolean> needBlur = new MutableLiveData<>();

    //Business
    private TasksOperationsUseCase tasksOperationsUseCase;

    //Rx
    private CompositeDisposable compositeDisposable;

    public MainViewModel(TasksOperationsUseCase tasksOperationsUseCase) {
        this.tasksOperationsUseCase = tasksOperationsUseCase;
        compositeDisposable = new CompositeDisposable();
    }

    public void addTasksList(String taskListRealName, ArrayList<String> tasksList) {
        compositeDisposable.add(tasksOperationsUseCase
                .addTasksList(taskListRealName, tasksList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            Log.d(TAG, "Completed");
                            },
                        throwable -> {
                            Log.e(TAG, "Error");
                        })
        );
    }

    public void removeTasksList(String listName) {
        compositeDisposable.add(tasksOperationsUseCase
                .removeTasksList(listName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        );
    }

    public void addTasks(String taskListRealName, String taskListName, ArrayList<HashMap<String, String>> tasksList) {
        compositeDisposable.add(tasksOperationsUseCase
                .addTasks(taskListRealName, taskListName, tasksList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        );
    }

    public void removeTasks(String taskListName, ArrayList<Integer> indexList) {
        compositeDisposable.add(tasksOperationsUseCase.
                removeTasks(taskListName, indexList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        );
    }

    public void removeTask(String taskListName, Integer taskId) {
        compositeDisposable.add(tasksOperationsUseCase
                .removeTask(taskListName, taskId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        );
    }

    public void updateTasks(String taskListName, int rowId, String taskName, String taskFinished) {
        compositeDisposable.add(tasksOperationsUseCase.
                updateTasks(taskListName, rowId, taskName, taskFinished)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        );
    }

    public void changeTaskListRealName(String taskListTableName, String taskListRealName) {
        compositeDisposable.add(tasksOperationsUseCase
                .changeTaskListRealName(taskListTableName, taskListRealName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        );
    }


    public void setNewTask(HashMap<String,String> newAddedTask) {
        newTask.setValue(newAddedTask);
    }

    public void clearTasks() {
        newTask.setValue(new HashMap<>());
    }

    public void clearLiveData() {
        allTasksList.setValue(new ArrayList<>());
        allTableNamesList.setValue(new ArrayList<>());
    }

    public MutableLiveData<HashMap<String,String>> getNewTask() {
        return newTask;
    }

    public void getAllTasks(String tasksListTableName) {

        compositeDisposable.add(
                tasksOperationsUseCase.getAllTasks(tasksListTableName)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(tasks -> allTasksList.setValue(tasks), Throwable::printStackTrace)
        );
    }

    public LiveData<ArrayList<HashMap>> get() {
        return allTasksList;
    }

    public void updatePoJoWithGetAllTasks(String taskListTableName) {
        compositeDisposable.add(tasksOperationsUseCase
                .updatePoJoWithGetAllTasks(taskListTableName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        );
    }

    public void getAllTableNames() {

        compositeDisposable.add(Single.fromCallable(() -> tasksOperationsUseCase.getAllTableNames())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(arrayList -> {
                    allTableNamesList.setValue(arrayList);
                    Log.d(TAG, "TableNameList added to LiveData");
                }));
    }

    public MutableLiveData<ArrayList<String>> getNames() {
        return allTableNamesList;
    }

    public void setNeedBlur(boolean needBlur) {
        this.needBlur.setValue(needBlur);
    }

    public MutableLiveData<Boolean> getNeedBlur() {
        return needBlur;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

}
