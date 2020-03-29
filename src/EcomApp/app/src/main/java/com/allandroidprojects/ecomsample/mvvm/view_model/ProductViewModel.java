package com.allandroidprojects.ecomsample.mvvm.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.model.Business;
import com.allandroidprojects.ecomsample.model.product.Product;
import com.allandroidprojects.ecomsample.mvvm.repository.ShopRepository;
import com.allandroidprojects.ecomsample.startup.data.Result;

public class ProductViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<Result<Product>> myProduct;
    private static volatile ShopRepository repository;

    public ProductViewModel(ShopRepository repository){
        this.repository = repository;
    }

    public ProductViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public MutableLiveData<Result<Product>> getMyProuducts(){
        return myProduct;
    }

    public void fetchMyProducts(Business business){
        myProduct = ShopRepository.findMyProducts(business);
    }


}