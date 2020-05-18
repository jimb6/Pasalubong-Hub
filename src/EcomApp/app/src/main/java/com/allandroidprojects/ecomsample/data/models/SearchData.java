package com.allandroidprojects.ecomsample.data.models;

import java.util.ArrayList;

public class SearchData {

    public String query;
    public double priceFrom;
    public double priceTo;
    public ArrayList<String> ratings;
    public ArrayList<String> locations;

    public SearchData() {
        this.query = "";
        this.priceFrom = 0d;
        this.priceTo = 999999999;
        this.ratings = new ArrayList<>();
        this.locations = new ArrayList<>();
    }

}
