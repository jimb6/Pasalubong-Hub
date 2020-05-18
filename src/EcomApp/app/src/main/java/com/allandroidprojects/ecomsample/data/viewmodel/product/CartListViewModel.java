package com.allandroidprojects.ecomsample.data.viewmodel.product;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.models.Product;
import com.allandroidprojects.ecomsample.data.remote.product.ProductListRepository;

public class CartListViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<Result<Product>> product;
    private static volatile ProductListRepository repository;

    public CartListViewModel(ProductListRepository repository){
        CartListViewModel.repository = repository;
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