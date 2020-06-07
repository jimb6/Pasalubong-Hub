package com.allandroidprojects.ecomsample.ui.composer.user.startup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.YoutubeVideo;
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
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(StartupViewModel.class);
        // TODO: Use the ViewModel
//        getBusinesses();
    }



}
