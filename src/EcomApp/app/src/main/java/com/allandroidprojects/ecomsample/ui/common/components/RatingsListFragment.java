package com.allandroidprojects.ecomsample.ui.common.components;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.models.Rating;
import com.allandroidprojects.ecomsample.ui.startup.ViewPagerActivity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

public class RatingsListFragment extends Fragment {

    private RatingsListViewModel mViewModel;
    private int rate;

    private View root;
    private static ItemDetailsActivity activity;
    private ArrayList<Rating> ratings;
    private ArrayList<Rating> filteredRatings = new ArrayList<>();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private String title;

    public static RatingsListFragment newInstance(int page, String title) {
        RatingsListFragment fragment = new RatingsListFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rate = getArguments().getInt("RATE", 0);
        title = getArguments().getString("TITLE");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.ratings_list_fragment, container, false);

        this.activity = (ItemDetailsActivity) getActivity();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(RatingsListViewModel.class);
        // TODO: Use the ViewModel
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        ratings = activity.item.getRatings();

        filterRatings();

        recyclerView = root.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RatingRecyclerViewAdapter(recyclerView, filteredRatings);
        recyclerView.setAdapter(adapter);
    }

    private void filterRatings() {
        filteredRatings = new ArrayList<>();
        for(int i=0; i<activity.item.getRatings().size(); i++){
            if((int) ratings.get(i).getRating() == rate){
                filteredRatings.add(ratings.get(i));
            }
        }
    }




    public static class RatingRecyclerViewAdapter
            extends RecyclerView.Adapter<RatingRecyclerViewAdapter.ViewHolder> {

        private ArrayList<Rating> mValues;
        private RecyclerView mRecyclerView;

        private static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final SimpleDraweeView mImageView;
            public final TextView userName, comments, date;
            public final RecyclerView rv;
            public final RatingBar ratingbar;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = view.findViewById(R.id.image1);
                ratingbar = view.findViewById(R.id.ratingBar);
                userName = view.findViewById(R.id.user_name);
                comments = view.findViewById(R.id.user_comment);
                date = view.findViewById(R.id.date);
                rv = view.findViewById(R.id.uploadedImage);
            }
        }

        public RatingRecyclerViewAdapter(RecyclerView recyclerView, ArrayList<Rating> items) {
            mValues = items;
            mRecyclerView = recyclerView;
        }

        @Override
        public RatingRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ratings_list_fragment, parent, false);
            return new RatingRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onViewRecycled(RatingRecyclerViewAdapter.ViewHolder holder) {
            if (holder.mImageView.getController() != null) {
                holder.mImageView.getController().onDetach();
            }
            if (holder.mImageView.getTopLevelDrawable() != null) {
                holder.mImageView.getTopLevelDrawable().setCallback(null);
//                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
            }
        }

        @Override
        public void onBindViewHolder(final RatingRecyclerViewAdapter.ViewHolder holder, final int position) {
            Rating item = mValues.get(position);
            final Uri uri = Uri.parse(item.getUserImage());
            holder.mImageView.setImageURI(uri);
            holder.userName.setText(item.getAuthornName());
            holder.ratingbar.setRating((float) item.getRating());
            holder.comments.setText(item.getComment());

            holder.rv.setLayoutManager( new LinearLayoutManager(activity.getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
            holder.rv.setAdapter(new RatingImageRecyclerViewAdapter(holder.rv, item.getUrls()));
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        double calculateAverageRatings(ArrayList<Rating> ratings) {
            double avg = 0;
            for (Rating rate : ratings)
                avg += rate.getRating();
            return avg / (double) ratings.size();
        }
    }


    public static class RatingImageRecyclerViewAdapter
            extends RecyclerView.Adapter<RatingImageRecyclerViewAdapter.ViewHolder> {

        private ArrayList<String> imageUri;
        private RecyclerView mRecyclerView;

        @Override
        public void onBindViewHolder(final RatingImageRecyclerViewAdapter.ViewHolder holder, final int position) {
            final String uri = imageUri.get(position);
            holder.deleteImage.setVisibility(View.GONE);
            holder.mImageView.setImageURI(Uri.parse(uri));
            holder.mImageView.setOnClickListener(v -> {
                    Intent intent = new Intent(activity, ViewPagerActivity.class);
                    intent.putExtra("position", position);
                    intent.putStringArrayListExtra("images", imageUri);
                    activity.startActivity(intent);
            });
        }

        public RatingImageRecyclerViewAdapter(RecyclerView recyclerView, ArrayList<String> imagesUri) {
            imageUri = imagesUri;
            mRecyclerView = recyclerView;
        }

        @Override
        public RatingImageRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
            return new RatingImageRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onViewRecycled(RatingImageRecyclerViewAdapter.ViewHolder holder) {
            if (holder.mImageView.getController() != null) {
                holder.mImageView.getController().onDetach();
            }
            if (holder.mImageView.getTopLevelDrawable() != null) {
                holder.mImageView.getTopLevelDrawable().setCallback(null);
//                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
            }
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final SimpleDraweeView mImageView;
            public final ImageView deleteImage;
            public final View add_new_button;


            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = view.findViewById(R.id.image1);
                deleteImage = view.findViewById(R.id.delete_image_button);
                deleteImage.bringToFront();
                add_new_button = view.findViewById(R.id.add_image_item);
            }
        }

        @Override
        public int getItemCount() {
            return imageUri.size();
        }
    }

}
