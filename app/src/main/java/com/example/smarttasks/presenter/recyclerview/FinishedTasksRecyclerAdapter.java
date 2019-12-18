package com.example.smarttasks.presenter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttasks.R;

import java.util.ArrayList;

public class FinishedTasksRecyclerAdapter extends RecyclerView.Adapter<FinishedTasksRecyclerAdapter.TaskViewHolder>{

    private ArrayList<String> finishedTasks;
    private OnItemClickListener mListener;

    public FinishedTasksRecyclerAdapter(ArrayList<String> finishedTasks) {
        this.finishedTasks = finishedTasks;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        public TextView finishedTasks;
        public ImageView finishedTasksId;

        public TaskViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            finishedTasks = itemView.findViewById(R.id.finished_view);
            finishedTasksId = itemView.findViewById(R.id.finished_task_id);

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_finished,parent,false);
        return new FinishedTasksRecyclerAdapter.TaskViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        if(finishedTasks != null && !finishedTasks.isEmpty()) {
            //holder.finishedTasksId.setText(String.valueOf(position));
            holder.finishedTasks.setText(finishedTasks.get(position));
        }
    }


    @Override
    public int getItemCount() {
        return finishedTasks.size();
    }


}
