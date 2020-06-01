package com.allandroidprojects.ecomsample.data.repository;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.allandroidprojects.ecomsample.data.mapping.ProductDataMapping;
import com.allandroidprojects.ecomsample.data.mapping.RatingDataMapping;
import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.Product;
import com.allandroidprojects.ecomsample.data.models.Rating;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.models.SearchData;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class ProductRepository {

    // Access a Cloud Firestore instance from your Activity
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final CollectionReference ref = db.collection("products");
    private static StorageReference storage = FirebaseStorage.getInstance().getReference();
    private static volatile ProductRepository instance;

    public static ProductRepository getInstance() {
        if (instance == null) {
            instance = new ProductRepository();
        }
        return instance;
    }

    public static MutableLiveData<Result<Product>> index() {
        final MutableLiveData<Result<Product>> productMutableLiveData = new MutableLiveData<>();
        ref.get().addOnCompleteListener(t -> {
            if (!t.isSuccessful())
                productMutableLiveData.setValue(new Result.Error(t.getException()));
            else {
                for (QueryDocumentSnapshot document : t.getResult()) {
                    ProductDataMapping mapping = new ProductDataMapping(document.getData());
                    productMutableLiveData.setValue(new Result.Success<Product>(mapping.getData()));
                }
            }
        });
        return productMutableLiveData;
    }

//    This shows product base on product
    public static MutableLiveData<Result<Product>> show(Product productID){
        final MutableLiveData<Result<Product>> productMutableLiveData = new MutableLiveData<>();
        ref.document(productID.getProductReference()).addSnapshotListener((documentSnapshot, e) -> {
            if (documentSnapshot != null) {
                ProductDataMapping mapping = new ProductDataMapping(documentSnapshot.getData());
                productMutableLiveData.setValue(new Result.Success<Product>(mapping.getData()));
            } else {
                productMutableLiveData.setValue(new Result.Error(e));
            }

        });
        return productMutableLiveData;
    }

    //This shows products to intended business
    public static MutableLiveData<Result<Product>> show(Business business) {
        final MutableLiveData<Result<Product>> productListMutableLiveData = new MutableLiveData<>();
        ref.whereEqualTo("businessOwnerId", business.getOwnerId())
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

//    This shows products based on searchdata
    public static MutableLiveData<Result<Product>> show(SearchData searchData) {
        final MutableLiveData<Result<Product>> productMutableLiveData = new MutableLiveData<>();
       ref.whereArrayContains("tags", searchData.query)
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

//    This sows products based on category
    public static MutableLiveData<Result<Product>> show(String category) {
        final MutableLiveData<Result<Product>> productListMutableLiveData = new MutableLiveData<>();
        ref.whereEqualTo("productCategory", category)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
//                            ArrayList<Product> products = new ArrayList<>();
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            ProductDataMapping mapping =  new ProductDataMapping(document.getData());
                            document.getReference()
                                    .collection("ratings")
                                    .get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    ArrayList<Rating> ratings = new ArrayList<>();
                                    for (QueryDocumentSnapshot document1 : Objects.requireNonNull(task1.getResult())) {
                                        RatingDataMapping ratingDataMapping = new RatingDataMapping(document1.getData());
                                        ratings.add(ratingDataMapping.getData());
                                    }
                                    mapping.getData().setRatings(ratings);
                                    productListMutableLiveData.setValue(new Result.Success<Product>(mapping.getData()));
                                }
                            });
                        }
                    } else {
                        productListMutableLiveData.setValue(new Result.Error(task.getException()));
                    }
                });
        return productListMutableLiveData;
    }

    public static MutableLiveData<Result<Product>> store(Product product){
        // Get a new write batch
        final MutableLiveData<Result<Product>> productRegistrationMutableData = new MutableLiveData<>();
        ref.document()
                .set(product)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        productRegistrationMutableData.setValue(new Result.Success<>(product));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        productRegistrationMutableData.setValue(new Result.Error(e));
                    }
                });
        return productRegistrationMutableData;
    }

    public static MutableLiveData<Result<Product>> update(Product product){
        final MutableLiveData<Result<Product>> productRegistrationMutableData = new MutableLiveData<>();
        ProductDataMapping mapping = new ProductDataMapping(product);
        ref.document(product.getProductReference())
                .update(mapping.getMapData())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        productRegistrationMutableData.setValue(new Result.Success<>(product));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        productRegistrationMutableData.setValue(new Result.Error(e));
                    }
                });
        return productRegistrationMutableData;
    }

    public static MutableLiveData<Boolean> destroy(Product product) {
        final MutableLiveData<Boolean> retVal = new MutableLiveData<Boolean>();
        ref.document(product.getProductReference())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        retVal.setValue(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        retVal.setValue(false);
                    }
                });
        return retVal;
    }



    public static MutableLiveData<Result<Uri>> saveNewProductImages(String ownerId, ArrayList<Uri> images) {
        final MutableLiveData<Result<Uri>> productImnages = new MutableLiveData<>();
//        Create Folder
        for (Uri uri : images) {
            final StorageReference ref = storage.child("products/" + ownerId + uri.getLastPathSegment());
//            ref.putFile(uri);
            UploadTask uploadTask = ref.putFile(uri);
//            final StorageReference ref = storage.child(uri.getLastPathSegment());
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        productImnages.setValue(new Result.Error(task.getException()));
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        productImnages.setValue(new Result.Success<>(downloadUri));
                    } else {
                        // Handle failures
                        // ...
                        productImnages.setValue(new Result.Error(task.getException()));
                    }
                }
            });
        }
        return productImnages;
    }
}
