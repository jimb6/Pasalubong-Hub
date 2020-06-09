package com.allandroidprojects.ecomsample.ui.composer.user.startup;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.Product;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.models.YoutubeVideo;
import com.allandroidprojects.ecomsample.interfaces.IDataHelper;
import com.allandroidprojects.ecomsample.ui.composer.user.product.adapter.CustomProductListAdapter;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;

public class StartupFragment extends Fragment {

    private StartupViewModel mViewModel;
    private View root;
//    private YoutubeRecyclerAdapter mRecyclerAdapter;
//    private BusinessRecyclerViewAdapter businessRecyclerViewAdapter;
    private List<YoutubeVideo> youtubeVideos = new ArrayList<>();
    private List<Business> businesses = new ArrayList<>();

    private ArrayList<Product> products = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private IDataHelper dataListener;
    private String category;

    CarouselView carouselView;
    int[] sampleImages = {R.drawable.sample_image1, R.drawable.sample_image2, R.drawable.sample_image3};

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }
    };

    public StartupFragment() {
        // doesn't do anything special
    }

    public static StartupFragment newInstance() {
        return new StartupFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root =  inflater.inflate(R.layout.startup_fragment, container, false);

        carouselView = (CarouselView) root.findViewById(R.id.carouselView);
        carouselView.setPageCount(sampleImages.length);

        carouselView.setImageListener(imageListener);

        Bundle bundle = getArguments();
        category = "Sweets";
        products = new ArrayList<>();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(StartupViewModel.class);
        // TODO: Use the ViewModel
        refreshData();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    private void getBestSeller(){

    }

    private void setupRecyclerView() {
        recyclerView = root.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CustomProductListAdapter(getActivity(), recyclerView, products);
        recyclerView.setAdapter(adapter);

    }

    private void refreshData(){

        products = new ArrayList<>();
        setupRecyclerView();

        mViewModel.fetchMyProducts(category);
        mViewModel.getMyProuducts(category).observe(getViewLifecycleOwner(), p -> {
            setupRecyclerView();
            if (p instanceof Result.Success) {
                products.add((Product) ((Result.Success) p).getData());
                adapter.notifyDataSetChanged();
            }
        });
    }



}
