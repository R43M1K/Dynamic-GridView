package com.example.smarttasks.presenter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;

import com.example.smarttasks.MainActivity;
import com.example.smarttasks.R;
import com.example.smarttasks.presenter.FragmentNavigationController;
import com.example.smarttasks.presenter.adapter.grid.MainGridViewAdapter;
import com.example.smarttasks.presenter.adapter.recycler.SingleTask;
import com.example.smarttasks.presenter.viewmodels.MainViewModel;
import com.example.smarttasks.repository.services.tasks.TasksPoJo;

import java.util.ArrayList;

public class GridFragment extends Fragment {

    //Constants
    private final String TAG = getClass().toString();

    private MainGridViewAdapter mainGridViewAdapter;
    private LifecycleOwner lifecycleOwner;
    private MainViewModel mainViewModel;

    //Fragments
    private Fragment taskListFragment = new TaskListViewFragment();

    //UI
    GridView gridView;
    Button addButton;
    Button removeButton;
    RelativeLayout removeLayout;

    //Vars
    private ArrayList<String> tableNames;
    private TasksPoJo tasksPoJo;
    private int pos;

    public GridFragment() {
        //Require empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tasksPoJo = TasksPoJo.getInstance();
        tableNames = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_grid_view, container, false);

        gridView = view.findViewById(R.id.grid_view);
        addButton = view.findViewById(R.id.add_button);
        removeLayout = view.findViewById(R.id.remove_layout);
        removeButton = view.findViewById(R.id.remove_button);

        mainGridViewAdapter = new MainGridViewAdapter(getContext(), mainViewModel, lifecycleOwner);
        gridView.setAdapter(mainGridViewAdapter);

        mainViewModel.getAllTableNames();
        mainViewModel.getNames().observe(this, strings -> {
            tableNames = strings;
            mainGridViewAdapter.refresh(tableNames);
        });

        init();

        return view;
    }

    private void init() {
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
                /*
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

                 */
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
            mainViewModel.removeTasksList(tableNames.get(pos));
            pos = 0;
            addButton.setVisibility(View.VISIBLE);
            removeLayout.setVisibility(View.GONE);
            mainViewModel.getAllTableNames();
        });
    }

    private void callFragment() {
        addButton.setVisibility(View.GONE);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentNavigationController.replaceFragment(R.id.fragment_container, taskListFragment, fragmentManager);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //addButton.setVisibility(View.VISIBLE);
        if(context instanceof MainActivity) {
            MainActivity activity = (MainActivity) context;
            mainViewModel = activity.getMainViewModel();
            lifecycleOwner = activity.getLifecycleOwner();
        }
    }
}
