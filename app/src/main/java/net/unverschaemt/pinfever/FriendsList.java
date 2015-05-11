package net.unverschaemt.pinfever;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;


public class FriendsList extends Activity {
    public final static String USER = "net.unverschaemt.PinIt.USER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        /*TODO: Remove! Only For Testing*/
        User[] friends = new User[10];
        friends[0] = new User("98hzo2in3re", "Peter", "Nils Hirsekorn", "Nils_Hirsekorn@online.de", R.mipmap.dummy_avatar);
        friends[1] = new User("dn98znx98zn", "Robin", "Nils Hirsekorn", "Nils_Hirsekorn@online.de", R.mipmap.dummy_avatar);
        friends[2] = new User("98znx98u3n0", "Dehlen", "Nils Hirsekorn", "Nils_Hirsekorn@online.de", R.mipmap.dummy_avatar);
        friends[3] = new User("09u7nx9n82n", "Dustin", "Nils Hirsekorn", "Nils_Hirsekorn@online.de", R.mipmap.dummy_avatar);
        friends[4] = new User("98724nc97xj", "Hex0r", "Nils Hirsekorn", "Nils_Hirsekorn@online.de", R.mipmap.dummy_avatar);
        friends[5] = new User("576nx982nsz", "Pottsau", "Nils Hirsekorn", "Nils_Hirsekorn@online.de", R.mipmap.dummy_avatar);
        friends[6] = new User("323nx9x9732", "Zettel", "Nils Hirsekorn", "Nils_Hirsekorn@online.de", R.mipmap.dummy_avatar);
        friends[7] = new User("9834nc09x20", "Kaputt", "Nils Hirsekorn", "Nils_Hirsekorn@online.de", R.mipmap.dummy_avatar);
        friends[8] = new User("c29873nmj02", "Uboot", "Nils Hirsekorn", "Nils_Hirsekorn@online.de", R.mipmap.dummy_avatar);
        friends[9] = new User("woieun22zei", "Hase", "Nils Hirsekorn", "Nils_Hirsekorn@online.de", R.mipmap.dummy_avatar);
        /*****/
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
        Toast.makeText(this, "TODO: Add Friend", Toast.LENGTH_SHORT).show();
    }

    public void fillFriendsList(User[] friends){
        ListView friendsList = (ListView) findViewById(R.id.FriendsList_friends);
        friendsList.setAdapter(new FriendListAdapter(this, friends));
    }
}
