package com.allandroidprojects.ecomsample.data.viewmodel.account;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.LoggedInUser;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.repository.account.AccountRepository;

public class AccountViewModel extends ViewModel {

    private MutableLiveData<Result<Business>> business;
    private MutableLiveData<Result<Business>> userBusiness;
    private AccountRepository accountRepository;

    public AccountViewModel(AccountRepository accountRepository) {
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
