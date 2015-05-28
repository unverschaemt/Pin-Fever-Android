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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        serverAPI.connect(ServerAPI.urlFriendsList, "", null, new Connector() {
            @Override
            protected void onPostExecute(String resultString) {
                busyIndicator.setVisibility(View.GONE);
                JSONObject result = null;
                try {
                    List<User> friends = new ArrayList<User>();
                    result = new JSONObject(resultString);
                    if (result.isNull(serverAPI.errorObject)) {
                        JSONObject data = result.getJSONObject(ServerAPI.dataObject);
                        JSONArray friendsJSON = data.getJSONArray(ServerAPI.friends);
                        for (int i = 0; i < friendsJSON.length(); i++) {
                            JSONObject friendJSON = friendsJSON.getJSONObject(i);
                            friends.add(convertJSONToUser(friendJSON));
                        }
                        updateFriendsList(friends);
                    } else {
                        showErrorMessage(result);
                    }
                } catch (JSONException e) {
                    busyIndicator.setVisibility(View.GONE);
                    e.printStackTrace();
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
        serverAPI.connect(serverAPI.urlPlayersSearch, friendName, null, new Connector() {
            @Override
            protected void onPostExecute(String resultString) {
                JSONObject result = null;
                try {
                    busyIndicator.setVisibility(View.GONE);
                    result = new JSONObject(resultString);
                    if (result.isNull(serverAPI.errorObject)) {
                        JSONObject data = result.getJSONObject(ServerAPI.dataObject);
                        JSONArray players = data.getJSONArray(ServerAPI.players);
                        if (players.length() > 0) {
                            JSONObject playerJSON = (JSONObject) players.get(0);
                            addFriend(playerJSON.getString(ServerAPI.id));
                            User player = convertJSONToUser(playerJSON);
                            updateFriendsList(player);
                        } else {
                            JSONObject userNotFoundObject = new JSONObject();
                            userNotFoundObject.put(ServerAPI.errorInfo, "No user found!");
                            showErrorMessage(userNotFoundObject);
                        }
                    } else {
                        showErrorMessage(result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addFriend(final String friendId) {
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put(ServerAPI.paramPlayerId, friendId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        serverAPI.connect(serverAPI.urlAddFriend, "", jsonParam, new Connector() {
            @Override
            protected void onPostExecute(String resultString) {
                JSONObject result = null;
                busyIndicator.setVisibility(View.GONE);
                try {
                    result = new JSONObject(resultString);
                    if (!result.isNull(serverAPI.errorObject)) {
                        showErrorMessage(result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private User convertJSONToUser(JSONObject playerJSON) throws JSONException {
        User player = new User();
        player.setId(playerJSON.getString(ServerAPI.id));
        player.setUserName(playerJSON.getString(ServerAPI.displayName));
        player.setScore(playerJSON.getInt(ServerAPI.level));
        player.setAvatar(R.mipmap.dummy_avatar);//TODO get real avatar
        return player;
    }

    private void showErrorMessage(JSONObject result) {
        String errorMessage = "";
        try {
            errorMessage = result.getString(serverAPI.errorInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    public void fillFriendsList(List<User> friends) {
        ListView friendsList = (ListView) findViewById(R.id.FriendsList_friends);
        friendsList.setAdapter(new FriendListAdapter(this, friends));
    }
}
