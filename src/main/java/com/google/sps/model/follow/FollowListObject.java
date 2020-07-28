package com.google.sps.model.follow;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.google.sps.model.user.UserObject;

import java.util.List;

@Entity
public class FollowListObject {

    public FollowListObject(List<UserObject> followersList,
                                List<UserObject> followingList,
                                int followerLength, int followingLength) {
        this.followersList = followersList;
        this.followingList = followingList;
        this.followerLength = followerLength;
        this.followingLength = followingLength;
    }

    @JsonProperty
    @Index
    private List<UserObject> followersList;

    @JsonProperty
    @Index
    private List<UserObject> followingList;

    @JsonProperty
    @Index
    private int followerLength;

    @JsonProperty
    @Index
    private int followingLength;
}