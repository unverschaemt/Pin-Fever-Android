package net.unverschaemt.pinfever;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by D060338 on 26.05.2015.
 */
public class serverAPI {
    public final static String serverURL = "http://87.106.19.69:8080";
    public final static String errorObject = "err";
    public final static String errorInfo = "info";
    public final static String data = "data";
    public final static String token = "token";

    public static String connect(String urlString) {

        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        urlString = serverURL + urlString;
        String response = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url
                    .openConnection();
            if (con.getResponseCode() == con.HTTP_BAD_REQUEST) {
                response = readStream(con.getErrorStream());
            } else {
                response = readStream(con.getInputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private static String readStream(InputStream in) {
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
