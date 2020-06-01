package com.allandroidprojects.ecomsample.data.viewmodel.product;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.data.models.Product;
import com.allandroidprojects.ecomsample.data.models.ProductOrder;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.repository.OrderRepository;

public class ItemDetailsViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<Result<Product>> product;
    private MutableLiveData<Result<Product>> cart;
    private  MutableLiveData<Result<ProductOrder>> productOrder;
    private volatile OrderRepository repository;

    public ItemDetailsViewModel(OrderRepository repository){
        this.repository = repository;
    }

    public ItemDetailsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void saveToCartList(Product product){
    }

    public void storeOrder(ProductOrder order, Context context){
      if (repository != null){
        productOrder = repository.store(order);
      }
      else{
          Toast.makeText(context, "Unable to process order.", Toast.LENGTH_SHORT).show();
      }
    }

    public MutableLiveData<Result<ProductOrder>> storeOrderResponse(){
        return productOrder;
    }

    public MutableLiveData<Result<Product>> getCart(){
        return cart;
    }
}