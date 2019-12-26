package com.example.smarttasks.presenter.adapter.grid;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;

import com.example.smarttasks.R;
import com.example.smarttasks.presenter.viewmodels.MainViewModel;

import java.util.ArrayList;

public class MainGridViewAdapter extends BaseAdapter {

    //Constants
    private final String TAG = getClass().toString();

    //View
    private Context context;
    private LayoutInflater inflater;
    private MainViewModel mainViewModel;
    private LifecycleOwner lifecycleOwner;

    //Vars
    ArrayList<String> tableNames;

    public MainGridViewAdapter(Context context, MainViewModel mainViewModel, LifecycleOwner lifecycleOwner) {
        this.context = context;
        this.mainViewModel = mainViewModel;
        this.lifecycleOwner = lifecycleOwner;
        tableNames = new ArrayList<>();
    }

    @Override
    public int getCount() {
        Log.d(TAG, "NewList size is " + tableNames.size());
        return tableNames.size();
    }

    @Override
    public Object getItem(int position) {
        return tableNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        Log.d(TAG, "You are on " + position);
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.task_item_grid_view, null);
        }
        final TextView title = convertView.findViewById(R.id.title);
        final TextView unfinishedTasks = convertView.findViewById(R.id.unfinished_size);
        final LinearLayout linearLayout = convertView.findViewById(R.id.tasks_layout);

        ArrayList<Integer> tableCounter = new ArrayList<>();
        if(!tableNames.get(position).isEmpty()) {
            mainViewModel.getAllTasks(tableNames.get(position));
            mainViewModel.get().observe(lifecycleOwner, tasks -> {
                if (tableCounter.isEmpty() || !tableCounter.contains(position)) {
                    tableCounter.add(position);
                    int unfinishedTaskCount = 0;
                    if(!tasks.isEmpty()) {
                        linearLayout.removeAllViews();
                        title.setText(tasks.get(0).get("taskListRealName").toString());
                        for (int i = 0; i < tasks.size(); i++) {
                            if (tasks.get(i).get("taskFinished").equals("Active")) {
                                TextView task = new TextView(context);
                                task.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT));
                                task.setText(tasks.get(i).get("taskName").toString());
                                unfinishedTaskCount++;
                                linearLayout.addView(task);
                            }
                        }
                        unfinishedTasks.setText(String.valueOf(unfinishedTaskCount));
                    } else {
                        mainViewModel.removeTasksList(tableNames.get(position));
                        //TODO anotiate method below.
                        //context.deleteSharedPreferences("lastTaskListId");
                        Log.d(TAG, "Empty table removed");
                    }
                }
            });
        }
        Log.d(TAG, "GridView Updated");
        return convertView;
    }

    public void refresh(ArrayList<String> tableNames) {
        this.tableNames.clear();
        this.tableNames = tableNames;
        notifyDataSetChanged();
    }

}
