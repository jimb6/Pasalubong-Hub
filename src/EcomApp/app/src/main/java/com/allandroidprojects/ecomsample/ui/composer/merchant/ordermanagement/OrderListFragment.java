package com.allandroidprojects.ecomsample.ui.composer.merchant.ordermanagement;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.factory.product.OrderListViewModelFactory;
import com.allandroidprojects.ecomsample.data.models.ProductOrder;
import com.allandroidprojects.ecomsample.data.models.Rating;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.util.ProductOrderStatus;
import com.facebook.drawee.view.SimpleDraweeView;
import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;

import java.util.ArrayList;

public class OrderListFragment extends Fragment {

    private OrderListViewModel mViewModel;
    private View root;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private Skeleton skeleton;
    private ArrayList<ProductOrder> orders = new ArrayList<>();
    private String orderStatus;
    private String merchantId;


    public static OrderListFragment newInstance() {
        return new OrderListFragment();
    }

    private void refreshData() {
        orders = new ArrayList<>();
        setupRecyclerView();
        skeleton = SkeletonLayoutUtils.applySkeleton(recyclerView, R.layout.shop_list_item, 4);
        skeleton.showSkeleton();

        mViewModel.getMerchantOrders(merchantId, orderStatus);
        mViewModel.getMerchantOrdersResult().observe(getViewLifecycleOwner(), p -> {
            skeleton.showOriginal();
            setupRecyclerView();
            if (p instanceof Result.Success) {
                orders.add((ProductOrder) ((Result.Success) p).getData());
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView = root.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SimpleStringRecyclerViewAdapter(recyclerView, orders);
        recyclerView.setAdapter(adapter);
    }

    private void setupViewModel(){
        OrderListViewModelFactory factory = new OrderListViewModelFactory(getContext());
        mViewModel = ViewModelProviders.of(this, factory).get(OrderListViewModel.class);
    }

    protected void updateOrderedProduct(String reference, ProductOrderStatus status, View loadingView){
        String title = "CONFIRMATION", message = "YOU WANT TO " + status.get().toUpperCase() + " ORDER?";
        LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(getContext(), DialogTypes.TYPE_QUESTION)
                .setTitle(title)
                .setDescription(message)
                .setPositiveText("Okay")
                .setPositiveListener(lottieAlertDialog -> {
                    update(reference, status);
                    lottieAlertDialog.dismiss();
                }).setNegativeText("Cancel").setNegativeListener(lottieAlertDialog -> {
                    lottieAlertDialog.dismiss();
                    loadingView.setVisibility(View.GONE);
                }).build();
        alertDialog.show();
    }

    protected void showProductInfo(ProductOrder item){
        Intent intent = new Intent(getActivity(), OrderInfoActivity.class);
        intent.putExtra("ORDER", item);
        startActivity(intent);
    }

    private void update(String reference, ProductOrderStatus status){
        mViewModel.updateOrderdStatus(reference, status.get());
        mViewModel.getUpdateResponse().observe(this, result ->{
            String title = "Order Update", message = "No Message";
            int type;
            if (result instanceof Result.Success){
                ProductOrder order = (ProductOrder) ((Result.Success) result).getData();
                message = "Order Successfully updated:\nReference: " + order;
                type = DialogTypes.TYPE_SUCCESS;

            }else{
                message = "Unable to update product.";
                type = DialogTypes.TYPE_ERROR;
            }
            refreshData();
            showStatusDialog(title, message, type);
        });
    }

    private void showStatusDialog(String title, String message, int type){
        LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(getContext(), type)
                .setTitle(title)
                .setDescription(message)
                .setPositiveText("Close")
                .setPositiveListener(lottieAlertDialog -> {
                    lottieAlertDialog.dismiss();
                }).build();
        alertDialog.show();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.order_list_fragment, container, false);

        setupViewModel();
        final SwipeRefreshLayout pullToRefresh = root.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(() -> {
            refreshData();
            pullToRefresh.setRefreshing(false);
        });

        Bundle bundle = getArguments();
//        if (!bundle.containsKey("type")){
//            return root;
//        }
//
//        if (!bundle.containsKey("businessID")){
//            return root;
//        }

        orderStatus = bundle.getString("type");
        merchantId = bundle.getString("businessID");

        setupRecyclerView();
        // or apply a new SkeletonLayout to a RecyclerView (showing 5 items)
        skeleton = SkeletonLayoutUtils.applySkeleton(recyclerView, R.layout.order_list_item, 4);
        refreshData();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel
    }


    public class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private ArrayList<ProductOrder> mValues;
        private RecyclerView mRecyclerView;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final SimpleDraweeView mImageView;
            public final LinearLayout mLayoutItem;
            public final TextView tvName, tvDescription, tvPrice, orderDate, customerEmail;
            public final Button cancel, confirm, info;
            public final View loadingView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = view.findViewById(R.id.productImage);
                mLayoutItem = view.findViewById(R.id.layout_item);
                tvName = view.findViewById(R.id.product_name);
                tvDescription = view.findViewById(R.id.product_description);
                tvPrice = view.findViewById(R.id.total_price);
                orderDate = view.findViewById(R.id.date_ordered);
                customerEmail = view.findViewById(R.id.customer_email);
                cancel = view.findViewById(R.id.order_cancel);
                confirm = view.findViewById(R.id.order_confirm);
                info = view.findViewById(R.id.order_info);
                loadingView = view.findViewById(R.id.loadingView);
            }
        }

