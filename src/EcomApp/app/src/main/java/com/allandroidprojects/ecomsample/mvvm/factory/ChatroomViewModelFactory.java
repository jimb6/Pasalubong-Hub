package com.allandroidprojects.ecomsample.mvvm.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.allandroidprojects.ecomsample.mvvm.repository.ChatroomRepository;
import com.allandroidprojects.ecomsample.mvvm.repository.ProductListRepository;
import com.allandroidprojects.ecomsample.mvvm.view_model.CartListViewModel;
import com.allandroidprojects.ecomsample.mvvm.view_model.ChatroomViewModel;

public class ChatroomViewModelFactory implements ViewModelProvider.Factory {
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ChatroomViewModel.class)){
            return (T) new ChatroomViewModel(ChatroomRepository.getInstance());
        }else{
            throw new IllegalArgumentException(("Unknown ViewModel class"));
        }
    }
}
