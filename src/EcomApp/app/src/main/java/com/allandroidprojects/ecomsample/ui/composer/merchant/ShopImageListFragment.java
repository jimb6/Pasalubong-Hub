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

package com.allandroidprojects.ecomsample.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.ui.common.components.ItemDetailsActivity;
import com.allandroidprojects.ecomsample.config.helpers.utility.ImageUrlUtils;
import com.allandroidprojects.ecomsample.data.models.product.Product;
import com.allandroidprojects.ecomsample.merchant.startup.ShopActivity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;


public class ShopImageListFragment extends Fragment {

    public static final String STRING_IMAGE_URI = "ImageUri";
    public static final String STRING_IMAGE_POSITION = "ImagePosition";
    private ArrayList<Product> products;
    private static ShopActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (ShopActivity) this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView rv = (RecyclerView) inflater.inflate(R.layout.layout_recylerview_list, container, false);
        Bundle bundle = getArguments();
        products = bundle.getParcelableArrayList("products");
        setupRecyclerView(rv);
        return rv;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
      /*  if (ShopImageListFragment.this.getArguments().getInt("type") == 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        } else if (ShopImageListFragment.this.getArguments().getInt("type") == 2) {
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
        } else {
            GridLayoutManager layoutManager = new GridLayoutManager(recyclerView.getContext(), 3);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
        }*/
//        String[] items=null;
//        if (ShopImageListFragment.this.getArguments().getInt("type") == 1){
//            items =ImageUrlUtils.getOffersUrls();
//        }else if (ShopImageListFragment.this.getArguments().getInt("type") == 2){
//            items =ImageUrlUtils.getElectronicsUrls();
//        }else if (ShopImageListFragment.this.getArguments().getInt("type") == 3){
//            items =ImageUrlUtils.getLifeStyleUrls();
//        }else if (ShopImageListFragment.this.getArguments().getInt("type") == 4){
//            items =ImageUrlUtils.getHomeApplianceUrls();
//        }else if (ShopImageListFragment.this.getArguments().getInt("type") == 5){
//            items =ImageUrlUtils.getBooksUrls();
//        }else {
//            items = ImageUrlUtils.getImageUrls();
//        }
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(recyclerView, products));
    }

    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private ArrayList<Product> mValues;
        private RecyclerView mRecyclerView;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView productName, description, price;
            public final SimpleDraweeView mImageView;
            public final LinearLayout mLayoutItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = view.findViewById(R.id.image1);
                mLayoutItem = view.findViewById(R.id.layout_item);
                productName = view.findViewById(R.id.name);
                price = view.findViewById(R.id.price);
                description = view.findViewById(R.id.description);
            }
        }

        public SimpleStringRecyclerViewAdapter(RecyclerView recyclerView, ArrayList<Product> items) {
            mValues = items;
            mRecyclerView = recyclerView;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_list_item, parent, false);
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
           /* FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) holder.mImageView.getLayoutParams();
            if (mRecyclerView.getLayoutManager() instanceof GridLayoutManager) {
                layoutParams.height = 200;
            } else if (mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                layoutParams.height = 600;
            } else {
                layoutParams.height = 800;
            }*/
            final Uri uri = Uri.parse(ImageUrlUtils.getBooksUrls()[0]);
            holder.mImageView.setImageURI(uri);
            holder.productName.setText(mValues.get(position).getProductname());
            holder.description.setText(mValues.get(position).getProductDescription());
            holder.price.setText("₱"+ mValues.get(position).getPrice());
            holder.mLayoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, ItemDetailsActivity.class);
                    intent.putExtra(STRING_IMAGE_URI, ImageUrlUtils.getBooksUrls()[0]);
                    intent.putExtra(STRING_IMAGE_POSITION, position);
                    mActivity.startActivity(intent);

                }
            });

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }
}
