package com.allandroidprojects.ecomsample.ui.common.components.messaging.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.emoji.widget.EmojiTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.model.Inbox;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.model.Message;
import com.allandroidprojects.ecomsample.util.ChatMessageType;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ChatMessagesAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Message> mValues;
    private RecyclerView mRecyclerView;
    private Context context;
    private Inbox inbox;


    public class SenderViewHolder extends RecyclerView.ViewHolder {
        private final SimpleDraweeView mImageView;
        private final EmojiTextView message;
        private final TextView date;

        public SenderViewHolder(View view) {
            super(view);
            mImageView = view.findViewById(R.id.image_message_profile);
            message = view.findViewById(R.id.text_message_body);
            date = view.findViewById(R.id.text_message_time);
        }
    }


    public class ReceiverViewHolder extends RecyclerView.ViewHolder {
        private final SimpleDraweeView mImageView;
        private final EmojiTextView message;
        private final TextView date;

        public ReceiverViewHolder(View view) {
            super(view);
            mImageView = view.findViewById(R.id.image_message_profile);
            message = view.findViewById(R.id.text_message_body);
            date = view.findViewById(R.id.text_message_time);
        }
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        private final SimpleDraweeView productImage;
        private final TextView productName, productDescription, productMessage, date;

        public ProductViewHolder(View view) {
            super(view);
            productImage = view.findViewById(R.id.productImage);
            productName = view.findViewById(R.id.productName);
            productDescription = view.findViewById(R.id.productDescription);
            productMessage = view.findViewById(R.id.text_message_body);
            date = view.findViewById(R.id.text_message_time);
        }
    }

    public ChatMessagesAdapter(Context context, RecyclerView recyclerView, ArrayList<Message> items, Inbox inbox) {
        this.inbox = inbox;
        this.context = context;
        mValues = items;
        mRecyclerView = recyclerView;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ChatMessageType.CUSTOMER.getData()) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.their_message, parent, false);
            return new SenderViewHolder(view);
        } else if (viewType == ChatMessageType.SELLER.getData()) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_message, parent, false);
            return new ReceiverViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_message, parent, false);
            return new ProductViewHolder(view);
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (holder instanceof SenderViewHolder) {
            if (((SenderViewHolder) holder).mImageView.getController() != null) {
                ((SenderViewHolder) holder).mImageView.getController().onDetach();
            }
            if (((SenderViewHolder) holder).mImageView.getTopLevelDrawable() != null) {
                ((SenderViewHolder) holder).mImageView.getTopLevelDrawable().setCallback(null);
//                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
            }
        } else if (holder instanceof ReceiverViewHolder) {
            if (((ReceiverViewHolder) holder).mImageView.getController() != null) {
                ((ReceiverViewHolder) holder).mImageView.getController().onDetach();
            }
            if (((ReceiverViewHolder) holder).mImageView.getTopLevelDrawable() != null) {
                ((ReceiverViewHolder) holder).mImageView.getTopLevelDrawable().setCallback(null);
//                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
            }
        } else if (holder instanceof ProductViewHolder) {

        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        Message item = mValues.get(position);
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        if (getItemViewType(position) == ChatMessageType.CUSTOMER.getData()) {
            ((SenderViewHolder) holder).message.setText(item.getText());
            ((SenderViewHolder) holder).date.setText(sfd.format(item.getCreatedAt()));
            ((SenderViewHolder) holder).message.setOnClickListener(v -> {
                if (((SenderViewHolder) holder).date.getVisibility() == View.GONE)
                    ((SenderViewHolder) holder).date.setVisibility(View.VISIBLE);
                else
                    ((SenderViewHolder) holder).date.setVisibility(View.GONE);
            });
        } else if (getItemViewType(position) == ChatMessageType.SELLER.getData()) {
            ((ReceiverViewHolder) holder).message.setText(item.getText());
            ((ReceiverViewHolder) holder).date.setText(sfd.format(item.getCreatedAt()));
            ((ReceiverViewHolder) holder).message.setOnClickListener(v -> {
                if (((ReceiverViewHolder) holder).date.getVisibility() == View.GONE)
                    ((ReceiverViewHolder) holder).date.setVisibility(View.VISIBLE);
                else
                    ((ReceiverViewHolder) holder).date.setVisibility(View.GONE);
            });
        }
//        } else {
//            ((ProductViewHolder) holder).productName.setText(sfd.format(item.getCreatedAt()));
//            ((ProductViewHolder) holder).productDescription.setText(String.valueOf(item.getProduct().get("price")));
//            ((ProductViewHolder) holder).productImage.setImageURI(Uri.parse(item.getProduct().getImageUrls().get(0)));
//            ((ProductViewHolder) holder).productMessage.setText(item.getText());
//            ((ProductViewHolder) holder).date.setText(item.getTimestamp());
//            ((ProductViewHolder) holder).productImage.setOnClickListener(v -> {
//
//            });
//        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public int getItemViewType(int position) {
//        if ((mValues.get(position).getProduct() != null)) {
//            return ChatSenderType.PRODUC_ITEM.getData();
//        }
        if (isBelongToCurrentUser(mValues.get(position))) {
            return ChatMessageType.CUSTOMER.getData();
        } else {
            return ChatMessageType.SELLER.getData();
        }
    }

    private boolean isBelongToCurrentUser(Message message){
        return (message.getId().equals(inbox.getCreatorId()));
    }

    private void showBadge(int position){

    }
}