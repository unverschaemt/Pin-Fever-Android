package net.unverschaemt.pinfever;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by D060338 on 26.05.2015.
 */
public class ServerAPI {
    public final static String serverURL = "http://87.106.19.69:8080";

    public final static String errorObject = "err";
    public final static String errorInfo = "info";
    public final static String dataObject = "data";
    public final static String playerObject = "player";
    public final static String friends = "friends";
    public final static String players = "players";
    public final static String id = "_id";
    public final static String displayName = "displayName";
    public final static String level = "level";
    public final static String avatar = "avatar";
    public final static String token = "token";

    public final static String paramAuthToken = "api-auth-token";
    public final static String paramEmail = "email";
    public final static String paramPassword = "password";
    public final static String paramDisplayName = "displayName";
    public final static String paramPlayerId = "playerId";

    public final static String urlLogin = "/auth/login";
    public final static String urlRegister = "/auth/register";
    public final static String urlAddFriend = "/players/me/addfriend/";
    public final static String urlFriendsList = "/players/me/friends";
    public final static String urlPlayersSearch = "/players/search/";
    public final static String urlGetPlayer = "/players/";
    public final static String urlGetPlayerMe = "me";

    private java.util.Map<String, RequestMethod> requestMethods = new HashMap<String, RequestMethod>();
    private final Context context;

    public ServerAPI(Context context) {
        this.context = context;
        requestMethods.put(urlLogin, RequestMethod.POST);
        requestMethods.put(urlRegister, RequestMethod.POST);
        requestMethods.put(urlAddFriend, RequestMethod.POST);
        requestMethods.put(urlFriendsList, RequestMethod.GET);
        requestMethods.put(urlPlayersSearch, RequestMethod.GET);
        requestMethods.put(urlGetPlayer, RequestMethod.GET);

        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void connect(String urlRequest, String urlParameters, JSONObject jsonParam, Connector connector) {
        connector.execute(serverURL + urlRequest + urlParameters, jsonParam, requestMethods.get(urlRequest), getToken());
    }

    public String getToken() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ServerAPI.token, Context.MODE_PRIVATE);
        return sharedPreferences.getString(ServerAPI.token, "");
    }

    public static User convertJSONToUser(JSONObject playerJSON) throws JSONException {
        User player = new User();
        player.setId(playerJSON.getString(ServerAPI.id));
        player.setUserName(playerJSON.getString(ServerAPI.displayName));
        player.setScore(playerJSON.getInt(ServerAPI.level));
        player.setAvatar(R.mipmap.dummy_avatar + "");//TODO get real avatar
        return player;
    }


}
