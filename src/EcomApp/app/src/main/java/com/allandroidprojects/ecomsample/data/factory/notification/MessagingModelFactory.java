package com.allandroidprojects.ecomsample.data.factory.notification;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.allandroidprojects.ecomsample.data.repository.notification.MessagingRepository;
import com.allandroidprojects.ecomsample.ui.composer.user.messaging.messages.MessagingViewModel;

public class MessagingModelFactory extends ViewModelProvider.NewInstanceFactory {

    private Context context;

    public MessagingModelFactory(Context context){
        this.context = context;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MessagingViewModel.class))
            return (T) new MessagingViewModel(MessagingRepository.getInstance(context));
        else
            throw new IllegalArgumentException("Unknown View Model Class");
    }
}
