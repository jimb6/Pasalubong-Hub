package com.allandroidprojects.ecomsample.data.viewmodel.product;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.data.models.Product;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.repository.ProductRepository;

public class ProductListViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private static volatile ProductRepository repository;
    private MutableLiveData<Result<Product>> product;
    private MutableLiveData<Result<Product>> sweets;
    private MutableLiveData<Result<Product>> goods;
    private MutableLiveData<Result<Product>> clothing;
    private MutableLiveData<Result<Product>> books;

    public ProductListViewModel(ProductRepository repository) {
        this.repository = repository;
    }

    public ProductListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public MutableLiveData<Result<Product>> getMyProuducts(String category) {
        return product;
    }

    public void fetchMyProducts(String category) {
        product = ProductRepository.show(category);
    }
}
