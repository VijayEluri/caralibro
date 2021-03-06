package caralibro.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import caralibro.factory.PostFactory;
import caralibro.model.data.Application;
import caralibro.model.data.Group;
import caralibro.model.data.Page;
import caralibro.model.data.Session;
import caralibro.model.data.User;
import caralibro.model.data.stream.Post;
import caralibro.rest.Request;
import caralibro.rest.Response;

/*
 * Before you can get data from a user's stream, you need the extended permission "read_stream".
 * Do not trust the Post update_time because Facebook has bugs with it.
 * 
 * @author		Federico Pascual Mastellone (fmaste@gmail.com)
 * @author		Simon Aberg Cobo (sima.cobo@gmail.com)
 */ 
public class PostDao {
	private static final Logger logger = LoggerFactory.getLogger(PostDao.class);
	// By trial an error this was my calculated max number of posts that can be retrieved. More and you get an HTTP 500 error or an empty array.
	private static final String MAX_LIMIT = "300";
	private static final String DELTA = "50";
	
	/*
	 * Retrieve Posts from source.
	 * 
	 * @param startTime		Unix time in seconds (not milliseconds). Default is 1 day.
	 * @param endTime		Unix time in seconds (not milliseconds). Default is now.
	 * @return 				If there are no Posts returns null or empty.
	 */	
	public static Collection<Post> getFromSourceId(Application application, Session session, String sourceId, Long startTime, Long endTime) throws Exception {
		String debugMsg = "Retrieving Posts from " + sourceId + " starting from ";
		Long callTime = System.currentTimeMillis();
		Map<String,String> params = Request.create(application, session, "Stream.get");
		params.put("source_ids", sourceId);
		params.put("limit", MAX_LIMIT); // Facebook default limit is 30
		if (startTime != null) {
			params.put("start_time", startTime.toString()); // Defaults to 1 day
			debugMsg = debugMsg + (new Date(startTime*1000)).toString() + " (" + startTime + ")";
		} else {
			debugMsg = debugMsg + "the begining";
		}
		debugMsg = debugMsg + " up to ";
		if (endTime != null) {
			params.put("end_time", endTime.toString()); // Defaults to now
			debugMsg = debugMsg + (new Date(endTime*1000)).toString() + " (" + endTime + ")";
		} else {
			debugMsg = debugMsg + "now";
		}
		debugMsg = debugMsg + ".";
		logger.debug(debugMsg);
		Request.sign(params, application, session);
		String streamJsonResponse = Response.get(params);
		// JSON response must be a JSON object with 'posts', 'profiles' and 'albums' as keys!
		if (streamJsonResponse == null || streamJsonResponse.isEmpty() || !streamJsonResponse.startsWith("{")) {
			return null;
		}
		JSONObject streamJsonObject = new JSONObject(streamJsonResponse);	
		// Warning: When there are no posts, 'posts' is an empty object like this
		// Response: {"posts":{},"profiles":{},"albums":{}}
		// Otherwise, if there are posts, 'posts' is a JSON array.
		JSONArray postsJsonArray = streamJsonObject.optJSONArray("posts");
		if (postsJsonArray == null) {
			return null;
		}
		ArrayList<Post> posts = new ArrayList<Post>();
		for (int i = 0; i < postsJsonArray.length(); i++) {
			// The Post is retrieved as a String and PostFactory must know how to handle it!
			String postString = postsJsonArray.optString(i);
			if (postString != null && !postString.isEmpty()) {
				Post post = null;
				try {
					post = PostFactory.create(postString);
				} catch (Exception e) {
					post = null;
					logger.error("Skipping invalid JSON encoded Post: \"" + postString + "\".");
					e.printStackTrace();
				}
				if (post != null) {
					posts.add(post);
				}
			}
		}
		logger.debug("Posts response has " + posts.size() + " posts.");
		if (!posts.isEmpty() && posts.size() > (Integer.parseInt(MAX_LIMIT) - Integer.parseInt(DELTA))) {
			logger.debug("Limit reached, there could be more posts left, making another request.");
			// Wait at least 6 seconds between requests. Facebook allows up to 100 requests per 600 seconds.
			Long actualTime = System.currentTimeMillis();
			Long deltaTime = actualTime - callTime;
			if ((deltaTime) <= 6000) {
				Thread.sleep(6000 - deltaTime);
			}
			Collection<Post> morePosts = getFromSourceId(application, session, sourceId, startTime, posts.get(posts.size() - 1).getUpdateTime());
			if (morePosts != null) {
				posts.addAll(morePosts);
			}
		}
		return posts;
	}
	
	public static Collection<Post> getFromPage(Application application, Session session, Page page, Long startTime, Long endTime) throws Exception {
		return getFromSourceId(application, session, page.getId().toString(), startTime, endTime);
	}
	
	public static Collection<Post> getFromGroup(Application application, Session session, Group group, Long startTime, Long endTime) throws Exception {
		return getFromSourceId(application, session, group.getId().toString(), startTime, endTime);
	}
	
	public static Collection<Post> getFromUser(Application application, Session session, User user, Long startTime, Long endTime) throws Exception {
		return getFromSourceId(application, session, user.getId().toString(), startTime, endTime);
	}

	public static Collection<Post> getFromApplication(Application application, Session session, Application sourceApplication, Long startTime, Long endTime) throws Exception {
		return getFromSourceId(application, session, sourceApplication.getId().toString(), startTime, endTime);
	}
	
	public static boolean remove(Application application, Session session, Post post) throws Exception {
		logger.debug("Removing Post: \"" + post.getId() + "\"");
		Map<String,String> params = Request.create(application, session, "Stream.remove");
		params.put("post_id", post.getId());
		Request.sign(params, application, session);
		String response = Response.get(params);
		if (response != null && !response.isEmpty() && response.equalsIgnoreCase("true")) {
			logger.debug("Post " + post.getId() + " was removed succesfully");
			return true;
		} else {
			logger.error("Post " + post.getId() + " was not removed, response was: \"" + response + "\"");
			return false;
		}
	}
	
}
