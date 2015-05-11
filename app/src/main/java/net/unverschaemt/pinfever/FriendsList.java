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

import java.util.ArrayList;
import java.util.List;


public class FriendsList extends Activity {
    public final static String USER = "net.unverschaemt.pinfever.USER";
    private List<User> friends;
    private FriendsDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        dataSource = new FriendsDataSource(this);
        dataSource.open();
        friends = dataSource.getAllFriends();

        fillFriendsList(friends);

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

    public void addFriend(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(getString(R.string.addFriend));
        alert.setMessage(getString(R.string.message_addFriend));

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                //TODO: check if user exists. If so send user information. Else error message
                /*only for testing*/
                long id = Math.round(Math.random()*10000);
                saveFriendInternally(id, value ,20, R.mipmap.dummy_avatar);
                fillFriendsList(friends);
                /*****/
            }
        });

        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    private void saveFriendInternally(long id, String userName, int score, int avatar){
        User newFriend = dataSource.createFriend(id, userName, score, avatar);
        friends.add(newFriend);
    }

    public void fillFriendsList(List<User> friends){
        ListView friendsList = (ListView) findViewById(R.id.FriendsList_friends);
        friendsList.setAdapter(new FriendListAdapter(this, friends));
    }
}
