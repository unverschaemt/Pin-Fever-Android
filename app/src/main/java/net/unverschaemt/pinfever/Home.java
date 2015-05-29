package net.unverschaemt.pinfever;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class Home extends Activity {
    public final static String GAME = "net.unverschaemt.pinfever.GAME";
    public final static String OWNUSER = "ownUser";
    public final static String OWNUSER_ID = "id";
    public final static String OWNUSER_SCORE = "score";
    public final static String OWNUSER_AVATAR = "avatar";
    public final static String OWNUSER_DISPLAYNAME = "displayName";

    private TextView tvScore;
    private CircularImageButton cibAvatar;
    private ProgressBar busyIndicator;

    public static User ownUser = null;
    private DataSource dataSource;
    private ServerAPI serverAPI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tvScore = (TextView) findViewById(R.id.Home_score);
        cibAvatar = (CircularImageButton) findViewById(R.id.Home_avatar);
        busyIndicator = (ProgressBar) findViewById(R.id.Home_progressBar);
        dataSource = new DataSource(this);
        serverAPI = new ServerAPI(this);

        loadProfileData();
    }

    private void loadProfileData() {
        loadAndShowDataFromDatabase();
        updateProfile();
    }

    private void loadAndShowDataFromDatabase() {
        dataSource.open();
        List<Game> games = dataSource.getAllGames();
        dataSource.close();
        fillActiveGames(games);
        ownUser = getOwnUserFromDatabase();
        if (ownUser != null) {
            showDataFromOwnUser(ownUser);
        }
    }

    private void showDataFromOwnUser(User ownUser) {
        tvScore.setText(ownUser.getScore() + "");
        cibAvatar.setImageBitmap(AvatarHandler.getBitmapFromAvatarURL(ownUser.getAvatarURL()));
    }

    private User getOwnUserFromDatabase() {
        SharedPreferences sharedPreferences = getSharedPreferences(OWNUSER, MODE_PRIVATE);
        java.util.Map<String, ?> info = sharedPreferences.getAll();
        User ownUser = null;
        if (info.size() > 0) {
            ownUser = new User();
            ownUser.setId((String) info.get(OWNUSER_ID));
            ownUser.setUserName((String) info.get(OWNUSER_DISPLAYNAME));
            ownUser.setScore((Integer) info.get(OWNUSER_SCORE));
            ownUser.setAvatar((String) info.get(OWNUSER_AVATAR));
        }
        return ownUser;
    }

    private void saveOwnUserInDatabase(User ownUser) {
        SharedPreferences sharedPreferences = getSharedPreferences(OWNUSER, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(OWNUSER_ID, ownUser.getId());
        editor.putString(OWNUSER_DISPLAYNAME, ownUser.getUserName());
        editor.putInt(OWNUSER_SCORE, ownUser.getScore());
        editor.putString(OWNUSER_AVATAR, ownUser.getAvatarURL());
        editor.commit();
    }

    private void updateProfile() {
        busyIndicator.setVisibility(View.VISIBLE);
        serverAPI.connect(ServerAPI.urlGetPlayer, ServerAPI.urlGetPlayerMe, null, new Connector() {
            @Override
            protected void onPostExecute(String resultString) {
                busyIndicator.setVisibility(View.GONE);
                try {

                    JSONObject result = new JSONObject(resultString);
                    if (result.isNull(serverAPI.errorObject)) {
                        JSONObject data = result.getJSONObject(ServerAPI.dataObject);
                        JSONObject player = data.getJSONObject(ServerAPI.playerObject);
                        User ownUser = ServerAPI.convertJSONToUser(player);
                        saveOwnUserInDatabase(ownUser);
                        showDataFromOwnUser(ownUser);
                    } else {
                        ErrorHandler.showErrorMessage(result, getBaseContext());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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

    public void showProfile(View view) {
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
    }

    public void settings(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    public void friends(View view) {
        Intent intent = new Intent(this, FriendsList.class);
        startActivity(intent);
    }

    public void newGame(View view) {
        Intent intent = new Intent(this, NewGame.class);
        startActivity(intent);
    }

    public void fillActiveGames(List<Game> games) {
        ListView activeGames = (ListView) findViewById(R.id.Home_activeGames);
        activeGames.setAdapter(new GameListAdapter(this, games));
    }
}
