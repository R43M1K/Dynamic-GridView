package com.example.smarttasks.presenter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttasks.R;

import java.util.ArrayList;

public class ActiveTasksRecyclerAdapter extends RecyclerView.Adapter<ActiveTasksRecyclerAdapter.TaskViewHolder> {

    private ArrayList<String> activeTasks;
    private OnItemClickListener mListener;

    public ActiveTasksRecyclerAdapter(ArrayList<String> activeTasks) {
        this.activeTasks = activeTasks;
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
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
            holder.activeTaskId.setText(String.valueOf(position));
            holder.activeTask.setText(activeTasks.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return activeTasks.size();
    }

}
