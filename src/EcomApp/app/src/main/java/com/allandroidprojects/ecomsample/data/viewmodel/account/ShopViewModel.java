package com.allandroidprojects.ecomsample.data.view_model.account;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.DataResult;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.repository.account.ShopRepository;

public class ShopViewModel extends ViewModel {

    private ShopRepository shopRepository;
    private MutableLiveData<DataResult<Business>> businessDocument;
    private MutableLiveData<Result<Business>> myBusiness;
    private MutableLiveData<Result<Business>> business;

    public ShopViewModel(ShopRepository shopRepository){
        this.shopRepository = shopRepository;
    }

    public void businessDataChange(Business business) {
        businessDocument = ShopRepository.isBusinessDocumentChanged(business);
    }

    public MutableLiveData<DataResult<Business>> getBusinessDocumentChangesResult(){
        return businessDocument;
    }

    public void validateBusiness(Business business) {
        myBusiness = ShopRepository.getMyBusiness(business);
    }

    public MutableLiveData<Result<Business>> getBusinessPreferences(){
        return myBusiness;
    }

    public void getBusinessInfo(String ownerId) {
        business = ShopRepository.getMyBusiness(ownerId);
    }

    public MutableLiveData<Result<Business>> getBusinessInfo(){
        return business;
    }

    public void findAllBusiness() {
        business = ShopRepository.findAllBusiness();
    }

    public MutableLiveData<Result<Business>> getAllBusiness(){
        return business;
    }
}
