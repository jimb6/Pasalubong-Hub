package com.allandroidprojects.ecomsample.ui.common.components.termsandcondition;

import androidx.lifecycle.MutableLiveData;

import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.repository.BusinessRepository;

public class TermsConditionViewModel {

    private static volatile BusinessRepository repository;
    private MutableLiveData<Result<Business>> business;

    public TermsConditionViewModel(BusinessRepository repository){
        this.repository = repository;
    }

    public MutableLiveData<Result<Business>> createNewBusiness(Business businessToSave){
        return business = BusinessRepository.store(businessToSave);
    }

}
