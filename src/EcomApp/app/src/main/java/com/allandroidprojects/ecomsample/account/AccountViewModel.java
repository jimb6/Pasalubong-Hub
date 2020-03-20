package com.allandroidprojects.ecomsample.account;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.model.Business;
import com.allandroidprojects.ecomsample.model.LoggedInUser;
import com.allandroidprojects.ecomsample.startup.data.Result;

public class AccountViewModel extends ViewModel {

    private MutableLiveData<Result<Business>> business;
    private MutableLiveData<Result<Business>> userBusiness;
    private AccountRepository accountRepository;

    AccountViewModel(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    public MutableLiveData<Result<Business>> createNewBusiness(Business businessToSave){
        return business = AccountRepository.registerBusiness(businessToSave);
    }

    public MutableLiveData<Result<Business>> getSaveBusinessResult() {
        return business;
    }

    public void getBusiness(LoggedInUser user){
        userBusiness = AccountRepository.getBusiness(user);
    }

    public MutableLiveData<Result<Business>> getBusinessResult(){
        return userBusiness;
    }
}
