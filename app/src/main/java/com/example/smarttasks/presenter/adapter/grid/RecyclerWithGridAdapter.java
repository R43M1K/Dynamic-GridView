package com.example.smarttasks.presenter.adapter.grid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttasks.R;
import com.example.smarttasks.presenter.model.GridItem;

import java.util.ArrayList;

public class RecyclerWithGridAdapter extends RecyclerView.Adapter<RecyclerWithGridAdapter.ViewHolder> {

        //Vars
        private ArrayList<GridItem> taskItems;
        private Context context;
        private ItemListener itemListener;
        private LongClickListener longClickListener;

        public RecyclerWithGridAdapter(Context context, ArrayList<GridItem> taskItems, ItemListener itemListener, LongClickListener longClickListener) {
            this.taskItems = taskItems;
            this.longClickListener = longClickListener;
            this.context = context;
            this.itemListener = itemListener;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

            private GridItem gridItem;
            //Views
            private TextView taskListNameView;
            private TextView activeTaskCountView;
            private LinearLayout linearLayout;


            private ViewHolder(View v) {
                super(v);
                v.setOnClickListener(this);
                v.setOnLongClickListener(this);
                taskListNameView = v.findViewById(R.id.title);
                activeTaskCountView = v.findViewById(R.id.active_task_count_gird);
                linearLayout = v.findViewById(R.id.tasks_layout);
            }

            private void setData(GridItem gridItem) {
                this.gridItem = gridItem;
                ArrayList<String> currentTaskListTasks;
                currentTaskListTasks = gridItem.getActiveTasksText();
                taskListNameView.setText(gridItem.getTaskListRealName());
                activeTaskCountView.setText(String.valueOf(currentTaskListTasks.size()));
                linearLayout.removeAllViews();
                for(int i=0; i<currentTaskListTasks.size(); i++) {
                    TextView task = new TextView(context);
                    task.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    task.setText(currentTaskListTasks.get(i));
                    linearLayout.addView(task);
                }
            }


            @Override
            public void onClick(View view) {
                if (itemListener != null) {
                    itemListener.onItemClick(gridItem);
                }
            }

            @Override
            public boolean onLongClick(View v) {
                if(longClickListener != null) {
                    longClickListener.onItemLongClick(gridItem);
                }
                return true;
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.task_item_recycler_with_grid, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            viewHolder.setData(taskItems.get(position));
        }

        @Override
        public int getItemCount() {
            return taskItems.size();
        }

        public interface ItemListener {
            void onItemClick(GridItem gridItem);
        }

        public interface LongClickListener {
            void onItemLongClick(GridItem gridItem);
        }

        public void refresh(ArrayList<GridItem> arrayList) {
            taskItems = arrayList;
            notifyDataSetChanged();
        }
    }

