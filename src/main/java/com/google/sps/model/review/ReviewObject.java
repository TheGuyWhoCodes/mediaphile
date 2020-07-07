package com.google.sps.model.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.sql.Timestamp;

@Entity
public class ReviewObject {

    public ReviewObject() {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        this.timestamp = currentTime.getTime();
    }

    @Id
    @JsonProperty
    @Index
    private Long id;

    @JsonProperty
    @Index
    private long timestamp;

    @JsonProperty
    @Index
    private int rating;

    @JsonProperty
    @Index
    private String authorName;

    @JsonProperty
    @Index
    private String authorId;

    @JsonProperty
    @Index
    private String reviewTitle;

    @JsonProperty
    @Index
    private String reviewBody;

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
}