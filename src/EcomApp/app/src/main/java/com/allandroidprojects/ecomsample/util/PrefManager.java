package com.allandroidprojects.ecomsample.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.allandroidprojects.ecomsample.data.models.SearchData;

/**
 * Created by Lincoln on 05/05/16.
 */
public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences constants
    private static final String PREF_NAME = "MyPreference";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    private static final String PRICE_FROM = "PRICE_FROM";
    private static final String PRICE_TO = "PRICE_TO";


    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public void setSearchData(SearchData searchData){
        editor.putFloat(PRICE_TO, (float) searchData.priceTo);
        editor.putFloat(PRICE_FROM, (float) searchData.priceFrom);
        editor.commit();
    }


    public SearchData getSearhData(){
        SearchData data = new SearchData();
        data.priceFrom = pref.getFloat(PRICE_FROM, 0);
        data.priceTo = pref.getFloat(PRICE_TO, 1000000);
        return data;
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

}
