package com.example.smarttasks.presenter.adapter.recycler;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttasks.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ActiveTasksRecyclerAdapter extends RecyclerView.Adapter<ActiveTasksRecyclerAdapter.TaskViewHolder> {

    //Constants
    private final String TAG = getClass().toString();

    private ArrayList<String> activeTasks;
    private OnItemClickListener mListener;
    private HashMap currentTask;
    private MutableLiveData<HashMap> taskLive = new MutableLiveData<>();

    public ActiveTasksRecyclerAdapter(ArrayList<String> activeTasks) {
        this.activeTasks = activeTasks;
        currentTask = new HashMap();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {


        public EditText activeTask;
        public TextView activeTaskId;

        public TaskViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            activeTask = itemView.findViewById(R.id.active_view);
            activeTaskId = itemView.findViewById(R.id.active_task_id);

            itemView.setOnClickListener(v -> {
                if(listener != null){
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        listener.onItemClick(position);
                    }
                }
            });
        }

    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_active,parent,false);
        return new TaskViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        if(activeTasks != null && !activeTasks.isEmpty()) {
            String numberOfTask = String.valueOf(position + 1);
            holder.activeTaskId.setText(numberOfTask);
            holder.activeTask.setText(activeTasks.get(position));
            final Integer pos = position;
            holder.activeTask.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(!activeTasks.get(position).equals(s.toString())) {
                        currentTask.put("position", pos);
                        currentTask.put("taskText", s.toString());
                        taskLive.setValue(currentTask);
                        Log.d(TAG, "EditText was changed");
                    }
                }
            });
        }
    }

    public MutableLiveData<HashMap> getCurrentTask() {
        return taskLive;
    }

    @Override
    public int getItemCount() {
        return activeTasks.size();
    }

}
