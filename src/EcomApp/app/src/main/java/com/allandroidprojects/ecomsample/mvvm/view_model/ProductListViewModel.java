package com.allandroidprojects.ecomsample.mvvm.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.model.product.Product;
import com.allandroidprojects.ecomsample.mvvm.repository.ProductListRepository;
import com.allandroidprojects.ecomsample.startup.data.Result;

public class ProductListViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private static volatile ProductListRepository repository;
    private MutableLiveData<Result<Product>> product;

    public ProductListViewModel(ProductListRepository repository){
        this.repository = repository;
    }

    public ProductListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public MutableLiveData<Result<Product>> getMyProuducts(){
        return product;
    }

    public void fetchMyProducts(){
        product = ProductListRepository.getAllProducts();
    }

}
