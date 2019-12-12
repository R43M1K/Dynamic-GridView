package com.example.smarttasks.presenter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttasks.R;
import com.example.smarttasks.presenter.recyclerview.ActiveTasksRecyclerAdapter;
import com.example.smarttasks.presenter.recyclerview.FinishedTasksRecyclerAdapter;
import com.example.smarttasks.presenter.recyclerview.SingleTask;
import com.example.smarttasks.presenter.recyclerview.OnClickInter;
import com.example.smarttasks.presenter.viewmodels.MainViewModel;
import com.example.smarttasks.repository.services.tasks.TasksPoJo;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskListViewFragment extends Fragment {

    //Constants
    private final String TAG = getClass().toString();
    private final String ACTIVE_TASKS_TEXT = " Active Tasks";
    private final String FINISHED_TASKS_TEXT = " Finished Tasks";
    private final String CHANGE_TO_ACTIVE = "Active";
    private final String CHANGE_TO_FINISHED = "Finished";

    //Views
    private EditText taskListNameView;
    private TextView activeTaskCountView;
    private TextView finishedTaskCountView;
    private Button addTaskView;
    private Button saveTaskView;

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
    private ArrayList<Integer> taskListIds = new ArrayList<>();
    private ArrayList<String> activeTasksList = new ArrayList<>();
    private ArrayList<String> finishedTasksList = new ArrayList<>();

    private TasksPoJo tasksPoJo;
    private MainViewModel mainViewModel;

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
        try{
            mainViewModel = getArguments().getParcelable("mainViewModel");
        }catch (Exception e) {
            Log.d(TAG, "Null parcable");
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_open_task_list, container, false);

        activeTasksList.clear();
        finishedTasksList.clear();
        taskList.clear();


        tasksPoJo = TasksPoJo.getInstance();
        taskListIds = tasksPoJo.getTasksIds();

        // Initialize Views
        taskListNameView = view.findViewById(R.id.task_list_name);
        if(tasksPoJo.getTaskListRealName() != null && !tasksPoJo.getTaskListRealName().isEmpty()) {
            taskListNameView.setText(tasksPoJo.getTaskListRealName());
        }

        addTaskView = view.findViewById(R.id.add_button_fragment);
        saveTaskView = view.findViewById(R.id.save_button_fragment);

        // Calculate active and finished tasks counts
        activeTaskCountView = view.findViewById(R.id.active_tasks);
        finishedTaskCountView = view.findViewById(R.id.finished_tasks);

        taskList = tasksPoJo.getTasks();

        if(taskList != null && !taskList.isEmpty()) {
            for(int i=0; i<taskList.size(); i++) {
                if(taskList.get(i).getTaskState().equals("Active")) {
                    activeTasksList.add(taskList.get(i).getTask());
                }else if(taskList.get(i).getTaskState().equals("Finished")) {
                    finishedTasksList.add(taskList.get(i).getTask());
                }else{
                    throw new IllegalStateException("Task State is neither 'Active' or 'Finished'");
                }
            }
        }

        String activeCountText = activeTasksList.size()  + ACTIVE_TASKS_TEXT;
        String finishedCountText = finishedTasksList.size() + FINISHED_TASKS_TEXT;

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
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                deleteTask(position);
            }

        };


        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(activeRecyclerView);
        activeAdapter.getCurrentTask().observe(this, hashMap -> {
            activeTasksList.set(((Integer) hashMap.get("position")), (String) hashMap.get("taskText"));
        });

        saveButtonClick();

        return view;
    }

    private void deleteTask(int position) {
        String currentTask = activeTasksList.get(position);
        activeTasksList.remove(position);
        String taskListName = tasksPoJo.getTaskListName();
        if(!activeTasksList.isEmpty()) {
            //Update task from task list to 'Finished' from database
            taskListIds = tasksPoJo.getTasksIds();
            mainViewModel.updateTasks(taskListName, taskListIds.get(position), currentTask, CHANGE_TO_FINISHED);
            taskListIds.remove(position);
            finishedTasksList.add(currentTask);
            String finishedCountText = finishedTasksList.size() + FINISHED_TASKS_TEXT;
            finishedTaskCountView.setText(finishedCountText);
            String activeCountText = activeTasksList.size() + ACTIVE_TASKS_TEXT;
            activeTaskCountView.setText(activeCountText);
        }else{
            //If last element is removed from task list , destroy fragment
            mainViewModel.removeTasksList(taskListName);
            Toast.makeText(getContext(), "CONGRATULATIONS YOU HAVE FINISHED ALL TASKS", Toast.LENGTH_SHORT).show();
            //destroy fragment
            mListener.onFragmentInteraction(true);
        }
        //Notify both recyclerViews that items have changed
        activeAdapter.notifyDataSetChanged();
        finishedAdapter.notifyDataSetChanged();
    }

    private void addButtonClick() {
        addTaskView.setOnClickListener(v -> {
            //TODO call another fragment to add task in EditView
        });
    }

    private void saveButtonClick() {
        saveTaskView.setOnClickListener(v -> {
            for(int i=0; i<activeTasksList.size(); i++) {
                String newTask = activeTasksList.get(i);
                Integer rowId = taskListIds.get(i);
                mainViewModel.updateTasks(tasksPoJo.getTaskListName(), rowId, newTask, CHANGE_TO_ACTIVE);
            }
        });
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
