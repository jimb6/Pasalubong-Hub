package com.allandroidprojects.ecomsample.shop.ui.chat_room;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.allandroidprojects.ecomsample.mvvm.repository.MessagingRepository;

public class MessagingModelFactory implements ViewModelProvider.Factory {
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MessagingViewModel.class))
            return (T) new MessagingViewModel(MessagingRepository.getInstance());
        else
            throw new IllegalArgumentException("Unknown View Model Class");
    }
}
