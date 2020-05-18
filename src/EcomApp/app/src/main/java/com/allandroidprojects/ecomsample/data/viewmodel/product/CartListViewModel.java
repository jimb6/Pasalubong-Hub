package com.allandroidprojects.ecomsample.data.viewmodel.product;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.data.models.Product;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.repository.ProductRepository;

public class CartListViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<Result<Product>> product;
    private static volatile ProductRepository repository;

    public CartListViewModel(ProductRepository repository){
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
        product = ProductRepository.show(userId);
    }

    public MutableLiveData<Result<Product>> getCartList(){
        return product;
    }


}