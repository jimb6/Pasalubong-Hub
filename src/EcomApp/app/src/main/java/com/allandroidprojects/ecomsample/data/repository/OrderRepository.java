package com.allandroidprojects.ecomsample.data.repository;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.allandroidprojects.ecomsample.data.models.ProductOrder;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

public class OrderRepository {

    // Access a Cloud Firestore instance from your Activity
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final CollectionReference ref = db.collection("products");
    private static StorageReference storage = FirebaseStorage.getInstance().getReference();
    private static volatile OrderRepository instance;
    private Context context;

    public static OrderRepository getInstance(Context ctx) {
        if (instance == null) {
            instance = new OrderRepository(ctx);
        }
        return instance;
    }

    public OrderRepository(Context ctx){
        this.context = ctx;
    }

    public MutableLiveData<Result<ProductOrder>> store(ProductOrder order) {
//        Check if order quantity is valid
        final  MutableLiveData<Result<ProductOrder>> resultMutableLiveData = new  MutableLiveData<Result<ProductOrder>>();
        ref.document(order.getProduct().getProductReference()).get().addOnCompleteListener( (task) -> {
            if (task.isSuccessful()){
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot.exists()){
                    Map<String, Object> data = snapshot.getData();
                    int quantity = 0;
                    if (data.containsKey("stock")){
                        quantity = Integer.parseInt(String.valueOf(data.get("stock")));
                        if (quantity > order.getQuantity()){
                            // Get a new write batch
                            WriteBatch batch = db.batch();

                            //Add New Order
                            DocumentReference nycRef = ref.document(order.getProduct().getProductReference())
                                    .collection("orders").document();
                            batch.set(nycRef, order);

                            // Update the population of 'SF'
                            DocumentReference sfRef = ref.document(order.getProduct().getProductReference());
                            batch.update(sfRef, "stock", quantity - order.getQuantity());

                            // Commit the batch
                            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        resultMutableLiveData.setValue(new Result.Success<>(order));
                                        Toast.makeText(context, "Your order has been added!", Toast.LENGTH_SHORT).show();
                                    }else{
                                        resultMutableLiveData.setValue(new Result.Error(task.getException()));
                                        Toast.makeText(context, "Unable to process your order.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{
                            resultMutableLiveData.setValue(new Result.Error(new Exception("Quantity Error")));
                            Toast.makeText(context, "Unable to process your order.", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        resultMutableLiveData.setValue(new Result.Error(new Exception("Out of stock.")));
                        Toast.makeText(context, "Unable to process your order. Out of stock.", Toast.LENGTH_SHORT).show();
                    }
                }

            }else{
                resultMutableLiveData.setValue(new Result.Error(new Exception("Unable to process your order.")));
                Toast.makeText(context, "Unable to process your order.", Toast.LENGTH_SHORT).show();
            }

        });
        return resultMutableLiveData;
    }

    public MutableLiveData<Result<ProductOrder>> getMerchantOrderByCategory(String category) {
        final MutableLiveData<Result<ProductOrder>> resultMutableLiveData = new MutableLiveData<Result<ProductOrder>>();
        return null;
    }
}
