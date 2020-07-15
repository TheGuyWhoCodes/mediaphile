package com.google.sps.model.queue;

import com.googlecode.objectify.annotation.Entity;

/**
 * Simple class to extend the entity db queue, this allows us
 * to store this in a separate database table.
 */
@Entity(name="ViewedListItem")
public class ViewedListItemObject extends MediaListItem { }