package com.allandroidprojects.ecomsample.data.viewmodel.product;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.data.models.Product;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.repository.ProductRepository;

public class ItemDetailsViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<Result<Product>> product;
    private MutableLiveData<Result<Product>> cart;
    private static volatile ProductRepository repository;

    public ItemDetailsViewModel(ProductRepository repository){
        this.repository = repository;
    }

    public ItemDetailsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void saveToCartList(Product product){
    }

    public MutableLiveData<Result<Product>> getCart(){
        return cart;
    }
}