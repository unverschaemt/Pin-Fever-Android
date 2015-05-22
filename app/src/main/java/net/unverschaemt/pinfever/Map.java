package net.unverschaemt.pinfever;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.TilesOverlay;


public class Map extends Activity {
    private Question question = null;
    private boolean showingQuestion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        question = getQuestion();
        TextView questionTextView = (TextView) findViewById(R.id.Map_questionText);
        questionTextView.setText(question.getText());
        toggleQuestionVisibility(null);

        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(false);
        mapView.setMinZoomLevel(2);
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

    private Question getQuestion() {
        Question question = new Question();
        question.setText("Where were the olympic games set in 2012?");
        float lat = (float) 51.3030;
        float longAnswer = (float) 0.0732;
        question.setAnswerLat((float) 51.3030);
        question.setAnswerLong((float) 0.0732);
        return question;
    }

    public void toggleQuestionVisibility(View view) {
        int visibility;
        if(showingQuestion){
            showingQuestion = false;
            visibility = View.GONE;
        }else{
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
