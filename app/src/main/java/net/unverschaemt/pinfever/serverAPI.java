package net.unverschaemt.pinfever;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.io.IOException;
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
    public final static String urlSetPlayer = "/players/me/set";
    public final static String urlUploadAvatar = "/players/me/avatarupload";
    public final static String urlGetPlayerMe = "me";
    public final static String urlFindAutoGame = "/turnbasedmatches/findauto";

    private java.util.Map<String, RequestMethod> requestMethods = new HashMap<String, RequestMethod>();
    private java.util.Map<String, ContentType> contentTypes = new HashMap<String, ContentType>();
    private final Context context;

    public ServerAPI(Context context) {
        this.context = context;
        requestMethods.put(urlLogin, RequestMethod.POST);
        contentTypes.put(urlLogin, ContentType.JSON);
        requestMethods.put(urlRegister, RequestMethod.POST);
        contentTypes.put(urlRegister, ContentType.JSON);
        requestMethods.put(urlAddFriend, RequestMethod.POST);
        contentTypes.put(urlAddFriend, ContentType.JSON);
        requestMethods.put(urlFriendsList, RequestMethod.GET);
        requestMethods.put(urlPlayersSearch, RequestMethod.GET);
        requestMethods.put(urlGetPlayer, RequestMethod.GET);
        requestMethods.put(urlSetPlayer, RequestMethod.POST);
        contentTypes.put(urlSetPlayer, ContentType.JSON);
        requestMethods.put(urlUploadAvatar, RequestMethod.POST);
        contentTypes.put(urlUploadAvatar, ContentType.FORM_DATA);
        requestMethods.put(urlFindAutoGame, RequestMethod.POST);
        contentTypes.put(urlFindAutoGame, ContentType.JSON);

        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void connect(String urlRequest, String urlParameters, Object postObject, FutureCallback callback) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            RequestMethod requestMethod = requestMethods.get(urlRequest);
            switch (requestMethod) {
                case GET:
                    makeGETRequest(urlRequest, urlParameters, callback);
                    break;
                case POST:
                    makePostRequest(urlRequest, urlParameters, postObject, callback);
                    break;
            }
        } else {
            JsonObject error = new JsonObject();
            error.addProperty(errorObject, "connection failed");
            error.addProperty(errorInfo, context.getString(R.string.message_NoInternetConnection));
            callback.onCompleted(null, error);
        }
    }

    public void downloadFile(String urlRequest, String urlParameters, File file, FutureCallback callback) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Ion.with(context)
                    .load(ServerAPI.serverURL + urlRequest + urlParameters)
                    .addHeader(ServerAPI.paramAuthToken, getToken())
                    .write(file)
                    .setCallback(callback);
        } else {
            JsonObject error = new JsonObject();
            error.addProperty(errorObject, "connection failed");
            error.addProperty(errorInfo, context.getString(R.string.message_NoInternetConnection));
            callback.onCompleted(null, error);
        }
    }

    private void makeGETRequest(String urlRequest, String urlParameters, FutureCallback callback) {
        Ion.with(context)
                .load(ServerAPI.serverURL + urlRequest + urlParameters)
                .addHeader(ServerAPI.paramAuthToken, getToken())
                .asJsonObject()
                .setCallback(callback);
        return;
    }

    private void makePostRequest(String urlRequest, String urlParameters, Object postObject, FutureCallback callback) {
        ContentType contentType = contentTypes.get(urlRequest);
        switch (contentType) {
            case JSON:
                JsonObject jsonObject = (JsonObject) postObject;
                Ion.with(context)
                        .load(ServerAPI.serverURL + urlRequest + urlParameters)
                        .addHeader(ServerAPI.paramAuthToken, getToken())
                        .setJsonObjectBody(jsonObject)
                        .asJsonObject()
                        .setCallback(callback);
                break;
            case FORM_DATA:
                File file = (File) postObject;
                Ion.with(context)
                        .load(ServerAPI.serverURL + urlRequest + urlParameters)
                        .addHeader(ServerAPI.paramAuthToken, getToken())
                        .setMultipartFile("image", "image/jpeg", file)
                        .setMultipartContentType("multipart/form-data")
                        .asJsonObject()
                        .setCallback(callback);
                break;
        }
    }

    public String getToken() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ServerAPI.token, Context.MODE_PRIVATE);
        return sharedPreferences.getString(ServerAPI.token, "");
    }

    public static User convertJSONToUser(JsonObject playerJSON) {
        User player = new User();
        player.setId(playerJSON.get(ServerAPI.id).getAsString());
        player.setUserName(playerJSON.get(ServerAPI.displayName).getAsString());
        player.setScore(playerJSON.get(ServerAPI.level).getAsInt());
        return player;
    }


}
