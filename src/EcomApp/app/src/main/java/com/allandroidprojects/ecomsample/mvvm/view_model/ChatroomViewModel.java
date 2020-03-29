package com.allandroidprojects.ecomsample.mvvm.view_model;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.model.product.Product;
import com.allandroidprojects.ecomsample.mvvm.repository.AddProductRepository;
import com.allandroidprojects.ecomsample.mvvm.repository.ChatroomRepository;
import com.allandroidprojects.ecomsample.startup.data.Result;

import java.util.ArrayList;

public class ChatroomViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private static volatile ChatroomRepository repository;

    public ChatroomViewModel(ChatroomRepository repository){
        this.repository = repository;
    }

    public ChatroomViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }


}
