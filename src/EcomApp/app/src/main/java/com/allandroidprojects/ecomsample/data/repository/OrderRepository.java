package com.allandroidprojects.ecomsample.data.repository;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.allandroidprojects.ecomsample.data.mapping.ProductDataMapping;
import com.allandroidprojects.ecomsample.data.models.ProductOrder;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    public OrderRepository(Context ctx) {
        this.context = ctx;
    }

    public MutableLiveData<Result<ProductOrder>> store(ProductOrder order) {
//        Check if order quantity is valid
        final MutableLiveData<Result<ProductOrder>> resultMutableLiveData = new MutableLiveData<Result<ProductOrder>>();
        ref.document(order.getProduct().getProductReference()).get().addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    Map<String, Object> data = snapshot.getData();
                    int quantity = 0;
                    if (data.containsKey("stock")) {
                        quantity = Integer.parseInt(String.valueOf(data.get("stock")));
                        if (quantity > order.getQuantity()) {
                            // Get a new write batch
                            WriteBatch batch = db.batch();

                            //Add New Order
                            DocumentReference nycRef = ref.document(order.getProduct().getProductReference())
                                    .collection("orders").document();
                            batch.set(nycRef, order);

                            DocumentReference id = ref.document(order.getProduct().getProductReference())
                                    .collection("orders").document(nycRef.getId());
                            batch.update(id, "id", nycRef.getId());

                            // Update the population of 'SF'
                            DocumentReference sfRef = ref.document(order.getProduct().getProductReference());
                            batch.update(sfRef, "stock", quantity - order.getQuantity());

                            // Commit the batch
                            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        resultMutableLiveData.setValue(new Result.Success<>(order));
                                        Toast.makeText(context, "Your order has been added!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        resultMutableLiveData.setValue(new Result.Error(task.getException()));
                                        Toast.makeText(context, "Unable to process your order.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            resultMutableLiveData.setValue(new Result.Error(new Exception("Quantity Error")));
                            Toast.makeText(context, "Unable to process your order.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        resultMutableLiveData.setValue(new Result.Error(new Exception("Out of stock.")));
                        Toast.makeText(context, "Unable to process your order. Out of stock.", Toast.LENGTH_SHORT).show();
                    }
                }

            } else {
                resultMutableLiveData.setValue(new Result.Error(new Exception("Unable to process your order.")));
                Toast.makeText(context, "Unable to process your order.", Toast.LENGTH_SHORT).show();
            }

        });
        return resultMutableLiveData;
    }

    public MutableLiveData<Result<ProductOrder>> getMerchantOrderByCategory(String merchantId, String category) {
        final MutableLiveData<Result<ProductOrder>> resultMutableLiveData = new MutableLiveData<Result<ProductOrder>>();
        db.collectionGroup("orders").get().addOnCompleteListener(tasks -> {
            if (tasks.isSuccessful()) {
                QuerySnapshot snapshots = tasks.getResult();
                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(tasks.getResult())) {
                    Map<String, Object> data = snapshot.getData();
                    String status = String.valueOf(data.get("status"));
                    String sellerID = String.valueOf(data.get("seller_reference"));
                    if (status.equals(category) && sellerID.equals(merchantId)) {
                        String date_ordered = String.valueOf(data.get("date_ordered"));
                        int quantity = Integer.parseInt(String.valueOf(data.get("quantity")));
                        String seller_reference = String.valueOf(data.get("seller_reference"));
                        String user_reference = String.valueOf(data.get("user_reference"));
                        String customerEmail = String.valueOf(data.get("customerEmail"));
                        ProductDataMapping productDataMapping = new ProductDataMapping((HashMap<String, Object>) data.get("product"));

                        ProductOrder order = new ProductOrder();
                        order.setId(snapshot.getString("id"));
                        order.setUser_reference(user_reference);
                        order.setDate_ordered(date_ordered);
                        order.setSeller_reference(seller_reference);
                        order.setQuantity(quantity);
                        order.setStatus(status.toUpperCase());
                        order.setProduct(productDataMapping.getData());
                        order.setCustomerEmail(customerEmail);

                        resultMutableLiveData.setValue(new Result.Success<>(order));
                    }
                }
                resultMutableLiveData.setValue(new Result.Error(new Exception("No more data.")));
            }
        });
        return resultMutableLiveData;
    }

    public MutableLiveData<Result<ProductOrder>> updateOrderedProduct(String reference, String status) {
        final MutableLiveData<Result<ProductOrder>> resultMutableLiveData = new MutableLiveData<>();
        db.collectionGroup("orders").get().addOnCompleteListener(tasks -> {
            if (tasks.isSuccessful()) {
                QuerySnapshot snapshots = tasks.getResult();
                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(tasks.getResult())) {
                    String id = snapshot.getString("id");
                    Map<String, Object> data = snapshot.getData();
                    if (id.equals(reference)) {
                        snapshot.getReference().update("status", status);
                        ProductOrder order = new ProductOrder();
                        order.setCustomerEmail(snapshot.getString("customerEmail"));
                        order.setDate_ordered(snapshot.getString("customerEmail"));
                        order.setQuantity(Integer.parseInt(String.valueOf(snapshot.get("quantity"))));
                        order.setStatus(snapshot.getString("status"));
                        order.setUser_reference(snapshot.getString("user_reference"));
                        order.setSeller_reference(snapshot.getString("seller_reference"));
                        ProductDataMapping productDataMapping = new ProductDataMapping((Map<String, Object>) snapshot.get("product"));
                        order.setProduct(productDataMapping.getData());
                        resultMutableLiveData.setValue(new Result.Success<>(order));
                    }
                }
            }else{
                resultMutableLiveData.setValue(new Result.Error(tasks.getException()));
            }
        });
        return resultMutableLiveData;
    }
}
