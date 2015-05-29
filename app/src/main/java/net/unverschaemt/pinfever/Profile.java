package net.unverschaemt.pinfever;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class Profile extends Activity {
    private final int SELECT_PHOTO = 1;

    private DataSource dataSource;
    private ServerAPI serverAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        dataSource = new DataSource(this);
        serverAPI = new ServerAPI(this);
        showProfile(Home.ownUser);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showProfile(User user) {
        CircularImageButton imageButton = (CircularImageButton) findViewById(R.id.Profile_avatar);
        imageButton.setImageBitmap(user.getAvatar());
    }

    public void signOut(View view) {
        tidyUp();
        startActivity(new Intent(this, Login.class));
    }

    private void tidyUp() {
        deleteToken();
        deleteOwnUser();
        dropTables();
    }

    private void dropTables() {
        dataSource.open();
        dataSource.dropAllTables();
        dataSource.close();
    }

    private void deleteOwnUser() {
        SharedPreferences sharedPreferences = getSharedPreferences(Home.OWNUSER, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    private void deleteToken() {
        SharedPreferences sharedPreferences = getSharedPreferences(ServerAPI.token, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(ServerAPI.token);
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    final Uri imageUri = imageReturnedIntent.getData();
                    Bitmap bitmap = null;
                    try {
                        bitmap = AvatarHandler.decodeUri(this, imageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    updateAvatar(bitmap, Home.ownUser.getId());
                }
        }
    }

    private void updateAvatar(Bitmap bitmap, String fileName) {
        File image = AvatarHandler.saveAvatarToStorage(this, bitmap, fileName);
        updateAvatarOnServer(image);
        Home.ownUser.setAvatar(bitmap);
        showProfile(Home.ownUser);
    }

    private void updateAvatarOnServer(File f) {
        serverAPI.connect(ServerAPI.urlUploadAvatar, "", f, new FutureCallback() {
            @Override
            public void onCompleted(Exception e, Object result) {
                JsonObject jsonObject = (JsonObject) result;
                if (!jsonObject.get(ServerAPI.errorObject).isJsonNull()) {
                    ErrorHandler.showErrorMessage(jsonObject, getBaseContext());
                }
            }
        });
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        String jsonString = "";
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                jsonString += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonString;
    }

}
