package com.allandroidprojects.ecomsample.ui.common.components.messaging.inbox;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.repository.notification.MessagingRepository;
import com.allandroidprojects.ecomsample.data.models.Inbox;

public class ChatInboxViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private MessagingRepository repository;
    private MutableLiveData<Result<Inbox>> inboxes;


    public static ChatInboxViewModel newInstance(MessagingRepository repository){
        return new ChatInboxViewModel(repository);
    }

    public ChatInboxViewModel(MessagingRepository repository){
        this.repository = repository;
    }

    public void getAllInbox(String userId){
        inboxes = repository.getMyInbox(userId);
    }

    public MutableLiveData<Result<Inbox>> getAllInboxMutableLiveData(){
        return inboxes;
    }

}
