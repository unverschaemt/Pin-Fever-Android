package net.unverschaemt.pinfever;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

import java.util.List;


public class FriendsList extends Activity {
    public final static String USER = "net.unverschaemt.pinfever.USER";
    private List<User> friends;
    private DataSource dataSource;
    private ProgressBar busyIndicator;
    private FriendsHandler friendsHandler;

    private ServerAPI serverAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        serverAPI = new ServerAPI(this);
        busyIndicator = (ProgressBar) findViewById(R.id.FriendsList_progressBar);
        dataSource = new DataSource(this);
        friendsHandler = new FriendsHandler(this);

        busyIndicator.setVisibility(View.VISIBLE);
        friends = friendsHandler.getFriends(new FriendsCallback() {
            @Override
            public void onFriendsLoaded(List<User> friends) {
                busyIndicator.setVisibility(View.GONE);
                fillFriendsList(friends);
            }
        });
        fillFriendsList(friends);
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

    public void openDialogToAddAFriend(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(getString(R.string.addFriend));
        alert.setMessage(getString(R.string.message_addFriend));

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String searchKey = input.getText().toString();
                tryToAddTheFriend(searchKey);
            }
        });

        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    private void tryToAddTheFriend(String searchKey) {
        busyIndicator.setVisibility(View.VISIBLE);
        friendsHandler.getPlayer(searchKey, new GetPlayerCallback() {
            @Override
            public void onPlayerLoaded(User player) {
                busyIndicator.setVisibility(View.GONE);
                if (player != null) {
                    addFriend(player);
                }
            }
        });
    }

    private void addFriend(final User friend) {
        JsonObject jsonParam = new JsonObject();
        jsonParam.addProperty(ServerAPI.paramPlayerId, friend.getId());
        serverAPI.connect(serverAPI.urlAddFriend, friend.getId(), jsonParam, new FutureCallback() {
            @Override
            public void onCompleted(Exception e, Object result) {
                JsonObject jsonObject = (JsonObject) result;
                if (jsonObject.get(ServerAPI.errorObject).isJsonNull()) {
                    updateFriendsList(friend);
                } else {
                    ErrorHandler.showErrorMessage(jsonObject, getBaseContext());
                }
            }
        });
    }

    public void fillFriendsList(List<User> friends) {
        this.friends = friends;
        ListView friendsList = (ListView) findViewById(R.id.FriendsList_friends);
        friendsList.setAdapter(new FriendListAdapter(this, this.friends));
    }
}
