package com.allandroidprojects.ecomsample.messages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.shop.ShopActivity;

public class MessagesFragment extends Fragment {

    private MessagesViewModel messagesViewModel;
    private ShopActivity shopActivity;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.shopActivity = (ShopActivity) this.getActivity();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        messagesViewModel =
                ViewModelProviders.of(this).get(MessagesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_messages, container, false);

        RecyclerView rv = (RecyclerView) root.findViewById(R.id.messages_recycler_view);
//        setupRecyclerView(rv);

        return root;
    }


//    private void setupRecyclerView(RecyclerView recyclerView) {
//        Message[] items = new Message[]{
//                new Message("Jimwell Buot", "Hi I am Jim", "03/19/2020"),
//                new Message("Valeen Algabre", "Hi I am Leen", "03/19/2020"),
//                new Message("Hayma Usman", "Hi I am Hayma", "03/19/2020"),
//                new Message("Bonn RJ de Guzman", "Hi I am Bonn", "03/19/2020"),
//                new Message("Tine Alde", "Hi I am Tine", "03/19/2020"),
//                new Message("Riyan Jeorge Tinae", "Hi I am Jeorge", "03/19/2020"),
//                new Message("Vince Morales", "Hi I am Vince", "03/19/2020"),
//                new Message("Paula Awing", "Hi I am Paula", "03/19/2020"),
//                new Message("Jessa Mae Longyapon", "Hi I am Jessa", "03/19/2020"),
//                new Message("Mary Jane Manabat", "Hi I am Mary Jane", "03/19/2020"),
//                new Message("John Patrick Punay", "Hi", "03/19/2020"),
//        };
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(recyclerView, items));
//    }

//    public static class SimpleStringRecyclerViewAdapter
//            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {
//
//        private Message[] mValues;
//        private RecyclerView mRecyclerView;
//
//        public static class ViewHolder extends RecyclerView.ViewHolder {
//            public final View mView;
//            public final TextView business_name, business_message, business_date;
//
//            public ViewHolder(View view) {
//                super(view);
//                mView = view;
//                business_name = (TextView) view.findViewById(R.id.message_business_name);
//                business_message= (TextView) view.findViewById(R.id.message_business_text);
//                business_date = (TextView) view.findViewById(R.id.message_business_date);
//            }
//        }
//
//        public SimpleStringRecyclerViewAdapter(RecyclerView recyclerView, Message[] items) {
//            mValues = items;
//            mRecyclerView = recyclerView;
//        }
//
//        @Override
//        public SimpleStringRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_item, parent, false);
//            return new SimpleStringRecyclerViewAdapter.ViewHolder(view);
//        }
//
//        @Override
//        public void onViewRecycled(SimpleStringRecyclerViewAdapter.ViewHolder holder) {
////            if (holder.business_name. != null) {
////                holder.mImageView.getController().onDetach();
////            }
////            if (holder.mImageView.getTopLevelDrawable() != null) {
////                holder.mImageView.getTopLevelDrawable().setCallback(null);
//////                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
////            }
//        }
//
//        @Override
//        public void onBindViewHolder(final SimpleStringRecyclerViewAdapter.ViewHolder holder, final int position) {
//            Message message = mValues[position];
//            //Set click action for wishlist
//            holder.business_name.setText(message.getSender());
//            holder.business_message.setText(message.getBody());
//            holder.business_date.setText(message.getDate());
//        }
//
//        @Override
//        public int getItemCount() {
//            return mValues.length;
//        }
//    }

}
