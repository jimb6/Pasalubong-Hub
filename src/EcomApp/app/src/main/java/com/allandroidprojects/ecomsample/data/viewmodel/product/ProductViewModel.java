package com.allandroidprojects.ecomsample.data.view_model.product;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.models.SearchData;
import com.allandroidprojects.ecomsample.data.models.product.Product;
import com.allandroidprojects.ecomsample.data.repository.account.ShopRepository;

public class ProductViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<Result<Product>> searchProduct;
    private MutableLiveData<Result<Product>> myProduct;
    private static volatile ShopRepository repository;

    public ProductViewModel(ShopRepository repository){
        ProductViewModel.repository = repository;
    }

    public ProductViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public MutableLiveData<Result<Product>> getMyProuducts(){
        return myProduct;
    }

    public void fetchMyProducts(Business business){
        myProduct = ShopRepository.findMyProducts(business);
    }

    public void searchProducts(SearchData searchData) {
        searchProduct = ShopRepository.searchProducts(searchData);
    }

    public MutableLiveData<Result<Product>> getSearchProduct() {
        return searchProduct;
    }


}