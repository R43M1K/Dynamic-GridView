package com.example.smarttasks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.example.smarttasks.presenter.ViewModelFactory;
import com.example.smarttasks.presenter.gridview.MainGridViewAdapter;
import com.example.smarttasks.presenter.viewmodels.MainViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //Constants
    private final String TAG = getClass().toString();

    //Views
    private ViewModelFactory factory;
    private MainViewModel mainViewModel;
    private MainGridViewAdapter mainGridViewAdapter;
    private LifecycleOwner lifecycleOwner;

    //UI
    GridView gridView;
    Button addButton;
    Button removeButton;
    RelativeLayout removeLayout;

    //Vars
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lifecycleOwner = MainActivity.this;

        factory = new ViewModelFactory(getApplicationContext());
        mainViewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);

        gridView = findViewById(R.id.grid_view);
        addButton = findViewById(R.id.add_button);
        removeLayout = findViewById(R.id.remove_layout);
        removeButton = findViewById(R.id.remove_button);

        //mainViewModel.removeTasksList("myTasksTable72");

        mainViewModel.getAllTableNames();
        mainViewModel.getNames().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                mainGridViewAdapter = new MainGridViewAdapter(getApplicationContext(), mainViewModel, lifecycleOwner, strings);
                gridView.setAdapter(mainGridViewAdapter);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> myList = new ArrayList<>();
                myList.add("Get children from kindergarden");
                //myList.add("Buy eggs , bread, nutella, coca-cola, milk, butter, kitkat, twix from magazine in a way from home");
                //myList.add("Delete League of Legends");
                mainViewModel.addTasksList("My First Task List", myList);
                mainViewModel.getAllTableNames();
                mainViewModel.getNames().observe(lifecycleOwner, new Observer<ArrayList<String>>() {
                    @Override
                    public void onChanged(ArrayList<String> arrayList) {
                        mainGridViewAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                addButton.setVisibility(View.GONE);
                removeLayout.setVisibility(View.VISIBLE);
                pos = (int) id;
                Log.d(TAG, "You have hold " + id);
                return true;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "You have clicked on " + id);
            }
        });

        removeButtonPressed();
    }

    private void removeButtonPressed() {
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewModel.getAllTableNames();
                mainViewModel.getNames().observe(lifecycleOwner, new Observer<ArrayList<String>>() {
                    @Override
                    public void onChanged(ArrayList<String> arrayList) {
                        if(!arrayList.isEmpty() && removeLayout.getVisibility() == View.VISIBLE) {
                            mainViewModel.removeTasksList(arrayList.get(pos));
                            mainViewModel.getAllTableNames();
                            addButton.setVisibility(View.VISIBLE);
                            removeLayout.setVisibility(View.GONE);
                            mainViewModel.getNames().observe(lifecycleOwner, new Observer<ArrayList<String>>() {
                                @Override
                                public void onChanged(ArrayList<String> arrayList) {
                                    mainGridViewAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    //TODO Before creating new table, check if there is an empty table in database, if it is there
    // Then just call addTasks() and insert list into table. If there is no  empty table in database
    // First call getApplicationContext().deleteSharedPreferences("lastTaskListId")
    // Then call addTasksList() which will automaticly create a new table and fill it with list.
}
