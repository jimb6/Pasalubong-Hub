package com.allandroidprojects.ecomsample.util;

public enum RatingType {
    FIVE(5), FOUR(4), THRE(3), TWO(2), ONE(1);
    int value;
    RatingType(int i) {
        this.value = i;
    }

    public int getRatingValue(){
        return value;
    }
}
