package com.allandroidprojects.ecomsample.data.viewmodel.notification;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.repository.notification.MessagingRepository;

public class MessagingViewModel extends ViewModel {

    private MessagingRepository messagingRepository;
    private MutableLiveData<Result<String>> serverKey;


    public MessagingViewModel(MessagingRepository repository){
        this.messagingRepository = repository;
    }


    public void getServerKey(){
        this.serverKey = MessagingRepository.getServerKey();
    }

    public MutableLiveData<Result<String>> getServerKeyResult(){
        return this.serverKey;
    }
}
