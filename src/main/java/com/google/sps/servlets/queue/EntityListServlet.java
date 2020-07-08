package com.google.sps.servlets.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.sps.model.queue.EntityDbQueue;
import com.google.sps.model.queue.QueueResponse;
import com.google.sps.model.queue.WantToWatchQueueObject;
import com.google.sps.model.queue.WatchedQueueObject;
import com.google.sps.util.Utils;
import com.googlecode.objectify.cmd.QueryKeys;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.google.sps.util.HttpUtils.*;
import static com.google.sps.util.Utils.isCorrectListType;
import static com.googlecode.objectify.ObjectifyService.ofy;

@WebServlet("/list/entity")
public class EntityListServlet extends HttpServlet {

    private final UserService userService = UserServiceFactory.getUserService();
    private final Gson gson = new GsonBuilder().serializeNulls().create();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json; charset=utf-8");
        String userID = request.getParameter("userID");
        String queueType = request.getParameter("entityType");

        if(queueType == null) {
            setInvalidGetResponse(response);
            return;
        }

        if(!isCorrectListType(queueType)) {
            setInvalidGetResponse(response);
            return;
        }

        List<? extends EntityDbQueue> result = getQueueWithType(queueType, userID);
        response.getWriter().println(gson.toJsonTree(result));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        QueueResponse newResponse = new QueueResponse();
        String body = Utils.parseBody(request);
        EntityDbQueue entityDb = null;
        User user = userService.getCurrentUser();
        if(userService.isUserLoggedIn()) {
            String currentUserID = user.getUserId();
            try {
                entityDb = decideDbType(body);
                if(!currentUserID.equals(entityDb.getUserID())) {
                    sendInvalidPostResponse(response, newResponse);
                    return;
                }
            } catch(Exception exception) {
                sendInvalidPostResponse(response, newResponse);
                return;
            }

            try {
                // Entry being saved to the datastore instance
                ofy().save().entity(entityDb).now();
            } catch(Exception e) {
                sendInvalidPostResponse(response, newResponse);
                return;
            }
            newResponse.setSuccess(true);
            newResponse.setEntity(entityDb);
            response.getWriter().println(gson.toJsonTree(newResponse));
        } else {
            sendNotLoggedIn(response);
        }
    }

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
            setInvalidGetResponse(response);
            return;
        }

        User user = userService.getCurrentUser();
        if(!userService.isUserLoggedIn()) {
            sendNotLoggedIn(response);
            return;
        }
        // if the user sends something that isn't viewed / queued
        if(!isCorrectListType(listType)) {
            setInvalidGetResponse(response);
            return;
        }
        // gather keys related to list
        QueryKeys<?> allKeys = returnListQueryKeys(listType, user, id);

        // if no entities are found, we should throw a 404 to notate
        // nothing was found to delete
        if(Iterables.size(allKeys) == 0) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // delete from db, will delete ANY instance of that id from the user
        // ie. if somehow multiple of the same movie / book get saved in the list
        ofy().delete().keys(allKeys).now();
    }

    /**
     * A helper function to get entity data from database
     * @param type: what list type should it look for (queue / watched)
     * @param userID: userID to look for
     * @return: a list of abstracted type EntityDbQueue
     */
    private List<? extends EntityDbQueue> getQueueWithType(String type, String userID) {
        if(type.equals(Utils.typeQueue)) {
            return ofy().load().type(WantToWatchQueueObject.class).filter("userID", userID).list();
        } else {
            return ofy().load().type(WatchedQueueObject.class).filter("userID", userID).list();
        }
    }

    /**
     * Used to decide which data type we should map POST request to
     * @param body: body to parse into object
     * @return: null if invalid entity type, an EntityDbQueue type object if it can
     * @throws JsonProcessingException: if it can't parse out data.
     */
    private EntityDbQueue decideDbType(String body) throws JsonProcessingException, NoSuchFieldException {
        EntityDbQueue entity = mapper.readValue(body, EntityDbQueue.class);
        if(entity.getEntityType().equals(Utils.typeViewed)) {
            return mapper.readValue(body, WatchedQueueObject.class);
        } else if(entity.getEntityType().equals(Utils.typeQueue)) {
            return mapper.readValue(body, WantToWatchQueueObject.class);
        } else {
            throw new NoSuchFieldException();
        }
    }

    private QueryKeys<?> returnListQueryKeys(String listType, User user, String id) {
        if(listType.equals("queue")) {
            return ofy().load().type(WantToWatchQueueObject.class)
                    .filter("userID", user.getUserId())
                    .filter("entityId", id).keys();
        } else {
            return ofy().load().type(WatchedQueueObject.class)
                    .filter("userID", user.getUserId())
                    .filter("entityId", id).keys();

        }
    }
}