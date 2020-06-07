package com.allandroidprojects.ecomsample.ui.composer.user.startup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.Product;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.repository.BusinessRepository;
import com.allandroidprojects.ecomsample.data.repository.ProductRepository;

public class StartupViewModel extends ViewModel {

    private MutableLiveData<Result<Business>> business;
    private ProductRepository repository;
    private MutableLiveData<Result<Product>> product;

    public StartupViewModel(ProductRepository repository) {
        this.repository = repository;
    }

    public StartupViewModel() {
    }


    public MutableLiveData<Result<Product>> getMyProuducts(String category) {
        return product;
    }

    public void fetchMyProducts(String category) {
        product = ProductRepository.show(category);
    }
}
