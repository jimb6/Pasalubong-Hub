package com.allandroidprojects.ecomsample.data.remote.product;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.allandroidprojects.ecomsample.data.models.Product;
import com.allandroidprojects.ecomsample.data.models.Rating;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.google.android.gms.tasks.OnCompleteListener;
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

    public static MutableLiveData<Result<Product>> getAllProducts(String category) {
        final MutableLiveData<Result<Product>> productListMutableLiveData = new MutableLiveData<>();
        db.collection("products")
                .whereEqualTo("productCategory", category)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
//                            ArrayList<Product> products = new ArrayList<>();
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Map<String, Object> productData = document.getData();
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
                            product.setTotalSales(Integer.parseInt(String.valueOf(productData.get("totalSales"))));

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
                        productListMutableLiveData.setValue(new Result.Error(new Exception("No products remaining.")));
                    } else {
                        productListMutableLiveData.setValue(new Result.Error(task.getException()));
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
