package com.allandroidprojects.ecomsample.mvvm.repository;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.allandroidprojects.ecomsample.model.product.Product;
import com.allandroidprojects.ecomsample.startup.data.Result;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class AddProductRepository {

    public static volatile AddProductRepository instance;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static StorageReference storage = FirebaseStorage.getInstance().getReference();

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

    public static MutableLiveData<Result<Uri>> saveNewProductImages(String ownerId, ArrayList<Uri> arrayList) {
        final MutableLiveData<Result<Uri>> productImnages = new MutableLiveData<>();
//        Create Folder
        for (Uri uri : arrayList) {
            final StorageReference ref = storage.child("products/" + ownerId + uri.getLastPathSegment());
//            ref.putFile(uri);
            UploadTask uploadTask = ref.putFile(uri);
//            final StorageReference ref = storage.child(uri.getLastPathSegment());
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
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

//    @RequiresApi(api = Build.VERSION_CODES.N)
//    private MutableLiveData<ArrayList<Result>> saveProductImages(Uri uri){
//        final MutableLiveData
//            UploadTask uploadTask = storage.child("images/mountains.jpg").putFile(uri);
//            uploadTask.addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Handle unsuccessful uploads
//                }
//            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
//                    // ...
//                }
//            });
//        }
////        Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
////        StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
////        uploadTask = riversRef.putFile(file);
//
//        // Register observers to listen for when the download is done or if it fails
//
//    }
}
