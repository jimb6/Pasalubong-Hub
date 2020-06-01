package com.allandroidprojects.ecomsample.ui.composer.merchant.ordermanagement;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.models.Product;
import com.allandroidprojects.ecomsample.data.models.Rating;
import com.allandroidprojects.ecomsample.ui.composer.user.product.ProductListFragment;
import com.facebook.drawee.view.SimpleDraweeView;
import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;

import java.util.ArrayList;

public class OrderListFragment extends Fragment {

    private OrderListViewModel mViewModel;
    private View root;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private Skeleton skeleton;
    private ArrayList<Product> orders = new ArrayList<>();


    public static OrderListFragment newInstance() {
        return new OrderListFragment();
    }

    private void refreshData() {
        orders = new ArrayList<>();
        setupRecyclerView();
        skeleton = SkeletonLayoutUtils.applySkeleton(recyclerView, R.layout.shop_list_item, 4);
        skeleton.showSkeleton();

//        mViewModel.fetchMyProducts(category);
//        mViewModel.getMyProuducts(category).observe(getViewLifecycleOwner(), p -> {
//            skeleton.showOriginal();
//            setupRecyclerView();
//            if (p instanceof Result.Success) {
//                products.add((Product) ((Result.Success) p).getData());
//                adapter.notifyDataSetChanged();
//            }
//        });
    }

    private void setupRecyclerView() {
        recyclerView = root.findViewById(R.id.recyclerview);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ProductListFragment.SimpleStringRecyclerViewAdapter(recyclerView, orders);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.order_list_fragment, container, false);
        final SwipeRefreshLayout pullToRefresh = root.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(() -> {
//            refreshData();
            pullToRefresh.setRefreshing(false);
        });

        setupRecyclerView();
        // or apply a new SkeletonLayout to a RecyclerView (showing 5 items)
        skeleton = SkeletonLayoutUtils.applySkeleton(recyclerView, R.layout.list_item, 4);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(OrderListViewModel.class);
        // TODO: Use the ViewModel
    }


    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private ArrayList<Product> mValues;
        private RecyclerView mRecyclerView;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final SimpleDraweeView mImageView;
            public final LinearLayout mLayoutItem;
            public final ImageView mImageViewWishlist;
            public final TextView tvName, tvDescription, tvPrice, totalSales;
            public final RatingBar ratingbar;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = view.findViewById(R.id.image1);
                mLayoutItem = view.findViewById(R.id.layout_item);
                tvName = view.findViewById(R.id.product_name);
                tvDescription = view.findViewById(R.id.product_description);
                tvPrice = view.findViewById(R.id.product_price);
                mImageViewWishlist = view.findViewById(R.id.ic_wishlist);
                totalSales = view.findViewById(R.id.totalSales);
                ratingbar = view.findViewById(R.id.ratingBar);
            }
        }

        public SimpleStringRecyclerViewAdapter(RecyclerView recyclerView, ArrayList<Product> items) {
            mValues = items;
            mRecyclerView = recyclerView;
        }

        @Override
        public SimpleStringRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
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
            Product item = mValues.get(position);
            final Uri uri = Uri.parse(item.getImageUrls().get(0));
            holder.mImageView.setImageURI(uri);
            holder.tvName.setText(item.getProductname());
            holder.tvDescription.setText(item.getProductDescription());
            holder.tvPrice.setText(String.valueOf(item.getPrice()));
            holder.ratingbar.setRating((float) calculateAverageRatings(item.getRatings()));
            holder.totalSales.setText(String.valueOf(item.getTotalSales()));
            holder.mLayoutItem.setOnClickListener(v -> {
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
