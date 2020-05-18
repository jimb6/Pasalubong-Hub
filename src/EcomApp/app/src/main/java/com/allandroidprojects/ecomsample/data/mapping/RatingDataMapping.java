package com.allandroidprojects.ecomsample.data.mapping;

import com.allandroidprojects.ecomsample.data.models.Rating;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RatingDataMapping {

    private Map<String, Object> ratingMap;
    private Rating rating;

    private final String RATING_USER_ID = "userId";
    private final String RATING_COMMENT = "comment";
    private final String RATING_RATE = "rate";
    private final String RATING_USER_NAME = "userName";
    private final String RATING_DATE = "date";
    private final String RATING_IMAGE_URI = "imagesUrl";
    private final String RATING_USER_IMAGE = "userImage";

    private final String DEFAULT_USER_ID = "NaN";
    private final String DEFAULT_COMMENT = "NaN";
    private final double DEFAULT_RATE = 0d;
    private final String DEFAULT_USER_NAME = "NaN";
    private final String DEFAULT_DATE = "NaN";
    private final ArrayList<String> DEFAULT_IMAGE_URI = new ArrayList<>();
    private final String DEFAULT_USER_IMAGE = "https://firebasestorage.googleapis.com/v0/b/pasalubong-hub-66cfd.appspot.com/o/products%2F1200px-No_image_available.svg.png?alt=media&token=d9a90048-5253-4f8f-a170-32799664aee7";

    public RatingDataMapping(Rating rating){
        this.rating = rating;
        this.ratingMap = new HashMap<>();
        bindMapData();
    }

    public RatingDataMapping(Map<String, Object> ratingMap){
        this.ratingMap = ratingMap;
        this.rating = new Rating();
        bindData();
    }

    public void bindData(){
        if (ratingMap.containsKey(RATING_COMMENT))
            rating.setComment((String) ratingMap.get(RATING_COMMENT));
        else rating.setComment(DEFAULT_COMMENT);

        if (ratingMap.containsKey(RATING_DATE))
            rating.setComment(String.valueOf(ratingMap.get(RATING_DATE)));
        else rating.setComment(DEFAULT_DATE);

        if (ratingMap.containsKey(RATING_IMAGE_URI))
            rating.setUrls((ArrayList<String>) ratingMap.get(RATING_IMAGE_URI));
        else rating.setUrls(DEFAULT_IMAGE_URI);

        if (ratingMap.containsKey(RATING_RATE))
            rating.setRating(Double.parseDouble(String.valueOf(ratingMap.get(RATING_RATE))));
        else rating.setRating(DEFAULT_RATE);

        if (ratingMap.containsKey(RATING_USER_ID))
            rating.setAuthorId((String) ratingMap.get(RATING_USER_ID));
        else rating.setAuthorId(DEFAULT_USER_ID);

        if (ratingMap.containsKey(RATING_USER_IMAGE))
            rating.setUserImage((String) ratingMap.get(RATING_USER_IMAGE));
        else rating.setUserImage(DEFAULT_USER_IMAGE);

        if (ratingMap.containsKey(RATING_USER_NAME))
            rating.setComment((String) ratingMap.get(RATING_USER_NAME));
        else rating.setComment(DEFAULT_USER_NAME);
    }

    private void bindMapData(){
        ratingMap.put(RATING_USER_NAME, rating.getAuthornName());
        ratingMap.put(RATING_USER_IMAGE, rating.getUserImage());
        ratingMap.put(RATING_USER_ID, rating.getAuthorId());
        ratingMap.put(RATING_RATE, rating.getRating());
        ratingMap.put(RATING_IMAGE_URI, rating.getUrls());
        ratingMap.put(RATING_DATE, rating.getDate());
        ratingMap.put(RATING_COMMENT, rating.getComment());
    }

    public Map<String, Object> getMapedData(){
        return this.ratingMap;
    }

    public Rating getData(){
        return this.rating;
    }
}
