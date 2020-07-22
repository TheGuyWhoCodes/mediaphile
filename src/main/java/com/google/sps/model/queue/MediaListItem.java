package com.google.sps.model.queue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.sql.Timestamp;

/**
 * Base class used as a schema for our database. This represents a
 * queue object or already watched object. We abstract these later as
 * to end up in different tables in the db.
 */
@Entity
public class MediaListItem {

    public static final String TYPE_VIEWED = "viewed";
    public static final String TYPE_QUEUE = "queue";

    public MediaListItem() {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        timestamp = currentTime.getTime();
    }

    @JsonProperty
    @Index
    private String title;

    @JsonProperty
    @Index
    private String mediaType;

    @JsonProperty
    @Index
    private String mediaId;

    @JsonProperty
    @Index
    @Id
    private Long id;

    @JsonProperty
    @Index
    private String listType;

    @JsonProperty
    @Index
    private String artUrl;

    @JsonProperty
    @Index
    private long timestamp;

    @JsonProperty
    @Index
    private String userId;

    @JsonProperty
    @Index
    private String username;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getArtUrl() {
        return artUrl;
    }

    public void setArtUrl(String artUrl) {
        this.artUrl = artUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getListType() {
        return listType;
    }

    public void setListType(String listType) {
        this.listType = listType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}