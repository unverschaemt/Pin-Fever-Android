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
import java.util.HashMap;
import java.util.List;


public class NewGame extends Activity implements TokenCompleteTextView.TokenListener {
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
        User randomUser = new User();
        randomUser.setUserName(getString(R.string.userName_random));
        randomUser.setAvatar(R.mipmap.random_user_avatar);
        user.add(randomUser);
        DataSource dataSource = new DataSource(this);
        dataSource.open();
        user.addAll(dataSource.getAllFriends());
        dataSource.close();
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
        if (completionView.getObjects().size() == 0) {
            popup.setVisibility(View.GONE);
        }
    }

    public void start(View view) {
        /*** only for testing ***/
        Game game = new Game();
        List<Round> rounds = new ArrayList<Round>();
        Round round = new Round();
        List<Question> questions = new ArrayList<Question>();
        Question question = new Question();
        java.util.Map<Long, Turninformation> turninformation = new HashMap<Long, Turninformation>();
        Turninformation info = new Turninformation();
        info.setAnswerLat((long) 12.7684);
        info.setAnswerLong((long) 23.7684);
        turninformation.put((long) 9734802, info);
        info = new Turninformation();
        info.setAnswerLat((long) 17.7684);
        info.setAnswerLong((long) 43.7684);
        turninformation.put((long) 9734803, info);
        question.setTurninformation(turninformation);
        question.setAnswerLat((long) 52.9384);
        question.setAnswerLong((long) 07.9384);
        question.setText("Where is my mom?");
        questions.add(question);
        question = new Question();
        turninformation = new HashMap<Long, Turninformation>();
        info = new Turninformation();
        info.setAnswerLat((long) 04.7684);
        info.setAnswerLong((long) 53.7684);
        turninformation.put((long) 9734802, info);
        info = new Turninformation();
        info.setAnswerLat((long) 20.7684);
        info.setAnswerLong((long) 23.7684);
        turninformation.put((long) 9734803, info);
        question.setTurninformation(turninformation);
        question.setAnswerLat((long) 32.9384);
        question.setAnswerLong((long) 02.9384);
        question.setText("Where is your mother fucker?");
        questions.add(question);
        round.setQuestions(questions);
        question = new Question();
        turninformation = new HashMap<Long, Turninformation>();
        info = new Turninformation();
        info.setAnswerLat((long) 08.7684);
        info.setAnswerLong((long) 33.7684);
        turninformation.put((long) 9734802, info);
        info = new Turninformation();
        info.setAnswerLat((long) 06.7684);
        info.setAnswerLong((long) 43.7684);
        turninformation.put((long) 9734803, info);
        question.setTurninformation(turninformation);
        question.setAnswerLat((long) 42.9384);
        question.setAnswerLong((long) 11.9384);
        question.setText("Where is everybody?");
        questions.add(question);
        rounds.add(round);
        game.setRounds(rounds);
        game.setActiveRound(round);
        game.setState(GameState.COMPLETED);
        /*** only for testing ***/
        Intent intent = new Intent(this, Map.class);
        intent.putExtra(Map.GAME, game);
        startActivity(intent);
    }
}
