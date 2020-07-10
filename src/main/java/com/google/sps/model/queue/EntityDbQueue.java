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
public class EntityDbQueue {

    public EntityDbQueue() {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        timeStamp = currentTime.getTime();
    }

    @JsonProperty
    @Index
    private String title;

    @JsonProperty
    @Index
    private String type;

    @JsonProperty
    @Index
    private String entityId;

    @JsonProperty
    @Index
    private String entityType;

    @JsonProperty
    @Index
    private String artUrl;

    @JsonProperty
    @Index
    private long timeStamp;

    @JsonProperty
    @Index
    private String userID;

    @Id
    @Index
    private Long id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArtUrl() {
        return artUrl;
    }

    public void setArtUrl(String artUrl) {
        this.artUrl = artUrl;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
}