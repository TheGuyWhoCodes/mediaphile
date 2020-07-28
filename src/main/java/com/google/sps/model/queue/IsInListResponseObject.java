package com.google.sps.model.queue;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IsInListResponseObject {

    @JsonProperty
    private boolean isInQueue;

    @JsonProperty
    private boolean isInViewed;

    public boolean isInQueue() {
        return isInQueue;
    }

    public void setInQueue(boolean inQueue) {
        isInQueue = inQueue;
    }

    public boolean isInViewed() {
        return isInViewed;
    }

    public void setInViewed(boolean inViewed) {
        isInViewed = inViewed;
    }
}
