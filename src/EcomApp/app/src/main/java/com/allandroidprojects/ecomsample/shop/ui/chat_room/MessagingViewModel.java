package com.allandroidprojects.ecomsample.shop.ui.chat_room;

import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.mvvm.repository.MessagingRepository;

public class MessagingViewModel extends ViewModel {

    private MessagingRepository messagingRepository;


    public MessagingViewModel(MessagingRepository repository){
        this.messagingRepository = repository;
    }

}
