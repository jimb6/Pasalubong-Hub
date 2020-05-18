package com.allandroidprojects.ecomsample.ui.composer.user.product;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.ui.common.components.ItemDetailsActivity;
import com.allandroidprojects.ecomsample.interfaces.OnSearchOptionDataChangeListener;
import com.allandroidprojects.ecomsample.util.PrefManager;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.models.SearchData;
import com.allandroidprojects.ecomsample.data.models.Product;
import com.allandroidprojects.ecomsample.data.viewmodel.product.ProductViewModel;
import com.allandroidprojects.ecomsample.ui.common.widget.SearchOptionDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class SearchResultActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnSearchOptionDataChangeListener {
    private NavigationView navigationView;
    private SearchData searchData = new SearchData();
    private ArrayList<Product> searchProducts = new ArrayList<>();
    public static final String STRING_IMAGE_POSITION = "ImagePosition";
    private PrefManager prefManager;

    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private TextView search_result;
    private int result = 0;
    private ProductViewModel viewModel;

    private static SearchResultActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        initViewModel();
        initializeCompoinents();

        prefManager = new PrefManager(this);
        handleIntent(getIntent());

        navigationView = findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        mActivity = this;
    }


    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);
        MenuItem searchItem = menu.getItem(0);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setFocusable(true);
        searchItem.expandActionView();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search_option) {
            SearchOptionDialog.display(getSupportFragmentManager());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            searchData.query = query;
            result = 0;
            searchData.priceTo = prefManager.getSearhData().priceTo;
            searchData.priceFrom = prefManager.getSearhData().priceFrom;

            viewModel.searchProducts(searchData);
            viewModel.getSearchProduct().observe(this, p -> {
                if (p instanceof Result.Success) {
                    searchProducts.add((Product) ((Result.Success) p).getData());
                    adapter.notifyDataSetChanged();
                }
                search_result.setText("Search results for " + query);
            });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onDataChange(SearchData searchData) {
        this.searchData = searchData;
        Toast.makeText(getApplicationContext(), "Data Changed!", Toast.LENGTH_SHORT).show();
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ProductViewModel.class);
    }

    private void initializeCompoinents() {
        recyclerView = findViewById(R.id.searchproduct_recyler_view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SearchProductRecyclerViewAdapter(recyclerView, searchProducts);
        recyclerView.setAdapter(adapter);

        search_result = findViewById(R.id.search_result);
    }


    private void setupRecyclerView(RecyclerView recyclerView) {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new SearchProductRecyclerViewAdapter(recyclerView, searchProducts));
    }




    public static class SearchProductRecyclerViewAdapter
            extends RecyclerView.Adapter<SearchProductRecyclerViewAdapter.ViewHolder> {

        private ArrayList<Product> mValues;
        private RecyclerView mRecyclerView;

        public SearchProductRecyclerViewAdapter(RecyclerView recyclerView, ArrayList<Product> items) {
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

            Product product = mValues.get(position);
            holder.mImageView.setImageURI(product.getImageUrls().get(0));
            holder.productName.setText(mValues.get(position).getProductname());
            holder.description.setText(mValues.get(position).getProductDescription());
            holder.price.setText("₱" + mValues.get(position).getPrice());
            holder.mLayoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, ItemDetailsActivity.class);
                    intent.putExtra("product", product);
                    intent.putExtra(STRING_IMAGE_POSITION, 0);
                    mActivity.startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

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
    }
}