        public SimpleStringRecyclerViewAdapter(RecyclerView recyclerView, ArrayList<ProductOrder> items) {
            mValues = items;
            mRecyclerView = recyclerView;
        }

        @Override
        public SimpleStringRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_item, parent, false);
            return new SimpleStringRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onViewRecycled(SimpleStringRecyclerViewAdapter.ViewHolder holder) {
            if (holder.mImageView.getController() != null) {
                holder.mImageView.getController().onDetach();
            }
            if (holder.mImageView.getTopLevelDrawable() != null) {
                holder.mImageView.getTopLevelDrawable().setCallback(null);
//                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
            }
        }

        @Override
        public void onBindViewHolder(final SimpleStringRecyclerViewAdapter.ViewHolder holder, final int position) {
            ProductOrder item = mValues.get(position);
            final Uri uri = Uri.parse(item.getProduct().getImageUrls().get(0));
            holder.mImageView.setImageURI(uri);
            holder.tvName.setText(item.getProduct().getProductname());
            holder.tvDescription.setText(item.getProduct().getProductDescription());
            holder.tvPrice.setText(String.valueOf(item.getProduct().getPrice()) + " x " + item.getQuantity());
            holder.customerEmail.setText(item.getCustomerEmail());
            holder.orderDate.setText(item.getDate_ordered());
            holder.info.setOnClickListener(v -> {
                showProductInfo(item);
            });

            holder.cancel.setOnClickListener(v -> {
                Log.d("ORDER ITEM: ", "Canceled!" );
                holder.loadingView.setVisibility(View.VISIBLE);
                updateOrderedProduct(item.getId(), ProductOrderStatus.CANCELLED, holder.loadingView);
            });

            if (item.getStatus().equals(ProductOrderStatus.CANCELLED.get())){
                holder.cancel.setVisibility(View.GONE);
                holder.confirm.setText("DELETE ORDER");
                holder.confirm.setOnClickListener(v -> {
                    holder.loadingView.setVisibility(View.VISIBLE);
                    Log.d("ORDER ITEM: ", "Confirmed!" );
                    updateOrderedProduct(item.getId(), ProductOrderStatus.DELETE, holder.loadingView);
                });
            } else if (item.getStatus().equals(ProductOrderStatus.ACCEPTED.get())) {
                holder.confirm.setText("MOVE TO SALES");
                holder.cancel.setVisibility(View.GONE);
                holder.confirm.setOnClickListener(v ->{
                    holder.loadingView.setVisibility(View.VISIBLE);
                    Log.d("ORDER ITEM: ", "Confirmed!" );
                    updateOrderedProduct(item.getId(), ProductOrderStatus.TO_REVIEW, holder.loadingView);
                });
            }else {
                holder.confirm.setOnClickListener(v -> {
                    holder.loadingView.setVisibility(View.VISIBLE);
                    Log.d("ORDER ITEM: ", "Confirmed!" );
                    updateOrderedProduct(item.getId(), ProductOrderStatus.ACCEPTED, holder.loadingView);
                });
            }
            holder.info.setOnClickListener(v -> {
                Log.d("ORDER ITEM: ", "Info" );
            });

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        double calculateAverageRatings(ArrayList<Rating> ratings){
            double avg = 0;
            for (Rating rate : ratings)
                avg+= rate.getRating();
            return avg / (double) ratings.size();
        }
    }

}
