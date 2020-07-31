package com.google.sps.servlets.datacreation;

import com.google.api.client.util.Charsets;
import com.google.appengine.repackaged.com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.sps.KeyConfig;
import com.google.sps.model.follow.FollowItem;
import com.google.sps.model.follow.FollowListObject;
import com.google.sps.model.queue.MediaListItem;
import com.google.sps.model.queue.QueueListItemObject;
import com.google.sps.model.queue.ViewedListItemObject;
import com.google.sps.model.review.ReviewObject;
import com.google.sps.model.user.UserObject;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.*;

import static com.googlecode.objectify.ObjectifyService.ofy;

@WebServlet("/createData")
public class DataCreationServlet extends HttpServlet {

    Gson gson = new Gson();
    private final TmdbMovies moviesQuery = new TmdbMovies(new TmdbApi(KeyConfig.MOVIE_KEY));

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fixture = this.readResource("username.txt", Charsets.UTF_8);
        String[] userArray = fixture.split("\n");
        ArrayList<UserObject> userObjects = new ArrayList<>();

        generateUsers(userArray, userObjects);

        generateFollows(userObjects);

        generateReviews(userObjects);

        generateQueues(userObjects, MediaListItem.TYPE_QUEUE);

        generateQueues(userObjects, MediaListItem.TYPE_VIEWED);
    }

    public String readResource(final String fileName, Charset charset) throws IOException {
        return Resources.toString(Resources.getResource(fileName), charset);
    }

    public static String getRandom(String[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    public MovieDb getDetails(int id) throws IOException {
        try {
            // Uses null to default language to en-US
            return moviesQuery.getMovie(id, null);
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public void generateUsers(String[] names, List<UserObject> userObjects) {
        for(int i = 0; i < 100; i++) {
            String userName = getRandom(names) + new Random().nextInt(1000);
            UserObject user = new UserObject();

            user.setEmail(userName + "@gmail.com");
            user.setUsername(userName);
            user.setUsernameNorm(userName.toLowerCase());
            user.setId(UUID.randomUUID().toString().replace("-", ""));
            user.setProfilePicUrl("https://icotar.com/avatar/"+userName);
            userObjects.add(user);
            ofy().save().entity(user).now();
        }
    }

    public void generateReviews(List<UserObject> userObjects) throws IOException {
        MovieDb movie = getDetails(299537);
        for(int i = 0; i < 100; i++) {
            Lorem lorem = LoremIpsum.getInstance();

            ReviewObject review = new ReviewObject();
            int rnd = new Random().nextInt(userObjects.size());
            UserObject user = userObjects.get(rnd);

            review.setAuthorName(user.getUsername());
            review.setAuthorId(user.getId());
            review.setContentTitle(movie.getTitle());
            review.setArtUrl("https://image.tmdb.org/t/p/w500/"+movie.getPosterPath());
            review.setContentId("299537");
            review.setRating(new Random().nextInt(5)+1);
            review.setReviewTitle(lorem.getWords(5,10));
            review.setReviewBody(lorem.getParagraphs(2,4));
            review.setContentType("movie");
            ofy().save().entity(review).now();
        }
    }

    public void generateFollows(List<UserObject> userObjects) {
        for(int i = 0; i < 1000; i++) {
            int rnd = new Random().nextInt(userObjects.size());
            UserObject follower = userObjects.get(rnd);
            int foloweeRandom = new Random().nextInt(userObjects.size());
            UserObject followee = userObjects.get(foloweeRandom);
            if(follower == followee) {
                continue;
            }
            if(ofy().load().type(FollowItem.class).filter("userId", follower.getId()).filter("targetId", followee.getId()).list().size() != 0) {
                continue;
            }
            FollowItem follow = new FollowItem();
            follow.setUserId(follower.getId());
            follow.setTargetId(followee.getId());
            ofy().save().entity(follow);
        }
    }

    public void generateQueues(List<UserObject> userObjects, String type) throws IOException {
        for(int i = 0; i < 500; i++) {
            int rnd = new Random().nextInt(userObjects.size());
            UserObject user = userObjects.get(rnd);
            MediaListItem list;
            if(type == MediaListItem.TYPE_QUEUE) {
                list = new QueueListItemObject();
            } else {
                list = new ViewedListItemObject();
            }

            MovieDb movie = getDetails(new Random().nextInt(10000) + 1);
            if(movie != null) {
                list.setUsername(user.getUsername());
                list.setListType(MediaListItem.TYPE_QUEUE);
                list.setTitle(movie.getTitle());
                list.setMediaId(Integer.toString(movie.getId()));
                list.setMediaType("movie");
                list.setUserId(user.getId());

                if(movie.getTitle() == null) {
                    System.out.println(movie.getId());
                }

                if(movie.getPosterPath() == null) {
                    list.setArtUrl("assets/poster-placeholder.png");
                } else {
                    list.setArtUrl("https://image.tmdb.org/t/p/w500/"+movie.getPosterPath());
                }

                ofy().save().entity(list).now();
            }
        }
    }
}
