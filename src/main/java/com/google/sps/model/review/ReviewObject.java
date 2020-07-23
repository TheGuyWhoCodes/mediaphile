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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public String getArtUrl() {
        return artUrl;
    }

    public void setArtUrl(String artUrl) {
        this.artUrl = artUrl;
    }

    public String getReviewTitle() {
        return reviewTitle;
    }

    public void setReviewTitle(String reviewTitle) {
        this.reviewTitle = reviewTitle;
    }

    public String getReviewBody() {
        return reviewBody;
    }

    public void setReviewBody(String reviewBody) {
        this.reviewBody = reviewBody;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}