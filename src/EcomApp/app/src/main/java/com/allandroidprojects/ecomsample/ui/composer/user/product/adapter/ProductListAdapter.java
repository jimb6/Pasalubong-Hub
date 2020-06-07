package com.allandroidprojects.ecomsample.ui.composer.user.product.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.models.Product;
import com.allandroidprojects.ecomsample.data.models.Rating;
import com.allandroidprojects.ecomsample.ui.common.components.ItemDetailsActivity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

public class ProductListAdapter
        extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    private ArrayList<Product> mValues;
    private RecyclerView mRecyclerView;
    private Context context;
    public static final String STRING_IMAGE_POSITION = "ImagePosition";

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

    public ProductListAdapter(Context context, RecyclerView recyclerView, ArrayList<Product> items) {
        mValues = items;
        this.context = context;
        mRecyclerView = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_large, parent, false);
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
            Intent intent = new Intent(context, ItemDetailsActivity.class);
//                    intent.putExtra(STRING_IMAGE_URI, mValues[position]);
            intent.putExtra("product", item);
            intent.putExtra(STRING_IMAGE_POSITION, 0);
            context.startActivity(intent);
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