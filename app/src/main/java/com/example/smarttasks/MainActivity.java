package com.example.smarttasks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.example.smarttasks.presenter.FragmentNavigationController;
import com.example.smarttasks.presenter.ViewModelFactory;
import com.example.smarttasks.presenter.fragments.AddNewTaskFragment;
import com.example.smarttasks.presenter.fragments.TaskListViewFragment;
import com.example.smarttasks.presenter.adapter.grid.MainGridViewAdapter;
import com.example.smarttasks.presenter.adapter.recycler.SingleTask;
import com.example.smarttasks.presenter.viewmodels.MainViewModel;
import com.example.smarttasks.repository.services.tasks.TasksPoJo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TaskListViewFragment.OnFragmentInteractionListener, AddNewTaskFragment.OnAddNewTaskFragmentInteractionListener {

    //Constants
    private final String TAG = getClass().toString();

    //Views
    private ViewModelFactory factory;
    private MainViewModel mainViewModel;
    private MainGridViewAdapter mainGridViewAdapter;
    private LifecycleOwner lifecycleOwner;
    private Fragment fragment = new TaskListViewFragment();
    private Fragment addTaskFragment = new AddNewTaskFragment();

    //UI
    GridView gridView;
    Button addButton;
    Button removeButton;
    RelativeLayout removeLayout;

    //Vars
    private ArrayList<String> tableNames;
    private TasksPoJo tasksPoJo;
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lifecycleOwner = MainActivity.this;
        tasksPoJo = TasksPoJo.getInstance();

        factory = new ViewModelFactory(getApplicationContext());
        mainViewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);

        gridView = findViewById(R.id.grid_view);
        addButton = findViewById(R.id.add_button);
        removeLayout = findViewById(R.id.remove_layout);
        removeButton = findViewById(R.id.remove_button);


        mainGridViewAdapter = new MainGridViewAdapter(getApplicationContext(), mainViewModel, lifecycleOwner);
        gridView.setAdapter(mainGridViewAdapter);

        tableNames = new ArrayList<>();
        mainViewModel.getAllTableNames();
        mainViewModel.getNames().observe(this, strings -> {
            tableNames = strings;
            mainGridViewAdapter.refresh(tableNames);
        });


        addButton.setOnClickListener(v -> {
            addButtonPressed();
        });

        gridView.setOnItemLongClickListener((parent, view, position, id) -> {
            addButton.setVisibility(View.GONE);
            removeLayout.setVisibility(View.VISIBLE);
            pos = (int) id;
            Log.d(TAG, "You have hold " + id);
            return true;
        });

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Log.d(TAG, "You have clicked on " + id);
            pos = (int) id;
            mainViewModel.getAllTasks(tableNames.get(pos));
            mainViewModel.get().observe(lifecycleOwner, hashMaps -> {
                if(!tableNames.isEmpty()) {
                    ArrayList<SingleTask> tasks = new ArrayList<>();
                    ArrayList<Integer> taskIds = new ArrayList<>();
                    tasksPoJo.setTaskListRealName((String) hashMaps.get(0).get("taskListRealName"));
                    String currentTableName = tableNames.get(pos);
                    tasksPoJo.setTaskListName(currentTableName);
                    for (int i = 0; i < hashMaps.size(); i++) {
                        String task = (String) hashMaps.get(i).get("taskName");
                        String taskState = (String) hashMaps.get(i).get("taskFinished");
                        Integer taskId = Integer.valueOf((String) hashMaps.get(i).get("taskId"));
                        SingleTask currentTask = new SingleTask(task, taskState);
                        tasks.add(currentTask);
                        taskIds.add(taskId);
                    }
                    tasksPoJo.setTasks(tasks);
                    tasksPoJo.setTasksIds(taskIds);
                }
            });
            callFragment();
        });

        removeButtonPressed();
    }

    private void addButtonPressed() {
        tasksPoJo.clear();
        callFragment();
    }

    private void removeButtonPressed() {
        removeButton.setOnClickListener(v -> {
            mainViewModel.getAllTableNames();
            mainViewModel.getNames().observe(lifecycleOwner, arrayList -> {
                if(!arrayList.isEmpty() && removeLayout.getVisibility() == View.VISIBLE) {
                    mainViewModel.removeTasksList(arrayList.get(pos));
                    pos = 0;
                    mainViewModel.getAllTableNames();
                    addButton.setVisibility(View.VISIBLE);
                    removeLayout.setVisibility(View.GONE);
                }
            });
        });
    }

    private void callFragment() {
        addButton.setVisibility(View.GONE);
        FragmentNavigationController.replaceFragment(R.id.fragment_frame, fragment, getSupportFragmentManager());
    }

    //OpenTaskListFragment response
    @Override
    public void onFragmentInteraction(Boolean fragmentClosed) {
        if(!fragment.isDetached() && fragmentClosed) {
            FragmentNavigationController.removeFragmentPopBackStack(fragment, getSupportFragmentManager());
            pos = 0;
            mainViewModel.getAllTableNames();
        }
        addButton.setVisibility(View.VISIBLE);
    }

    //AddNewTaskFragment Response
    @Override
    public void onAddNewTaskFragmentInteraction(Boolean fragmentClosed) {
        mainViewModel.getAllTasks(tasksPoJo.getTaskListName());
    }

    public MainViewModel getMainViewModel() {
        return mainViewModel;
    }

    //TODO Before creating new table, check if there is an empty table in database, if it is there
    // Then just call addTasks() and insert list into table. If there is no  empty table in database
    // First call getApplicationContext().deleteSharedPreferences("lastTaskListId")
    // Then call addTasksList() which will automaticly create a new table and fill it with list.
}
