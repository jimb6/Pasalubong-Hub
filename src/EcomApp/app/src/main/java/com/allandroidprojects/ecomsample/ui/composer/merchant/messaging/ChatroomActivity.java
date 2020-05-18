package com.allandroidprojects.ecomsample.ui.composer.merchant.messaging;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.ui.composer.user.authentication.login.LoginActivity;
import com.allandroidprojects.ecomsample.util.ChatMessageListAdapter;
import com.allandroidprojects.ecomsample.interfaces.FCM;
import com.allandroidprojects.ecomsample.data.models.ChatMessage;
import com.allandroidprojects.ecomsample.data.models.Chatroom;
import com.allandroidprojects.ecomsample.data.models.User;
import com.allandroidprojects.ecomsample.data.models.fcm.Data;
import com.allandroidprojects.ecomsample.data.models.fcm.FirebaseCloudMessage;
import com.allandroidprojects.ecomsample.data.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ChatroomActivity extends AppCompatActivity {

    private static final String TAG = "ChatroomActivity";
    //widgets
    private static final String BASE_URL = "https://fcm.googleapis.com/fcm/";
    //firebase
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mMessagesReference;

    //widgets
    private TextView mChatroomName;
    private ListView mListView;
    private EditText mMessage;
    private ImageView mCheckmark;

    //vars
    private Set<String> mTokens;
    private Chatroom mChatroom;
    private List<ChatMessage> mMessagesList;
    private String mServerKey;
    private Set<String> mMessageIdSet;
    private ChatMessageListAdapter mAdapter;
    public static boolean isActivityRunning;
    private Product item;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        mChatroomName = findViewById(R.id.text_chatroom_name);
        mListView = findViewById(R.id.listView);
        mMessage = findViewById(R.id.input_message);
        mCheckmark = findViewById(R.id.checkmark);

        if (getIntent() != null) {
            if (getIntent().hasExtra("product")) {
                item = getIntent().getParcelableExtra("product");
            }
//            stringImageUri = getIntent().getStringExtra(ProductListFragment.STRING_IMAGE_URI);
        }

        setupFirebaseAuth();
//        getChatroom();
//        init();
//        hideSoftKeyboard();
    }

    private void init(){

        mMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListView.setSelection(mAdapter.getCount() - 1); //scroll to the bottom of the list
            }
        });

        mCheckmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!mMessage.getText().toString().equals("")){
                    String message = mMessage.getText().toString();
                    Log.d(TAG, "onClick: sending new message: " + message);

                    //create the new message object for inserting
                    ChatMessage newMessage = new ChatMessage();
                    newMessage.setMessage(message);
                    newMessage.setTimestamp(getTimestamp());
                    newMessage.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    //get a database reference
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                            .child(getString(R.string.dbnode_chatrooms))
                            .child(mChatroom.getChatroom_id())
                            .child(getString(R.string.field_chatroom_messages));

                    //create the new messages id
                    String newMessageId = reference.push().getKey();

                    //insert the new message into the chatroom
                    reference
                            .child(newMessageId)
                            .setValue(newMessage);

                    //clear the EditText
                    mMessage.setText("");

                    String myMessage = mMessage.getText().toString();
                    String title = "Pasalubong Hub";


                        //send message
                        sendMessageToDepartment(title, message);

                        mMessage.setText("");
//                        mTitle.setText("");




                    //refresh the messages list? Or is it done by the listener??
                }else{
                    Toast.makeText(ChatroomActivity.this, "Fill out the title and message fields", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    /**
     * Retrieves the server key for the Firebase server.
     * This is required to send FCM messages.
     */
    private void getServerKey(){
        Log.d(TAG, "getServerKey: retrieving server key.");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(getString(R.string.dbnode_server))
                .orderByValue();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: got the server key.");
                DataSnapshot singleSnapshot = dataSnapshot.getChildren().iterator().next();
                mServerKey = singleSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void sendMessageToDepartment(String title, String message){
        Log.d(TAG, "sendMessageToDepartment: sending message to selected departments.");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //create the interface
        FCM fcmAPI = retrofit.create(FCM.class);

        //attach the headers
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "key=" + "AAAAbeRM7Q4:APA91bGyabDgRxpBF8vg0M-dv-XjKVlZWpWmTuak-eOxkWU6dDKAipjK59aNuPkWg54kBENhvnmXILu1uu3x0WZT7DpMVc8MIV2sG3t-jI8vP8u34BRUUWAC-C6RnBco47SZ7XV861Fk");

        //send the message to all the tokens
        for(String token : mTokens){
            Log.d(TAG, "sendMessageToDepartment: sending to token: " + token);
            Data data = new Data();
            data.setMessage(message);
            data.setTitle(title);
            data.setData_type(getString(R.string.data_type_chat_message));
            FirebaseCloudMessage firebaseCloudMessage = new FirebaseCloudMessage();
            firebaseCloudMessage.setData(data);
            firebaseCloudMessage.setTo(token);

            Call<ResponseBody> call = fcmAPI.send(headers, firebaseCloudMessage);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d(TAG, "onResponse: Server Response: "  + response.toString());
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "onFailure: Unable to send the message." + t.getMessage() );
                    Toast.makeText(ChatroomActivity.this, "error", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    /**
     * Retrieve the chatroom name using a query
     */
    private void getChatroom(){
        Log.d(TAG, "getChatroom: getting selected chatroom details");

        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.intent_chatroom))){
            Chatroom chatroom = intent.getParcelableExtra(getString(R.string.intent_chatroom));
            mChatroom = chatroom;
            mChatroomName.setText(mChatroom.getChatroom_name());
            enableChatroomListener();
        }
    }


    private void getChatroomMessages(){

        if(mMessagesList == null){
            mMessagesList = new ArrayList<>();
            mMessageIdSet = new HashSet<>();
            initMessagesList();
        }
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbnode_chatrooms))
                .child(mChatroom.getChatroom_id())
                .child(getString(R.string.field_chatroom_messages));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    Log.d(TAG, "onDataChange: found chatroom message: "
                            + snapshot.getValue());
                    try {//need to catch null pointer here because the initial welcome message to the
                        //chatroom has no user id
                        ChatMessage message = new ChatMessage();
                        String userId = snapshot.getValue(ChatMessage.class).getUser_id();

                        //check to see if the message has already been added to the list
                        //if the message has already been added we don't need to add it again
                        if(!mMessageIdSet.contains(snapshot.getKey())){
                            Log.d(TAG, "onDataChange: adding a new message to the list: " + snapshot.getKey());
                            //add the message id to the message set
                            mMessageIdSet.add(snapshot.getKey());
                            if(userId != null){ //check and make sure it's not the first message (has no user id)
                                message.setMessage(snapshot.getValue(ChatMessage.class).getMessage());
                                message.setUser_id(snapshot.getValue(ChatMessage.class).getUser_id());
                                message.setTimestamp(snapshot.getValue(ChatMessage.class).getTimestamp());
                                message.setProfile_image("");
                                message.setName("");
                                mMessagesList.add(message);
                            }else{
                                message.setMessage(snapshot.getValue(ChatMessage.class).getMessage());
                                message.setTimestamp(snapshot.getValue(ChatMessage.class).getTimestamp());
                                message.setProfile_image("");
                                message.setName("");
                                mMessagesList.add(message);
                            }
                        }

                    } catch (NullPointerException e) {
                        Log.e(TAG, "onDataChange: NullPointerException: " + e.getMessage());
                    }
                }
                //query the users node to get the profile images and names
