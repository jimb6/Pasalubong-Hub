package com.allandroidprojects.ecomsample.data.remote.product;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.allandroidprojects.ecomsample.data.models.Result;
import com.allandroidprojects.ecomsample.data.models.Product;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddProductRepository {

    public static volatile AddProductRepository instance;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static StorageReference storage = FirebaseStorage.getInstance().getReference();
    private static Boolean bool = false;

    public static AddProductRepository getInstance() {
        if (instance == null)
            return new AddProductRepository();
        return instance;
    }

    public static MutableLiveData<Result<Product>> saveNewProduct(Product product) {
        final MutableLiveData<Result<Product>> productMutableLiveData = new MutableLiveData<>();
//        City city = new City("Los Angeles", "CA", "USA",
//                false, 5000000L, Arrays.asList("west_coast", "sorcal"));
        db.collection("products").document().set(product)
                .addOnSuccessListener(command -> {
                    productMutableLiveData.setValue(new Result.Success<>(product));
                }).addOnFailureListener(command -> {
            new Result.Error(command);
        });
        return productMutableLiveData;
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

    public static MutableLiveData<Result<Product>> updateProduct(Product productToUpdate) {
        final MutableLiveData<Result<Product>> productMutableLiveData = new MutableLiveData<>();
//        City city = new City("Los Angeles", "CA", "USA",
//                false, 5000000L, Arrays.asList("west_coast", "sorcal"));
        Map<String, Object> mapdata = new HashMap<>();
        mapdata.put("productname", productToUpdate.getProductname());
        mapdata.put("productDescription", productToUpdate.getProductDescription());
        mapdata.put("productCategory", productToUpdate.getProductCategory());
        mapdata.put("brand", productToUpdate.getBrand());
        mapdata.put("price", productToUpdate.getPrice());
        mapdata.put("stock", productToUpdate.getStock());
        mapdata.put("condition", productToUpdate.getCondition());
        mapdata.put("wholeSeller", productToUpdate.getWholeSeller());
        mapdata.put("imageUrls", productToUpdate.getImageUrls());
        mapdata.put("businessOwnerId", productToUpdate.getBusinessOwnerId());
        mapdata.put("productReference", productToUpdate.getProductReference());
        mapdata.put("tags", productToUpdate.getTags());

        db.collection("products").document(productToUpdate.getProductReference()).update(mapdata)
                .addOnSuccessListener(command -> {
                    productMutableLiveData.setValue(new Result.Success<>(productToUpdate));
                }).addOnFailureListener(command -> {
            new Result.Error(command);
        });
        return productMutableLiveData;
    }

    public static MutableLiveData<Boolean> deleteProduct(Product productToUpdate) {
//        City city = new City("Los Angeles", "CA", "USA",
//                false, 5000000L, Arrays.asList("west_coast", "sorcal"));
        final MutableLiveData<Boolean> retVal = new MutableLiveData<Boolean>();
        db.collection("products").document(productToUpdate.getProductReference())
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
}
