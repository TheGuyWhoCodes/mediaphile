package com.google.sps.model.queue;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used to represent a response type when we GET /list/entity
 * This allows us to do a clean implementation with a strict
 * response type. It returns a success and an entity IFF
 * we found one, if not, it'll return null.
 */
public class MediaListResponse {
    @JsonProperty
    private boolean success;

    @JsonProperty
    private MediaListItem entity;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public MediaListItem getEntity() {
        return entity;
    }

    public void setEntity(MediaListItem entity) {
        this.entity = entity;
    }
}