//                getUserDetails();
                mAdapter.notifyDataSetChanged(); //notify the adapter that the dataset has changed
                mListView.setSelection(mAdapter.getCount() - 1); //scroll to the bottom of the list
                //initMessagesList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getUserDetails(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        for(int i = 0; i < mMessagesList.size(); i++) {
           // Log.d(TAG, "onDataChange: searching for userId: " + mMessagesList.get(i).getUser_id());
            final int j = i;
            if(mMessagesList.get(i).getUser_id() != null && mMessagesList.get(i).getProfile_image().equals("")){
                Query query = reference.child(getString(R.string.dbnode_users))
                        .orderByKey()
                        .equalTo(mMessagesList.get(i).getUser_id());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DataSnapshot singleSnapshot = dataSnapshot.getChildren().iterator().next();
                        mMessagesList.get(j).setProfile_image(singleSnapshot.getValue(User.class).getProfile_image());
                        mMessagesList.get(j).setName(singleSnapshot.getValue(User.class).getName());
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }

    }

    private void initMessagesList(){
        mAdapter = new ChatMessageListAdapter(ChatroomActivity.this,
                R.layout.layout_chatmessage_listitem, mMessagesList);
        mListView.setAdapter(mAdapter);
        mListView.setSelection(mAdapter.getCount() - 1); //scroll to the bottom of the list
    }

    /**
     * Return the current timestamp in the form of a string
     * @return
     */
    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /*
            ----------------------------- Firebase setup ---------------------------------
    */

    @Override
    protected void onResume() {
        super.onResume();
        checkAuthenticationState();
    }

    private void checkAuthenticationState(){
        Log.d(TAG, "checkAuthenticationState: checking authentication state.");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            Log.d(TAG, "checkAuthenticationState: user is null, navigating back to login screen.");

            Intent intent = new Intent(ChatroomActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }else{
            Log.d(TAG, "checkAuthenticationState: user is authenticated.");
        }
    }

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: started.");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());


                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Toast.makeText(ChatroomActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ChatroomActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                // ...
            }
        };

    }

    /**
     * upadte the total number of message the user has seen
     */
    private void updateNumMessages(int numMessages){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference
                .child(getString(R.string.dbnode_chatrooms))
                .child(mChatroom.getChatroom_id())
                .child(getString(R.string.field_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.field_last_message_seen))
                .setValue(String.valueOf(numMessages));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMessagesReference.removeEventListener(mValueEventListener);
    }

    ValueEventListener mValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            getChatroomMessages();

            //get the number of messages currently in the chat and update the database
            int numMessages = (int) dataSnapshot.getChildrenCount();
            updateNumMessages(numMessages);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void enableChatroomListener(){
         /*
            ---------- Listener that will watch the 'chatroom_messages' node ----------
         */
        mMessagesReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbnode_chatrooms))
                .child(mChatroom.getChatroom_id())
                .child(getString(R.string.field_chatroom_messages));

        mMessagesReference.addValueEventListener(mValueEventListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
        isActivityRunning = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
        isActivityRunning = false;
    }
}






















