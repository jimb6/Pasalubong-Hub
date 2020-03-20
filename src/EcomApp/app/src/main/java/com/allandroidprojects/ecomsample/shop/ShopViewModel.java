package com.allandroidprojects.ecomsample.shop;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.model.Business;
import com.allandroidprojects.ecomsample.shop.data.DataResult;
import com.allandroidprojects.ecomsample.shop.data.ShopRepository;

public class ShopViewModel extends ViewModel {

    private ShopRepository shopRepository;
    private MutableLiveData<DataResult<Business>> businessDocument;

    public ShopViewModel(ShopRepository shopRepository){
        this.shopRepository = shopRepository;
    }

    public void businessDataChange(Business business) {
        businessDocument = ShopRepository.isBusinessDocumentChanged(business);
    }

    public MutableLiveData<DataResult<Business>> getBusinessDocumentChangesResult(){
        return businessDocument;
    }
}
