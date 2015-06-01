package net.unverschaemt.pinfever;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by D060338 on 30.05.2015.
 */
public class FriendsHandler {
    private Context context;
    private DataSource dataSource;
    private ServerAPI serverAPI;

    int avatarsLoaded = 0;

    public FriendsHandler(Context context) {
        this.context = context;
        this.dataSource = new DataSource(context);
        this.serverAPI = new ServerAPI(context);
    }

    public List<User> getFriends(FriendsCallback callback) {
        dataSource.open();
        List<User> friends = dataSource.getAllFriends();
        dataSource.close();
        for (User friend : friends) {
            friend.setAvatar(AvatarHandler.loadAvatarFromStorage(context, friend.getId()));
        }
        updateFriendsFromServer(callback);
        return friends;
    }

    public void getPlayer(String searchKey, final GetPlayerCallback callback) {
        serverAPI.connect(serverAPI.urlPlayersSearch, searchKey, null, new FutureCallback() {
            @Override
            public void onCompleted(Exception e, Object result) {
                JsonObject jsonObject = (JsonObject) result;
                if (jsonObject.get(ServerAPI.errorObject).isJsonNull()) {
                    JsonObject data = jsonObject.getAsJsonObject(ServerAPI.dataObject);
                    JsonArray players = data.getAsJsonArray(ServerAPI.players);
                    if (players.size() > 0) {
                        JsonObject playerJSON = players.get(0).getAsJsonObject();
                        final User player = ServerAPI.convertJSONToUser(playerJSON);
                        loadAvatar(player, new FutureCallback() {
                            @Override
                            public void onCompleted(Exception e, Object result) {
                                player.setAvatar(AvatarHandler.loadAvatarFromStorage(context, player.getId()));
                                callback.onPlayerLoaded(player);
                            }
                        });
                    } else {
                        callback.onPlayerLoaded(null);
                        JsonObject userNotFoundObject = new JsonObject();
                        userNotFoundObject.addProperty(ServerAPI.errorInfo, context.getString(R.string.message_UserNotFound));
                        userNotFoundObject.addProperty(ServerAPI.errorObject, "");
                        ErrorHandler.showErrorMessage(userNotFoundObject, context);
                    }
                } else {
                    callback.onPlayerLoaded(null);
                    ErrorHandler.showErrorMessage(jsonObject, context);
                }
            }
        });
    }

    private void updateFriendsFromServer(final FriendsCallback callback) {
        serverAPI.connect(ServerAPI.urlFriendsList, "", null, new FutureCallback() {
            @Override
            public void onCompleted(Exception e, Object result) {
                JsonObject jsonObject = (JsonObject) result;
                final List<User> friends = new ArrayList<User>();
                if (jsonObject.get(ServerAPI.errorObject).isJsonNull()) {
                    JsonObject data = jsonObject.getAsJsonObject(ServerAPI.dataObject);
                    JsonArray friendsJSON = data.getAsJsonArray(ServerAPI.friends);
                    avatarsLoaded = 0;
                    for (final JsonElement friend : friendsJSON) {
                        final User newFriend = ServerAPI.convertJSONToUser(friend.getAsJsonObject());
                        loadAvatar(newFriend, new FutureCallback() {
                            @Override
                            public void onCompleted(Exception e, Object result) {
                                avatarsLoaded++;
                                newFriend.setAvatar(AvatarHandler.loadAvatarFromStorage(context, newFriend.getId()));
                                if (avatarsLoaded == friends.size()) {
                                    callback.onFriendsLoaded(friends);
                                }
                            }
                        });

                        friends.add(newFriend);
                    }
                    updateFriendsList(friends);
                } else {
                    callback.onFriendsLoaded(null);
                    ErrorHandler.showErrorMessage(jsonObject, context);
                }
            }
        });
    }

    private void loadAvatar(User player, FutureCallback futureCallback) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        serverAPI.downloadFile(ServerAPI.urlGetPlayer, player.getId() + "/img.jpeg", new File(directory, player.getId() + ".jpeg"), futureCallback);
    }

    private void updateFriendsList(List<User> friends) {
        dataSource.open();
        dataSource.updateFriends(friends);
        dataSource.close();
    }
}
