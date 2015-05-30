package net.unverschaemt.pinfever;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.tokenautocomplete.TokenCompleteTextView;

import java.util.ArrayList;
import java.util.List;


public class NewGame extends Activity implements TokenCompleteTextView.TokenListener {

    final static int numberOfFriendsToPlayWith = 1;

    ArrayAdapter<User> adapter;
    private UserAutoCompleteView completionView;
    private ServerAPI serverAPI;
    private ProgressBar busyIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        busyIndicator = (ProgressBar) findViewById(R.id.NewGame_progressBar);
        completionView = (UserAutoCompleteView) findViewById(R.id.NewGame_participants);
        completionView.setTokenListener(this);
        completionView.setTokenLimit(numberOfFriendsToPlayWith);
        serverAPI = new ServerAPI(this);
        fillGridLayout();
    }

    private void fillGridLayout() {
        List<User> user = getUser();
        GridView layout = (GridView) findViewById(R.id.NewGame_gridLayout);
        layout.setAdapter(new NewGameGridAdapter(this, user, completionView));

        adapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, user);
        completionView.setAdapter(adapter);

    }

    public UserAutoCompleteView getCompletionView() {
        return completionView;
    }

    private List<User> getUser() {
        List<User> user = new ArrayList<User>();
        User randomUser = getRandomUser();
        user.add(randomUser);
        user.addAll(new FriendsHandler(this).getFriends(new FriendsCallback() {
            @Override
            public void onFriendsLoaded(List<User> friends) {
                List<User> player = new ArrayList<User>();
                player.add(getRandomUser());
                player.addAll(friends);
                GridView layout = (GridView) findViewById(R.id.NewGame_gridLayout);
                layout.setAdapter(new NewGameGridAdapter(getBaseContext(), player, completionView));

                adapter = new ArrayAdapter<User>(getBaseContext(), android.R.layout.simple_list_item_1, player);
                completionView.setAdapter(adapter);
            }
        }));
        return user;
    }

    private User getRandomUser() {
        User randomUser = new User();
        randomUser.setUserName(getString(R.string.userName_random));
        randomUser.setId("random");
        Bitmap randomUserAvatar = BitmapFactory.decodeResource(getResources(), R.mipmap.random_user_avatar);
        randomUser.setAvatar(randomUserAvatar);
        return randomUser;
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
        if (completionView.getObjects().size() == 0) {
            popup.setVisibility(View.GONE);
        }
    }

    public void start(View view) {
        busyIndicator.setVisibility(View.VISIBLE);
        JsonObject jsonParam = new JsonObject();
        serverAPI.connect(ServerAPI.urlFindAutoGame, "", jsonParam, new FutureCallback() {
            @Override
            public void onCompleted(Exception e, Object result) {
                busyIndicator.setVisibility(View.GONE);
            }
        });
    }
}
