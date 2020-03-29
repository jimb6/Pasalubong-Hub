package com.allandroidprojects.ecomsample.mvvm.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.allandroidprojects.ecomsample.model.product.Product;
import com.allandroidprojects.ecomsample.startup.data.Result;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class ProductListRepository {

    private static volatile ProductListRepository instance;
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static ProductListRepository getInstance() {
        if (instance == null) {
            instance = new ProductListRepository();
        }
        return instance;
    }

    public static MutableLiveData<Result<Product>> getAllProducts() {
        final MutableLiveData<Result<Product>> productListMutableLiveData = new MutableLiveData<>();
        db.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
//                            ArrayList<Product> products = new ArrayList<>();
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Map<String, Object> productData = document.getData();
                                Product product = new Product();
                                product.setProductname((String) productData.get("productname"));
                                product.setProductDescription((String) productData.get("productDescription"));
                                product.setProductCategory((String) productData.get("productCategory"));
                                product.setBrand((String) productData.get("brand"));
                                product.setPrice(Double.parseDouble(productData.get("price").toString()));
                                product.setStock(Integer.parseInt(productData.get("stock").toString()));
                                product.setWholeSeller((String) productData.get("wholeSeller"));
                                product.setImageUrls((ArrayList<String>) productData.get("imageUrls"));
//                                product.setVariation((Map<String, Object>) productData.get("variation"));
//                                DocumentReference rating  = (DocumentReference) productData.get("ratings");
//                                Map<String, Object> rate = (Map<String, Object>) rating.get().getResult();

                                productListMutableLiveData.setValue(new Result.Success<Product>(product));
                            }
                        } else {
                            productListMutableLiveData.setValue(new Result.Error(task.getException()));
                        }
                    }
                });
        return productListMutableLiveData;
    }

    public static MutableLiveData<Result<Product>> getAllProductsInCart(String userId) {
        final MutableLiveData<Result<Product>> productMutableLiveData = new MutableLiveData<>();
        db.collection("cart").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
//                            ArrayList<Product> products = new ArrayList<>();
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Map<String, Object> productData = document.getData();
                                Product product = new Product();
                                product.setProductname((String) productData.get("productname"));
                                product.setProductDescription((String) productData.get("productDescription"));
                                product.setProductCategory((String) productData.get("productCategory"));
                                product.setBrand((String) productData.get("brand"));
                                product.setPrice(Double.parseDouble(productData.get("price").toString()));
                                product.setStock(Integer.parseInt(productData.get("stock").toString()));
                                product.setWholeSeller((String) productData.get("wholeSeller"));
                                product.setImageUrls((ArrayList<String>) productData.get("imageUrls"));
//                                product.setVariation((Map<String, Object>) productData.get("variation"));
//                                DocumentReference rating  = (DocumentReference) productData.get("ratings");
//                                Map<String, Object> rate = (Map<String, Object>) rating.get().getResult();

                                productMutableLiveData.setValue(new Result.Success<Product>(product));
                            }
                        } else {
                            productMutableLiveData.setValue(new Result.Error(task.getException()));
                        }
                    }
                });
        return productMutableLiveData;
    }

    public static MutableLiveData<Result<Product>> saveProductToCart(Product product) {
        final MutableLiveData<Result<Product>> cartMutableLiveData = new MutableLiveData<>();
        db.collection("cart").document().set(product)
                .addOnSuccessListener(command -> {
                    cartMutableLiveData.setValue(new Result.Success<>(product));
                }).addOnFailureListener(command -> {
            new Result.Error(command);
        });
        return cartMutableLiveData;
    }
}
