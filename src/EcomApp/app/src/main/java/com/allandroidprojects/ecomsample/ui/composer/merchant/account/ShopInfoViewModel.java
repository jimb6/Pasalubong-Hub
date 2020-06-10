package com.allandroidprojects.ecomsample.ui.composer.merchant.account;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.repository.BusinessRepository;

public class ShopInfoViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private  BusinessRepository repository;
    private ShopInfoViewModel instance;
    private MutableLiveData<Result<Business>> business;

    public ShopInfoViewModel newInstance(BusinessRepository repository){
        if (instance == null){
            return new ShopInfoViewModel();
        }
        return instance;
    }

    public void getShopImages(String businessId){
        business = repository.show(businessId);
    }

    public MutableLiveData<Result<Business>> getShopImagesResult(){
        return business;
    }


    public void saveState(Business myBusiness) {
        business = repository.update(myBusiness);
    }

    public MutableLiveData<Result<Business>> getSavingStateResult(){
        return business;
    }
}
