package com.example.smarttasks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import com.example.smarttasks.presenter.FragmentNavigationController;
import com.example.smarttasks.presenter.ViewModelFactory;
import com.example.smarttasks.presenter.fragments.AddNewTaskFragment;
import com.example.smarttasks.presenter.fragments.GridFragment;
import com.example.smarttasks.presenter.fragments.TaskListViewFragment;
import com.example.smarttasks.presenter.viewmodels.MainViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TaskListViewFragment.OnFragmentInteractionListener, AddNewTaskFragment.OnAddNewTaskFragmentInteractionListener {

    //Views
    private ViewModelFactory factory;
    private MainViewModel mainViewModel;
    private Fragment gridFragment = new GridFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        factory = new ViewModelFactory(getApplicationContext());
        mainViewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);

        callFragment();
    }

    private void callFragment() {
        FragmentNavigationController.addFragment(R.id.fragment_container, gridFragment, null, getSupportFragmentManager());
    }



    //OpenTaskListFragment response
    @Override
    public void onFragmentInteraction(Boolean fragmentClosed, Fragment currentFragment) {

    }

    //AddNewTaskFragment Response
    @Override
    public void onAddNewTaskFragmentInteraction(Boolean fragmentClosed) {
        /*
        mainViewModel.getAllTasks(tasksPoJo.getTaskListName());

         */
    }


    @Override
    public void onBackPressed() {
        ArrayList<Fragment> allFragments = (ArrayList<Fragment>) getSupportFragmentManager().getFragments();

        //TODO check if current attached fragment implements OnBackPressedListener and call onBackPressed accordingly, if not call super.onBackPressed()
        for(int i=0; i<allFragments.size(); i++) {
            if(allFragments.get(i) != null && allFragments.get(i) instanceof TaskListViewFragment) {
                ((TaskListViewFragment) allFragments.get(i)).onBackPressed();
                return;
            }
        }
        super.onBackPressed();
    }

    public MainViewModel getMainViewModel() {
        return mainViewModel;
    }

    public LifecycleOwner getLifecycleOwner() { return MainActivity.this; }

    //TODO Before creating new table, check if there is an empty table in database, if it is there
    // Then just call addTasks() and insert list into table. If there is no  empty table in database
    // First call getApplicationContext().deleteSharedPreferences("lastTaskListId")
    // Then call addTasksList() which will automaticly create a new table and fill it with list.
}
