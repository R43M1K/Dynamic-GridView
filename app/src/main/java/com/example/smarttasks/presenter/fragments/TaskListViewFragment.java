package com.example.smarttasks.presenter.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.example.smarttasks.presenter.OnBackPressedListener;
import com.example.smarttasks.presenter.adapter.recycler.ActiveTasksRecyclerAdapter;
import com.example.smarttasks.presenter.adapter.recycler.FinishedTasksRecyclerAdapter;
import com.example.smarttasks.presenter.adapter.recycler.SingleTask;
import com.example.smarttasks.presenter.viewmodels.MainViewModel;
import com.example.smarttasks.repository.services.operations.BitmapOperator;
import com.example.smarttasks.repository.services.operations.BlurBuilder;
import com.example.smarttasks.repository.services.preferences.PreferencesService;
import com.example.smarttasks.repository.services.preferences.PreferencesServiceInter;
import com.example.smarttasks.repository.services.tasks.TasksPoJo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import jp.wasabeef.blurry.Blurry;

public class TaskListViewFragment extends Fragment implements OnBackPressedListener {

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
    private LinearLayout listLayout;
    private View mainView;

    //Fragments
    private Fragment fragment;
    private Fragment recyclerGridFragment;

    //RecyclerView classes
    private RecyclerView activeRecyclerView;
    private RecyclerView finishedRecyclerView;
    private ActiveTasksRecyclerAdapter activeAdapter;
    private FinishedTasksRecyclerAdapter finishedAdapter;
    private LinearLayoutManager activeLayoutManager;
    private LinearLayoutManager finishedLayoutManager;

    //Vars
    private OnFragmentInteractionListener mListener;
    private ArrayList<SingleTask> taskList = new ArrayList<>();
    private ArrayList<Integer> taskListIds;
    private ArrayList<String> activeTasksList = new ArrayList<>();
    private ArrayList<String> finishedTasksList = new ArrayList<>();
    private boolean newTask;
    private boolean isKeyboardShowing = false;


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
        }

        addTaskView = view.findViewById(R.id.add_button_fragment);
        listLayout = view.findViewById(R.id.open_list_frame);

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
        //Reset liveData
        mainViewModel.clearTasks();
        newTaskListener();
        blurListener();
        keyboardDetect(view);

        mainView = view;
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
        Collections.reverse(activeTasksList);
    }

    private void deleteTask(int position) {
        String currentTask = activeTasksList.get(position);
        activeTasksList.remove(position);
        String taskListName = tasksPoJo.getTaskListName();
        if(!activeTasksList.isEmpty()) {
            //Take only Active tasks
            taskListIds = new ArrayList<>();
            ArrayList<String> tasks = new ArrayList<>();
            for(int i=0; i<tasksPoJo.getTasks().size(); i++) {
                tasks.add(tasksPoJo.getTasks().get(i).getTask());
            }
            for(int i=0; i<tasksPoJo.getTasksIds().size(); i++) {
                if(tasksPoJo.getTaskCondition().get(i).equals("Active")) {
                    taskListIds.add(tasksPoJo.getTasksIds().get(i));
                }
            }
            Collections.reverse(taskListIds);
            Collections.reverse(tasks);
            int currentPosition = tasks.indexOf(currentTask);

            mainViewModel.updateTasks(taskListName, taskListIds.get(position), currentTask, CHANGE_TO_FINISHED);
            //Change last removed task condition to Finished in taskPoJo TaskCondition ArrayList
            ArrayList<String> conditionList = tasksPoJo.getTaskCondition();
            Collections.reverse(conditionList);
            conditionList.set(currentPosition, CHANGE_TO_FINISHED);
            Collections.reverse(conditionList);
            tasksPoJo.setTaskCondition(conditionList);
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
                FragmentNavigationController.addFragment(R.id.fragment_container, fragment, BACK_STACK_FRAG, fragmentManager);
                addTaskView.setVisibility(View.GONE);
                //Blur with below function
                Blurry.with(getContext()).radius(25).sampling(2).onto(listLayout);
            }
        });
    }

    private void blurListener() {
        mainViewModel.getNeedBlur().observe(this, apply -> {
            Blurry.delete(listLayout);
            addTaskView.setVisibility(View.VISIBLE);
        });

    }


    //Observe a new task from addNewTask fragment
    private void newTaskListener() {
        mainViewModel.getNewTask().observe(this, hashMap -> {
            if(!hashMap.isEmpty()) {
                ArrayList<HashMap<String, String>> newTasks = new ArrayList<>();
                newTasks.add(hashMap);
                mainViewModel.addTasks(tasksPoJo.getTaskListRealName(), tasksPoJo.getTaskListName(), newTasks);
                activeTasksList.add(hashMap.get("taskName"));
                String activeTaskCountText = activeTasksList.size() + ACTIVE_TASKS_TEXT;
                activeTaskCountView.setText(activeTaskCountText);
                activeAdapter.notifyDataSetChanged();
                mainViewModel.getAllTasks(tasksPoJo.getTaskListName());
            }
        });
    }

    private void keyboardDetect(View contentView) {

        contentView.getViewTreeObserver().addOnGlobalLayoutListener(
                () -> {
                    Rect r = new Rect();
                    contentView.getWindowVisibleDisplayFrame(r);
                    int screenHeight = contentView.getRootView().getHeight();

                    // r.bottom is the position above soft keypad or device button.
                    // if keypad is shown, the r.bottom is smaller than that before.
                    int keypadHeight = screenHeight - r.bottom;


                    if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                        // keyboard is opened
                        if (!isKeyboardShowing) {
                            isKeyboardShowing = true;
                            addTaskView.setVisibility(View.GONE);
                        }
                    }
                    else {
                        // keyboard is closed
                        if (isKeyboardShowing) {
                            isKeyboardShowing = false;
                            addTaskView.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void removeMe() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            recyclerGridFragment = new RecyclerWithGridFragment();
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            FragmentNavigationController.replaceFragment(R.id.fragment_container, recyclerGridFragment, fragmentManager);
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
