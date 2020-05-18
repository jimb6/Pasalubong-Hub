package com.allandroidprojects.ecomsample.data.viewmodel.product;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.Product;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.models.SearchData;
import com.allandroidprojects.ecomsample.data.repository.ProductRepository;

public class ProductViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<Result<Product>> searchProduct;
    private MutableLiveData<Result<Product>> myProduct;
    private static volatile ProductRepository repository;

    public ProductViewModel(ProductRepository repository){
        ProductViewModel.repository = repository;
    }

    public ProductViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public MutableLiveData<Result<Product>> getMyProducts(){
        return myProduct;
    }

    public void myProducts(Business business){
        myProduct = ProductRepository.show(business);
    }

    public void searchProducts(SearchData searchData) {
        searchProduct = ProductRepository.show(searchData);
    }

    public MutableLiveData<Result<Product>> getSearchProduct() {
        return searchProduct;
    }


}