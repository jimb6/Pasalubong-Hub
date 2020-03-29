package com.allandroidprojects.ecomsample.mvvm.view_model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.model.Business;
import com.allandroidprojects.ecomsample.model.LoggedInUser;
import com.allandroidprojects.ecomsample.shop.data.DataResult;
import com.allandroidprojects.ecomsample.mvvm.repository.ShopRepository;
import com.allandroidprojects.ecomsample.startup.data.Result;

public class ShopViewModel extends ViewModel {

    private ShopRepository shopRepository;
    private MutableLiveData<DataResult<Business>> businessDocument;
    private MutableLiveData<Result<Business>> myBusiness;

    public ShopViewModel(ShopRepository shopRepository){
        this.shopRepository = shopRepository;
    }

    public void businessDataChange(Business business) {
        businessDocument = ShopRepository.isBusinessDocumentChanged(business);
    }

    public MutableLiveData<DataResult<Business>> getBusinessDocumentChangesResult(){
        return businessDocument;
    }

    public void validateBusiness(LoggedInUser user) {
        myBusiness = ShopRepository.getMyBusiness(user);
    }

    public MutableLiveData<Result<Business>> getBusinessPreferences(){
        return myBusiness;
    }
}
