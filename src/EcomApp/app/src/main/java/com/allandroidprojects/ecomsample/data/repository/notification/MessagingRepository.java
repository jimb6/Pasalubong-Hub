package com.allandroidprojects.ecomsample.data.repository.notification;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.LoggedInUser;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.model.Inbox;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.model.Message;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.model.MessageProduct;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class MessagingRepository {

    private static FirebaseFirestore firestoreInstance = FirebaseFirestore.getInstance();
    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private Context context;

    public static volatile MessagingRepository instance;

    public MessagingRepository(Context context) {
        this.context = context;
    }

    public static MessagingRepository getInstance(Context context) {
        if (instance == null)
            return new MessagingRepository(context);
        return instance;
    }

    public static MutableLiveData<Result<String>> getServerKey() {
        final MutableLiveData<Result<String>> serverMutableLiveData = new MutableLiveData<Result<String>>();
        return serverMutableLiveData;
    }

    public MutableLiveData<Result<Message>> sendMessage(String inboxID, Message message) {
        final MutableLiveData<Result<Message>> messageMutableLiveData = new MutableLiveData<Result<Message>>();
        DatabaseReference reference = databaseReference.child("messages").child(inboxID).push();
        reference.setValue(message).addOnSuccessListener(aVoid -> {
            messageMutableLiveData.setValue(new Result.Success(message));
        }).addOnFailureListener(runnable -> {
            messageMutableLiveData.setValue(new Result.Error(runnable));
        });
        return messageMutableLiveData;
    }

    public MutableLiveData<Result<Message>> sendNewMesage(Inbox inbox, Message myMessage) {
        final MutableLiveData<Result<Message>> resultMutableLiveData = new MutableLiveData<>();
        DatabaseReference reference = databaseReference.child("chatrooms").push();
        reference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                mutableData.setValue(inbox);
                mutableData.child("messages").setValue(myMessage);
                resultMutableLiveData.setValue(new Result.Success(myMessage));
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                resultMutableLiveData.setValue(new Result.Error(databaseError.toException()));
            }
        });

        databaseReference.child(Objects.requireNonNull(reference.getKey()))
                .setValue(inbox)
                .addOnSuccessListener(runnable -> {
                    resultMutableLiveData.setValue(new Result.Success(myMessage));
                }).addOnFailureListener(runnable -> {
            resultMutableLiveData.setValue(new Result.Error(runnable));
        });
        return resultMutableLiveData;
    }

    public MutableLiveData<Result<Message>> getInboxMessages(String inboxID) {
        final MutableLiveData<Result<Message>> messageMutableLiveData = new MutableLiveData<Result<Message>>();
        databaseReference.child("messages").child(inboxID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                Iterator<Map.Entry<String, Object>> parent = data.entrySet().iterator();
                while (parent.hasNext()) {
                    Map<String, Object> values = (Map<String, Object>) parent.next().getValue();
                    String senderId = (String) values.get("senderId");
                    String receiverId = (String) values.get("receiverId");
                    String messageText = (String) values.get("message");
                    String createdAt = (String) values.get("createdAt");
                    String businessId = (String) values.get("businessId");
                    String userId = (String) values.get("userId");
                    MessageProduct itemProduct = null;
                    if(values.containsKey("product")){
                        Map<String, Object> product = (Map<String, Object>) values.get("product");
                        String id = (String) product.get("id");
                        String name = (String) product.get("name");
                        String description = (String) product.get("description");
                        String price = (String) product.get("price");
                        String image = (String) product.get("image");
                        itemProduct = new MessageProduct(id, name, description, price, image);
                    }

                    Message message = new Message(senderId, receiverId, messageText, createdAt, itemProduct, businessId, userId);
                    messageMutableLiveData.setValue(new Result.Success<>(message));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                messageMutableLiveData.setValue(new Result.Error(databaseError.toException()));
            }
        });
        return messageMutableLiveData;
    }

    public MutableLiveData<Result<Inbox>> getMyInbox(String userId) {
        final MutableLiveData<Result<Inbox>> resultMutableLiveData = new MutableLiveData<Result<Inbox>>();
        Query myTopPostsQuery = databaseReference.child("chatrooms").child(userId);
        myTopPostsQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Inbox inbox = dataSnapshot.getValue(Inbox.class);
                resultMutableLiveData.setValue(new Result.Success<>(inbox));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return resultMutableLiveData;
    }

    public MutableLiveData<Result<Message>> registerMessageListener(String inboxID) {
        final MutableLiveData<Result<Message>> messageMutableLiveData = new MutableLiveData<Result<Message>>();
        databaseReference.child("messages").child(inboxID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                String senderId = (String) data.get("senderId");
                String receiverId = (String) data.get("receiverId");
                String messageText = (String) data.get("message");
                String createdAt = (String) data.get("createdAt");
                String businessId = (String) data.get("businessId");
                String userId = (String) data.get("userId");

                MessageProduct itemProduct = null;
                if(data.containsKey("product")){
                    Map<String, Object> product = (Map<String, Object>) data.get("product");
                    String id = (String) product.get("id");
                    String name = (String) product.get("name");
                    String description = (String) product.get("description");
                    String price = (String) product.get("price");
                    String image = (String) product.get("image");
                    itemProduct = new MessageProduct(id, name, description, price, image);
                }

                Message message = new Message(senderId, receiverId, messageText, createdAt, itemProduct, businessId, userId);
                messageMutableLiveData.setValue(new Result.Success<>(message));

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                messageMutableLiveData.setValue(new Result.Error(databaseError.toException()));
            }
        });
        return messageMutableLiveData;
    }

    public MutableLiveData<Result<LoggedInUser>> getUserDetails(String userId) {
        final MutableLiveData<Result<LoggedInUser>> resultMutableLiveData = new MutableLiveData<>();

        return resultMutableLiveData;
    }

    public MutableLiveData<Result<Business>> getBusinessDetails(String businessId) {
        final MutableLiveData<Result<Business>> resultMutableLiveData = new MutableLiveData<>();

        return resultMutableLiveData;
    }


}
