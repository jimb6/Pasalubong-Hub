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
import androidx.core.provider.FontRequest;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.models.Product;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.MessagingActivity;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.adapter.ChatMessagesAdapter;
import com.allandroidprojects.ecomsample.data.models.Inbox;
import com.allandroidprojects.ecomsample.data.models.Message;
import com.allandroidprojects.ecomsample.data.models.MessageProduct;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    private View view;

    private MessagingActivity parent;
    private ArrayList<Message> messages = new ArrayList<>();
    private Inbox inbox;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Product product;

    //    Variables
    private boolean containProduct;
    private String conversationId;


    public static MessagingFragment newInstance() {
        return new MessagingFragment();
    }

    private void getInboxMessages(Inbox inbox) {
        if (inbox == null) {
            return;
        }
        mViewModel.getInboxMessages(inbox);
        mViewModel.getAllInboxMessageMutableLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
//                messages.add((Message) ((Result.Success) result).getData());
//                adapter.notifyDataSetChanged();
            }
            messagesLoading.setVisibility(View.GONE);
        });
    }

    private void registerMessageEventHandler(Inbox inbox) {
        mViewModel.registerEventListener(inbox);
        mViewModel.newMessageArrived().observe(getViewLifecycleOwner(), messageResult -> {
            if (messageResult instanceof Result.Success) {
                messages.add((Message) ((Result.Success) messageResult).getData());
                adapter.notifyDataSetChanged();
                messagesLoading.setVisibility(View.GONE);
            }
        });
    }

    private void registerMessageEventHandler(String id) {
        mViewModel.registerEventListener(id);
        mViewModel.newMessageArrived().observe(getViewLifecycleOwner(), messageResult -> {
            if (messageResult instanceof Result.Success) {
                messages.add((Message) ((Result.Success) messageResult).getData());
                adapter.notifyDataSetChanged();
                messagesLoading.setVisibility(View.GONE);
            }
        });
    }

    private void setupProduct(Product product) {
        productBottomSheet.setVisibility(View.VISIBLE);
        productName.setText(product.getProductname());
        productPrice.setText(product.getPrice() + "");
        productImage.setImageURI(product.getImageUrls().get(0));
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChatMessagesAdapter(getActivity(), recyclerView, messages, inbox);
        recyclerView.setAdapter(adapter);
    }

    private void initialzieViewModel() {
        MessagingViewModelFactory factory = new MessagingViewModelFactory(getContext());
        mViewModel = ViewModelProviders.of(this, factory).get(MessagingViewModel.class);
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
            setMessage();
            mMessage.setText("");
        });
    }

    private void setMessage() {
        if (mMessage.getText().equals(""))
            return;

        Message message = new Message();
        message.setMessage(mMessage.getText().toString());
//      Set date for message
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String strDate = dateFormat.format(date);
//
        message.setCreatedAt(strDate);
        message.setSenderId(user.getUid());
        message.setUserId(user.getUid());

        message.setSenderId(user.getUid());

        String id = "";

        if (inbox != null) {
            id = inbox.getChatroomId();
            message.setBusinessId(inbox.getBusinessId());
            message.setReceiverId(inbox.getBusinessId());
        }
        if (product != null) {
            MessageProduct messageProduct = new MessageProduct(product.getBusinessOwnerId(),
                    product.getProductname(), product.getProductDescription(),
                    String.valueOf(product.getPrice()), product.getImageUrls().get(0));
            message.setProduct(messageProduct);

            message.setBusinessId(product.getBusinessOwnerId());
            message.setReceiverId(product.getBusinessOwnerId());
            id = conversationId;
        }

        mViewModel.sendMessage(id, message);
        mViewModel.sendMessageResult().observe(getViewLifecycleOwner(), messageResult -> {

        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.messaging_fragment, container, false);
        FontRequest fontRequest = new FontRequest(
                "com.example.fontprovider",
                "com.example",
                "emoji compat Font Query",
                R.array.com_google_android_gms_fonts_certs);
        EmojiCompat.Config config = new FontRequestEmojiCompatConfig(getContext(), fontRequest);
        EmojiCompat.init(config);

        initialzieViewModel();
        this.parent = (MessagingActivity) getActivity();
        setupComponents(view);

        if (getArguments().containsKey(getString(R.string.message_with_product_item))) {
            product = getArguments().getParcelable(getString(R.string.message_with_product_item));
            conversationId = getArguments().getString("CONVERSATION_ID");
            if (product != null) {
                setupProduct(product);
                registerMessageEventHandler(conversationId);
                containProduct = false;
            }
            setupRecyclerView(view);
        }

        containProduct = false;

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    public void setCurrentInbox(Inbox inbox) {
        this.inbox = inbox;
        if (inbox != null) {
            registerMessageEventHandler(inbox);
        }
        setupRecyclerView(view);
    }

    public void setCurrentInboxWithProduct(String conversationId, Product product) {
        if (product != null) {
            setupProduct(product);
            registerMessageEventHandler(conversationId);
        }
        setupRecyclerView(view);
    }


}
