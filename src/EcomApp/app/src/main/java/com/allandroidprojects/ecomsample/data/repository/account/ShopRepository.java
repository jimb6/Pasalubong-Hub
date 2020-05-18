package com.allandroidprojects.ecomsample.data.repository.account;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.allandroidprojects.ecomsample.data.mapping.BusinessDataMapping;
import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.DataResult;
import com.allandroidprojects.ecomsample.data.models.Product;
import com.allandroidprojects.ecomsample.data.models.Rating;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.models.SearchData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class ShopRepository {

    private static volatile ShopRepository instance;
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final CollectionReference businessRef = db.collection("BUSINESS");
    private static final CollectionReference productRef = db.collection("products");

    public static ShopRepository getInstance() {
        if (instance == null) {
            return new ShopRepository();
        }
        return instance;
    }


    public static MutableLiveData<DataResult<Business>> isBusinessDocumentChanged(Business business) {
        final MutableLiveData<DataResult<Business>> documentChangeMutableLiveData = new MutableLiveData<>();
        final DocumentReference docRef = businessRef.document(business.getOwnerId());
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

    public static MutableLiveData<Result<Product>> findMyProducts(Business business) {
        final MutableLiveData<Result<Product>> productListMutableLiveData = new MutableLiveData<>();
        productRef.whereEqualTo("businessOwnerId", business.getOwnerId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
//                            ArrayList<Product> products = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> productData = document.getData();
                            Product product = new Product(business);
                            product.setProductReference(document.getId());
                            product.setBusinessOwnerId((String) productData.get("businessOwnerId"));
                            product.setProductname((String) productData.get("productname"));
                            product.setProductDescription((String) productData.get("productDescription"));
                            product.setProductCategory((String) productData.get("productCategory"));
                            product.setBrand((String) productData.get("brand"));
                            product.setPrice(Double.parseDouble(productData.get("price").toString()));
                            product.setStock(Integer.parseInt(productData.get("stock").toString()));
                            product.setWholeSeller((String) productData.get("wholeSeller"));
                            product.setCondition((String) productData.get("condition"));
                            product.setImageUrls((ArrayList<String>) productData.get("imageUrls"));
                            product.setTags((ArrayList<String>) productData.get("tags"));

                            document.getReference()
                                    .collection("ratings")
                                    .get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    ArrayList<Rating> ratings = new ArrayList<>();
                                    for (QueryDocumentSnapshot document1 : Objects.requireNonNull(task1.getResult())) {
                                        Map<String, Object> ratingsData = document1.getData();
                                        Rating rating = new Rating();
                                        rating.setAuthorId((String) ratingsData.get("userId"));
                                        rating.setComment((String) ratingsData.get("comment"));
                                        rating.setRating(Double.parseDouble(String.valueOf(ratingsData.get("rate"))));
                                        rating.setAuthornName((String) ratingsData.get("userName"));
                                        rating.setAuthornName((String) ratingsData.get("userName"));
                                        rating.setDate(String.valueOf(ratingsData.get("date")));
                                        rating.setUrls((ArrayList<String>) ratingsData.get("imagesUrl"));
                                        rating.setUserImage((String) ratingsData.get("userImage"));
                                        ratings.add(rating);
                                    }
                                    product.setRatings(ratings);
                                    productListMutableLiveData.setValue(new Result.Success<Product>(product));
                                }
                            });
                        }
                    } else {
                        productListMutableLiveData.setValue(new Result.Error(task.getException()));
                    }
                });
        return productListMutableLiveData;
    }

    public static MutableLiveData<Result<Business>> getMyBusiness(Business business) {
        final MutableLiveData<Result<Business>> businessMutableLiveData = new MutableLiveData<>();
        businessRef.document(business.getOwnerId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    BusinessDataMapping mapping = new BusinessDataMapping(documentSnapshot.getData());
                    mapping.bindData();
                    businessMutableLiveData.setValue(new Result.Success<>(mapping.getData()));
                } else {
                    businessMutableLiveData.setValue(new Result.Error(e));
                }

            }
        });
        return businessMutableLiveData;
    }

    public static MutableLiveData<Result<Business>> getMyBusiness(String ownerId) {
        final MutableLiveData<Result<Business>> businessMutableLiveData = new MutableLiveData<>();
        businessRef.whereEqualTo("ownerId", ownerId).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // ...
                        if(queryDocumentSnapshots.isEmpty())
                            businessMutableLiveData.setValue(new Result.Error(new Exception("Business Not Found!")));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                businessMutableLiveData.setValue(new Result.Error(e));
            }
        }).addOnCompleteListener(t -> {
            for (QueryDocumentSnapshot document : t.getResult()) {
                Map<String, Object> data = document.getData();

                businessMutableLiveData.setValue(new Result.Success<Business>(business));
            }
        });
        return businessMutableLiveData;
    }

    public static MutableLiveData<Result<Product>> searchProducts(SearchData searchData) {
        final MutableLiveData<Result<Product>> productMutableLiveData = new MutableLiveData<>();
        db.collection("products")
//                .orderBy("productname").startAt(searchData.query).endAt(searchData.query + "\uf8ff")
//                .whereGreaterThanOrEqualTo("price", searchData.priceFrom)
                .whereArrayContains("tags", searchData.query)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
//                            ArrayList<Product> products = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> productData = document.getData();
                            double price = Double.parseDouble(productData.get("price").toString());

                            if(price >= searchData.priceFrom){
                                if (searchData.priceTo > 0 && price <= searchData.priceTo){
                                    Product product = new Product();
                                    product.setBusinessOwnerId((String) productData.get("businessOwnerId"));
                                    product.setProductname((String) productData.get("productname"));
                                    product.setProductDescription((String) productData.get("productDescription"));
                                    product.setProductCategory((String) productData.get("productCategory"));
                                    product.setBrand((String) productData.get("brand"));
                                    product.setPrice(Double.parseDouble(productData.get("price").toString()));
                                    product.setStock(Integer.parseInt(productData.get("stock").toString()));
                                    product.setWholeSeller((String) productData.get("wholeSeller"));
                                    product.setImageUrls((ArrayList<String>) productData.get("imageUrls"));
//
                                    document.getReference()
                                            .collection("ratings")
                                            .get().addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            ArrayList<Rating> ratings = new ArrayList<>();
                                            for (QueryDocumentSnapshot document1 : Objects.requireNonNull(task1.getResult())) {
                                                Map<String, Object> ratingsData = document1.getData();
                                                Rating rating = new Rating();
                                                rating.setAuthorId((String) ratingsData.get("userId"));
                                                rating.setComment((String) ratingsData.get("comment"));
                                                rating.setRating(Double.parseDouble(String.valueOf(ratingsData.get("rate"))));
                                                rating.setAuthornName((String) ratingsData.get("userName"));
                                                rating.setAuthornName((String) ratingsData.get("userName"));
                                                rating.setDate(String.valueOf(ratingsData.get("date")));
                                                rating.setUrls((ArrayList<String>) ratingsData.get("imagesUrl"));
                                                rating.setUserImage((String) ratingsData.get("userImage"));
                                                ratings.add(rating);
                                            }
                                            product.setRatings(ratings);
                                            productMutableLiveData.setValue(new Result.Success<Product>(product));
                                        }
                                    });
                                }
                            }
                        }
                    } else {
                        productMutableLiveData.setValue(new Result.Error(task.getException()));
                    }
                });
        return productMutableLiveData;
    }


    public static MutableLiveData<Result<Business>> findAllBusiness() {
        final MutableLiveData<Result<Business>> businessMutableLiveData = new MutableLiveData<>();
        businessRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // ...
                        if(queryDocumentSnapshots.isEmpty())
                            businessMutableLiveData.setValue(new Result.Error(new Exception("Business Not Found!")));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                businessMutableLiveData.setValue(new Result.Error(e));
            }
        }).addOnCompleteListener(t -> {
            for (QueryDocumentSnapshot document : t.getResult()) {
                Map<String, Object> data = document.getData();
                Business business = new Business();
                business.setBusinessName((String) data.get("businessName"));
                business.setBusinessAddress((String) data.get("businessAddress"));
                business.setBusinessEmail((String) data.get("businessEmail"));
                business.setBusinessPhotos((ArrayList<String>) data.get("businessPhotos"));
                business.setLat((String) data.get("lat"));
                business.setLng((String) data.get("lng"));

                businessMutableLiveData.setValue(new Result.Success<Business>(business));
            }
        });
        return businessMutableLiveData;
    }
}
