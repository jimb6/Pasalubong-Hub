package com.allandroidprojects.ecomsample.data.viewmodel.account;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.DataResult;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.repository.BusinessRepository;

public class ShopViewModel extends ViewModel {

    private BusinessRepository businessRepository;
    private MutableLiveData<DataResult<Business>> businessDocument;
    private MutableLiveData<Result<Business>> myBusiness;
    private MutableLiveData<Result<Business>> business;

    public ShopViewModel(BusinessRepository businessRepository){
        this.businessRepository = businessRepository;
    }

    public void businessDataChange(Business business) {
        businessDocument = BusinessRepository.isBusinessDocumentChanged(business);
    }

    public MutableLiveData<DataResult<Business>> getBusinessDocumentChangesResult(){
        return businessDocument;
    }

    public void validateBusiness(Business business) {
        myBusiness = BusinessRepository.show(business.getOwnerId());
    }

    public MutableLiveData<Result<Business>> getBusinessPreferences(){
        return myBusiness;
    }

    public void getBusinessInfo(String ownerId) {
        business = BusinessRepository.show(ownerId);
    }

    public MutableLiveData<Result<Business>> getBusinessInfo(){
        return business;
    }

    public void findAllBusiness() {
        business = BusinessRepository.index();
    }

    public MutableLiveData<Result<Business>> getAllBusiness(){
        return business;
    }
}
