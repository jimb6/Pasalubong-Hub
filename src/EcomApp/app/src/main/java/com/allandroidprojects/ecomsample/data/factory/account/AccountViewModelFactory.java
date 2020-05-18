package com.allandroidprojects.ecomsample.data.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.allandroidprojects.ecomsample.data.view_model.*;

import com.allandroidprojects.ecomsample.data.repository.AccountRepository;
import com.allandroidprojects.ecomsample.data.view_model.AccountViewModel;

public class AccountViewModelFactory implements ViewModelProvider.Factory{

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AccountViewModel.class)) {
            return (T) new AccountViewModel(AccountRepository.getInstance());
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
