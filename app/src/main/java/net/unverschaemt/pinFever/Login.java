package net.unverschaemt.pinFever;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/*
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;*/

import java.io.InputStream;


public class Login extends Activity{
    /*private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean signedInUser;
    private ConnectionResult mConnectionResult;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(Plus.API, Plus.PlusOptions.builder().build()).addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }

    /*protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }

        if (!mIntentInProgress) {
            // store mConnectionResult
            mConnectionResult = result;

            if (signedInUser) {
                resolveSignInError();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        switch (requestCode) {
            case RC_SIGN_IN:
                if (responseCode == RESULT_OK) {
                    signedInUser = false;

                }
                mIntentInProgress = false;
                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
                break;
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        signedInUser = false;
        Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
        getProfileInformation();
    }

    private void updateProfile(boolean isSignedIn) {
        if (isSignedIn) {
            signinFrame.setVisibility(View.GONE);
            profileFrame.setVisibility(View.VISIBLE);
        } else {
            signinFrame.setVisibility(View.VISIBLE);
            profileFrame.setVisibility(View.GONE);
        }
    }

    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                username.setText(personName);
                emailLabel.setText(email);

                new LoadProfileImage(image).execute(personPhotoUrl);

                // update profile frame with new info about Google Account
                // profile
                updateProfile(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
        updateProfile(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signin:
                googlePlusLogin();
                break;
        }
    }

    public void signIn(View v) {
        googlePlusLogin();
    }

    public void logout(View v) {
        googlePlusLogout();
    }

    private void googlePlusLogin() {
        if (!mGoogleApiClient.isConnecting()) {
            signedInUser = true;
            resolveSignInError();
        }
    }

    private void googlePlusLogout() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            updateProfile(false);
        }
    }

            // download Google Account profile image, to complete profile
    private class LoadProfileImage extends AsyncTask {
        ImageView downloadedImage;

        public LoadProfileImage(ImageView image) {
            this.downloadedImage = image;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap icon = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                icon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return icon;
        }

        protected void onPostExecute(Bitmap result) {
            downloadedImage.setImageBitmap(result);
        }
    }*/


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

    public void login(View view){
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

}
