package net.unverschaemt.pinfever;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;


public class CategoryChooser extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_chooser);

        LinearLayout layout = (LinearLayout) findViewById(R.id.Category_layout);
        List<String> categories = getCategories();
        float buttonHeight = 100 / categories.size();
        for (String category : categories) {
            Button button = new Button(this);
            button.setText(category);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, buttonHeight);
            button.setLayoutParams(param);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*** only for testing ***/
                    Game game = new Game();
                    List<Round> rounds = new ArrayList<Round>();
                    Round round = new Round();
                    List<Question> questions = new ArrayList<Question>();
                    Question question = new Question();
                    question.setAnswerLat((long) 52.9384);
                    question.setAnswerLong((long) 07.9384);
                    question.setText("Where is my mom?");
                    questions.add(question);
                    question = new Question();
                    question.setAnswerLat((long) 32.9384);
                    question.setAnswerLong((long) 02.9384);
                    question.setText("Where is your mother fucker?");
                    questions.add(question);
                    round.setQuestions(questions);
                    question = new Question();
                    question.setAnswerLat((long) 42.9384);
                    question.setAnswerLong((long) 11.9384);
                    question.setText("Where is everybody?");
                    questions.add(question);
                    rounds.add(round);
                    game.setRounds(rounds);
                    game.setActiveRound(round);
                    game.setState(GameState.MATCH_ACTIVE);
                    /*** only for testing ***/
                    Intent intent = new Intent(v.getContext(), Map.class);
                    intent.putExtra(Map.GAME, game);
                    startActivity(intent);
                }
            });
            layout.addView(button);
        }
    }

    private List<String> getCategories() {
        List<String> categories = new ArrayList<String>();
        categories.add("Sport");
        categories.add("Sex");
        categories.add("Education");
        return categories;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category_chooser, menu);
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
}
