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
import com.allandroidprojects.ecomsample.ui.common.components.messaging.MessagingActivity;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.model.Inbox;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Map;

public class ChatInboxAdapter
        extends RecyclerView.Adapter<ChatInboxAdapter.ViewHolder> {
    private ArrayList<Inbox> mValues;
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

    public ChatInboxAdapter(Context context, RecyclerView recyclerView, ArrayList<Inbox> items) {
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
        Inbox item = mValues.get(position);
        holder.name.setText(item.getChatroomname());
        holder.message.setText(item.getLastmessage());
        holder.mImageView.setImageURI(Uri.parse(item.getInboxImage()));
        holder.mView.setOnClickListener(v ->{
            ((MessagingActivity) context).goToMessaging(item);
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