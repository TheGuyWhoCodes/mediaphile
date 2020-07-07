package com.google.sps.servlets.queue;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.collect.Iterables;
import com.google.sps.model.queue.EntityDbQueue;
import com.google.sps.model.queue.WantToWatchQueueObject;
import com.google.sps.model.queue.WatchedQueueObject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.QueryKeys;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Used to delete an element from list (watched / queue)
 */
@WebServlet("/list/delete")
public class DeleteEntityFromListServlet extends HttpServlet {

    private final UserService userService = UserServiceFactory.getUserService();

    /**
     *  the delete endpoint is used to delete an entity from either a "watched" or "queued" list
     *  needs an "id" representing the entity id, and a "list type" being either queued or watched
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        String listType = request.getParameter("listType");

        // check to make sure all needed fields are there.
        if(id == null || listType == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        User user = userService.getCurrentUser();
        if(userService.isUserLoggedIn()) {
            QueryKeys<?> allKeys;
            if(listType.equals("queue")) {
                allKeys = ofy().load().type(WantToWatchQueueObject.class)
                        .filter("userID", user.getUserId())
                        .filter("entityId", id).keys();
            } else if(listType.equals("viewed")) {
                allKeys = ofy().load().type(WatchedQueueObject.class)
                        .filter("userID", user.getUserId())
                        .filter("entityId", id).keys();

            } else {
                // not a valid list type
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            // if no entities are found, we should throw a 404 to notate
            // nothing was found to delete
            if(Iterables.size(allKeys) == 0) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            // delete from db, will delete ANY instance of that id from the user
            // ie. if somehow multiple of the same movie / book get saved in the list
            ofy().delete().keys(allKeys).now();
        } else {
            System.out.println("6");
             // user is not logged in
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
