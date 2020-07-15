package com.google.sps.servlets.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.sps.model.queue.MediaListItem;
import com.google.sps.model.queue.MediaListResponse;
import com.google.sps.model.queue.QueueListItemObject;
import com.google.sps.model.queue.ViewedListItemObject;
import com.google.sps.util.Utils;
import com.googlecode.objectify.cmd.QueryKeys;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.google.sps.util.HttpUtils.*;
import static com.google.sps.util.Utils.isCorrectListType;
import static com.google.sps.util.Utils.mediaItemExists;
import static com.googlecode.objectify.ObjectifyService.ofy;

@WebServlet("/list/entity")
public class MediaListItemServlet extends HttpServlet {

    private final UserService userService = UserServiceFactory.getUserService();
    private final Gson gson = new GsonBuilder().serializeNulls().create();
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * doGet() returns the entirety of a list of type listType belonging to userId
     * Returns error 400 if an invalid or empty parameter is given
     * Returns error 500 if there is an error sending the response
     * Returns an empty list if the user doesn't exist
     * @param request: expects parameters userId and listType
     * @param response: returns the appropriate list
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        String userId = request.getParameter("userId");
        String listType = request.getParameter("listType");

        if(userId == null || userId.isEmpty()
                || listType == null || !isCorrectListType(listType)) {
            setInvalidGetResponse(response);
            return;
        }

        List<? extends MediaListItem> result = getListWithType(listType, userId);
        response.getWriter().println(gson.toJsonTree(result));
    }

    /**
     * doPost() attempts to add a given list item to the appropriate list
     * Returns error 400 if the body or any of its fields is invalid
     * Returns error 401 if user is not authenticated
     * Returns error 404 if the target media item does not exist
     * Returns error 500 if there is an error sending the response
     * @param request: expects a body containing the fields of MediaListItem, excluding its unique ID
     * @param response: returns a copy of the POSTed item on success
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        MediaListResponse newResponse = new MediaListResponse();
        String body = Utils.collectRequestLines(request);

        User user = userService.getCurrentUser();
        if (!userService.isUserLoggedIn()) {
            sendNotLoggedIn(response);
            return;
        }

        String currentUserID = user.getUserId();
        MediaListItem newListItem = null;
        try {
            newListItem = decideDbType(body);
            if(!currentUserID.equals(newListItem.getUserId())) {
                sendInvalidPostResponse(HttpServletResponse.SC_UNAUTHORIZED, response, newResponse);
                return;
            }
        } catch(Exception exception) {
            sendInvalidPostResponse(HttpServletResponse.SC_BAD_REQUEST, response, newResponse);
            return;
        }

        Boolean mediaItemExists = mediaItemExists(newListItem.getMediaType(), newListItem.getMediaId());
        if (mediaItemExists == null) {
            sendInvalidPostResponse(HttpServletResponse.SC_BAD_REQUEST, response, newResponse);
            return;
        }
        else if (!mediaItemExists) {
            sendInvalidPostResponse(HttpServletResponse.SC_NOT_FOUND, response, newResponse);
            return;
        }

        // Check if the posted object already exists
        if (Iterables.size(getMatchingListItems(
                newListItem.getListType(), user,
                newListItem.getMediaType(), newListItem.getMediaId())) != 0) {
            sendInvalidPostResponse(HttpServletResponse.SC_CONFLICT, response, newResponse);
            return;
        }

        try {
            // Entry being saved to the datastore instance
            ofy().save().entity(newListItem).now();
        } catch(Exception e) {
            sendInvalidPostResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response, newResponse);
            return;
        }
        newResponse.setSuccess(true);
        newResponse.setEntity(newListItem);
        response.getWriter().println(gson.toJsonTree(newResponse));
    }

    /**
     * doDelete() attempts to remove a given media item from a list of the logged in user
     * Returns error 400 if any parameters are invalid
     * Returns error 401 if user is not authenticated
     * Returns error 404 if the media item is not in the user's list of the given type
     * Returns error 500 if there is an error sending the response
     * @param request: expects mediaType, mediaId and listType
     * @param response: returns OK code on success
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String mediaType = request.getParameter("mediaType");
        String mediaId = request.getParameter("mediaId");
        String listType = request.getParameter("listType");

        // check to make sure all needed fields are there.
        if(mediaType == null || mediaType.isEmpty()
                || mediaId == null || mediaId.isEmpty()
                || listType == null || !isCorrectListType(listType)) {
            setInvalidGetResponse(response);
            return;
        }

        User user = userService.getCurrentUser();
        if(!userService.isUserLoggedIn()) {
            sendNotLoggedIn(response);
            return;
        }

        // gather keys related to list
        QueryKeys<?> allKeys = getMatchingListItems(listType, user, mediaType, mediaId);

        // if no entities are found, we should throw a 404 to notate
        // nothing was found to delete
        if(Iterables.size(allKeys) == 0) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // delete from db, will delete ANY instance of that id from the user
        // ie. if somehow multiple of the same movie / book get saved in the list
        ofy().delete().keys(allKeys).now();
        response.sendError(HttpServletResponse.SC_OK);
    }

    /**
     * A helper function to get entity data from database
     * @param type: what list type should it look for (queue / watched)
     * @param userId: userId to look for
     * @return: a list of abstracted type EntityDbQueue
     */
    private List<? extends MediaListItem> getListWithType(String type, String userId) {
        if(type.equals(QueueListItemObject.TYPE_QUEUE)) {
            return ofy().load().type(QueueListItemObject.class).filter("userId", userId).order("timestamp").list();
        } else {
            return ofy().load().type(ViewedListItemObject.class).filter("userId", userId).order("timestamp").list();
        }
    }

    /**
     * Used to decide which data type we should map POST request to
     * @param body: body to parse into object
     * @return: null if invalid entity type, an EntityDbQueue type object if it can
     * @throws JsonProcessingException: if it can't parse out data.
     */
    private MediaListItem decideDbType(String body) throws JsonProcessingException, NoSuchFieldException {
        MediaListItem entity = mapper.readValue(body, MediaListItem.class);
        if(entity.getListType().equals(MediaListItem.TYPE_VIEWED)) {
            return mapper.readValue(body, ViewedListItemObject.class);
        } else if(entity.getListType().equals(MediaListItem.TYPE_QUEUE)) {
            return mapper.readValue(body, QueueListItemObject.class);
        } else {
            throw new NoSuchFieldException();
        }
    }

    private QueryKeys<?> getMatchingListItems(String listType, User user, String mediaType, String mediaId) {
        if(listType.equals("queue")) {
            return ofy().load().type(QueueListItemObject.class)
                    .filter("userId", user.getUserId())
                    .filter("mediaType", mediaType)
                    .filter("mediaId", mediaId).keys();
        } else {
            return ofy().load().type(ViewedListItemObject.class)
                    .filter("userId", user.getUserId())
                    .filter("mediaType", mediaType)
                    .filter("mediaId", mediaId).keys();

        }
    }
}