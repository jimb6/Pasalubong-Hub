package com.allandroidprojects.ecomsample.ui.common.components.messaging.messages;

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
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.MessagingActivity;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.adapter.ChatInboxAdapter;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.adapter.ChatMessagesAdapter;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.model.Inbox;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.model.Message;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

public class MessagingFragment extends Fragment {

    private MessagingViewModel mViewModel;

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

    private MessagingActivity parent;

    private ArrayList<Message> messages = new ArrayList<>();
    private Inbox inbox;

    public static MessagingFragment newInstance() {
        return new MessagingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.messaging_fragment, container, false);

        initialzieViewModel();
        this.parent = (MessagingActivity) getActivity();
        setupComponents(view);
        setupRecyclerView(view);

        return view;
    }

    private void getInboxMessages(Inbox inbox){
        if(inbox == null) {
            return;
        }
        mViewModel.getInboxMessages(inbox);
        mViewModel.getAllInboxMessageMutableLiveData().observe(getViewLifecycleOwner(), result ->{
            if (result instanceof Result.Success){
                messages.add((Message) ((Result.Success) result).getData());
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChatMessagesAdapter(getActivity(), recyclerView, messages, inbox);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel
    }

    private void initialzieViewModel(){
        MessagingViewModelFactory factory = new MessagingViewModelFactory(getContext());
        mViewModel = ViewModelProviders.of(this,factory).get(MessagingViewModel.class);
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

    @Override
    public void onResume() {
        super.onResume();
    }


    public void setCurrentInbox(Inbox inbox) {
        this.inbox = inbox;
        if (inbox != null)
            getInboxMessages(inbox);
    }
}
