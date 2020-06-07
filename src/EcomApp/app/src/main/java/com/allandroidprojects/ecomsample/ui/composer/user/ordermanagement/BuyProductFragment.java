package com.allandroidprojects.ecomsample.ui.composer.user.ordermanagement;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.models.ProductOrder;
import com.allandroidprojects.ecomsample.data.models.Rating;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.util.ProductOrderStatus;
import com.facebook.drawee.view.SimpleDraweeView;
import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;

import java.util.ArrayList;

public class BuyProductFragment extends Fragment {

    private BuyProductViewModel mViewModel;
    private View root;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private Skeleton skeleton;
    private ArrayList<ProductOrder> orders = new ArrayList<>();
    private String orderStatus;
    private String merchantId;
    public static BuyProductFragment newInstance() {
        return new BuyProductFragment();
    }

    private void setupViewModel(){
        BuyProductViewModelFactory factory = new BuyProductViewModelFactory(getContext());
        mViewModel = ViewModelProviders.of(this, factory).get(BuyProductViewModel.class);
    }

    private void refreshData() {
        orders = new ArrayList<>();
        setupRecyclerView();
        skeleton = SkeletonLayoutUtils.applySkeleton(recyclerView, R.layout.shop_list_item, 4);
        skeleton.showSkeleton();

        mViewModel.getUserOrders(merchantId);
        mViewModel.getUserOrdersResult().observe(getViewLifecycleOwner(), p -> {
            skeleton.showOriginal();
            setupRecyclerView();
            if (p instanceof Result.Success) {
                orders.add((ProductOrder) ((Result.Success) p).getData());
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView = root.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SimpleStringRecyclerViewAdapter(recyclerView, orders);
        recyclerView.setAdapter(adapter);
    }


    protected void updateOrderedProduct(String reference, ProductOrderStatus status){
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.buy_product_fragment, container, false);

        setupViewModel();
        final SwipeRefreshLayout pullToRefresh = root.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(() -> {
            refreshData();
            pullToRefresh.setRefreshing(false);
        });

        merchantId = FirebaseAuth.getInstance().getUid();

        setupRecyclerView();
        // or apply a new SkeletonLayout to a RecyclerView (showing 5 items)
        skeleton = SkeletonLayoutUtils.applySkeleton(recyclerView, R.layout.list_item_large, 4);
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
            public final TextView tvProductName, tvProductDescription,
                    tvProductPrice, tvDateOrdered, tvCancel, tvStatus;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = view.findViewById(R.id.image);
                tvProductName = view.findViewById(R.id.tvProductName);
                tvProductDescription = view.findViewById(R.id.tvProductDescription);
                tvProductPrice = view.findViewById(R.id.tvProductPrice);
                tvDateOrdered = view.findViewById(R.id.tvDateOrdered);
                tvCancel = view.findViewById(R.id.tvCancel);
                tvStatus = view.findViewById(R.id.tvStatus);
            }
        }

        public SimpleStringRecyclerViewAdapter(RecyclerView recyclerView, ArrayList<ProductOrder> items) {
            mValues = items;
            mRecyclerView = recyclerView;
        }

        @Override
        public SimpleStringRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_order_item, parent, false);
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
            holder.tvProductName.setText(item.getProduct().getProductname());
            holder.tvProductDescription.setText(item.getProduct().getProductDescription());
            holder.tvProductPrice.setText(String.valueOf(item.getProduct().getPrice()) + " x " + item.getQuantity());
            holder.tvDateOrdered.setText(item.getDate_ordered());
            holder.tvStatus.setText(item.getStatus());
            if (item.getStatus().equals(ProductOrderStatus.TO_REVIEW.get())){
                holder.tvStatus.setOnClickListener(v -> {
                    Log.d("ORDER ITEM: ", "TO REVIEW!" );
                    //To REVIEW
                });
                holder.tvCancel.setVisibility(View.GONE);
            }else if (item.getStatus().equals(ProductOrderStatus.ACCEPTED.get())){
                holder.tvStatus.setText("Ready to pickup");
                holder.tvCancel.setText("Request Cancel");
                holder.tvCancel.setOnClickListener(v -> {
                    Log.d("ORDER ITEM: ", "Confirmed!" );
                    //Request Cancel
                });
            }else if (item.getStatus().equals(ProductOrderStatus.PENDING.get())){
                holder.tvCancel.setOnClickListener(v -> {
                    Log.d("ORDER ITEM: ", "PENDING!" );
                    //Request Cancel
                });
            } else if(item.getStatus().equals(ProductOrderStatus.CANCELLED.get())){
                holder.tvCancel.setText("Delete Order");
                holder.tvCancel.setOnClickListener(v -> {
                    Log.d("ORDER ITEM: ", "CANCELLED!" );
                    //Request Cancel
                });
            }
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
