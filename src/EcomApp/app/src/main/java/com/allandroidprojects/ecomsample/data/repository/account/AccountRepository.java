package com.allandroidprojects.ecomsample.data.repository.account;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.allandroidprojects.ecomsample.data.mapping.BusinessDataMapping;
import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.LoggedInUser;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AccountRepository {

    // Access a Cloud Firestore instance from your Activity
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final CollectionReference ref = db.collection("BUSINESS");
    private static volatile AccountRepository instance;

    public static AccountRepository getInstance() {
        if (instance == null) {
            instance = new AccountRepository();
        }
        return instance;
    }

    public static MutableLiveData<Result<Business>> registerBusiness(Business business) {
        final MutableLiveData<Result<Business>> businessRegistrationMutableData = new MutableLiveData<>();
        // Create a new user with a first and last name
        Map<String, Object> businessToSave = new HashMap<>();
        businessToSave.put("Business_Owener", business.getOwnerId());
        businessToSave.put("Business_Address", business.getBusinessAddress());
        businessToSave.put("Business_Name", business.getBusinessName());
        businessToSave.put("Business_Email", business.getBusinessEmail());
        businessToSave.put("Business_Photos", business.getBusinessPhotos());

        // Add a new document with a generated ID
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

    public static MutableLiveData<Result<Business>> getBusiness(LoggedInUser user) {
        final MutableLiveData<Result<Business>> businessMutableData = new MutableLiveData<>();
        DocumentReference docRef = db.collection("BUSINESS").document(user.getUserId());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            Map<String, Object> businessObjects = document.getData();

                            BusinessDataMapping data = new BusinessDataMapping(document.getData());
                            data.bindData();

                            businessMutableData.setValue(new Result.Success<>(data.getData()));
                        } else {
                            businessMutableData.setValue(new Result.Error(new Exception("Document Not Exists!")));
                        }
                    }
                } else {
                    businessMutableData.setValue(new Result.Error(task.getException()));
                }
            }
        });
        return businessMutableData;
    }

}
