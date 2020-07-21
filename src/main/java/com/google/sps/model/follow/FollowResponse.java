package com.google.sps.model.follow;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FollowResponse {
    @JsonProperty
    private boolean success;

    @JsonProperty
    private FollowItem entity;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public FollowItem getEntity() {
        return entity;
    }

    public void setEntity(FollowItem entity) {
        this.entity = entity;
    }
}