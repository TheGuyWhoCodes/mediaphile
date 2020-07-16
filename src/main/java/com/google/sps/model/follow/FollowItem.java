package com.google.sps.model.follow;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.sql.Timestamp;

@Entity
public class FollowItem {

    public static final String TYPE_FOLLOWERS = "followers";
    public static final String TYPE_FOLLOWING = "following";

    public FollowItem(String followingId, String followerId) {
        this.followingId = followingId;
        this.followerId = followerId;
    }

    @JsonProperty
    @Index
    private String followingId;

    @JsonProperty
    @Index
    private String followerId;
}