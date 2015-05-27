package net.unverschaemt.pinfever;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class Login extends Activity {
    public final static String USERNAME = "net.unverschaemt.PinFever.USERNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (isClientSignedIn()) {
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
        }
    }

    private boolean isClientSignedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences(serverAPI.token, MODE_PRIVATE);
        String token = sharedPreferences.getString(serverAPI.token, "");
        if (token.equals("")) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    public void login(View view) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    public void register(View view) {
        Intent intent = new Intent(this, Register.class);
        EditText userNameTextField = (EditText) findViewById(R.id.Login_userName);
        String userName = userNameTextField.getText().toString();
        if (!userName.equals("")) {
            intent.putExtra(USERNAME, userName);
        }
        startActivity(intent);
    }

}
