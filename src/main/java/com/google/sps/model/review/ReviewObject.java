package com.google.sps.model.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.sps.model.activity.Activity;
import com.google.sps.model.user.UserObject;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Subclass;

import java.sql.Timestamp;

@Subclass(index=true, name="ReviewObject")
public class ReviewObject extends Activity {

    public ReviewObject() {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        this.timestamp = currentTime.getTime();
    }

    public ReviewObject(UserObject userObject,
                        String contentType, String contentId,
                        String contentTitle, String artUrl,
                        String reviewTitle, String reviewBody, int rating) {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        this.timestamp = currentTime.getTime();

        this.authorId = userObject.getId();
        this.authorName = userObject.getUsername();

        this.contentType = contentType;
        this.contentId = contentId;
        this.contentTitle = contentTitle;
        this.artUrl = artUrl;

        this.reviewTitle = reviewTitle;
        this.reviewBody = reviewBody;
        this.rating = rating;
    }

    @JsonProperty
    @Index
    private long timestamp;

    @JsonProperty
    @Index
    private String authorId;

    @JsonProperty
    @Index
    private String authorName;

    @JsonProperty
    @Index
    private String contentType;

    @JsonProperty
    @Index
    private String contentId;

    @JsonProperty
    @Index
    private String contentTitle;

    @JsonProperty
    @Index
    private String artUrl;

    @JsonProperty
    @Index
    private String reviewTitle;

    @JsonProperty
    @Index
    private String reviewBody;

    @JsonProperty
    @Index
    private int rating;
}