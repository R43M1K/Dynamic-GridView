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
import androidx.fragment.app.Fragment;

import com.example.smarttasks.R;
import com.example.smarttasks.presenter.recyclerview.SingleTask;
import com.example.smarttasks.repository.services.tasks.TasksPoJo;

import java.util.ArrayList;

public class AddNewTaskFragment extends Fragment {

    //Constants
    private final String TAG = getClass().toString();
    private final String CHANGE_TO_ACTIVE = "Active";

    //Vars
    private OnAddNewTaskFragmentInteractionListener mListener;

    //Views
    private Button cancelButton;
    private Button confirmButton;
    private EditText newTaskView;

    private TasksPoJo tasksPoJo;

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

    public void cancelClicked() {
        cancelButton.setOnClickListener(v -> {
            mListener.onAddNewTaskFragmentInteraction(true);
        });
    }

    public void confirmClicked() {
        confirmButton.setOnClickListener(v -> {
            if(!newTaskView.getText().toString().isEmpty()) {
                ArrayList<SingleTask> currentTableTasks;
                currentTableTasks = tasksPoJo.getTasks();
                SingleTask singleTask = new SingleTask(newTaskView.getText().toString(), CHANGE_TO_ACTIVE);
                currentTableTasks.add(singleTask);
                tasksPoJo.setTasks(currentTableTasks);
                mListener.onAddNewTaskFragmentInteraction(true);
            }else{
                Toast.makeText(getContext(), "Please fill task field",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

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
        mListener.onAddNewTaskFragmentInteraction(true);
    }
}