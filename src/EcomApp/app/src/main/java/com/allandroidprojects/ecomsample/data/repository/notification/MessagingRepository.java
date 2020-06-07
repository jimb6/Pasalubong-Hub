package com.allandroidprojects.ecomsample.data.repository.notification;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.allandroidprojects.ecomsample.data.mapping.BusinessDataMapping;
import com.allandroidprojects.ecomsample.data.mapping.ProductDataMapping;
import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.LoggedInUser;
import com.allandroidprojects.ecomsample.data.models.Message;
import com.allandroidprojects.ecomsample.data.models.Product;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.models.fcm.Chatroom;
import com.allandroidprojects.ecomsample.data.models.fcm.ChatroomUsers;
import com.google.firebase.Timestamp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagingRepository {

    private static FirebaseFirestore firestoreInstance = FirebaseFirestore.getInstance();
    private static FirebaseDatabase databaseInstance = FirebaseDatabase.getInstance();

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

    public MutableLiveData<Result<Message>> sendMessage(String sender, String receiver, Message myMessage) {
        final MutableLiveData<Result<Message>> messageMutableLiveData = new MutableLiveData<Result<Message>>();
        Map<String, Object> data = new HashMap<>();
        data.put("message", myMessage.getText());
        data.put("recipient", Arrays.asList(new String[]{sender, receiver}));
        data.put("customer_id", sender);
        data.put("timestamp", Timestamp.now());
        if (myMessage.getProduct() != null)
            data.put("product", myMessage.getProduct());
        data.put("seller_id", receiver);

        Query query = firestoreInstance.collection("chatrooms").whereEqualTo("creator_id", sender);
        query = query.whereEqualTo("business_id", receiver);
        query.get()
                .addOnCompleteListener(tasks -> {
                    if (tasks.isSuccessful()) {
                        QuerySnapshot documents = tasks.getResult();
                        if (documents == null) {
                        } else {
                            for (DocumentSnapshot document : documents) {
                                document.getReference().collection("messages")
                                        .document().set(data).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Message sent.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Message sending failed.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                });
        return messageMutableLiveData;
    }

    public MutableLiveData<Result<Message>> getMessage(String userId, String sellerId) {
        final MutableLiveData<Result<Message>> messageMutableLiveData = new MutableLiveData<Result<Message>>();
        Query query = chatrooms.whereEqualTo("creator_id", userId);
        query.whereEqualTo("business_id", sellerId).orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (queryDocumentSnapshots != null) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentChange docs : queryDocumentSnapshots.getDocumentChanges()) {
                                DocumentSnapshot snapshot = docs.getDocument();
                                snapshot.getReference().collection("messages")
                                        .addSnapshotListener((task, e2) -> {
                                            if (!task.isEmpty()) {
                                                List<DocumentChange> subSnapshot = task.getDocumentChanges();
                                                for (DocumentChange documentChange : subSnapshot) {
                                                    DocumentSnapshot subDocument = documentChange.getDocument();
                                                    if (subDocument.exists()) {
                                                        Log.d("CHAT APP:", subDocument.getReference().getPath());
                                                        Product product = null;
                                                        if (subDocument.getData().containsKey("product")) {
                                                            ProductDataMapping productDataMapping = new ProductDataMapping((Map<String, Object>) subDocument.getData().get("product"));
                                                            product = productDataMapping.getData();
                                                        }
                                                        String text = subDocument.getString("message");
                                                        String time = String.valueOf(subDocument.getTimestamp("timestamp"));
                                                        String sender = subDocument.getString("seller_id");
                                                        Message message = new Message(text, time, sender.equals(userId), product);
                                                        messageMutableLiveData.setValue(new Result.Success<>(message));
                                                    } else {
                                                        messageMutableLiveData.setValue(new Result.Error(new Exception("Inbox not available")));
                                                    }
                                                }
                                            } else {
                                                messageMutableLiveData.setValue(new Result.Error(new Exception("Inbox not available")));
                                            }
                                        });
                            }

                        } else {
                            messageMutableLiveData.setValue(new Result.Error(new Exception("Inbox not available")));
                        }
                    } else {
                        messageMutableLiveData.setValue(new Result.Error(new Exception("Inbox not available")));
                    }

                });
        return messageMutableLiveData;
    }

    public MutableLiveData<Result<Chatroom>> getMyInbox(String userId) {
        final MutableLiveData<Result<Chatroom>> resultMutableLiveData = new MutableLiveData<Result<Chatroom>>();
        chatrooms.whereEqualTo("creator_id", userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapshots = task.getResult();
                if (!snapshots.isEmpty()) {
                    for (DocumentSnapshot snapshot : snapshots) {
                        Map<String, Object> chatroomData = snapshot.getData();

                        Chatroom message = new Chatroom();
                        message.setChatroom_id((String) chatroomData.get("chatroom_id"));
                        message.setChatroom_name((String) chatroomData.get("chatroom_name"));
                        message.setCreator_id((String) chatroomData.get("creator_id"));
                        message.setBusinessId((String) chatroomData.get("business_id"));
                        message.setSecurity_level(String.valueOf(chatroomData.get("security_level")));

                        HashMap<String, ChatroomUsers> chatroomUsers = new HashMap<>();

                        snapshot.getReference().collection("users").get().addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                QuerySnapshot snapshots2 = task2.getResult();
                                if (!snapshots2.isEmpty()) {
                                    for (DocumentSnapshot documentSnapshot : snapshots2) {
                                        Map<String, Object> userData = snapshot.getData();
                                        String id = (String) documentSnapshot.get("id");
                                        String lastUnseenMessage = (String) documentSnapshot.get("lastUnseenMessage");
                                        String lastSeenMessageNumber = String.valueOf(documentSnapshot.get("lastSeenMessageNumber"));
                                        String userImage = (String) documentSnapshot.get("userProfileImage");
                                        ChatroomUsers chatroomUser = new ChatroomUsers(id, userImage, lastUnseenMessage, lastSeenMessageNumber);
                                        chatroomUsers.put(id, chatroomUser);
                                    }
                                    message.setUsers(chatroomUsers);
                                    resultMutableLiveData.setValue(new Result.Success<>(message));
                                } else {
                                    resultMutableLiveData.setValue(new Result.Error(new Exception("No Data.")));
                                }
                            } else {
                                resultMutableLiveData.setValue(new Result.Error(task2.getException()));
                            }
                        });
                    }
                } else {
                    resultMutableLiveData.setValue(new Result.Error(new Exception("No Data.")));
                }
            } else {
                resultMutableLiveData.setValue(new Result.Error(task.getException()));
            }
        });


        return resultMutableLiveData;
    }

    public MutableLiveData<Result<LoggedInUser>> getUserDetails(String userId) {
        final MutableLiveData<Result<LoggedInUser>> resultMutableLiveData = new MutableLiveData<>();
        firestoreInstance.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> data = document.getData();
                    LoggedInUser user = new LoggedInUser((String) data.get("userId"),
                            (String) data.get("displayName"),
                            (String) data.get("email"),
                            Uri.parse((String) data.get("photoUrl")));
                    resultMutableLiveData.setValue(new Result.Success<>(user));
                }
            } else {

            }
        });
        return resultMutableLiveData;
    }

    public MutableLiveData<Result<Business>> getBusinessDetails(String businessId) {
        final MutableLiveData<Result<Business>> resultMutableLiveData = new MutableLiveData<>();
        firestoreInstance.collection("BUSINESS").document(businessId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    BusinessDataMapping mapping = new BusinessDataMapping(document.getData());
                    Business business = mapping.getData();
                    resultMutableLiveData.setValue(new Result.Success<>(business));
                }
            }
        });
        return resultMutableLiveData;
    }

    public MutableLiveData<Result<Message>> sendNewMesage(String sender, Business receiver, Message myMessage) {
        final MutableLiveData<Result<Message>> resultMutableLiveData = new MutableLiveData<>();
        Map<String, Object> data = new HashMap<>();
        data.put("message", myMessage.getText());
        data.put("recipient", Arrays.asList(new String[]{sender, receiver.getOwnerId()}));
        data.put("customer_id", sender);
        data.put("timestamp", Timestamp.now());
        if (myMessage.getProduct() != null)
            data.put("product", myMessage.getProduct());
        data.put("seller_id", sender);

        Query query = firestoreInstance.collection("chatrooms").whereEqualTo("creator_id", sender);
        query = query.whereEqualTo("business_id", receiver.getOwnerId());
        query.get()
                .addOnCompleteListener(tasks -> {
                    if (tasks.isSuccessful()) {
                        QuerySnapshot documents = tasks.getResult();
                        Map<String, Object> convo = new HashMap<>();
                        convo.put("business_id", receiver.getOwnerId());
                        convo.put("chatroom_name", receiver.getBusinessName());
                        convo.put("creator_id", sender);
                        convo.put("security_level", 10);
                        convo.put("timestamp", Timestamp.now());

                        WriteBatch batch = firestoreInstance.batch();
                        DocumentReference nycRef = firestoreInstance.collection("chatrooms").document();
                        batch.set(nycRef, convo);

                        DocumentReference sfRef = nycRef.collection("messages").document();
                        batch.set(sfRef, data);

                        Map<String, Object> users = new HashMap<>();
                        users.put("id", sender);
                        users.put("lastSeenMessageNumber", 1L);
                        users.put("lastUnseenMessage", myMessage.getText());
                        users.put("userProfileImage", 10);
                        users.put("timestamp", Timestamp.now());

                        DocumentReference storeUser = nycRef.collection("users").document(receiver.getOwnerId());
                        batch.set(storeUser, users);

                        users = new HashMap<>();
                        users.put("id", receiver.getOwnerId());
                        users.put("lastSeenMessageNumber", "1");
                        users.put("lastUnseenMessage", myMessage.getText());
                        users.put("userProfileImage", receiver.getCoverUri());
                        users.put("timestamp", Timestamp.now());

                        DocumentReference storeSeller = nycRef.collection("users").document(sender);
                        batch.set(storeSeller, users);

                        batch.commit()
                                .addOnCompleteListener(task -> {
                                    resultMutableLiveData.setValue(new Result.Success<>(myMessage));
                                    Toast.makeText(context, "Message sent!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    resultMutableLiveData.setValue(new Result.Error(e));
                                    Toast.makeText(context, "Message send failed.", Toast.LENGTH_SHORT).show();
                                });

                    }
                });
        return resultMutableLiveData;
    }
}
