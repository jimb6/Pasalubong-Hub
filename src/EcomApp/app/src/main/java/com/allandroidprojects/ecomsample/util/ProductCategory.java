package com.allandroidprojects.ecomsample.util;

public enum ProductCategory {
    GOODS("Goods"), SWEETS("Sweets"), CLOTHING("Clothing"), DECORATION("Decoration"), SOUVENIR("Souvenir");
    String value;
    ProductCategory(String str){
        this.value = str;
    }

    public String getValue(){
        return value;
    }
}
