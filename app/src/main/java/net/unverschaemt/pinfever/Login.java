package net.unverschaemt.pinfever;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

public class Login extends Activity {
    public final static String USERNAME = "net.unverschaemt.PinFever.USERNAME";

    private EditText tvEmail = null;
    private EditText tvPassword = null;
    private ProgressBar busyIndicator = null;

    private ServerAPI serverAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (isClientSignedIn()) {
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
        }
        tvEmail = (EditText) findViewById(R.id.Login_email);
        tvPassword = (EditText) findViewById(R.id.Login_password);
        busyIndicator = (ProgressBar) findViewById(R.id.Login_progressBar);
        serverAPI = new ServerAPI(this);

        notifyUserIfSessionHasExpired();
    }

    private void notifyUserIfSessionHasExpired() {
        Intent intent = getIntent();
        if (intent.hasExtra(ErrorHandler.errorUnauthorized)) {
            Toast.makeText(this, getString(R.string.sessionExpired), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isClientSignedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences(ServerAPI.token, MODE_PRIVATE);
        return sharedPreferences.contains(ServerAPI.token);
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
        String email = tvEmail.getText().toString();
        String password = tvPassword.getText().toString();

        if (entriesAreValid(email, password)) {
            busyIndicator.setVisibility(View.VISIBLE);
            final JsonObject jsonParam = new JsonObject();
            jsonParam.addProperty(ServerAPI.paramEmail, email);
            jsonParam.addProperty(ServerAPI.paramPassword, password);
            serverAPI.connect(ServerAPI.urlLogin, "", jsonParam, new FutureCallback() {
                @Override
                public void onCompleted(Exception e, Object result) {
                    busyIndicator.setVisibility(View.GONE);
                    JsonObject jsonObject = (JsonObject) result;
                    if (jsonObject.get(ServerAPI.errorObject).isJsonNull()) {
                        String token = getTokenFromRequest(jsonObject);
                        storeToken(token);
                        startActivity(new Intent(getBaseContext(), Home.class));
                    } else {
                        ErrorHandler.showErrorMessage(jsonObject, getBaseContext());
                    }
                }
            });
        }
    }

    private void storeToken(String token) {
        if (!token.equals("")) {
            SharedPreferences sharedpreferences = getSharedPreferences(ServerAPI.token, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(ServerAPI.token, token);
            editor.commit();
        }
    }

    private String getTokenFromRequest(JsonObject jsonObject) {
        String token = "";
        JsonObject data = jsonObject.getAsJsonObject(ServerAPI.dataObject);
        token = data.get(ServerAPI.token).getAsString();
        return token;
    }

    private boolean entriesAreValid(String email, String password) {
        boolean valid = true;
        tvEmail.setError(null);
        tvPassword.setError(null);
        if (email.equals("")) {
            makeError(tvEmail, getString(R.string.required));
            valid = false;
        }
        if (password.equals("")) {
            makeError(tvPassword, getString(R.string.required));
            valid = false;
        }
        return valid;
    }

    private void makeError(EditText editText, String errorMessage) {
        editText.setError(errorMessage);
    }

    public void register(View view) {
        Intent intent = new Intent(this, Register.class);
        EditText userNameTextField = (EditText) findViewById(R.id.Login_email);
        String userName = userNameTextField.getText().toString();
        if (!userName.equals("")) {
            intent.putExtra(USERNAME, userName);
        }
        startActivity(intent);
    }

}
