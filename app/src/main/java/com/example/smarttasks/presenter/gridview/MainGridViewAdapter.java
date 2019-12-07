package com.example.smarttasks.presenter.gridview;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.smarttasks.R;
import com.example.smarttasks.presenter.viewmodels.MainViewModel;

import java.util.ArrayList;
import java.util.HashMap;

public class MainGridViewAdapter extends BaseAdapter {

    //Constants
    private final String TAG = getClass().toString();

    //View
    private Context context;
    private LayoutInflater inflater;
    private MainViewModel mainViewModel;
    private LifecycleOwner lifecycleOwner;

    //Vars
    final ArrayList<String> newList;

    public MainGridViewAdapter(Context context, MainViewModel mainViewModel, LifecycleOwner lifecycleOwner) {
        this.context = context;
        this.mainViewModel = mainViewModel;
        this.lifecycleOwner = lifecycleOwner;
        newList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        mainViewModel.getAllTableNames();
        mainViewModel.getNames().observe(lifecycleOwner, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                newList.clear();
                newList.addAll(strings);
            }
        });

        return newList.size();
    }

    @Override
    public Object getItem(int position) {
        return newList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.task_item, null);
        }
        final TextView title = convertView.findViewById(R.id.title);
        final TextView unfinishedTasks = convertView.findViewById(R.id.unfinished_size);
        final LinearLayout linearLayout = convertView.findViewById(R.id.tasks_layout);

        if(!newList.get(position).isEmpty()) {
            mainViewModel.getAllTasks(newList.get(position));
            mainViewModel.get().observe(lifecycleOwner, new Observer<ArrayList<HashMap>>() {
                @Override
                public void onChanged(ArrayList<HashMap> hashMaps) {
                    int unfinishedTaskCount = 0;
                    if(!hashMaps.isEmpty()) {
                        linearLayout.removeAllViews();
                        title.setText(hashMaps.get(0).get("taskListRealName").toString());
                        for(int i=0; i<hashMaps.size(); i++) {
                            if (hashMaps.get(i).get("taskFinished").equals("Active")) {
                               TextView task = new TextView(context);
                               task.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                               task.setText(hashMaps.get(i).get("taskName").toString());
                               unfinishedTaskCount++;
                               linearLayout.addView(task);
                            }
                        }
                        unfinishedTasks.setText(String.valueOf(unfinishedTaskCount));
                    }else{
                        mainViewModel.removeTasksList(newList.get(position));
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

}
