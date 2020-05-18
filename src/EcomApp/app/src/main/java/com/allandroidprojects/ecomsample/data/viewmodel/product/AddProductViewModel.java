package com.allandroidprojects.ecomsample.data.viewmodel.product;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.models.Product;
import com.allandroidprojects.ecomsample.data.remote.product.AddProductRepository;

import java.util.ArrayList;

public class AddProductViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private static volatile AddProductRepository repository;
    private static MutableLiveData<Result<Product>> products;
    private static MutableLiveData<Result<Product>> product;
    private MutableLiveData<Result<Uri>> productImages;
    private MutableLiveData<Boolean> isProductDeleted;

    public AddProductViewModel(AddProductRepository repository){
        AddProductViewModel.repository = repository;
    }

    public AddProductViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public void saveNewProduct(Product product){
        AddProductViewModel.product = AddProductRepository.saveNewProduct(product);
    }

    public MutableLiveData<Result<Product>> getNewProductResult(){
        return product;
    }

    public void saveNewProductImages(String ownerId, ArrayList<Uri> images) {
        this.productImages = AddProductRepository.saveNewProductImages(ownerId, images);
    }

    public MutableLiveData<Result<Uri>> getNewProductImagesResult() {
        return productImages;
    }

    public void updateProduct(Product productToUpdate) {
        product = AddProductRepository.updateProduct(productToUpdate);
    }

    public MutableLiveData<Result<Product>> getUpdatedProduct() {
        return product;
    }

    public void deleteProduct(Product productToUpdate) {
        this.isProductDeleted = AddProductRepository.deleteProduct(productToUpdate);
    }

    public MutableLiveData<Boolean> isProductDeleted() {
        return isProductDeleted;
    }
}
