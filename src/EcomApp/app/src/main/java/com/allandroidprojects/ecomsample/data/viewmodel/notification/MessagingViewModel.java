package com.allandroidprojects.ecomsample.data.viewmodel.notification;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.models.fcm.Chatroom;
import com.allandroidprojects.ecomsample.data.repository.notification.MessagingRepository;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.model.Inbox;

public class MessagingViewModel extends ViewModel {

    private MessagingRepository messagingRepository;
    private MutableLiveData<Result<String>> serverKey;
    private MutableLiveData<Result<Inbox>> messageInbox;

    public MessagingViewModel(MessagingRepository repository){
        this.messagingRepository = repository;
    }


    public void getServerKey(){
        this.serverKey = MessagingRepository.getServerKey();
    }

    public MutableLiveData<Result<String>> getServerKeyResult(){
        return this.serverKey;
    }

    public void getInbox(String userId){
        messageInbox = messagingRepository.getMyInbox(userId);
    }

    public MutableLiveData<Result<Inbox>> inboxResult(){
        return messageInbox;
    }
}
