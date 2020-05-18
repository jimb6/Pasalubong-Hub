package com.allandroidprojects.ecomsample.data;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.allandroidprojects.ecomsample.data.repository.MessagingRepository;
import com.allandroidprojects.ecomsample.data.view_model.MessagingViewModel;

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
