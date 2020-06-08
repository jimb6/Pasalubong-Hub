package com.allandroidprojects.ecomsample.ui.common.components.messaging.messages;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.allandroidprojects.ecomsample.data.repository.notification.MessagingRepository;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.inbox.ChatInboxViewModel;

public class MessagingViewModelFactory extends ViewModelProvider.NewInstanceFactory{


    private Context context;

    public MessagingViewModelFactory(Context context){
        this.context = context;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MessagingViewModel.class)){
            return (T) MessagingViewModel.newInstance(new MessagingRepository(context));
        }else{
            throw new IllegalArgumentException(("Unknown ViewModel class"));
        }
    }
}
