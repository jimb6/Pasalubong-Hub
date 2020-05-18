package com.allandroidprojects.ecomsample.data.view_model.notification;

import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.data.repository.notification.MessagingRepository;

public class MessagingViewModel extends ViewModel {

    private MessagingRepository messagingRepository;


    public MessagingViewModel(MessagingRepository repository){
        this.messagingRepository = repository;
    }

}
