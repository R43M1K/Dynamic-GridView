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

import com.example.smarttasks.presenter.ViewModelFactory;
import com.example.smarttasks.presenter.gridview.MainGridViewAdapter;
import com.example.smarttasks.presenter.viewmodels.MainViewModel;

import java.util.ArrayList;
import java.util.HashMap;

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
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lifecycleOwner = MainActivity.this;

        factory = new ViewModelFactory(getApplicationContext());
        mainViewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);

        gridView = findViewById(R.id.grid_view);
        button = findViewById(R.id.add_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> myList = new ArrayList<>();
                myList.add("Get children from kindergarden");
                myList.add("Buy eggs , bread, nutella, coca-cola, milk, butter, kitkat, twix from magazine in a way from home");
                myList.add("Delete League of Legends");
                mainViewModel.addTasksList("My First Task List", myList);
                mainGridViewAdapter.notifyDataSetChanged();
            }
        });


        //mainViewModel.removeTasksList("myTasksTable60");

        mainGridViewAdapter = new MainGridViewAdapter(getApplicationContext(), mainViewModel, lifecycleOwner);
        gridView.setAdapter(mainGridViewAdapter);

    }

    //TODO Before creating new table, check if there is an empty table in database, if it is there
    // Then just call addTasks() and insert list into table. If there is no  empty table in database
    // First call getApplicationContext().deleteSharedPreferences("lastTaskListId")
    // Then call addTasksList() which will automaticly create a new table and fill it with list.
}
