package com.allandroidprojects.ecomsample.account;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.allandroidprojects.ecomsample.model.Address;
import com.allandroidprojects.ecomsample.model.Business;
import com.allandroidprojects.ecomsample.model.LoggedInUser;
import com.allandroidprojects.ecomsample.startup.data.Result;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AccountRepository {

    // Access a Cloud Firestore instance from your Activity
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
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
        businessToSave.put("Business_Owener", business.getUserId());
        businessToSave.put("Business_Name", "");
        businessToSave.put("Business_Address", new Address(business.getUserId()));
        businessToSave.put("Business_Photos", "");

        // Add a new document with a generated ID
        db.collection("BUSINESS").document(business.getUserId())
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

    public static MutableLiveData<Result<Business>> getBusiness(LoggedInUser user){
        final MutableLiveData<Result<Business>> businessMutableData = new MutableLiveData<>();
        DocumentReference docRef = db.collection("BUSINESS").document(user.getUserId());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> business = document.getData();
                        String userId = (String) business.get("Business_Owener");
                        String businessName = (String) business.get("Business_Name");
                        Address businessAddress = (Address) business.get("Business_Address");
                        String businessPhotos = (String) business.get("Business_Photos");
                        Business business1 = new Business(user, businessName, businessAddress);
                        businessMutableData.setValue(new Result.Success<>(business1));
                    } else {
                        businessMutableData.setValue(new Result.Error(new Exception("Document Not Exists!")));
                    }
                } else {
                    businessMutableData.setValue(new Result.Error(task.getException()));
                }
            }
        });
        return businessMutableData;
    }

}
