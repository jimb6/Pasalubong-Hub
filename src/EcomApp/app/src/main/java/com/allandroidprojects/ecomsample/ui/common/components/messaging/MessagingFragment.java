package com.allandroidprojects.ecomsample.ui.common.components.messaging;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.allandroidprojects.ecomsample.R;
import com.facebook.drawee.view.SimpleDraweeView;

public class MessagingFragment extends Fragment {

    private MessegaingViewModel mViewModel;

    //widgets
    private EditText mMessage;
    private TextView sellerName, productName, productPrice;
    private ImageButton mCheckmark;
    private SimpleDraweeView sellerLogo, productImage;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private View productBottomSheet;
    private View messagesLoading;
    private View userInfoLoading;
    private TextView loadingText;

    public static MessagingFragment newInstance() {
        return new MessagingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.messegaing_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MessegaingViewModel.class);
        // TODO: Use the ViewModel
    }

    private void setupComponents(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        messagesLoading = view.findViewById(R.id.messagesLoading);
        userInfoLoading = view.findViewById(R.id.userInfoLoading);
        loadingText = view.findViewById(R.id.loadingText);
        mMessage = view.findViewById(R.id.input_message);
        mCheckmark = view.findViewById(R.id.send);
        productName = view.findViewById(R.id.productName);
        productPrice = view.findViewById(R.id.productPrice);
        productImage = view.findViewById(R.id.productImage);
        productBottomSheet = view.findViewById(R.id.productBottomSheet);

        mCheckmark.setOnClickListener(v -> {
//            setMessage();
        });
    }

}
