package com.allandroidprojects.ecomsample.ui.composer.user.ordermanagement;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.data.models.ProductOrder;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.repository.OrderRepository;

public class BuyProductViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    // TODO: Implement the ViewModel
    private static volatile OrderRepository repository;
    private MutableLiveData<Result<ProductOrder>> order;
    private MutableLiveData<Result<String>> updateStatus;

    public BuyProductViewModel(OrderRepository repository){
        this.repository = repository;
    }


    public void getUserOrders(String userId){
        order = repository.getUserOrder(userId);
    }

    public MutableLiveData<Result<ProductOrder>> getUserOrdersResult(){
        return order;
    }


    public MutableLiveData<Result<ProductOrder>> getMerchantOrdersResult(){
        return order;
    }

    public void updateOrderdStatus(String reference, String status) {
        order = repository.updateOrderedProduct(reference, status);
    }

    public MutableLiveData<Result<ProductOrder>> getUpdateResponse(){
        return order;
    }
}
