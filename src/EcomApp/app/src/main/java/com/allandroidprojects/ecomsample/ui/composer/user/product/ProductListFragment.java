/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.allandroidprojects.ecomsample.ui.composer.user.product;

import android.content.Intent;
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
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.viewmodel.product.ProductListViewModel;
import com.allandroidprojects.ecomsample.interfaces.IDataHelper;
import com.allandroidprojects.ecomsample.ui.common.components.ItemDetailsActivity;
import com.allandroidprojects.ecomsample.ui.composer.user.startup.MainActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;

import java.util.ArrayList;


public class ProductListFragment extends Fragment {

    public static final String STRING_IMAGE_URI = "ImageUri";
    public static final String STRING_IMAGE_POSITION = "ImagePosition";
    private static MainActivity mActivity;
    private ProductListViewModel viewModel;
    private ArrayList<Product> products = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private IDataHelper dataListener;
    private Skeleton skeleton;
    private String category;
    private View root;


    public ProductListFragment(IDataHelper dataHelper) {
        this.dataListener = dataHelper;
    }

    private void refreshData(){

        products = new ArrayList<>();
        setupRecyclerView();
        skeleton = SkeletonLayoutUtils.applySkeleton(recyclerView, R.layout.shop_list_item, 4);
        skeleton.showSkeleton();

        viewModel.fetchMyProducts(category);
        viewModel.getMyProuducts(category).observe(getViewLifecycleOwner(), p -> {
            skeleton.showOriginal();
            setupRecyclerView();
            if (p instanceof Result.Success) {
                products.add((Product) ((Result.Success) p).getData());
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.layout_recylerview_list, container, false);

        final SwipeRefreshLayout pullToRefresh = root.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(() -> {
            refreshData();
            pullToRefresh.setRefreshing(false);
        });

        initializeViewModel();
        setupRecyclerView();
        // or apply a new SkeletonLayout to a RecyclerView (showing 5 items)
        skeleton = SkeletonLayoutUtils.applySkeleton(recyclerView, R.layout.list_item, 4);

        Bundle bundle = getArguments();
        category = bundle.getString("type");
        products = new ArrayList<>();
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        skeleton.showSkeleton();
        refreshData();
    }


    private void initializeViewModel() {
        viewModel = ViewModelProviders.of(this).get(ProductListViewModel.class);
    }


    private void setupRecyclerView() {
        recyclerView = root.findViewById(R.id.recyclerview);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SimpleStringRecyclerViewAdapter(recyclerView, products);
        recyclerView.setAdapter(adapter);
        dataListener.onDataComplete(products.size() > 0);
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
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            if (holder.mImageView.getController() != null) {
                holder.mImageView.getController().onDetach();
            }
            if (holder.mImageView.getTopLevelDrawable() != null) {
                holder.mImageView.getTopLevelDrawable().setCallback(null);
//                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
            }
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            Product item = mValues.get(position);
            final Uri uri = Uri.parse(item.getImageUrls().get(0));
            holder.mImageView.setImageURI(uri);
            holder.tvName.setText(item.getProductname());
            holder.tvDescription.setText(item.getProductDescription());
            holder.tvPrice.setText(String.valueOf(item.getPrice()));
            holder.ratingbar.setRating((float) calculateAverageRatings(item.getRatings()));
            holder.totalSales.setText(String.valueOf(item.getTotalSales()));
            holder.mLayoutItem.setOnClickListener(v -> {
                Intent intent = new Intent(mActivity, ItemDetailsActivity.class);
//                    intent.putExtra(STRING_IMAGE_URI, mValues[position]);
                intent.putExtra("product", item);
                intent.putExtra(STRING_IMAGE_POSITION, 0);
                mActivity.startActivity(intent);

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
