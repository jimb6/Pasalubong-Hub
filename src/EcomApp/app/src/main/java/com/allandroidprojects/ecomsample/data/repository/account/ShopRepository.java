package com.allandroidprojects.ecomsample.data.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.allandroidprojects.ecomsample.data.models.Business;
import com.allandroidprojects.ecomsample.data.models.LoggedInUser;
import com.allandroidprojects.ecomsample.data.models.SearchData;
import com.allandroidprojects.ecomsample.data.models.product.Product;
import com.allandroidprojects.ecomsample.data.models.DataResult;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

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

    public static MutableLiveData<Result<Product>> findMyProducts(Business business) {
        final MutableLiveData<Result<Product>> productListMutableLiveData = new MutableLiveData<>();
        db.collection("products")
                .whereEqualTo("businessOwnerId", business.getUserId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
//                            ArrayList<Product> products = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> productData = document.getData();
                                Product product = new Product(business);
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

    public static MutableLiveData<Result<Business>> getMyBusiness(LoggedInUser user) {
        final MutableLiveData<Result<Business>> businessMutableLiveData = new MutableLiveData<>();
        db.collection("BUSINESS").document(user.getUserId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Map<String, Object> mapData = documentSnapshot.getData();
                String name = (String) mapData.get("businessName");
                String address = (String) mapData.get("businessAddress");
                Business business = new Business(user, name, address);
                businessMutableLiveData.setValue(new Result.Success<>(business));
            }
        });
        return businessMutableLiveData;
    }

    public static MutableLiveData<Result<Product>> searchProducts(SearchData searchData) {
        final MutableLiveData<Result<Product>> productMutableLiveData = new MutableLiveData<>();


        db.collection("products")
                .orderBy("productname").startAt(searchData.query).endAt(searchData.query + "\uf8ff")
                .whereGreaterThanOrEqualTo("price", searchData.priceFrom > 0 ? searchData.priceFrom: 0d)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
//                            ArrayList<Product> products = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> productData = document.getData();

                                if (Double.parseDouble(productData.get("price").toString()) > searchData.priceTo){
                                    break;
                                }

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
}
