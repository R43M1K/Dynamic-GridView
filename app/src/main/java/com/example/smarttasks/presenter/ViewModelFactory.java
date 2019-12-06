package com.example.smarttasks.presenter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.smarttasks.business.tasks.TasksOperations;
import com.example.smarttasks.business.tasks.TasksOperationsUseCase;
import com.example.smarttasks.presenter.viewmodels.MainViewModel;
import com.example.smarttasks.repository.ProvideTasksOperationsInter;
import com.example.smarttasks.repository.taskshandler.ProvideTasksOperationsRepo;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private Context context;

    public ViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if(modelClass.isAssignableFrom(MainViewModel.class)) {

            ProvideTasksOperationsInter provideTasksOperationsInter = new ProvideTasksOperationsRepo(context);
            TasksOperationsUseCase tasksOperationsUseCase = new TasksOperations(provideTasksOperationsInter);

            return (T) new MainViewModel(tasksOperationsUseCase);
        }

        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
