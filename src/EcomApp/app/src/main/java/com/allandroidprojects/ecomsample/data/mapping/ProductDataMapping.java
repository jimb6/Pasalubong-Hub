package com.allandroidprojects.ecomsample.data.mapping;

import com.allandroidprojects.ecomsample.data.models.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductDataMapping {

    private Map<String, Object> productMap;
    private Product product;

    public final String PRODUCT_NAME = "productname";
    public final String PRODUCT_DESCRIPTION = "productDescription";
    public final String PRODUCT_CATEGORY = "productCategory";
    public final String PRODUCT_PRICE = "price";
    public final String PRODUCT_BRAND = "brand";
    public final String PRODUCT_CONDITION = "condition";
    public final String PRODUCT_WHOLE_SELLER = "wholeSeller";
    public final String PRODUCT_VARIATION = "variation";
    public final String PRODUCT_TOTAL_SALES = "totalSales";
    public final String PRODUCT_TAGS = "tags";
    public final String PRODUCT_STOCK = "stock";
    public final String PRODUCT_REFERENCE = "productReference";
    public final String PRODUCT_IMAGE_URL = "imageUrls";
    public final String PRODUCT_OWNER_ID = "businessOwnerId";

    public final String DEFAULT_NAME = "NaN";
    public final String DEFAULT_DESCRIPTION = "NaN";
    public final String DEFAULT_CATEGORY = "NaN";
    public final String DEFAULT_PRICE = "NaN";
    public final String DEFAULT_BRAND = "NaN";
    public final String DEFAULT_CONDITION = "NaN";
    public final String DEFAULT_WHOLE_SELLER = "NaN";
    public final String DEFAULT_VARIATION = "NaN";
    public final int DEFAULT_TOTAL_SALES = 0;
    public final String DEFAULT_TAGS = "NaN";
    public final int DEFAULT_STOCK = 0;
    public final String DEFAULT_REFERENCE = "NaN";
    public final String DEFAULT_IMAGE_URL = "NaN";
    public final String DEFAULT_OWNER_ID = "NaN";


    public ProductDataMapping(Map<String, Object> productMap)
    {
        this.product = new Product();
        this.productMap = productMap;
        this.bindData();
    }

    public ProductDataMapping(Product product)
    {
        this.product = product;
        this.productMap = new HashMap<>();
        this.bindToMap();
    }

    public void bindData(){
        if (productMap.containsKey(PRODUCT_NAME))
            product.setProductname((String) productMap.get(PRODUCT_NAME));
        else product.setProductname(DEFAULT_NAME);

        if (productMap.containsKey(PRODUCT_BRAND))
            product.setBrand((String) productMap.get(PRODUCT_BRAND));
        else product.setBrand(DEFAULT_BRAND);

        if (productMap.containsKey(PRODUCT_CATEGORY))
            product.setProductCategory((String) productMap.get(PRODUCT_CATEGORY));
        else product.setProductCategory(DEFAULT_CATEGORY);

        if (productMap.containsKey(PRODUCT_CONDITION))
            product.setCondition((String) productMap.get(PRODUCT_CONDITION));
        else product.setCondition(DEFAULT_CONDITION);

        if (productMap.containsKey(PRODUCT_IMAGE_URL))
            product.setImageUrls((ArrayList<String>) productMap.get(PRODUCT_IMAGE_URL));
        else product.setImageUrls(new ArrayList<String>());

        if (productMap.containsKey(PRODUCT_DESCRIPTION))
            product.setProductDescription((String) productMap.get(PRODUCT_DESCRIPTION));
        else product.setProductDescription(DEFAULT_DESCRIPTION);

        if (productMap.containsKey(PRODUCT_OWNER_ID))
            product.setBusinessOwnerId((String) productMap.get(PRODUCT_OWNER_ID));
        else product.setBusinessOwnerId(DEFAULT_OWNER_ID);

        if (productMap.containsKey(PRODUCT_PRICE))
            product.setPrice(Double.parseDouble(String.valueOf(productMap.get(PRODUCT_PRICE))));
        else product.setPrice(0d);

        if (productMap.containsKey(PRODUCT_REFERENCE))
            product.setProductReference((String) productMap.get(PRODUCT_REFERENCE));
        else product.setProductReference(DEFAULT_REFERENCE);

        if (productMap.containsKey(PRODUCT_STOCK))
            product.setStock(Integer.parseInt(String.valueOf(productMap.get(PRODUCT_STOCK))));
        else product.setStock(DEFAULT_STOCK);

        if (productMap.containsKey(PRODUCT_TAGS))
            product.setTags((ArrayList<String>) productMap.get(PRODUCT_TAGS));
        else product.setTags(new ArrayList<>());

        if (productMap.containsKey(PRODUCT_TOTAL_SALES))
            product.setTotalSales(Integer.parseInt(String.valueOf(productMap.get(PRODUCT_TOTAL_SALES))));
        else product.setTotalSales(DEFAULT_TOTAL_SALES);

        if (productMap.containsKey(PRODUCT_WHOLE_SELLER))
            product.setWholeSeller((String) productMap.get(PRODUCT_WHOLE_SELLER));
        else product.setWholeSeller(DEFAULT_WHOLE_SELLER);

        if (productMap.containsKey(PRODUCT_VARIATION))
            product.setVariation((Map<String, Object>) productMap.get(PRODUCT_VARIATION));
        else product.setVariation(new HashMap<String, Object>());


    }

    public void bindToMap(){
        productMap.put(PRODUCT_NAME, product.getProductname());
        productMap.put(PRODUCT_VARIATION, product.getVariation());
        productMap.put(PRODUCT_WHOLE_SELLER, product.getWholeSeller());
        productMap.put(PRODUCT_TOTAL_SALES, product.getTotalSales());
        productMap.put(PRODUCT_TAGS, product.getTags());
        productMap.put(PRODUCT_STOCK, product.getStock());
        productMap.put(PRODUCT_REFERENCE, product.getProductReference());
        productMap.put(PRODUCT_PRICE, product.getPrice());
        productMap.put(PRODUCT_OWNER_ID, product.getBusinessOwnerId());
        productMap.put(PRODUCT_DESCRIPTION, product.getProductDescription());
        productMap.put(PRODUCT_IMAGE_URL, product.getImageUrls());
        productMap.put(PRODUCT_CONDITION, product.getCondition());
        productMap.put(PRODUCT_CATEGORY, product.getProductCategory());
        productMap.put(PRODUCT_BRAND, product.getBrand());
    }

    public Product getData(){
        return this.product;
    }

    public Map<String, Object> getMapData(){
        return productMap;
    }
}
