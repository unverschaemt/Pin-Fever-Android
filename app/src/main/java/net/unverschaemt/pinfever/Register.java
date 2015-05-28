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

import org.json.JSONException;
import org.json.JSONObject;


public class Register extends Activity {
    private EditText tvEmail;
    private EditText tvPassword1;
    private EditText tvPassword2;
    private EditText tvDisplayName;
    private ProgressBar busyIndicator;

    private ServerAPI serverAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Intent intent = getIntent();
        tvDisplayName = (EditText) findViewById(R.id.Register_displayName);
        tvEmail = (EditText) findViewById(R.id.Register_email);
        tvPassword1 = (EditText) findViewById(R.id.Register_password1);
        tvPassword2 = (EditText) findViewById(R.id.Register_password2);
        String userName = intent.getStringExtra(Login.USERNAME);
        tvDisplayName.setText(userName);

        busyIndicator = (ProgressBar) findViewById(R.id.Register_progressBar);
        serverAPI = new ServerAPI(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

    public void changeAvatar(View view) {
        Toast.makeText(this, "TODO: Change Avatar", Toast.LENGTH_SHORT).show();
    }

    public void register(View view) {
        String displayName = tvDisplayName.getText().toString();
        String email = tvEmail.getText().toString();
        String password1 = tvPassword1.getText().toString();
        String password2 = tvPassword2.getText().toString();

        if (entriesAreValid(displayName, email, password1, password2)) {
            busyIndicator.setVisibility(View.VISIBLE);
            JSONObject jsonParam = new JSONObject();
            try {
                jsonParam.put(ServerAPI.paramEmail, email);
                jsonParam.put(ServerAPI.paramPassword, password1);
                jsonParam.put(ServerAPI.paramDisplayName, displayName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            serverAPI.connect(ServerAPI.urlRegister, "", jsonParam, new Connector() {
                @Override
                protected void onPostExecute(String resultString) {
                    busyIndicator.setVisibility(View.GONE);
                    JSONObject result = null;
                    try {
                        result = new JSONObject(resultString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (result.isNull(ServerAPI.errorObject)) {
                        String token = getTokenFromRequest(result);
                        storeToken(token);
                        startActivity(new Intent(getBaseContext(), Home.class));
                    } else {
                        ErrorHandler.showErrorMessage(result, getBaseContext());
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

    private boolean entriesAreValid(String displayName, String email, String password1, String password2) {
        boolean valid = true;
        tvDisplayName.setError(null);
        tvEmail.setError(null);
        tvPassword1.setError(null);
        tvPassword2.setError(null);

        if (displayName.equals("")) {
            makeError(tvDisplayName, getString(R.string.required));
            valid = false;
        }
        if (email.equals("")) {
            makeError(tvEmail, getString(R.string.required));
            valid = false;
        }
        if (password1.equals("")) {
            makeError(tvPassword1, getString(R.string.required));
            valid = false;
        }
        if (password2.equals("")) {
            makeError(tvPassword2, getString(R.string.required));
            valid = false;
        }
        if (!password1.equals(password2)) {
            makeError(tvPassword1, getString(R.string.passwordsDontMatch));
            makeError(tvPassword2, getString(R.string.passwordsDontMatch));
            valid = false;
        }
        return valid;
    }

    private void makeError(EditText editText, String errorMessage) {
        editText.setError(errorMessage);
    }

    private String getTokenFromRequest(JSONObject result) {
        String token = "";
        JSONObject data = null;
        try {
            data = result.getJSONObject(ServerAPI.dataObject);
            token = data.getString(ServerAPI.token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return token;
    }
}
