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
        getUserInfo(customerId);
        getSellerInfo(sellerId);
        getMessages(customerId, sellerId);
        hideSoftKeyboard();
    }


    private void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void getSellerInfo(String businessID) {
        mViewmodel.getBusinessDetails(businessID);
        mViewmodel.getBusinessDetailsResult().observe(this, result -> {
            if(result instanceof Result.Success){
                business = (Business) ((Result.Success) result).getData();
                userInfoLoading.setVisibility(View.GONE);
                sellerLogo.setImageURI(business.getCoverUri());
                sellerName.setText(business.getBusinessName());
            }
        });
    }

    private void getUserInfo(String userId) {
        mViewmodel.getUserInfo(userId);
        mViewmodel.getUserInfoResult().observe(this, result -> {
            if(result instanceof Result.Success){
                user = (LoggedInUser) ((Result.Success) result).getData();
            }
        });
    }

    private void getMessages(String customerId, String sellerId) {
        if (customerId == null || sellerId == null || customerId.equals("") || sellerId.equals(""))
            return;

        mViewmodel.getMessages(customerId, sellerId);
        mViewmodel.getMessagesResult().observe(this, result -> {
            if(result instanceof Result.Success){
                messages.add((Message) ((Result.Success) result).getData());
                adapter.notifyDataSetChanged();
                conversationExists = true;
            }else{
                conversationExists = false;
            }
            messagesLoading.setVisibility(View.GONE);
        });
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
        adapter = new SimpleStringRecyclerViewAdapter(recyclerView, messages);
        // Scroll to bottom on new messages
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                layoutManager.smoothScrollToPosition(recyclerView, null, adapter.getItemCount());
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void sendMessage(String sender, String receiver, Message message) {
        mViewmodel.sendMessage(sender, receiver, message);
        mViewmodel.sendMessageResult().observe(this, result ->{
            if(result instanceof Result.Success){
                mMessage.setText("");
                messageSuccess(message);
            }else{
                messageError(message);
            }
        });
    }

    private void sendNewMessage(String sender, Business receiver, Message message){

        mViewmodel.sendNewMessage(sender, receiver, message);
        mViewmodel.sendNewMessageResult().observe(this, result ->{
            if(result instanceof Result.Success){
                mMessage.setText("");
                messageSuccess(message);
            }else{
                messageError(message);
            }
        });
    }

    private void setMessage() {
        if (!mMessage.getText().toString().trim().equals("")) {
            Message message;
            if (product != null)
                message  = new Message(mMessage.getText().toString(), String.valueOf(Timestamp.now()), true, product);
            else
                message = new Message(mMessage.getText().toString(), String.valueOf(Timestamp.now()), true);

            message.setPosition(messages.size());

            messages.add(message);
            adapter.notifyDataSetChanged();

            if(!conversationExists){
                sendNewMessage(user.getUserId(), business, message);
            }else{
                sendMessage(user.getUserId(), business.getOwnerId(), message);
            }
        }
    }

    private void messageSuccess(Message message){
        View  view = layoutManager.findViewByPosition(message.getPosition());
        SimpleDraweeView badge = view.findViewById(R.id.image_message_profile);
        badge.setVisibility(View.VISIBLE);
    }

    private void messageError(Message message){
        View  view = layoutManager.findViewByPosition(message.getPosition());
        SimpleDraweeView badge = view.findViewById(R.id.image_message_profile);
        badge.setImageDrawable(getResources().getDrawable(R.drawable.icon_close));
        badge.setVisibility(View.VISIBLE);
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


    enum ChatSenderType {
        SELLER(0), CUSTOMER(1), PRODUC_ITEM(2);

        private int data;

        ChatSenderType(int data) {
            this.data = data;
        }

        int getData() {
            return data;
        }
    }


    public class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList<Message> mValues;
        private RecyclerView mRecyclerView;

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

        public SimpleStringRecyclerViewAdapter(RecyclerView recyclerView, ArrayList<Message> items) {
            mValues = items;
            mRecyclerView = recyclerView;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == ChatSenderType.CUSTOMER.getData()) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.their_message, parent, false);
                return new SenderViewHolder(view);
            } else if (viewType == ChatSenderType.SELLER.getData()) {
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

            if (getItemViewType(position) == ChatSenderType.CUSTOMER.getData()) {
                ((SenderViewHolder) holder).message.setText(item.getText());
                ((SenderViewHolder) holder).date.setText(item.getTimestamp());
                ((SenderViewHolder) holder).message.setOnClickListener(v -> {
                    if (((SenderViewHolder) holder).date.getVisibility() == View.GONE)
                        ((SenderViewHolder) holder).date.setVisibility(View.VISIBLE);
                    else
                        ((SenderViewHolder) holder).date.setVisibility(View.GONE);
                });
            } else if (getItemViewType(position) == ChatSenderType.SELLER.getData()) {
                ((ReceiverViewHolder) holder).message.setText(item.getText());
                ((ReceiverViewHolder) holder).date.setText(item.getTimestamp());
                ((ReceiverViewHolder) holder).message.setOnClickListener(v -> {
                    if (((ReceiverViewHolder) holder).date.getVisibility() == View.GONE)
                        ((ReceiverViewHolder) holder).date.setVisibility(View.VISIBLE);
                    else
                        ((ReceiverViewHolder) holder).date.setVisibility(View.GONE);
                });
            } else {
                ((ProductViewHolder) holder).productName.setText(item.getProduct().getProductname());
                ((ProductViewHolder) holder).productDescription.setText(String.valueOf(item.getProduct().getPrice()));
                ((ProductViewHolder) holder).productImage.setImageURI(Uri.parse(item.getProduct().getImageUrls().get(0)));
                ((ProductViewHolder) holder).productMessage.setText(item.getText());
                ((ProductViewHolder) holder).date.setText(item.getTimestamp());
                ((ProductViewHolder) holder).productImage.setOnClickListener(v -> {

                });
            }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        @Override
        public int getItemViewType(int position) {
            if ((mValues.get(position).getProduct() != null)) {
                return ChatSenderType.PRODUC_ITEM.getData();
            }
            if (mValues.get(position).isBelongsToCurrentUser()) {
                return ChatSenderType.CUSTOMER.getData();
            } else {
                return ChatSenderType.SELLER.getData();
            }
        }

        private void showBadge(int position){

        }
    }
}





















