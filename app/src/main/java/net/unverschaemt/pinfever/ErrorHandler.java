package net.unverschaemt.pinfever;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.JsonObject;

/**
 * Created by D060338 on 28.05.2015.
 */
public class ErrorHandler {
    public static String errorUnauthorized = "Unauthorized!";

    public static void showErrorMessage(JsonObject result, Context context) {
        String errorInfo = "";
        String errorMessage = "";
        errorInfo = result.get(ServerAPI.errorInfo).toString();
        errorMessage = result.get(ServerAPI.errorObject).getAsString();
        if (errorMessage.equals(errorUnauthorized)) {
            removeToken(context);
            Intent intent = new Intent(context, Login.class);
            intent.putExtra(errorUnauthorized, true);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, errorInfo, Toast.LENGTH_LONG).show();
        }
    }

    private static void removeToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ServerAPI.token, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(ServerAPI.token);
        editor.commit();
    }
}
