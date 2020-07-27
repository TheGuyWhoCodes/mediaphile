package com.google.sps.model.queue;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Subclass;

/**
 * Simple class to extend the entity db queue, this allows us
 * to store this in a separate database table.
 */
@Subclass(index=true, name="QueueListItem")
public class QueueListItemObject extends MediaListItem { }