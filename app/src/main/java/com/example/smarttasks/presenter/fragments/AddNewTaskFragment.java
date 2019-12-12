package com.example.smarttasks.presenter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smarttasks.R;

public class AddNewTaskFragment extends Fragment {

    //Constants
    private final String TAG = getClass().toString();

    //Vars
    private OnAddNewTaskFragmentInteractionListener mListener;

    //Views
    private Button cancelButton;
    private Button confirmButton;


    public AddNewTaskFragment() {
        //Require empty constructor
    }

    public interface OnAddNewTaskFragmentInteractionListener {
        void onAddNewTaskFragmentInteraction(Boolean fragmentClosed);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add_new_task, container, false);

        cancelButton = view.findViewById(R.id.cancel_button);
        confirmButton = view.findViewById(R.id.confirm_button);

        cancelClicked();
        confirmClicked();

        return view;
    }

    public void cancelClicked() {
        cancelButton.setOnClickListener(v -> {
            //TODO add cancel logic here
        });
    }

    public void confirmClicked() {
        confirmButton.setOnClickListener(v -> {
            //TODO add confirm logic here
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
