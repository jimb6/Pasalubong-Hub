package com.allandroidprojects.ecomsample.mvvm.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.model.product.Product;
import com.allandroidprojects.ecomsample.mvvm.repository.ProductListRepository;
import com.allandroidprojects.ecomsample.startup.data.Result;

public class CartListViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<Result<Product>> product;
    private static volatile ProductListRepository repository;

    public CartListViewModel(ProductListRepository repository){
        this.repository = repository;
    }

    public CartListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

//    public MutableLiveData<Result<Product>> getMyProuducts(){
//        return myProduct;
//    }
//
//    public void getMyCartProducts(Business business){
//        myProduct = ShopRepository.findMyProducts(business);
//    }

    public void doCartList(String userId){
        product = ProductListRepository.getAllProductsInCart(userId);
    }

    public MutableLiveData<Result<Product>> getCartList(){
        return product;
    }


}