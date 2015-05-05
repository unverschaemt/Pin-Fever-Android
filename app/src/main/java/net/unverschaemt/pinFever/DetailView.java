package net.unverschaemt.pinFever;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collection;


public class DetailView extends ActionBarActivity {
    public final static String ROUND = "net.unverschaemt.PinFever.ROUND";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        Intent intent = getIntent();
        Game game = (Game)intent.getSerializableExtra(Home.GAME);
        if(game != null) {
            fillUI(game);
        }
    }

    private void fillUI(Game game) {
        setScore(game.getOwnScore(), game.getOpponentScore());
        ListView roundsList = (ListView) findViewById(R.id.DetailView_rounds);
        roundsList.setAdapter(new RoundListAdapter(this, game.getRounds()));
        if(game.isFinished()){
            Button revanche = (Button) findViewById(R.id.DetailView_revanche);
            revanche.setVisibility(View.VISIBLE);
        }
    }

    private void setScore(int ownScore, int opponentScore){
        TextView ownScoreTextView = (TextView) findViewById(R.id.DetailView_ownScore);
        ownScoreTextView.setText(ownScore);
        TextView opponentScoreTextView = (TextView) findViewById(R.id.DetailView_opponentScore);
        opponentScoreTextView.setText(opponentScore);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_view, menu);
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
