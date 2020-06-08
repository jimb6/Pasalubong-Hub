package com.allandroidprojects.ecomsample.ui.common.components.messaging;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.provider.FontRequest;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;
import androidx.emoji.widget.EmojiTextView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.LoggedInUser;
import com.allandroidprojects.ecomsample.data.models.Message;
import com.allandroidprojects.ecomsample.data.models.Product;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.models.fcm.Chatroom;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.adapter.ChatInboxAdapter;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class ChatroomActivity extends AppCompatActivity {

    private static final String TAG = "ChatroomActivity";
    //firebase
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore ref = FirebaseFirestore.getInstance();
    private CollectionReference chatroom = ref.collection("chatrooms");
    private DocumentReference docRef;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

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

    //vars
    private Chatroom mChatroom;
    public static boolean isActivityRunning;
    private int limit = 25;
    private boolean isScrolling = false;
    private String userId;
    private String customerId;
    private String sellerId;
    private boolean conversationExists;
    private ArrayList<Message> messages = new ArrayList<>();

    //  Objects Class
    private Business business;
    private LoggedInUser user;
    private Product product;
    private Chatroom chatroomInfo;
    private ChatroomViewModel mViewmodel;

    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true;
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
            if (layoutManager != null) {
                int firstVisibleProductPosition = layoutManager.findFirstVisibleItemPosition();
                int visibleProductCount = layoutManager.getChildCount();
                int totalProductCount = layoutManager.getItemCount();

                if (isScrolling && (firstVisibleProductPosition + visibleProductCount == totalProductCount)) {
                    isScrolling = false;
                    limit += 25;
                }
            }
        }
    };

    private void setupComponents() {
        recyclerView = findViewById(R.id.recycler_view);
        messagesLoading = findViewById(R.id.messagesLoading);
        userInfoLoading = findViewById(R.id.userInfoLoading);
        loadingText = findViewById(R.id.loadingText);
        sellerName = findViewById(R.id.tvSeller);
        mMessage = findViewById(R.id.input_message);
        mCheckmark = findViewById(R.id.send);
        sellerLogo = findViewById(R.id.imageView);
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        productImage = findViewById(R.id.productImage);
        productBottomSheet = findViewById(R.id.productBottomSheet);

        mCheckmark.setOnClickListener(v -> {
            setMessage();
        });
    }

    private void initializeViewModel() {
        ChatroomViewModelFactory factory = new ChatroomViewModelFactory(this);
        mViewmodel = ViewModelProviders.of(this, factory).get(ChatroomViewModel.class);
    }


    private void setFirebaseUserInstance() {
        mAuth = FirebaseAuth.getInstance();
        userId = FirebaseAuth.getInstance().getUid();
    }

    private void setFirebaseFirestoreInstance() {
        ref = FirebaseFirestore.getInstance();
        chatroom = ref.collection("chatrooms");
    }

    private void setupIntentExtra() {
        Intent intent = getIntent();
        //Check if extra is with product or not have product...
        if (intent.hasExtra(getString(R.string.message_with_product_item))) {
            product = intent.getParcelableExtra(getString(R.string.message_with_product_item));
            productBottomSheet.setVisibility(View.VISIBLE);
            productName.setText(product.getProductname());
            productPrice.setText(String.valueOf(product.getPrice()));
            productImage.setImageURI(Uri.parse(product.getImageUrls().get(0)));
            sellerId = product.getBusinessOwnerId();
            customerId = userId;
        } else if (intent.hasExtra(getString(R.string.message_without_product_item))) {
            chatroomInfo = intent.getParcelableExtra(getString(R.string.message_without_product_item));
            sellerId = chatroomInfo.getBusinessId();
            customerId = chatroomInfo.getCreator_id();
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        FontRequest fontRequest = new FontRequest(
                "com.example.fontprovider",
                "com.example",
                "emoji compat Font Query",
                R.array.com_google_android_gms_fonts_certs);
        EmojiCompat.Config config = new FontRequestEmojiCompatConfig(this, fontRequest);
        EmojiCompat.init(config);

        initializeViewModel();

        setupComponents();
        setFirebaseUserInstance();
        setFirebaseFirestoreInstance();
        setupIntentExtra();

        setupRecyclerView();
        hideSoftKeyboard();
    }


    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void setupRecyclerView() {
        messages = new ArrayList<>();

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setOnScrollListener(onScrollListener);
        recyclerView.setLayoutManager(layoutManager);
        attachRecyclerViewAdapter();

    }

    private void attachRecyclerViewAdapter() {
//        adapter = new ChatInboxAdapter(this, recyclerView, messages);
//        // Scroll to bottom on new messages
//        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
//                layoutManager.smoothScrollToPosition(recyclerView, null, adapter.getItemCount());
//            }
//        });
//
//        recyclerView.setAdapter(adapter);
    }

    private void sendMessage(String sender, String receiver, Message message) {
//        mViewmodel.sendMessage(sender, receiver, message);
//        mViewmodel.sendMessageResult().observe(this, result -> {
//            if (result instanceof Result.Success) {
//                mMessage.setText("");
////                messageSuccess(message);
//            } else {
////                messageError(message);
//            }
//        });
    }

    private void sendNewMessage(String sender, Business receiver, Message message) {

//        mViewmodel.sendNewMessage(sender, receiver, message);
//        mViewmodel.sendNewMessageResult().observe(this, result -> {
//            if (result instanceof Result.Success) {
//                mMessage.setText("");
////                messageSuccess(message);
//            } else {
////                messageError(message);
//            }
//        });
    }

    private void setMessage() {
        if (!mMessage.getText().toString().trim().equals("")) {
            Message message;
            if (product != null)
                message = new Message(mMessage.getText().toString(), String.valueOf(Timestamp.now()), true, product);
            else
                message = new Message(mMessage.getText().toString(), String.valueOf(Timestamp.now()), true);

            message.setPosition(messages.size());

            messages.add(message);
            adapter.notifyDataSetChanged();

            if (!conversationExists) {
                sendNewMessage(user.getUserId(), business, message);
            } else {
                sendMessage(user.getUserId(), business.getOwnerId(), message);
            }
        }
    }



    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAuth != null) {
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
        isActivityRunning = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
//            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
        isActivityRunning = false;
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}





















