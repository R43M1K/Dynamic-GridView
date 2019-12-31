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
        private CustomEditTextListener customEditTextListener;

        public TaskViewHolder(@NonNull View itemView, OnItemClickListener listener, CustomEditTextListener customEditTextListener) {
            super(itemView);

            activeTask = itemView.findViewById(R.id.active_view);
            activeTaskId = itemView.findViewById(R.id.active_task_id);
            this.customEditTextListener = customEditTextListener;

            activeTask.addTextChangedListener(customEditTextListener);

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
        return new TaskViewHolder(v, mListener, new CustomEditTextListener());
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        if(activeTasks != null && !activeTasks.isEmpty()) {
            final Integer pos = position;
            String numberOfTask = String.valueOf(position + 1);
            holder.activeTaskId.setText(numberOfTask);
            holder.customEditTextListener.updatePosition(holder.getAdapterPosition());
            holder.activeTask.setText(activeTasks.get(holder.getAdapterPosition()));
            //TODO Use different logic to catch edittext change
            Log.d(TAG, "OnBindView position " + position);
        }
    }

    public class CustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!activeTasks.get(position).equals(s.toString())) {
                currentTask.put("position", position);
                currentTask.put("taskText", s.toString());
                taskLive.setValue(currentTask);
                Log.d(TAG, "onTextChanged position " + position);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }

    public MutableLiveData<HashMap> getCurrentTask() {
        return taskLive;
    }

    @Override
    public int getItemCount() {
        return activeTasks.size();
    }

}
