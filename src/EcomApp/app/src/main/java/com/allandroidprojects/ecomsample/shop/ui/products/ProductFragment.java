package com.allandroidprojects.ecomsample.shop.ui.products;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.model.Business;
import com.allandroidprojects.ecomsample.model.product.Product;
import com.allandroidprojects.ecomsample.mvvm.view_model.ProductViewModel;
import com.allandroidprojects.ecomsample.shop.ShopActivity;
import com.allandroidprojects.ecomsample.startup.data.Result;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class ProductFragment extends Fragment {

    private ProductViewModel viewModel;
    public static final String STRING_IMAGE_URI = "ImageUri";
    public static final String STRING_IMAGE_POSITION = "ImagePosition";
    private static ShopActivity shopActivity;
    private ArrayList<Product> products = new ArrayList<>();;
    static ViewPager viewPager;
    static TabLayout tabLayout;
    private Business business;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private View root;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shopActivity = (ShopActivity) this.getActivity();
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shopActivity = (ShopActivity) getActivity();
        root = inflater.inflate(R.layout.fragment_product, container, false);
        initViewModel();
        initializeCompoinents();



//        viewPager = (ViewPager) root.findViewById(R.id.viewpager);
//        tabLayout = (TabLayout) root.findViewById(R.id.tabs);

//        if (viewPager != null) {
//            setupViewPager(viewPager);
//            tabLayout.setupWithViewPager(viewPager);
//        }
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.fetchMyProducts(shopActivity.myBusiness);
        viewModel.getMyProuducts().observe(getViewLifecycleOwner(), p -> {
            if (p instanceof Result.Success) {
                products.add((Product) ((Result.Success) p).getData());
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void initializeCompoinents() {
        recyclerView = (RecyclerView) root.findViewById(R.id.product_recyler_view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ProductsRecyclerViewAdapter(recyclerView, products);
        recyclerView.setAdapter(adapter);
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

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ProductViewModel.class);
    }

//    private void setupViewPager(ViewPager viewPager) {
//        Adapter adapter = new Adapter(getChildFragmentManager());
//        ShopImageListFragment fragment = new ShopImageListFragment();
//        Bundle bundle = new Bundle();
//        bundle.putInt("type", 1);
//        bundle.putParcelableArrayList("products", (ArrayList<? extends Parcelable>) products);
//        fragment.setArguments(bundle);
//        adapter.addFragment(fragment, getString(R.string.item_1));
//
//        viewPager.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
//    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.product_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public static class ProductsRecyclerViewAdapter
            extends RecyclerView.Adapter<ProductsRecyclerViewAdapter.ViewHolder> {

        private List<Product> mValues;
        private RecyclerView mRecyclerView;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView productName, productDescription, productPrice;
            public final SimpleDraweeView mImageView;
            public final LinearLayout mLayoutItem;
            public final ImageView mImageViewWishlist;

            public ViewHolder(View view) {
                super(view);
                mView = view;

                mImageView = (SimpleDraweeView) view.findViewById(R.id.image1);

                productPrice = view.findViewById(R.id.price);
                productName = view.findViewById(R.id.name);
                productDescription = view.findViewById(R.id.description);
                mLayoutItem = (LinearLayout) view.findViewById(R.id.layout_item);
                mImageViewWishlist = (ImageView) view.findViewById(R.id.ic_wishlist);
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

        @Override
        public void onBindViewHolder(final ProductsRecyclerViewAdapter.ViewHolder holder, final int position) {
            Product item = mValues.get(position);
            final Uri uri = Uri.parse(item.getImageUrls().get(0));
            holder.mImageView.setImageURI(uri);
            holder.productName.setText(item.getProductname());
            holder.productDescription.setText(item.getProductDescription());
            holder.productPrice.setText(String.valueOf(item.getPrice()));

//            holder.mLayoutItem.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(shopActivity, ItemDetailsActivity.class);
//                    intent.putExtra(STRING_IMAGE_URI, mValues[position]);
//                    intent.putExtra(STRING_IMAGE_POSITION, position);
//                    shopActivity.startActivity(intent);
//
//                }
//            });

//            //Set click action for wishlist
//            holder.mImageViewWishlist.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
//                    imageUrlUtils.addWishlistImageUri(mValues[position]);
//                    holder.mImageViewWishlist.setImageResource(R.drawable.ic_favorite_black_18dp);
//                    notifyDataSetChanged();
//                    Toast.makeText(shopActivity, "Item added to wishlist.", Toast.LENGTH_SHORT).show();
//
//                }
//            });

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
