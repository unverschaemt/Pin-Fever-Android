package net.unverschaemt.pinfever;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

import java.util.*;


public class CategoryChooser extends Activity {

    public static final int NUMBER_OF_CATEGORIES = 3;
    public static final int NUMBER_OF_QUESTIONS = 3;

    private ServerAPI serverAPI;
    private Game game;
    private DataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_chooser);

        game = (Game) getIntent().getSerializableExtra(NewGame.GAME);

        serverAPI = new ServerAPI(this);
        dataSource = new DataSource(this);
        setCategories();
    }

    private void setCategories() {
        String lang = Locale.getDefault().getLanguage();

        serverAPI.connect(ServerAPI.urlGetCategories, "?" + ServerAPI.paramAmountOfCategories + NUMBER_OF_CATEGORIES + "&" + ServerAPI.paramLanguage + lang, null, new FutureCallback() {
            @Override
            public void onCompleted(Exception e, Object result) {
                JsonObject jsonObject = (JsonObject) result;
                if (jsonObject.get(ServerAPI.errorObject).isJsonNull()) {
                    java.util.Map<String, String> categories = new HashMap<String, String>();
                    JsonObject data = ((JsonObject) result).getAsJsonObject(ServerAPI.dataObject);
                    JsonArray categoriesJSON = data.getAsJsonArray(ServerAPI.categoriesObject);
                    for (JsonElement categoryJSON : categoriesJSON) {
                        JsonObject categoryObject = (JsonObject) categoryJSON;
                        categories.put(categoryObject.get(ServerAPI.categoryName).getAsString(), categoryObject.get(ServerAPI.id).getAsString());
                    }
                    if (categories.size() > 0) {
                        buildUI(categories);
                    }
                } else {
                    ErrorHandler.showErrorMessage(jsonObject, getBaseContext());
                    startActivity(new Intent(getBaseContext(), Home.class));
                }
            }
        });
    }

    private void buildUI(final java.util.Map<String, String> categories) {
        float buttonHeight = 100 / categories.size();
        for (java.util.Map.Entry<String, String> category : categories.entrySet()) {
            Button button = new Button(this);
            button.setText(category.getKey());
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, buttonHeight);
            button.setLayoutParams(param);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button buttonClicked = (Button) v;
                    startNewGame(categories.get(buttonClicked.getText().toString()));
                }
            });
            LinearLayout layout = (LinearLayout) findViewById(R.id.Category_layout);
            layout.addView(button);
        }
    }

    private void startNewGame(final String categoryId) {
        String lang = Locale.getDefault().getLanguage();
        serverAPI.connect(ServerAPI.urlGetQuestions, "?" + ServerAPI.paramAmountOfQuestions + NUMBER_OF_QUESTIONS + "&" +
                ServerAPI.paramLanguage + lang + "&" +
                ServerAPI.paramCategory + categoryId, null, new FutureCallback() {
            @Override
            public void onCompleted(Exception e, Object result) {
                JsonObject jsonObject = (JsonObject) result;
                if (jsonObject.get(ServerAPI.errorObject).isJsonNull()) {
                    List<Question> questions = new ArrayList<Question>();
                    JsonObject data = ((JsonObject) result).getAsJsonObject(ServerAPI.dataObject);
                    JsonArray questionsJSON = data.getAsJsonArray(ServerAPI.questions);
                    for (JsonElement questionJSON : questionsJSON) {
                        Question question = new Question();
                        question.setText(((JsonObject) questionJSON).get(ServerAPI.question).getAsString());
                        JsonObject answerJSON = ((JsonObject) questionJSON).getAsJsonObject(ServerAPI.answerObject);
                        JsonObject coordinatesJSON = answerJSON.getAsJsonObject(ServerAPI.coordinates);
                        question.setAnswerLong(coordinatesJSON.get(ServerAPI.longitude).getAsFloat());
                        question.setAnswerLat(coordinatesJSON.get(ServerAPI.latitude).getAsFloat());
                        question.setAnswerText(answerJSON.get(ServerAPI.text).getAsString());
                        questions.add(question);
                    }
                    Round activeRound = game.getActiveRound();
                    activeRound.setQuestions(questions);
                    activeRound.setCategory(categoryId);
                    Intent intent = new Intent(getBaseContext(), Map.class);
                    intent.putExtra(Map.GAME, game);
                    startActivity(intent);
                    dataSource.open();
                    dataSource.createRound(activeRound);
                    dataSource.close();
                } else {
                    ErrorHandler.showErrorMessage(jsonObject, getBaseContext());
                    startActivity(new Intent(getBaseContext(), Home.class));
                }
            }
        });
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
