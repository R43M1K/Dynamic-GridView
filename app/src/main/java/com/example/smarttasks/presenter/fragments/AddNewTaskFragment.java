package com.example.smarttasks.presenter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.smarttasks.MainActivity;
import com.example.smarttasks.R;
import com.example.smarttasks.presenter.ViewModelFactory;
import com.example.smarttasks.presenter.recyclerview.SingleTask;
import com.example.smarttasks.presenter.viewmodels.MainViewModel;
import com.example.smarttasks.repository.services.tasks.TasksPoJo;

import java.util.ArrayList;
import java.util.HashMap;

public class AddNewTaskFragment extends Fragment {

    //Constants
    private final String TAG = getClass().toString();
    private final String BACK_STACK_FRAG = "backFrag";
    private final String CHANGE_TO_ACTIVE = "Active";

    //Vars
    private OnAddNewTaskFragmentInteractionListener mListener;

    //Views
    private Button cancelButton;
    private Button confirmButton;
    private EditText newTaskView;

    private TasksPoJo tasksPoJo;
    private MainViewModel mainViewModel;

    public AddNewTaskFragment() {
        //Require empty constructor
    }

    public interface OnAddNewTaskFragmentInteractionListener {
        void onAddNewTaskFragmentInteraction(Boolean fragmentClosed);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tasksPoJo = TasksPoJo.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add_new_task, container, false);

        cancelButton = view.findViewById(R.id.cancel_button);
        confirmButton = view.findViewById(R.id.confirm_button);
        newTaskView = view.findViewById(R.id.add_new_task);


        cancelClicked();
        confirmClicked();

        return view;
    }

    private void cancelClicked() {
        cancelButton.setOnClickListener(v -> onDetach());
    }

    private void confirmClicked() {
        confirmButton.setOnClickListener(v -> {
            if(!newTaskView.getText().toString().isEmpty()) {
                ArrayList<SingleTask> currentTableTasks = tasksPoJo.getTasks();
                SingleTask singleTask = new SingleTask(newTaskView.getText().toString(), CHANGE_TO_ACTIVE);
                currentTableTasks.add(singleTask);
                tasksPoJo.setTasks(currentTableTasks);
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("taskName", newTaskView.getText().toString());
                hashMap.put("taskFinished", CHANGE_TO_ACTIVE);
                mainViewModel.setNewTask(hashMap);
                onDetach();
            }else{
                Toast.makeText(getContext(), "Please fill task field",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof MainActivity) {
            mainViewModel = ((MainActivity) context).getMainViewModel();
        }

        if (context instanceof OnAddNewTaskFragmentInteractionListener) {
            mListener = (OnAddNewTaskFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if(activity != null) {
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .remove(this)
                    .commit();
            activity.getSupportFragmentManager().popBackStack(BACK_STACK_FRAG, activity.getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);
        }
        newTaskView.getText().clear();
        mListener.onAddNewTaskFragmentInteraction(true);
    }
}
