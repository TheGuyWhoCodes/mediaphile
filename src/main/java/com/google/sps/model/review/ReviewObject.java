package com.google.sps.model.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.services.books.model.Volume;
import com.google.sps.servlets.book.BookDetailsServlet;
import com.google.sps.servlets.movie.MovieDetailsServlet;
import com.google.sps.model.user.UserObject;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import info.movito.themoviedbapi.model.MovieDb;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;

import static com.google.sps.util.Utils.parseInt;

@Entity
public class ReviewObject {

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

    @Id
    @JsonProperty
    @Index
    private Long id;

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