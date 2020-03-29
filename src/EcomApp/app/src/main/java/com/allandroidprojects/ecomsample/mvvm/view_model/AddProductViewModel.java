package com.allandroidprojects.ecomsample.mvvm.view_model;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.model.product.Product;
import com.allandroidprojects.ecomsample.mvvm.repository.AddProductRepository;
import com.allandroidprojects.ecomsample.startup.data.Result;

import java.util.ArrayList;

public class AddProductViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private static volatile AddProductRepository repository;
    private static MutableLiveData<Result<Product>> products;
    private static MutableLiveData<Result<Product>> product;
    private MutableLiveData<Result<Uri>> productImages;

    public AddProductViewModel(AddProductRepository repository){
        this.repository = repository;
    }

    public AddProductViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public void saveNewProduct(Product product){
        this.product = AddProductRepository.saveNewProduct(product);
    }

    public MutableLiveData<Result<Product>> getNewProductResult(){
        return product;
    }

    public void saveNewProductImages(String ownerId, ArrayList<Uri> arrayList) {
        this.productImages = AddProductRepository.saveNewProductImages(ownerId, arrayList);
    }

    public MutableLiveData<Result<Uri>> getNewProductImagesResult() {
        return productImages;
    }
}
