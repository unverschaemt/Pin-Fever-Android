package net.unverschaemt.pinfever;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.overlay.TilesOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Map extends Activity {
    public final static String GAME = "net.unverschaemt.pinfever.GAME";
    public final static String QUESTION = "net.unverschaemt.pinfever.QUESTION";

    private Game game = null;
    private Question question = null;
    private List<Question> questions = null;
    private boolean showingQuestion = false;
    MapView mapView;
    private GeoPoint guess = null;
    private int questionCounter = 0;
    private GameState gameState;

    private ServerAPI serverAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        serverAPI = new ServerAPI(this);

        game = (Game) getIntent().getSerializableExtra(GAME);
        question = (Question) getIntent().getSerializableExtra(QUESTION);
        gameState = GameState.COMPLETED;

        if (game != null) {
            this.gameState = game.getState();
            this.questions = game.getActiveRound().getQuestions();
            question = this.questions.get(questionCounter);
        }

        resetQuestionText();
        initializeMap();

        if (gameState == GameState.MATCH_ACTIVE) {
            setMapTouchable();
        } else {
            Button submitButton = (Button) findViewById(R.id.Map_submit);
            submitButton.setText("Next");
            submitButton.setVisibility(View.VISIBLE);
            showGuessesAndAnswer();
        }
    }

    private void resetQuestionText() {
        final TextView questionTextView = (TextView) findViewById(R.id.Map_questionText);
        questionTextView.setText(question.getText());
        showingQuestion = false;
        toggleQuestionVisibility(null);
    }

    private void resetMap() {
        clearOverlays();
        if (gameState == GameState.MATCH_ACTIVE) {
            findViewById(R.id.Map_submit).setVisibility(View.GONE);
        } else {
            showGuessesAndAnswer();
        }
    }

    private void initializeMap() {
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setMinZoomLevel(2);
        mapView.setClickable(true);
        mapView.getController().setZoom(2);
        final MapTileProviderBasic tileProvider = new MapTileProviderBasic(getApplicationContext());
        final ITileSource tileSource = new XYTileSource("watercolor", null, 2, 17,
                256, ".jpg", new String[]{"http://tile.stamen.com/watercolor/"});
        tileProvider.setTileSource(tileSource);
        mapView.setTileSource(tileSource);
        final TilesOverlay tilesOverlay = new TilesOverlay(tileProvider, this.getBaseContext());
        tilesOverlay.setLoadingBackgroundColor(Color.WHITE);
        mapView.getOverlays().add(tilesOverlay);
    }

    private void setMapTouchable() {
        mapView.getOverlays().add(new TouchableOverlay(this));
    }

    private class TouchableOverlay extends Overlay {

        public TouchableOverlay(Context ctx) {
            super(ctx);
        }

        @Override
        protected void draw(Canvas c, MapView osmv, boolean shadow) {

        }

        @Override
        public boolean onSingleTapConfirmed(final MotionEvent event, final MapView mapView) {
            Projection proj = mapView.getProjection();
            GeoPoint point = (GeoPoint) proj.fromPixels((int) event.getX(), (int) event.getY());
            guess = point;
            setGuessMarker(point);
            return true;
        }

    }

    private void showGuessesAndAnswer() {
        GeoPoint answerPoint = new GeoPoint(question.getAnswerLat(), question.getAnswerLong());
        addMarker(answerPoint);
        for (java.util.Map.Entry<String, Turninformation> turninfo : question.getTurninformation().entrySet()) {
            GeoPoint guessPoint = showGuess(turninfo.getValue());
            PathOverlay myPath = new PathOverlay(Color.RED, this);
            myPath.addPoint(answerPoint);
            myPath.addPoint(guessPoint);
            mapView.getOverlays().add(myPath);
        }
        mapView.invalidate();
    }

    private GeoPoint showGuess(Turninformation turnInformation) {
        GeoPoint answerPoint = new GeoPoint(turnInformation.getAnswerLat(), turnInformation.getAnswerLong());
        addMarker(answerPoint);
        return answerPoint;
    }

    public void submitGuess(View view) {
        Turninformation turn = new Turninformation();
        if (gameState == GameState.MATCH_ACTIVE) {
            java.util.Map<String, Turninformation> turns = question.getTurninformation();
            GeoPoint guess = this.guess;
            if (turns == null) {
                turns = new HashMap<>();
            }
            turn.setAnswerLong((float) guess.getLongitude());
            turn.setAnswerLat((float) guess.getLatitude());
            turn.setDistance(calcDistance(guess));
            turns.put(Home.ownUser.getId(), turn);
        }

        question = getNextQuestion();
        if (question != null) {
            resetQuestionText();
            resetMap();
        } else {
            if (gameState == GameState.MATCH_ACTIVE) {
                sendTurnToServer(turn);
            }
            startActivity(new Intent(this, Home.class));
        }
    }

    private void sendTurnToServer(Turninformation turn) {
        JsonObject jsonParam = new JsonObject();
        JsonObject turnData = new JsonObject();
        JsonObject guess = new JsonObject();
        guess.addProperty("player", Home.ownUser.getId());
        guess.addProperty("latitude", turn.getAnswerLat());
        guess.addProperty("longitude", turn.getAnswerLong());
        guess.addProperty("distance", turn.getDistance());
        turnData.add("guess", guess);
        jsonParam.add("turndata", turnData);
        jsonParam.addProperty("matchcomplete", gameIsComplete());
        serverAPI.connect(ServerAPI.urlTakeTurn, game.getId() + "/taketurn", jsonParam, new FutureCallback() {
            @Override
            public void onCompleted(Exception e, Object result) {
                String a = "";
            }
        });
    }

    private boolean gameIsComplete() {
        return false;
    }

    private float calcDistance(GeoPoint guess) {
        float[] results = new float[3];
        Location.distanceBetween(question.getAnswerLong(), question.getAnswerLat(), guess.getLongitude(), guess.getLatitude(), results);
        return results[0];
    }

    private void setGuessMarker(GeoPoint point) {
        clearOverlays();
        addMarker(point);
        findViewById(R.id.Map_submit).setVisibility(View.VISIBLE);
    }

    private void clearOverlays() {
        mapView.getOverlays().clear();
        if (gameState == GameState.MATCH_ACTIVE) {
            mapView.getOverlays().add(new TouchableOverlay(this));
        }
        mapView.invalidate();
    }

    private void addMarker(GeoPoint point) {
        ArrayList<OverlayItem> overlaysItems = new ArrayList<OverlayItem>();
        overlaysItems.add(new OverlayItem("Marker", "Marker", point));
        DefaultResourceProxyImpl resourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
        ItemizedIconOverlay myLocationOverlay = new ItemizedIconOverlay<OverlayItem>(overlaysItems, null, resourceProxy);
        List<Overlay> overlays = mapView.getOverlays();
        overlays.add(myLocationOverlay);
        this.mapView.invalidate();
    }

    private Question getNextQuestion() {
        Question question = null;
        questionCounter++;
        if (questions != null && questions.size() > questionCounter) {
            question = questions.get(questionCounter);
        }
        return question;
    }

    public void toggleQuestionVisibility(View view) {
        int visibility;
        if (showingQuestion) {
            showingQuestion = false;
            visibility = View.GONE;
        } else {
            showingQuestion = true;
            visibility = View.VISIBLE;
        }
        findViewById(R.id.Map_questionFrame).setVisibility(visibility);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
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
