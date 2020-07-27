package com.google.sps.model.activity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * This activity interface is used to hold data for anything that would show up in the activity feed
 */
@Entity(name = "Activity")
public class Activity {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @JsonProperty
    @Index
    private Long id;
}
