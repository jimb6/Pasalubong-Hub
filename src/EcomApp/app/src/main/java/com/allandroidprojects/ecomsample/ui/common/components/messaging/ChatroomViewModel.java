package com.allandroidprojects.ecomsample.ui.common.components.messaging;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.LoggedInUser;
import com.allandroidprojects.ecomsample.data.models.Message;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.repository.notification.MessagingRepository;

public class ChatroomViewModel extends ViewModel {

    private MessagingRepository repository;
    private MutableLiveData<Result<Message>> messages;
    private MutableLiveData<Result<LoggedInUser>> user;
    private MutableLiveData<Result<Business>> business;
    private MutableLiveData<Result<Message>> newMessage;
    private MutableLiveData<Result<Message>> message;

    public ChatroomViewModel(MessagingRepository repository){
        this.repository = repository;
    }


    public void sendNewMessage(String sender, Business receiver, Message myMessage){
        newMessage = repository.sendNewMesage(sender, receiver, myMessage);
    }

    public MutableLiveData<Result<Message>> sendNewMessageResult(){
        return newMessage;
    }

    public void sendMessage(String sender, String receiver, Message myMessage){
        message = repository.sendMessage(sender, receiver, myMessage);
    }

    public MutableLiveData<Result<Message>> sendMessageResult(){
        return message;
    }



    public void getUserInfo(String userId){
        user = repository.getUserDetails(userId);
    }

    public MutableLiveData<Result<LoggedInUser>> getUserInfoResult(){
        return user;
    }

    public void getBusinessDetails(String businessId){
        business = repository.getBusinessDetails(businessId);
    }

    public MutableLiveData<Result<Business>> getBusinessDetailsResult(){
        return business;
    }

    public void getMessages(String userId, String businessId){
        messages = repository.getMessage(userId, businessId);
    }

    public MutableLiveData<Result<Message>> getMessagesResult(){
        return this.messages;
    }
}
