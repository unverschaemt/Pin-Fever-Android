package net.unverschaemt.pinfever;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class Home extends Activity {
    public final static String GAME = "net.unverschaemt.pinfever.GAME";
    public final static User ownUser = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        DataSource dataSource = new DataSource(this);
        dataSource.open();
        fillActiveGames(dataSource.getAllGames());
        dataSource.close();
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

    public void changeAvatar(View view){
        Toast.makeText(this, "TODO: Show Profile", Toast.LENGTH_SHORT).show();
    }

    public void settings(View view){
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    public void friends(View view){
        Intent intent = new Intent(this, FriendsList.class);
        startActivity(intent);
    }

    public void newGame(View view) {
        Intent intent = new Intent(this, NewGame.class);
        startActivity(intent);
    }

    public void fillActiveGames(List<Game> games){
        ListView activeGames = (ListView) findViewById(R.id.Home_activeGames);
        activeGames.setAdapter(new GameListAdapter(this, games));
    }
}
