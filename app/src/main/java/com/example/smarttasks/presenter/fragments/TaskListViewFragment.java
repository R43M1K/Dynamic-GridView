package com.example.smarttasks.presenter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttasks.MainActivity;
import com.example.smarttasks.R;
import com.example.smarttasks.presenter.FragmentNavigationController;
import com.example.smarttasks.presenter.OnBackPressed;
import com.example.smarttasks.presenter.adapter.recycler.ActiveTasksRecyclerAdapter;
import com.example.smarttasks.presenter.adapter.recycler.FinishedTasksRecyclerAdapter;
import com.example.smarttasks.presenter.adapter.recycler.SingleTask;
import com.example.smarttasks.presenter.viewmodels.MainViewModel;
import com.example.smarttasks.repository.services.preferences.PreferencesService;
import com.example.smarttasks.repository.services.preferences.PreferencesServiceInter;
import com.example.smarttasks.repository.services.tasks.TasksPoJo;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskListViewFragment extends Fragment implements OnBackPressed {

    //Constants
    private final String TAG = getClass().toString();
    private final String BACK_STACK_FRAG = "backFrag";
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

    //Fragments
    private Fragment fragment;
    private Fragment gridFragment;

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
    private boolean newTask;

    private PreferencesServiceInter preferences;
    private TasksPoJo tasksPoJo;
    private MainViewModel mainViewModel;
    private TextWatcher textWatcher;

    public TaskListViewFragment() {
        //Required empty constructor
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Boolean fragmentClosed, Fragment currentFragment);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragment = new AddNewTaskFragment();
        tasksPoJo = TasksPoJo.getInstance();
        preferences = PreferencesService.getInstance(getActivity().getBaseContext()); //Try to use getContext
        newTask = false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_open_task_list, container, false);

        init();


        // Initialize Views
        taskListNameView = view.findViewById(R.id.task_list_name);
        if (textWatcher != null) taskListNameView.removeTextChangedListener(textWatcher);


        if(tasksPoJo.getTaskListRealName() != null && !tasksPoJo.getTaskListRealName().isEmpty()) {
            taskListNameView.setText(tasksPoJo.getTaskListRealName());
        } else {
            newTask = true;
            String taskListRealName = taskListNameView.getText().toString();
            tasksPoJo.setTaskListRealName(taskListRealName);
            mainViewModel.addTasksList(taskListRealName, new ArrayList<>());
            tasksPoJo.setTaskListName(preferences.get("currentTableName", ""));
        }

        addTaskView = view.findViewById(R.id.add_button_fragment);
        saveTaskView = view.findViewById(R.id.save_button_fragment);

        // Calculate active and finished tasks counts
        activeTaskCountView = view.findViewById(R.id.active_tasks);
        finishedTaskCountView = view.findViewById(R.id.finished_tasks);



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

        //Get edited task from active recyclerView
        activeAdapter.getCurrentTask().observe(this, hashMap -> {
            activeTasksList.set(((Integer) hashMap.get("position")), (String) hashMap.get("taskText"));
        });

        if (textWatcher == null) {
            textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    tasksPoJo.setTaskListRealName(s.toString());
                    if (newTask) {
                        tasksPoJo.setTaskListName(preferences.get("currentTableName", ""));
                    }
                    mainViewModel.changeTaskListRealName(tasksPoJo.getTaskListName(), s.toString());
                }
            };
        }

        taskListNameView.addTextChangedListener(textWatcher);

        addButtonClick();
        saveButtonClick();
        //Reset liveData
        mainViewModel.clearTasks();
        newTaskObserver();

        return view;
    }

    private void init() {
        activeTasksList.clear();
        finishedTasksList.clear();
        taskList.clear();

        taskListIds = tasksPoJo.getTasksIds();
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
    }

    private void deleteTask(int position) {
        //TODO change below code for case that new task list is added.
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
        } else {
            removeMe();
        }
        //Notify both recyclerViews that items have changed
        activeAdapter.notifyDataSetChanged();
        finishedAdapter.notifyDataSetChanged();
    }

    private void addButtonClick() {
        addTaskView.setOnClickListener(v -> {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity != null) {
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentNavigationController.addFragment(R.id.open_list_frame, fragment, BACK_STACK_FRAG, fragmentManager);
            }
        });
    }

    private void saveButtonClick() {
        saveTaskView.setOnClickListener(v -> {
            /*
            if(activeTasksList.isEmpty()) {
                Toast.makeText(getContext(), "YOU HAVE NOT ADDED ANY TASKS YET", Toast.LENGTH_SHORT).show();
            }else{
                for (int i = 0; i < activeTasksList.size(); i++) {
                    String newTask = activeTasksList.get(i);
                    Integer rowId = taskListIds.get(i);
                    mainViewModel.updateTasks(tasksPoJo.getTaskListName(), rowId, newTask, CHANGE_TO_ACTIVE);
                }
            }

             */
            removeMe();
        });
    }

    //Observe a new task from addNewTask fragment
    private void newTaskObserver() {
        mainViewModel.getNewTask().observe(this, hashMap -> {
            if(!hashMap.isEmpty()) {
                ArrayList<HashMap<String, String>> newTasks = new ArrayList<>();
                newTasks.add(hashMap);
                mainViewModel.addTasks(tasksPoJo.getTaskListRealName(), tasksPoJo.getTaskListName(), newTasks);
                mainViewModel.getAllTasks(tasksPoJo.getTaskListName());
                activeTasksList.add(hashMap.get("taskName"));
                String activeTaskCountText = activeTasksList.size() + ACTIVE_TASKS_TEXT;
                activeTaskCountView.setText(activeTaskCountText);
                activeAdapter.notifyDataSetChanged();
            }
        });
    }

    private void removeMe() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            gridFragment = new GridFragment();
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentNavigationController.replaceFragment(R.id.fragment_container, gridFragment, fragmentManager);
        }
    }

    @Override
    public void onBackPressed() {
        removeMe();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(taskListNameView != null) taskListNameView.clearFocus();

        if(context instanceof MainActivity) {
            MainActivity activity = (MainActivity) context;
            mainViewModel = activity.getMainViewModel();
        }

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

        if(activeTasksList.isEmpty()) {
            mainViewModel.removeTasksList(tasksPoJo.getTaskListName());
        }
        mListener.onFragmentInteraction(true, this);
    }

}
