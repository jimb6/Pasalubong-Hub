package com.allandroidprojects.ecomsample.data.remote.product;

import com.google.firebase.firestore.FirebaseFirestore;

public class ProductRepository {

    private static volatile ProductListRepository instance;
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    
}
