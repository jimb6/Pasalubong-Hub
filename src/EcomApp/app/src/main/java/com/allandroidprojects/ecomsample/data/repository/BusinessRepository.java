package com.allandroidprojects.ecomsample.data.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.allandroidprojects.ecomsample.data.mapping.BusinessDataMapping;
import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.DataResult;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Map;

public class BusinessRepository {

    // Access a Cloud Firestore instance from your Activity
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final CollectionReference ref = db.collection("BUSINESS");
    private static volatile BusinessRepository instance;

    public static BusinessRepository getInstance() {
        if (instance == null) {
            instance = new BusinessRepository();
        }
        return instance;
    }

    public static MutableLiveData<Result<Business>> index() {
        final MutableLiveData<Result<Business>> businessMutableLiveData = new MutableLiveData<>();
        ref.get().addOnCompleteListener(t -> {
            if (!t.isSuccessful())
                businessMutableLiveData.setValue(new Result.Error(t.getException()));
            else {
                for (QueryDocumentSnapshot document : t.getResult()) {
                    Map<String, Object> data = document.getData();
                    BusinessDataMapping business = new BusinessDataMapping(document.getData());
                    businessMutableLiveData.setValue(new Result.Success<Business>(business.getData()));
                }
            }
        });
        return businessMutableLiveData;
    }

    public static MutableLiveData<Result<Business>> show(String businessID){
        final MutableLiveData<Result<Business>> businessMutableLiveData = new MutableLiveData<>();
        ref.document(businessID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    BusinessDataMapping mapping = new BusinessDataMapping(document.getData());
                    businessMutableLiveData.setValue(new Result.Success<>(mapping.getData()));
                }else{
                    businessMutableLiveData.setValue(new Result.Error(new Exception("Document Not Exists!")));
                }
            } else {
                businessMutableLiveData.setValue(new Result.Error(task.getException()));
            }

        });
        return businessMutableLiveData;
    }

    public static MutableLiveData<Result<Business>> store(Business business){
        final MutableLiveData<Result<Business>> businessRegistrationMutableData = new MutableLiveData<>();
        ref.document(business.getOwnerId())
                .set(business)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        businessRegistrationMutableData.setValue(new Result.Success<>(business));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        businessRegistrationMutableData.setValue(new Result.Error(e));
                    }
                });
        return businessRegistrationMutableData;
    }

    public static MutableLiveData<Result<Business>> update(Business business){
        final MutableLiveData<Result<Business>> businessRegistrationMutableData = new MutableLiveData<>();
        BusinessDataMapping mapping = new BusinessDataMapping(business);
        ref.document(business.getOwnerId())
                .update(mapping.getMapData())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        businessRegistrationMutableData.setValue(new Result.Success<>(business));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        businessRegistrationMutableData.setValue(new Result.Error(e));
                    }
                });
        return businessRegistrationMutableData;
    }

    public static MutableLiveData<Result<String>> getTermsAndCondition() {
        final MutableLiveData<Result<String>> termsResultMutableLiveData = new MutableLiveData<>();
        db.collection("config").document("terms-agreement")
                .get().addOnCompleteListener(command -> {
            if (command.isSuccessful()) {
                if (command.getResult() != null) {
                    termsResultMutableLiveData.setValue(new Result.Success<>(command.getResult()));
                }
            } else {
                termsResultMutableLiveData.setValue(new Result.Error(command.getException()));
            }
        });
        return termsResultMutableLiveData;
    }

    public static MutableLiveData<DataResult<Business>> isBusinessDocumentChanged(Business business) {
        final MutableLiveData<DataResult<Business>> documentChangeMutableLiveData = new MutableLiveData<>();
        ref.document(business.getOwnerId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
//                    Map<String, Object> business = snapshot.getData();
//                    String userId = (String) business.get("userId");
//                    String
//                    LoggedInUser user = new LoggedInUser();
//                    Business business1 = new Business();
                    documentChangeMutableLiveData.setValue(new DataResult.Modified<>(business));
                }
            }
        });
        return documentChangeMutableLiveData;
    }

}
