package com.allandroidprojects.ecomsample.mvvm.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.model.product.Product;
import com.allandroidprojects.ecomsample.mvvm.repository.ProductListRepository;
import com.allandroidprojects.ecomsample.startup.data.Result;

public class ItemDetailsViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<Result<Product>> product;
    private MutableLiveData<Result<Product>> cart;
    private static volatile ProductListRepository repository;

    public ItemDetailsViewModel(ProductListRepository repository){
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
        cart = ProductListRepository.saveProductToCart(product);
    }

    public MutableLiveData<Result<Product>> getCart(){
        return cart;
    }
}