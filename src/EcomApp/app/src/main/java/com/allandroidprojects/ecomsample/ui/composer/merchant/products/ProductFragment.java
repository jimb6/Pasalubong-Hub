package com.allandroidprojects.ecomsample.ui.composer.merchant.products;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.allandroidprojects.ecomsample.data.models.Rating;
import com.allandroidprojects.ecomsample.interfaces.IDataHelper;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.models.Product;
import com.allandroidprojects.ecomsample.data.viewmodel.product.ProductViewModel;
import com.allandroidprojects.ecomsample.ui.composer.merchant.startup.MerchantActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductFragment extends Fragment implements IDataHelper {

    private ProductViewModel viewModel;
    public static final String STRING_IMAGE_URI = "ImageUri";
    private static final String STRING_IMAGE_POSITION = "ImagePosition";
    private static MerchantActivity merchantActivity;
    private ArrayList<Product> products = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private View root;
    private Button save, update, delete;
    private Product product;
    private Skeleton skeleton;
    private boolean hasInstance = false;


    private void initializeCompoinents()
    {
        recyclerView = root.findViewById(R.id.product_recyler_view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ProductsRecyclerViewAdapter(recyclerView, products);
        recyclerView.setAdapter(adapter);
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ProductViewModel.class);
    }

    private void refreshData() {

        products = new ArrayList<>();
        initializeCompoinents();
        skeleton = SkeletonLayoutUtils.applySkeleton(recyclerView, R.layout.shop_list_item, 4);
        skeleton.showSkeleton();

        products.add(new Product());
        viewModel.myProducts(MerchantActivity.myBusiness);
        viewModel.getMyProducts().observe(getViewLifecycleOwner(), p -> {
            skeleton.showOriginal();
            initializeCompoinents();
            if (p instanceof Result.Success) {
                products.add((Product) ((Result.Success) p).getData());
                adapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        merchantActivity = (MerchantActivity) this.getActivity();
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_product, container, false);

        final SwipeRefreshLayout pullToRefresh = root.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(() -> {
            refreshData(); // your code
            pullToRefresh.setRefreshing(false);
        });

        initViewModel();
        initializeCompoinents();

        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // Handle item selection
        switch (menuItem.getItemId()) {
            case R.id.product_add:
                Intent intent = new Intent(getActivity(), AddProductActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (!hasInstance) {
            refreshData();
        }
        hasInstance = true;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.product_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDataComplete(boolean hasContents) {

    }


    public static class ProductsRecyclerViewAdapter
            extends RecyclerView.Adapter<ProductsRecyclerViewAdapter.ViewHolder> {

        private List<Product> mValues;
        private RecyclerView mRecyclerView;

        @Override
        public void onBindViewHolder(final ProductsRecyclerViewAdapter.ViewHolder holder, final int position) {
            Product item = mValues.get(position);
            if (item.getProductname() == null) {
                holder.addProductItem.setVisibility(View.VISIBLE);
                holder.addProductItem.setOnClickListener(c -> {
                    Intent intent = new Intent(merchantActivity, AddProductActivity.class);
                    merchantActivity.startActivity(intent);
                });
            } else {
                final Uri uri = Uri.parse(item.getImageUrls().get(0));
                holder.mImageView.setImageURI(uri);
                holder.productName.setText(item.getProductname());
                holder.productDescription.setText(item.getProductDescription());
                holder.productPrice.setText(String.valueOf(item.getPrice()));
                holder.ratingbar.setRating((float) calculateAverageRatings(item.getRatings()));
                holder.totalSales.setText(String.valueOf(item.getTotalSales()));
                if (item.getStock() <= 0){
                    holder.productTag.setVisibility(View.VISIBLE);
                }
                holder.mLayoutItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(merchantActivity, AddProductActivity.class);
                        intent.putExtra("PRODUCT", item);
                        intent.putExtra(STRING_IMAGE_POSITION, position);
                        merchantActivity.startActivity(intent);
                    }
                });
            }

        }

        public ProductsRecyclerViewAdapter(RecyclerView recyclerView, ArrayList<Product> items) {
            mValues = items;
            mRecyclerView = recyclerView;
        }

        @Override
        public ProductsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_list_item, parent, false);
            return new ProductsRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onViewRecycled(ProductsRecyclerViewAdapter.ViewHolder holder) {
            if (holder.mImageView.getController() != null) {
                holder.mImageView.getController().onDetach();
            }
            if (holder.mImageView.getTopLevelDrawable() != null) {
                holder.mImageView.getTopLevelDrawable().setCallback(null);
//                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
            }
        }

        double calculateAverageRatings(ArrayList<Rating> ratings){
            double avg = 0;
            for (Rating rate : ratings)
                avg+= rate.getRating();
            return avg / (double) ratings.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView productName, productDescription, productPrice;
            public final SimpleDraweeView mImageView;
            public final LinearLayout mLayoutItem;
            public final ImageView mImageViewWishlist;
            public final View addProductItem;
            public final TextView totalSales;
            public final RatingBar ratingbar;
            public final Button productTag;

            public ViewHolder(View view) {
                super(view);
                mView = view;

                mImageView = view.findViewById(R.id.image1);

                productPrice = view.findViewById(R.id.price);
                productName = view.findViewById(R.id.name);
                productDescription = view.findViewById(R.id.description);
                mLayoutItem = view.findViewById(R.id.layout_item);
                mImageViewWishlist = view.findViewById(R.id.ic_wishlist);
                addProductItem = view.findViewById(R.id.add_product_item);
                totalSales = view.findViewById(R.id.totalSales);
                ratingbar = view.findViewById(R.id.ratingBar);
                productTag = view.findViewById(R.id.product_tag);
            }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }

}
