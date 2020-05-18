package com.allandroidprojects.ecomsample.data.factory.notification;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.allandroidprojects.ecomsample.data.repository.notification.ChatroomRepository;
import com.allandroidprojects.ecomsample.data.viewmodel.notification.ChatroomViewModel;

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
