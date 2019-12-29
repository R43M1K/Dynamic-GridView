package com.example.smarttasks.presenter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.smarttasks.MainActivity;
import com.example.smarttasks.R;
import com.example.smarttasks.presenter.adapter.grid.RecyclerWithGridAdapter;
import com.example.smarttasks.presenter.manager.AutoFitGridLayoutManager;
import com.example.smarttasks.presenter.model.GridItem;
import com.example.smarttasks.presenter.viewmodels.MainViewModel;

import java.util.ArrayList;

public class RecyclerWithGridFragment extends Fragment implements RecyclerWithGridAdapter.ItemListener {

    //Constants
    private final String TAG = getClass().toString();
    //Views
    private RecyclerView recyclerView;
    //Vars
    private ArrayList<GridItem> gridItemArrayList;
    private ArrayList<String> tableNames;
    //Architecture
    private MainViewModel mainViewModel;
    private LifecycleOwner lifecycleOwner;

    public RecyclerWithGridFragment() {
        //Require empty constructor
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recycler_with_grid, container, false);

        recyclerView = view.findViewById(R.id.recycler_with_grid);
        gridItemArrayList = new ArrayList<>();
        tableNames = new ArrayList<>();

        RecyclerWithGridAdapter adapter = new RecyclerWithGridAdapter(getContext(), gridItemArrayList, this);
        recyclerView.setAdapter(adapter);

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, LinearLayout.VERTICAL);
        recyclerView.setLayoutManager(manager);


        //Get all tasks from all existing tables
        allTableTasksListener();
        getAllTasksListener(adapter);

        return view;
    }

    @Override
    public void onItemClick(GridItem gridItem) {
        Log.d(TAG, "You have clicked on item " + gridItem.getTaskListRealName());
    }

    private void allTableTasksListener() {
        mainViewModel.getAllTableNames();
        mainViewModel.getNames().observe(lifecycleOwner, tables -> {
            tableNames = tables;
            if(!tableNames.isEmpty()) {
                for (int i = 0; i < tableNames.size(); i++) {
                    mainViewModel.getAllTasks(tableNames.get(i));
                }
            }

        });
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
                    adapter.refresh(gridItemArrayList);
                    adapter.notifyDataSetChanged();
                } else {
                    /*
                    mainViewModel.removeTasksList(tasks.get(0).get("tableName").toString());
                    Log.d(TAG, "Empty table removed");
                    //TODO anotiate method below.
                    //context.deleteSharedPreferences("lastTaskListId");

                     */
                }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);


        if(context instanceof MainActivity) {
            mainViewModel = ((MainActivity) context).getMainViewModel();
            lifecycleOwner = ((MainActivity) context).getLifecycleOwner();
        }
    }
}
