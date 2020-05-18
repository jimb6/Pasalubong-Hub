package com.allandroidprojects.ecomsample.data.models.product;

import android.os.Parcel;
import android.os.Parcelable;

import com.allandroidprojects.ecomsample.data.models.Business;

import java.util.ArrayList;
import java.util.Map;

public class Product implements Parcelable {

    private String productReference;
    private String productname;
    private String productDescription;
    private String productCategory;
    private String brand;
    private double price;
    private int stock;
    private String condition;
    private Map<String, Object> variation;
    private String wholeSeller;
    private ArrayList<String> imageUrls;
    private String businessOwnerId;
    private ArrayList<String> tags;

    public Product(Business business) {

    }

    public Product() {}


    protected Product(Parcel in) {
        productReference = in.readString();
        productname = in.readString();
        productDescription = in.readString();
        productCategory = in.readString();
        brand = in.readString();
        price = in.readDouble();
        stock = in.readInt();
        condition = in.readString();
        wholeSeller = in.readString();
        imageUrls = in.createStringArrayList();
        businessOwnerId = in.readString();
        tags = in.createStringArrayList();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public ArrayList<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getBusinessOwnerId() {
        return businessOwnerId;
    }

    public void setBusinessOwnerId(String businessOwnerId) {
        this.businessOwnerId = businessOwnerId;
    }

    public String getProductReference() {
        return productReference;
    }

    public void setProductReference(String productReference) {
        this.productReference = productReference;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Map<String, Object> getVariation() {
        return variation;
    }

    public void setVariation(Map<String, Object> variation) {
        this.variation = variation;
    }

    public String getWholeSeller() {
        return wholeSeller;
    }

    public void setWholeSeller(String wholeSeller) {
        this.wholeSeller = wholeSeller;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productReference);
        dest.writeString(productname);
        dest.writeString(productDescription);
        dest.writeString(productCategory);
        dest.writeString(brand);
        dest.writeDouble(price);
        dest.writeInt(stock);
        dest.writeString(condition);
        dest.writeString(wholeSeller);
        dest.writeStringList(imageUrls);
        dest.writeString(businessOwnerId);
        dest.writeStringList(tags);
    }
}
