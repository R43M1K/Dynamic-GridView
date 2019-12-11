package com.example.smarttasks.presenter.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttasks.R;
import com.example.smarttasks.presenter.recyclerview.ActiveTasksRecyclerAdapter;
import com.example.smarttasks.presenter.recyclerview.FinishedTasksRecyclerAdapter;
import com.example.smarttasks.presenter.recyclerview.SingleTask;
import com.example.smarttasks.repository.services.tasks.TasksPoJo;

import java.util.ArrayList;

public class TaskListViewFragment extends Fragment {

    //Constants
    private final String TAG = getClass().toString();

    //Views
    private EditText taskListNameView;
    private TextView activeTaskCountView;
    private TextView finishedTaskCountView;

    //RecyclerView classes
    private RecyclerView activeRecyclerView;
    private RecyclerView finishedRecyclerView;
    private ActiveTasksRecyclerAdapter activeAdapter;
    private FinishedTasksRecyclerAdapter finishedAdapter;
    private RecyclerView.LayoutManager activeLayoutManager;
    private RecyclerView.LayoutManager finishedLayoutManager;

    //Vars
    private OnFragmentInteractionListener mListener;
    private ArrayList<SingleTask> taskList = new ArrayList<>();
    private ArrayList<String> activeTasksList = new ArrayList<>();
    private ArrayList<String> finishedTasksList = new ArrayList<>();

    private TasksPoJo tasksPoJo;

    public TaskListViewFragment() {
        //Required empty constructor
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Boolean fragmentClosed);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tasksPoJo = TasksPoJo.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_open_task_list, container, false);

        activeTasksList.clear();
        finishedTasksList.clear();
        taskList.clear();

        tasksPoJo = TasksPoJo.getInstance();
        // Initialize Views
        taskListNameView = view.findViewById(R.id.task_list_name);
        if(tasksPoJo.getTaskListName() != null && !tasksPoJo.getTaskListName().isEmpty()) {
            taskListNameView.setText(tasksPoJo.getTaskListName());
        }

        // Calculate active and finished tasks counts
        activeTaskCountView = view.findViewById(R.id.active_tasks);
        finishedTaskCountView = view.findViewById(R.id.finished_tasks);

        taskList = tasksPoJo.getTasks();

        if(taskList != null && !taskList.isEmpty()) {
            for(int i=0; i<taskList.size(); i++) {
                switch (taskList.get(i).getTaskState()) {
                    case "Active":
                        activeTasksList.add(taskList.get(i).getTask());
                    case "Finished":
                        finishedTasksList.add(taskList.get(i).getTask());
                }
            }
        }

        String activeCountText = activeTasksList.size() + " " + activeTaskCountView.getText();
        String finishedCountText = finishedTasksList.size() + " " + finishedTaskCountView.getText();

        activeTaskCountView.setText(activeCountText);
        finishedTaskCountView.setText(finishedCountText);

        //Initialize RecyclerViews
        activeRecyclerView = view.findViewById(R.id.active_recycler_view);
        finishedRecyclerView = view.findViewById(R.id.finished_recycle_view);
        activeRecyclerView.setHasFixedSize(true);
        finishedRecyclerView.setHasFixedSize(true);
        activeLayoutManager = new LinearLayoutManager(getContext());
        finishedLayoutManager = new LinearLayoutManager(getContext());
        activeAdapter = new ActiveTasksRecyclerAdapter(activeTasksList);
        finishedAdapter = new FinishedTasksRecyclerAdapter(finishedTasksList);
        activeRecyclerView.setLayoutManager(activeLayoutManager);
        finishedRecyclerView.setLayoutManager(finishedLayoutManager);
        activeRecyclerView.setAdapter(activeAdapter);
        finishedRecyclerView.setAdapter(finishedAdapter);

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            private Drawable icon;
            private ColorDrawable background;

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                icon = ContextCompat.getDrawable(getContext(),
                        R.drawable.ic_launcher_background);
                background = new ColorDrawable(Color.RED);
                deleteTask(position);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                int backgroundCornerOffset = 20;
                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + icon.getIntrinsicHeight();

                if (dX > 0) { // Swiping to the right
                    int iconLeft = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
                    int iconRight = itemView.getLeft() + iconMargin;
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                    background.setBounds(itemView.getLeft(), itemView.getTop(),
                            itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                            itemView.getBottom());
                } else if (dX < 0) { // Swiping to the left
                    int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                    int iconRight = itemView.getRight() - iconMargin;
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                    background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                            itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else { // view is unSwiped
                    background.setBounds(0, 0, 0, 0);
                }

                background.draw(c);
                icon.draw(c);
            }
        };


        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(activeRecyclerView);

        activeAdapter.setOnItemClickListener(position -> {
            //This is if user clicked on whole line of RecyclerViews line in position "position"
            Log.d("recycler item click: ", "item clicked by position: " + position);
        });

        return view;
    }

    private void deleteTask(int position) {
        taskList.remove(position);
        notifyAll();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.onFragmentInteraction(true);
    }
}
