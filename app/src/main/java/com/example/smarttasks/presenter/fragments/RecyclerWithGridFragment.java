package com.example.smarttasks.presenter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.smarttasks.MainActivity;
import com.example.smarttasks.R;
import com.example.smarttasks.presenter.FragmentNavigationController;
import com.example.smarttasks.presenter.adapter.grid.RecyclerWithGridAdapter;
import com.example.smarttasks.presenter.manager.AutoFitGridLayoutManager;
import com.example.smarttasks.presenter.model.GridItem;
import com.example.smarttasks.presenter.viewmodels.MainViewModel;
import com.example.smarttasks.repository.services.tasks.TasksPoJo;

import java.util.ArrayList;

public class RecyclerWithGridFragment extends Fragment implements RecyclerWithGridAdapter.ItemListener, RecyclerWithGridAdapter.LongClickListener{

    //Constants
    private final String TAG = getClass().toString();
    //Views
    private RecyclerView recyclerView;
    private Button addButton;
    private Button removeButton;
    private RelativeLayout removeLayout;
    //Fragments
    private Fragment taskListFragment;
    //Vars
    private ArrayList<GridItem> gridItemArrayList;
    private ArrayList<String> tableNames;
    private TasksPoJo tasksPoJo;
    private String currentTableName;
    //Architecture
    private MainViewModel mainViewModel;
    private LifecycleOwner lifecycleOwner;

    public RecyclerWithGridFragment() {
        //Require empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_with_grid, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        lifecycleOwner = getViewLifecycleOwner();

        taskListFragment = new TaskListViewFragment();

        recyclerView = view.findViewById(R.id.recycler_with_grid);
        addButton = view.findViewById(R.id.add_button);
        removeButton = view.findViewById(R.id.remove_button);
        removeLayout = view.findViewById(R.id.remove_layout);

        gridItemArrayList = new ArrayList<>();
        tableNames = new ArrayList<>();
        tasksPoJo = TasksPoJo.getInstance();

        RecyclerWithGridAdapter adapter = new RecyclerWithGridAdapter(getContext(), gridItemArrayList, this,this);
        recyclerView.setAdapter(adapter);

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, LinearLayout.VERTICAL);
        recyclerView.setLayoutManager(manager);

        //Get all tasks from all existing tables
        allTableTasksListener();
        getAllTasksListener(adapter);

        init();
    }

    @Override
    public void onItemClick(GridItem gridItem) {
        Log.d(TAG, "You have clicked on item " + gridItem.getTaskListRealName());
        currentTableName = gridItem.getTaskListName();
        tasksPoJo.setTaskListName(currentTableName);
        mainViewModel.getAllTasks(currentTableName);
        callFragment();
    }

    @Override
    public void onItemLongClick(GridItem gridItem) {
        Log.d(TAG, "You have long clicked on item " + gridItem.getTaskListRealName());
        currentTableName = gridItem.getTaskListName();
        addButton.setVisibility(View.GONE);
        removeLayout.setVisibility(View.VISIBLE);
    }

    private void allTableTasksListener() {
        mainViewModel.getNames().observe(lifecycleOwner, tables -> {
            tableNames = tables;
            gridItemArrayList.clear();
            if (!tableNames.isEmpty()) {
                for (int i = 0; i < tableNames.size(); i++) {
                    mainViewModel.getAllTasks(tableNames.get(i));
                }
            }
        });

        mainViewModel.clearLiveData();
        mainViewModel.getAllTableNames();
    }

    private void getAllTasksListener(RecyclerWithGridAdapter adapter) {
        mainViewModel.get().observe(lifecycleOwner, tasks -> {
            if (!tasks.isEmpty()) {
                GridItem gridItem = new GridItem();
                gridItem.setTaskListName(tasks.get(0).get("tableName").toString());
                gridItem.setTaskListRealName((tasks.get(0).get("taskListRealName").toString()));
                ArrayList<String> activeTasksText = new ArrayList<>();
                for (int j = 0; j < tasks.size(); j++) {
                    if (tasks.get(j).get("taskFinished").equals("Active")) {
                        activeTasksText.add(tasks.get(j).get("taskName").toString());
                    }
                }
                gridItem.setActiveTasksText(activeTasksText);
                gridItemArrayList.add(gridItem);
                //Use below checking to not get double call of same table on adapter
                if(tableNames.size() == gridItemArrayList.size()) {
                    adapter.refresh(gridItemArrayList);
                }
            }
        });
    }

    private void init() {
        addButtonPressed();
        removeButtonPressed();
    }

    private void addButtonPressed() {
        addButton.setOnClickListener(v -> {
            tasksPoJo.clear();
            callFragment();
        });
    }

    private void removeButtonPressed() {
        removeButton.setOnClickListener(v -> {
            mainViewModel.removeTasksList(currentTableName);
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

        if(context instanceof MainActivity) {
            mainViewModel = ((MainActivity) context).getMainViewModel();
        }
    }


}
