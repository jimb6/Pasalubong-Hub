package com.allandroidprojects.ecomsample.shop.data;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.allandroidprojects.ecomsample.model.Business;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ShopRepository {

    private static volatile ShopRepository instance;

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static ShopRepository getInstance() {
        if (instance == null) {
            return new ShopRepository();
        }
        return instance;
    }


    public static MutableLiveData<DataResult<Business>> isBusinessDocumentChanged(Business business) {
        final MutableLiveData<DataResult<Business>> documentChangeMutableLiveData = new MutableLiveData<>();
        final DocumentReference docRef = db.collection("BUSINESS").document(business.getUserId());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
