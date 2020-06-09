package com.allandroidprojects.ecomsample.ui.common.components.messaging.messages;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.repository.notification.MessagingRepository;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.model.Inbox;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.model.Message;

public class MessagingViewModel extends ViewModel {
    private MessagingRepository repository;
    private MutableLiveData<Result<Message>> messages;
    private MutableLiveData<Result<Message>> newMessage;
    private MutableLiveData<Result<Message>> messageListener;


    public static MessagingViewModel newInstance(MessagingRepository repository){
        return new MessagingViewModel(repository);
    }

    public MessagingViewModel(MessagingRepository repository){
        this.repository = repository;
    }

    public void getInboxMessages(Inbox inbox){
        String inboxId = inbox.getChatroomId();
        messages = repository.getInboxMessages(inboxId);
    }

    public MutableLiveData<Result<Message>> getAllInboxMessageMutableLiveData(){
        return messages;
    }

    public void sendMessage(String uid, Message message) {
        newMessage = repository.sendMessage(uid, message);
    }

    public MutableLiveData<Result<Message>> sendMessageResult(){
        return newMessage;
    }

    public void registerEventListener(Inbox inbox){
        messageListener = repository.registerMessageListener(inbox.getChatroomId());
    }

    public void registerEventListener(String id){
        messageListener = repository.registerMessageListener(id);
    }

    public MutableLiveData<Result<Message>> newMessageArrived(){
        return messageListener;
    }
}
