package com.allandroidprojects.ecomsample.data.viewmodel.account;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.repository.BusinessRepository;

public class AccountViewModel extends ViewModel {

    private MutableLiveData<Result<Business>> business;
    private MutableLiveData<Result<Business>> userBusiness;
    private MutableLiveData<Result<String>> termsAndCon;
    private BusinessRepository businessRepository;


    public AccountViewModel(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }

    public MutableLiveData<Result<Business>> createNewBusiness(Business businessToSave){
        return business = BusinessRepository.store(businessToSave);
    }

    public MutableLiveData<Result<Business>> getSaveBusinessResult() {
        return business;
    }

    public void getBusiness(String id){
        userBusiness = BusinessRepository.show(id);
    }

    public MutableLiveData<Result<Business>> getBusinessResult(){
        return userBusiness;
    }

    public void getTermsAndCondition() {
        termsAndCon = BusinessRepository.getTermsAndCondition();
    }

    public MutableLiveData<Result<String>> getTermsAndConditionData(){
        return this.termsAndCon;
    }
}
