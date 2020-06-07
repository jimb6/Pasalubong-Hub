package com.allandroidprojects.ecomsample.ui.common.components.messaging.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.models.Rating;
import com.allandroidprojects.ecomsample.data.models.fcm.Chatroom;
import com.allandroidprojects.ecomsample.data.models.fcm.ChatroomUsers;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Map;

public class ChatInboxAdapter
        extends RecyclerView.Adapter<ChatInboxAdapter.ViewHolder> {
    private ArrayList<Chatroom> mValues;
    private RecyclerView mRecyclerView;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        private final SimpleDraweeView mImageView;
        private final TextView name, message;

        public ViewHolder(View view) {
            super(view);
            mImageView = view.findViewById(R.id.photoProfile);
            name = view.findViewById(R.id.messageTitle);
            message = view.findViewById(R.id.message_business_text);
            mView = view.findViewById(R.id.message_business_card);
        }
    }

    public ChatInboxAdapter(Context context, RecyclerView recyclerView, ArrayList<Chatroom> items) {
        this.context = context;
        this.mValues = items;
        this.mRecyclerView = recyclerView;
    }

    @Override
    public ChatInboxAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_item, parent, false);
        return new ChatInboxAdapter.ViewHolder(view);
    }

    @Override
    public void onViewRecycled(ChatInboxAdapter.ViewHolder holder) {
        if (holder.mImageView.getController() != null) {
            holder.mImageView.getController().onDetach();
        }
        if (holder.mImageView.getTopLevelDrawable() != null) {
            holder.mImageView.getTopLevelDrawable().setCallback(null);
//                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
        }
    }

    @Override
    public void onBindViewHolder(final ChatInboxAdapter.ViewHolder holder, final int position) {
        Chatroom item = mValues.get(position);
        holder.name.setText(item.getChatroom_name());
        Map<String, ChatroomUsers> users = item.getUsers();
        holder.message.setText(users.get(item.getBusinessId()).getLastUnseenMessage());
        holder.mImageView.setImageURI(Uri.parse(users.get(item.getBusinessId()).getUserProfileImage()));
        holder.mView.setOnClickListener(v ->{

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