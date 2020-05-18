package com.allandroidprojects.ecomsample.data.view_model.notification;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.data.repository.notification.ChatroomRepository;

public class ChatroomViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private static volatile ChatroomRepository repository;

    public ChatroomViewModel(ChatroomRepository repository){
        ChatroomViewModel.repository = repository;
    }

    public ChatroomViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }


}
