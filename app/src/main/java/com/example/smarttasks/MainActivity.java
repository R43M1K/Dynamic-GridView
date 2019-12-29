package com.example.smarttasks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import com.example.smarttasks.presenter.FragmentNavigationController;
import com.example.smarttasks.presenter.OnBackPressedListener;
import com.example.smarttasks.presenter.ViewModelFactory;
import com.example.smarttasks.presenter.fragments.AddNewTaskFragment;
import com.example.smarttasks.presenter.fragments.GridFragment;
import com.example.smarttasks.presenter.fragments.RecyclerWithGridFragment;
import com.example.smarttasks.presenter.fragments.TaskListViewFragment;
import com.example.smarttasks.presenter.viewmodels.MainViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TaskListViewFragment.OnFragmentInteractionListener, AddNewTaskFragment.OnAddNewTaskFragmentInteractionListener {

    //Views
    private ViewModelFactory factory;
    private MainViewModel mainViewModel;
    private Fragment recyclerWithGridFragment = new RecyclerWithGridFragment();
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
        FragmentNavigationController.addFragment(R.id.fragment_container, recyclerWithGridFragment, null, getSupportFragmentManager());
        //FragmentNavigationController.addFragment(R.id.fragment_container, gridFragment, null, getSupportFragmentManager());
    }



    //OpenTaskListFragment response
    @Override
    public void onFragmentInteraction(Boolean fragmentClosed, Fragment currentFragment) {

    }

    //AddNewTaskFragment Response
    @Override
    public void onAddNewTaskFragmentInteraction(Boolean fragmentClosed) {

    }


    @Override
    public void onBackPressed() {
        ArrayList<Fragment> allFragments = (ArrayList<Fragment>) getSupportFragmentManager().getFragments();

        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        int index = backStackCount - 1;
        Fragment fragment = allFragments.get(index);
        if (fragment != null && fragment.isAdded() && fragment instanceof OnBackPressedListener) {
            ((OnBackPressedListener) fragment).onBackPressed();
        }else if(backStackCount == 1 && fragment instanceof RecyclerWithGridFragment) {
            //if user is in GridView and presses back , go straight to android menu , not to empty activity
            getSupportFragmentManager().popBackStack();
            super.onBackPressed();
        }else{
            super.onBackPressed();
        }
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
