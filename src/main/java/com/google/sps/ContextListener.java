package com.google.sps;


import com.google.sps.model.queue.EntityDbQueue;
import com.google.sps.model.queue.WantToWatchQueueObject;
import com.google.sps.model.queue.WatchedQueueObject;
import com.googlecode.objectify.ObjectifyService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        initDbObjects();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("The application stopped");
    }

    public void initDbObjects() {
        ObjectifyService.register(EntityDbQueue.class);
        ObjectifyService.register(WantToWatchQueueObject.class);
        ObjectifyService.register(WatchedQueueObject.class);
    }
}