package net.unverschaemt.pinfever;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends Activity {
    public final static String USERNAME = "net.unverschaemt.PinFever.USERNAME";

    private EditText tvEmail = null;
    private EditText tvPassword = null;
    private ProgressBar busyIndicator = null;

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
    }

    private boolean isClientSignedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences(serverAPI.token, MODE_PRIVATE);
        return sharedPreferences.contains(serverAPI.token);
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
            new ServerConnectTask().execute(email, password);
        }
    }

    private class ServerConnectTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return serverAPI.connect("/auth/login?email=" + params[0] + "&password=" + params[1]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String resultString) {
            busyIndicator.setVisibility(View.GONE);
            JSONObject result = null;
            try {
                result = new JSONObject(resultString);
                if (result.isNull(serverAPI.errorObject)) {
                    String token = getTokenFromRequest(result);
                    storeToken(token);
                    startActivity(new Intent(getBaseContext(), Home.class));
                } else {
                    showErrorMessage(result);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void storeToken(String token) {
        if (!token.equals("")) {
            SharedPreferences sharedpreferences = getSharedPreferences(serverAPI.token, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(serverAPI.token, token);
            editor.commit();
        }
    }

    private void showErrorMessage(JSONObject result) {
        String errorMessage = "";
        try {
            errorMessage = result.getString(serverAPI.errorInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private String getTokenFromRequest(JSONObject result) {
        String token = "";
        JSONObject data = null;
        try {
            data = result.getJSONObject(serverAPI.data);
            token = data.getString(serverAPI.token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
