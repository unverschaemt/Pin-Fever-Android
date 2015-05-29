package net.unverschaemt.pinfever;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

import java.util.ArrayList;
import java.util.List;


public class FriendsList extends Activity {
    public final static String USER = "net.unverschaemt.pinfever.USER";
    private List<User> friends;
    private DataSource dataSource;
    private ProgressBar busyIndicator;

    private ServerAPI serverAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        serverAPI = new ServerAPI(this);
        busyIndicator = (ProgressBar) findViewById(R.id.FriendsList_progressBar);
        dataSource = new DataSource(this);

        friends = getFriends();
        fillFriendsList(friends);
    }

    private List<User> getFriends() {
        dataSource.open();
        List<User> friends = dataSource.getAllFriends();
        dataSource.close();
        updateFriendsFromServer();
        return friends;
    }

    private void updateFriendsFromServer() {
        busyIndicator.setVisibility(View.VISIBLE);
        serverAPI.connect(ServerAPI.urlFriendsList, "", null, new FutureCallback() {
            @Override
            public void onCompleted(Exception e, Object result) {
                busyIndicator.setVisibility(View.GONE);
                JsonObject jsonObject = (JsonObject) result;
                List<User> friends = new ArrayList<User>();
                if (jsonObject.get(ServerAPI.errorObject).isJsonNull()) {
                    JsonObject data = jsonObject.getAsJsonObject(ServerAPI.dataObject);
                    JsonArray friendsJSON = data.getAsJsonArray(ServerAPI.friends);
                    for (JsonElement friend : friendsJSON) {
                        friends.add(ServerAPI.convertJSONToUser(friend.getAsJsonObject()));
                    }
                    updateFriendsList(friends);
                } else {
                    ErrorHandler.showErrorMessage(jsonObject, getBaseContext());
                }
            }
        });
    }

    private void updateFriendsList(List<User> friends) {
        this.friends = friends;
        dataSource.open();
        dataSource.updateFriends(friends);
        dataSource.close();
        fillFriendsList(this.friends);
    }

    private void updateFriendsList(User friend) {
        this.friends.add(friend);
        dataSource.open();
        dataSource.createFriend(friend);
        dataSource.close();
        fillFriendsList(this.friends);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void tryToAddFriend(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(getString(R.string.addFriend));
        alert.setMessage(getString(R.string.message_addFriend));

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String friendName = input.getText().toString();
                searchForFriendAndAddIfFriendExists(friendName);
            }
        });

        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    private void searchForFriendAndAddIfFriendExists(String friendName) {
        busyIndicator.setVisibility(View.VISIBLE);
        serverAPI.connect(serverAPI.urlPlayersSearch, friendName, null, new FutureCallback() {
            @Override
            public void onCompleted(Exception e, Object result) {
                busyIndicator.setVisibility(View.GONE);
                JsonObject jsonObject = (JsonObject) result;
                if (jsonObject.get(ServerAPI.errorObject).isJsonNull()) {
                    JsonObject data = jsonObject.getAsJsonObject(ServerAPI.dataObject);
                    JsonArray players = data.getAsJsonArray(ServerAPI.players);
                    if (players.size() > 0) {
                        JsonObject playerJSON = players.get(0).getAsJsonObject();
                        addFriend(playerJSON.get(ServerAPI.id).getAsString());
                        User player = ServerAPI.convertJSONToUser(playerJSON);
                        updateFriendsList(player);
                    } else {
                        JsonObject userNotFoundObject = new JsonObject();
                        userNotFoundObject.addProperty(ServerAPI.errorInfo, getString(R.string.message_UserNotFound));
                        userNotFoundObject.addProperty(ServerAPI.errorObject, "");
                        ErrorHandler.showErrorMessage(userNotFoundObject, getBaseContext());
                    }
                } else {
                    ErrorHandler.showErrorMessage(jsonObject, getBaseContext());
                }
            }
        });
    }

    private void addFriend(final String friendId) {
        JsonObject jsonParam = new JsonObject();
        jsonParam.addProperty(ServerAPI.paramPlayerId, friendId);
        serverAPI.connect(serverAPI.urlAddFriend, friendId, jsonParam, new FutureCallback() {
            @Override
            public void onCompleted(Exception e, Object result) {
                busyIndicator.setVisibility(View.GONE);
                JsonObject jsonObject = (JsonObject) result;
                if (!jsonObject.get(ServerAPI.errorObject).isJsonNull()) {
                    ErrorHandler.showErrorMessage(jsonObject, getBaseContext());
                }
            }
        });
    }

    public void fillFriendsList(List<User> friends) {
        ListView friendsList = (ListView) findViewById(R.id.FriendsList_friends);
        friendsList.setAdapter(new FriendListAdapter(this, friends));
    }
}
