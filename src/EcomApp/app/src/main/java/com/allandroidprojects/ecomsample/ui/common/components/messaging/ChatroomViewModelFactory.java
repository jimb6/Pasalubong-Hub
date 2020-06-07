package com.allandroidprojects.ecomsample.ui.common.components.messaging;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.allandroidprojects.ecomsample.data.repository.notification.MessagingRepository;

public class ChatroomViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private Context context;
    public ChatroomViewModelFactory(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(ChatroomViewModel.class)){
            return (T) new ChatroomViewModel(MessagingRepository.getInstance(context));
        }else{
            throw new IllegalArgumentException(("Unknown ViewModel class"));
        }
    }

}
