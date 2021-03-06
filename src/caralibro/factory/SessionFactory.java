package caralibro.factory;

import org.json.JSONObject;
import caralibro.model.data.Session;

/* 
 * @author		Federico Pascual Mastellone (fmaste@gmail.com)
 * @author		Simon Aberg Cobo (sima.cobo@gmail.com)
 */ 
public class SessionFactory {

	public static Session create(String key, String secret, Long userId, Long expirationTime) {
		Session session = new Session();
		session.setKey(key);
		session.setSecret(secret);
		session.setUserId(userId);
		session.setExpirationTime(expirationTime);
		return session;
	}
	
	public static Session create(String sessionJsonResponse) throws Exception {
		JSONObject sessionJson = new JSONObject(sessionJsonResponse);
		String key = sessionJson.getString("session_key");
		Long userId = sessionJson.getLong("uid");
		Long expirationTime = sessionJson.getLong("expires");
		String secret = sessionJson.optString("secret");
		if (secret.isEmpty()) {
			secret = null;
		}
		return create(key, secret, userId, expirationTime);
	}
	
}
