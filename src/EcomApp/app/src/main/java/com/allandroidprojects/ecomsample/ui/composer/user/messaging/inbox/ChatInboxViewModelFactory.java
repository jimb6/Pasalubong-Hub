package com.allandroidprojects.ecomsample.ui.common.components.messaging.inbox;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.allandroidprojects.ecomsample.data.repository.notification.MessagingRepository;

public class ChatInboxViewModelFactory extends ViewModelProvider.NewInstanceFactory{


    private Context context;

    public ChatInboxViewModelFactory(Context context){
        this.context = context;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ChatInboxViewModel.class)){
            return (T) ChatInboxViewModel.newInstance(new MessagingRepository(context));
        }else{
            throw new IllegalArgumentException(("Unknown ViewModel class"));
        }
    }
}
