package com.allandroidprojects.ecomsample.data.repository.notification;

import androidx.lifecycle.MutableLiveData;

import com.allandroidprojects.ecomsample.data.models.Message;
import com.allandroidprojects.ecomsample.data.models.Parties;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class MessagingRepository {

    private static FirebaseFirestore firestoreInstance = FirebaseFirestore.getInstance();
    private static FirebaseDatabase databaseInstance = FirebaseDatabase.getInstance();

    private static CollectionReference userCollection = firestoreInstance.collection("user");
    private static CollectionReference configCollection = firestoreInstance.collection("config");

    public static volatile MessagingRepository instance;
    public static MessagingRepository getInstance(){
        if (instance == null)
            return new MessagingRepository();
        return instance;
    }


    public static MutableLiveData<Result<String>> getServerKey(){
        final MutableLiveData<Result<String>> serverMutableLiveData = new MutableLiveData<Result<String>>();

        configCollection.document("server-key").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                if (task.getResult()!= null){
                    Map<String, Object> values = task.getResult().getData();
                    String serverKey = (String) values.get("server-key");
                    serverMutableLiveData.setValue(new Result.Success<String>(serverKey));
                }
            }else
                serverMutableLiveData.setValue(new Result.Error(task.getException()));
        });
        //Code Logic for Data
        return serverMutableLiveData;
    }

    public MutableLiveData<Result<Message>> sendMessage(Parties parties, Message myMessage){
        final MutableLiveData<Result<Message>> messageMutableLiveData = new MutableLiveData<Result<Message>>();

        return messageMutableLiveData;
    }


    public MutableLiveData<Result<Message>> getMessage(Parties parties){
        final MutableLiveData<Result<Message>> messageMutableLiveData = new MutableLiveData<Result<Message>>();

        return messageMutableLiveData;
    }
}
