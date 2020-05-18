package com.allandroidprojects.ecomsample.ui.composer.user.startup;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.models.YoutubeVideo;
import com.allandroidprojects.ecomsample.util.BaseViewHolder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.textfield.TextInputEditText;
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StartupFragment extends Fragment {

    private StartupViewModel mViewModel;
    private View root;
    private YoutubeRecyclerAdapter mRecyclerAdapter;
    private BusinessRecyclerViewAdapter businessRecyclerViewAdapter;
    private List<YoutubeVideo> youtubeVideos = new ArrayList<>();
    private List<Business> businesses = new ArrayList<>();

    @BindView(R.id.tags)
    TextInputEditText search;
    @BindView(R.id.recyclerViewFeed)
    RecyclerView recyclerViewFeed;
    @BindView(R.id.rvBusinesses)
    RecyclerView recyclerViewBusiness;



    public static StartupFragment newInstance() {
        return new StartupFragment();
    }

    private void getBusinesses(){
        mViewModel.getAllBusiness();
        mViewModel.getAllBusinessResult().observe(getActivity(), result -> {
            if(result instanceof Result.Success){
                businesses.add((Business) ((Result.Success) result).getData());
            }
            businessRecyclerViewAdapter.notifyDataSetChanged();
        });
    }

    private void setupVideoView(){
        // prepare data for list
        youtubeVideos = prepareList();

        mRecyclerAdapter = new YoutubeRecyclerAdapter(youtubeVideos);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewFeed.setLayoutManager(mLayoutManager);
        recyclerViewFeed.setItemAnimator(new DefaultItemAnimator());
        recyclerViewFeed.setAdapter(mRecyclerAdapter);
    }

    private void setupBusinessView(){
        // prepare data for list
        businessRecyclerViewAdapter = new BusinessRecyclerViewAdapter(businesses);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewBusiness.setLayoutManager(mLayoutManager);
        recyclerViewBusiness.setItemAnimator(new DefaultItemAnimator());
        recyclerViewBusiness.setAdapter(businessRecyclerViewAdapter);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root =  inflater.inflate(R.layout.startup_fragment, container, false);

        recyclerViewFeed = root.findViewById(R.id.recyclerViewFeed);
        recyclerViewBusiness = root.findViewById(R.id.rvBusinesses);
        search = root.findViewById(R.id.tags);
        ButterKnife.bind(getActivity());

        setupVideoView();
        setupBusinessView();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(StartupViewModel.class);
        // TODO: Use the ViewModel
        getBusinesses();
    }

    private List<YoutubeVideo> prepareList() {
        ArrayList<YoutubeVideo> videoArrayList=new ArrayList<>();
        // add first item
        YoutubeVideo video1 = new YoutubeVideo();
        video1.setId(1l);
        video1.setImageUrl("https://i.ytimg.com/vi/tAF258WR2hQ/maxresdefault.jpg");
        video1.setTitle(
                "Biyahe ni Drew: The rise of Baganga, Davao Oriental (full episode) | GMA Public Affairs");
        video1.setVideoId("tAF258WR2hQ");
        videoArrayList.add(video1);
        // add second item
        YoutubeVideo video2 = new YoutubeVideo();
        video2.setId(2l);
        video2.setImageUrl("https://i.ytimg.com/vi/dG5IW8IZ-cJ4/maxresdefault.jpg");
        video2.setTitle(
                "Dahican Beach, Mati | Budget Travel Vlog (Philippines) | Sincerely Bethel");
        video2.setVideoId("G5IW8IZ-cJ4");
        // add third item
        YoutubeVideo video3 = new YoutubeVideo();
        video3.setId(3l);
        video3.setImageUrl("https://i.ytimg.com/vi/HPMkCdEeQCU/hqdefault.jpg");
        video3.setTitle("Blue Bless Beach Resort - Mati City Davao Oriental | JecDD");
        video3.setVideoId("HPMkCdEeQCU");
        // add four item
        YoutubeVideo video4 = new YoutubeVideo();
        video4.setId(4l);
        video4.setImageUrl("https://i.ytimg.com/vi/kv3q9r1NGEA/maxresdefault.jpg");
        video4.setTitle("Aliwagwag Falls, Cateel, Davao Oriental, Philippines | Abel Avergonzado");
        video4.setVideoId("kv3q9r1NGEA");
        // add four item

        videoArrayList.add(video1);
        videoArrayList.add(video2);
        videoArrayList.add(video3);
        return videoArrayList;
    }


    public class YoutubeRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
        public static final int VIEW_TYPE_NORMAL = 1;
        private List<YoutubeVideo> mYoutubeVideos;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        public YoutubeRecyclerAdapter(List<YoutubeVideo> youtubeVideos) {
            mYoutubeVideos = youtubeVideos;
        }
        @NonNull
        @Override
        public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.video_item_list, parent, false));
        }
        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
            holder.onBind(position);
        }
        @Override
        public int getItemViewType(int position) {
            return VIEW_TYPE_NORMAL;
        }
        @Override
        public int getItemCount() {
            if (mYoutubeVideos != null && mYoutubeVideos.size() > 0) {
                return mYoutubeVideos.size();
            } else {
                return 1;
            }
        }
        public void setItems(List<YoutubeVideo> youtubeVideos) {
            mYoutubeVideos = youtubeVideos;
            notifyDataSetChanged();
        }
        public class ViewHolder extends BaseViewHolder {
            @BindView(R.id.textViewTitle)
            TextView textWaveTitle;
            @BindView(R.id.btnPlay)
            ImageView playButton;
            @BindView(R.id.imageViewItem)
            ImageView imageViewItems;
            @BindView(R.id.youtube_view)
            YouTubePlayerView youTubePlayerView;
            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
            protected void clear() {
            }
            public void onBind(int position) {
                super.onBind(position);
                final YoutubeVideo mYoutubeVideo = mYoutubeVideos.get(position);
                ((Activity) itemView.getContext()).getWindowManager()
                        .getDefaultDisplay()
                        .getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                if (mYoutubeVideo.getTitle() != null) {
                    textWaveTitle.setText(mYoutubeVideo.getTitle());
                }
                if (mYoutubeVideo.getImageUrl() != null) {
                    Glide.with(itemView.getContext())
                            .load(mYoutubeVideo.getImageUrl()).
                            apply(new RequestOptions().override(width - 36, 200))
                            .into(imageViewItems);
                }
                imageViewItems.setVisibility(View.VISIBLE);
                playButton.setVisibility(View.VISIBLE);
                youTubePlayerView.setVisibility(View.GONE);
                playButton.setOnClickListener(view -> {
                    imageViewItems.setVisibility(View.GONE);
                    youTubePlayerView.setVisibility(View.VISIBLE);
                    playButton.setVisibility(View.GONE);
                    youTubePlayerView.initialize(
                            initializedYouTubePlayer -> initializedYouTubePlayer.addListener(
                                    new AbstractYouTubePlayerListener() {
                                        @Override
                                        public void onReady() {
                                            initializedYouTubePlayer.loadVideo(mYoutubeVideo.getVideoId(), 0);
                                        }
                                    }), true);
                });
            }
        }
    }




    public class BusinessRecyclerViewAdapter extends  RecyclerView.Adapter<BaseViewHolder>{

        private List<Business> businesses;

        public BusinessRecyclerViewAdapter(List<Business> businesses){
            this.businesses = businesses;
        }

        @NonNull
        @Override
        public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.store_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
            holder.onBind(position);
        }

        @Override
        public int getItemCount() {
            if (businesses != null && businesses.size()> 0)
                return businesses.size();
            else
                return 1;
        }

        public void setItems(List<Business> businesses){
            this.businesses = businesses;
            notifyDataSetChanged();
        }

        public class ViewHolder extends BaseViewHolder{
            @BindView(R.id.store_name)
            TextView storeName;
            @BindView(R.id.store_address)
            TextView storeAddress;
            @BindView(R.id.image1)
            SimpleDraweeView storeImage;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            @Override
            protected void clear() {

            }

            @Override
            public void onBind(int position) {
                super.onBind(position);
                if(businesses.size() > 0){
                    final Business business = businesses.get(position);
                    if(business.getBusinessName() != null)
                        storeName.setText(business.getBusinessName());
                    if (business.getBusinessAddress() != null)
                        storeAddress.setText(business.getBusinessAddress());
                    if (business.getBusinessPhotos() != null && business.getBusinessPhotos().size() > 0)
                        storeImage.setImageURI(business.getCoverUri());
                }
            }
        }
    }



}
