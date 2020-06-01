package com.allandroidprojects.ecomsample.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.allandroidprojects.ecomsample.util.ProductOrderStatus;

public class ProductOrder implements Parcelable {

    private String id;
    private Product product;
    private String user_reference;
    private String seller_reference;
    private String date_ordered;
    private int quantity;
    private ProductOrderStatus status;

    public ProductOrder(){}


    protected ProductOrder(Parcel in) {
        id = in.readString();
        product = in.readParcelable(Product.class.getClassLoader());
        user_reference = in.readString();
        seller_reference = in.readString();
        date_ordered = in.readString();
        quantity = in.readInt();
    }

    public static final Creator<ProductOrder> CREATOR = new Creator<ProductOrder>() {
        @Override
        public ProductOrder createFromParcel(Parcel in) {
            return new ProductOrder(in);
        }

        @Override
        public ProductOrder[] newArray(int size) {
            return new ProductOrder[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getUser_reference() {
        return user_reference;
    }

    public void setUser_reference(String user_reference) {
        this.user_reference = user_reference;
    }

    public String getSeller_reference() {
        return seller_reference;
    }

    public void setSeller_reference(String seller_reference) {
        this.seller_reference = seller_reference;
    }

    public String getDate_ordered() {
        return date_ordered;
    }

    public void setDate_ordered(String date_ordered) {
        this.date_ordered = date_ordered;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ProductOrderStatus getStatus() {
        return status;
    }

    public void setStatus(ProductOrderStatus status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable(product, flags);
        dest.writeString(user_reference);
        dest.writeString(seller_reference);
        dest.writeString(date_ordered);
        dest.writeInt(quantity);
    }
}
