package com.allandroidprojects.ecomsample.data.repository.notification;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.LoggedInUser;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.model.Inbox;
import com.allandroidprojects.ecomsample.ui.common.components.messaging.model.Message;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class MessagingRepository {

    private static FirebaseFirestore firestoreInstance = FirebaseFirestore.getInstance();
    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private static CollectionReference userCollection = firestoreInstance.collection("user");
    private static CollectionReference configCollection = firestoreInstance.collection("config");
    private static CollectionReference chatrooms = firestoreInstance.collection("chatrooms");
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

        configCollection.document("server-key").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    Map<String, Object> values = task.getResult().getData();
                    String serverKey = (String) values.get("server-key");
                    serverMutableLiveData.setValue(new Result.Success<String>(serverKey));
                }
            } else
                serverMutableLiveData.setValue(new Result.Error(task.getException()));
        });
        //Code Logic for Data
        return serverMutableLiveData;
    }

    public MutableLiveData<Result<Message>> sendMessage(String inboxID, Message message) {
        final MutableLiveData<Result<Message>> messageMutableLiveData = new MutableLiveData<Result<Message>>();
        databaseReference.child("chatrooms").child(inboxID).child("messages").push().setValue(message)
        .addOnSuccessListener(aVoid -> {
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
                Iterator<Map.Entry<String,Object>> parent = data.entrySet().iterator();
                while (parent.hasNext()){
                    Map<String, Object> values = (Map<String, Object>) parent.next().getValue();
                    String id = (String) values.get("id");
                    String text = (String) values.get("text");
                    String createdAt = (String) values.get("createdAt");

                    Message message = new Message(id, text, null);
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
        Query myTopPostsQuery = databaseReference.child("user_conversation").child(userId);
        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                    Iterator<Map.Entry<String,Object>> parent = data.entrySet().iterator();
                    while (parent.hasNext()){
                        String value = (String) parent.next().getValue();
                        databaseReference.child("chatrooms").child(value).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Inbox inbox = dataSnapshot.getValue(Inbox.class);
                                resultMutableLiveData.setValue(new Result.Success<>(inbox));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                resultMutableLiveData.setValue(new Result.Error(new Exception(new Exception("No Data"))));
                            }
                        });
                    }
                }else{
                    resultMutableLiveData.setValue(new Result.Error(new Exception(new Exception("No Data"))));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                resultMutableLiveData.setValue(new Result.Error(databaseError.toException()));
            }
        });
        return resultMutableLiveData;
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
