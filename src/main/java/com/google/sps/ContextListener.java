package com.google.sps;


import com.google.sps.model.queue.EntityDbQueue;
import com.google.sps.model.queue.WantToWatchQueueObject;
import com.google.sps.model.queue.WatchedQueueObject;
import com.googlecode.objectify.ObjectifyService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * ContextLister class is a listener used to track when a Java Servlet
 * thread starts and ends. We need this class because we need to register
 * our database objects before we do anything else with database entries.
 */
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
