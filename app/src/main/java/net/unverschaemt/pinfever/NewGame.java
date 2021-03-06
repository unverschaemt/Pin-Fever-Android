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
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.tokenautocomplete.TokenCompleteTextView;

import java.util.ArrayList;
import java.util.List;


public class NewGame extends Activity implements TokenCompleteTextView.TokenListener {

    final static int numberOfFriendsToPlayWith = 1;
    final static String RANDOMPLAYER_ID = "random";

    private ArrayAdapter<User> completionViewAdapter;
    private BaseAdapter gridLayoutAdapter;
    private UserAutoCompleteView completionView;
    private ServerAPI serverAPI;
    private DataSource dataSource;
    private ProgressBar busyIndicator;
    private List<User> userToPlayGameWith = new ArrayList<User>();
    private List<User> allUser;

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
        dataSource = new DataSource(this);
        fillGridLayout();
    }

    private void fillGridLayout() {
        allUser = getUser();
        GridView layout = (GridView) findViewById(R.id.NewGame_gridLayout);
        gridLayoutAdapter = new NewGameGridAdapter(this, allUser, completionView);
        layout.setAdapter(gridLayoutAdapter);

        completionViewAdapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, allUser);
        completionView.setAdapter(completionViewAdapter);

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
                updatePlayer(friends);
            }
        }));
        return user;
    }

    private void updatePlayer(List<User> friends) {
        setChangesOfPlayer(friends);
        removeOldPlayer(friends);
        gridLayoutAdapter.notifyDataSetChanged();
        completionViewAdapter.notifyDataSetChanged();
    }

    private void removeOldPlayer(List<User> friends) {
        List<User> usersToRemove = new ArrayList<User>();
        for (User user : allUser) {
            if (!user.getId().equals(RANDOMPLAYER_ID) && !friends.contains(user)) {
                usersToRemove.add(user);
            }
        }
        allUser.removeAll(usersToRemove);
    }

    private void setChangesOfPlayer(List<User> friends) {
        for (User friend : friends) {
            int index = allUser.indexOf(friend);
            if (index > -1) {
                User oldFriend = allUser.get(index);
                if (!oldFriend.getUserName().equals(friend.getUserName()) || !oldFriend.getAvatar().sameAs(friend.getAvatar())) {
                    allUser.set(index, friend);
                }
            } else {
                allUser.add(friend);
            }
        }
    }

    private User getRandomUser() {
        User randomUser = new User();
        randomUser.setUserName(getString(R.string.userName_random));
        randomUser.setId(RANDOMPLAYER_ID);
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
        userToPlayGameWith.add((User) o);

    }

    @Override
    public void onTokenRemoved(Object o) {
        View popup = findViewById(R.id.NewGame_popup);
        if (completionView.getObjects().size() == 0) {
            popup.setVisibility(View.GONE);
        }
        userToPlayGameWith.remove((User) o);
    }

    public void start(View view) {
        busyIndicator.setVisibility(View.VISIBLE);
        if (userToPlayGameWith.contains(getRandomUser())) {
            startAutoMatch();
        } else {
            startNewGame();
        }
    }

    private void startNewGame() {
        JsonObject jsonParam = new JsonObject();
        JsonArray participants = new JsonArray();
        Gson gson = new Gson();
        for (User participant : userToPlayGameWith) {
            participants.add(gson.fromJson(participant.getId(), JsonElement.class));
        }
        jsonParam.add(ServerAPI.participants, participants);
        serverAPI.connect(ServerAPI.urlCreateGame, "", jsonParam, new FutureCallback() {
            @Override
            public void onCompleted(Exception e, Object result) {
                busyIndicator.setVisibility(View.GONE);
                JsonObject jsonObject = (JsonObject) result;
                if (jsonObject.get(ServerAPI.errorObject).isJsonNull()) {
                    JsonObject data = jsonObject.getAsJsonObject(ServerAPI.dataObject);
                    Game game = createGameOfResponse(data);
                    goToCategoryChooser();
                }
            }
        });
    }

    private void goToCategoryChooser() {
        Intent intent = new Intent(this, CategoryChooser.class);
        startActivity(intent);
    }

    private Game createGameOfResponse(JsonObject data) {
        JsonObject turnbasedMatch = data.getAsJsonObject(ServerAPI.turnbasedMatchObject);
        Game game = new Game();
        String a = turnbasedMatch.get(ServerAPI.id).getAsString();
        game.setId(a);
        game.setRounds(getRounds(turnbasedMatch));
        game.setParticipants(getParticipants(turnbasedMatch));
        game.setState(GameState.valueOf(turnbasedMatch.get(ServerAPI.status).getAsString()));
        //dataSource.createGame(game);
        return game;
    }

    private List<Participant> getParticipants(JsonObject autoGame) {
        List<Participant> participants = new ArrayList<Participant>();
        JsonArray participantsJSON = autoGame.getAsJsonArray(ServerAPI.participants);
        for (JsonElement participantJSON : participantsJSON) {
            Participant participant = new Participant();
            participant.setId(participantJSON.getAsString());
            participants.add(participant);
        }
        return participants;
    }

    private List<Round> getRounds(JsonObject autoGame) {
        List<Round> rounds = new ArrayList<Round>();
        JsonArray roundsJSON = autoGame.getAsJsonArray(ServerAPI.rounds);
        for (JsonElement roundJSON : roundsJSON) {
            Round round = new Round();
            round.setId(roundJSON.getAsString());
            rounds.add(round);
        }
        return rounds;
    }

    private void startAutoMatch() {
        JsonObject jsonParam = new JsonObject();
        serverAPI.connect(ServerAPI.urlFindAutoGame, "", jsonParam, new FutureCallback() {
            @Override
            public void onCompleted(Exception e, Object result) {
                busyIndicator.setVisibility(View.GONE);
                JsonObject jsonObject = (JsonObject) result;
                if (jsonObject.get(ServerAPI.errorObject).isJsonNull()) {
                    JsonObject data = jsonObject.getAsJsonObject(ServerAPI.dataObject);
                    JsonObject autoGame = data.getAsJsonObject(ServerAPI.autoGameObject);
                    //TODO: save id, if WAIT_FOR_PLAYERS, else create Game with matchID
                }
            }
        });
    }
}
