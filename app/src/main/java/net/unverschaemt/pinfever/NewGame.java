package net.unverschaemt.pinfever;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.tokenautocomplete.TokenCompleteTextView;

import java.util.ArrayList;
import java.util.List;


public class NewGame extends Activity implements TokenCompleteTextView.TokenListener{
    ArrayAdapter<User> adapter;
    private UserAutoCompleteView completionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        completionView = (UserAutoCompleteView) findViewById(R.id.NewGame_participants);
        completionView.setTokenListener(this);
        fillGridLayout();
    }

    private void fillGridLayout() {
        List<User> user = getUser();
        GridView layout = (GridView)findViewById(R.id.NewGame_gridLayout);
        layout.setAdapter(new NewGameGridAdapter(this, user, completionView));

        adapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, user);
        completionView.setAdapter(adapter);

    }

    public UserAutoCompleteView getCompletionView(){
        return completionView;
    }

    private List<User> getUser() {
        //TODO implement choice of user to display
        /* only for testing*/
        List<User> user = new ArrayList<User>();
        user.add(new User("98hzo2in3re", "Peter", "Nils Hirsekorn", "Nils_Hirsekorn@online.de", R.mipmap.dummy_avatar));
        user.add(new User("dn98znx98zn", "Robin", "Nils Hirsekorn", "Nils_Hirsekorn@online.de", R.mipmap.dummy_avatar));
        user.add(new User("98znx98u3n0", "Dehlen", "Nils Hirsekorn", "Nils_Hirsekorn@online.de", R.mipmap.dummy_avatar));
        user.add(new User("09u7nx9n82n", "Dustin", "Nils Hirsekorn", "Nils_Hirsekorn@online.de", R.mipmap.dummy_avatar));
        user.add(new User("98724nc97xj", "Hex0r", "Nils Hirsekorn", "Nils_Hirsekorn@online.de", R.mipmap.dummy_avatar));
        user.add(new User("576nx982nsz", "Pottsau", "Nils Hirsekorn", "Nils_Hirsekorn@online.de", R.mipmap.dummy_avatar));
        user.add(new User("323nx9x9732", "Zettel", "Nils Hirsekorn", "Nils_Hirsekorn@online.de", R.mipmap.dummy_avatar));
        user.add(new User("9834nc09x20", "Kaputt", "Nils Hirsekorn", "Nils_Hirsekorn@online.de", R.mipmap.dummy_avatar));
        user.add(new User("c29873nmj02", "Uboot", "Nils Hirsekorn", "Nils_Hirsekorn@online.de", R.mipmap.dummy_avatar));
        user.add(new User("woieun22zei", "Hase", "Nils Hirsekorn", "Nils_Hirsekorn@online.de", R.mipmap.dummy_avatar));
        /******/
        return user;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_game, menu);
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

    @Override
    public void onTokenAdded(Object o) {
        View popup = findViewById(R.id.NewGame_popup);
        popup.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTokenRemoved(Object o) {
        View popup = findViewById(R.id.NewGame_popup);
        popup.setVisibility(View.GONE);
    }

    public void start(View view){
        Intent intent = new Intent(this, Map.class);
        startActivity(intent);
    }
}
