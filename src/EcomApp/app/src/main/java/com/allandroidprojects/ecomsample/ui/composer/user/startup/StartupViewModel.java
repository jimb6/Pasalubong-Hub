package com.allandroidprojects.ecomsample.ui.composer.user.startup;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.repository.BusinessRepository;

public class StartupViewModel extends ViewModel {

    private MutableLiveData<Result<Business>> business;
    private BusinessRepository instance;

    public StartupViewModel(BusinessRepository instance){
        this.instance = instance;
    }

    public StartupViewModel() {
    }

    public void getAllBusiness(){
        business = BusinessRepository.index();
    }

    public MutableLiveData<Result<Business>> getAllBusinessResult(){
        return this.business;
    }

}
