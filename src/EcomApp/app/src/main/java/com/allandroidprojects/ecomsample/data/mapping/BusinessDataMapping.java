package com.allandroidprojects.ecomsample.data.mapping;

import com.allandroidprojects.ecomsample.data.models.Business;

import java.util.ArrayList;
import java.util.Map;

public class BusinessDataMapping {

    private Map<String, Object> businessData;
    private Business business;

    public final String BUSINESS_NAME = "businessName";
    public final String BUSINESS_ADDRESS = "businessAddress";
    public final String BUSINESS_EMAIL = "businessEmail";
    public final String BUSINESS_PHOTOS = "businessPhotos";
    public final String BUSINESS_COVER_IMAGE = "coverImage";
    public final String BUSINESS_LAT = "lat";
    public final String BUSINESS_LNG = "lng";
    public final String BUSINESS_OWNER_ID = "ownerId";
    public final String DEFAULT_VALUE = "DATA NOT AVAILABLE";
    public final String DEFAULT_LAT_VALUE = "6.952187";
    public final String DEFAULT_LNG_VALUE = "126.217418";


    public BusinessDataMapping(Map<String, Object> businessData)
    {
        this.business = new Business();
        this.businessData = businessData;
    }

    public void bindData(){
        if (businessData.containsKey(BUSINESS_NAME))
            business.setBusinessName((String) businessData.get(BUSINESS_NAME));
        else
            business.setBusinessName(DEFAULT_VALUE);

        if (businessData.containsKey(BUSINESS_ADDRESS))
            business.setBusinessAddress((String) businessData.get(BUSINESS_ADDRESS));
        else business.setBusinessAddress(DEFAULT_VALUE);

        if (businessData.containsKey(BUSINESS_EMAIL))
            business.setBusinessEmail((String) businessData.get(BUSINESS_EMAIL));
        else business.setBusinessEmail(DEFAULT_VALUE);

        if (businessData.containsKey(BUSINESS_COVER_IMAGE))
            business.setCoverUri((String) businessData.get(BUSINESS_COVER_IMAGE));
        else business.setCoverUri(DEFAULT_VALUE);

        if (businessData.containsKey(BUSINESS_PHOTOS))
            business.setBusinessPhotos((ArrayList<String>) businessData.get(BUSINESS_PHOTOS));
        else business.setBusinessPhotos(new ArrayList<String>());

        if (businessData.containsKey(BUSINESS_LAT))
            business.setLat((String) businessData.get(BUSINESS_LAT));
        else business.setLat(DEFAULT_LAT_VALUE);

        if (businessData.containsKey(BUSINESS_LNG))
            business.setLng((String) businessData.get(BUSINESS_LNG));
        else business.setLng(DEFAULT_LNG_VALUE);

        if (businessData.containsKey(BUSINESS_OWNER_ID))
            business.setOwnerId((String) businessData.get(BUSINESS_OWNER_ID));
        else business.setOwnerId(DEFAULT_VALUE);

    }

    public Business getData(){
        return this.business;
    }


}
