package com.allandroidprojects.ecomsample.ui.composer.merchant.ordermanagement;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.data.models.ProductOrder;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.repository.OrderRepository;

public class OrderListViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private static volatile OrderRepository repository;
    private MutableLiveData<Result<ProductOrder>> order;
    private MutableLiveData<Result<String>> updateStatus;

    public OrderListViewModel(OrderRepository repository){
        this.repository = repository;
    }

    public void getMerchantOrders(String merchantId, String category){
        order = repository.getMerchantOrderByCategory(merchantId, category);
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
