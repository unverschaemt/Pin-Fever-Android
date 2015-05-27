package net.unverschaemt.pinfever;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.provider.*;
import android.provider.Settings;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by D060338 on 26.05.2015.
 */
public class serverAPI {
    public final static String serverURL = "http://87.106.19.69:8080";
    public final static String errorObject = "err";
    public final static String errorInfo = "info";
    public final static String data = "data";
    public final static String token = "token";

    public static String connect(String urlString, JSONObject jsonParam) {

        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        urlString = serverURL + urlString;
        String response = "";
        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url
                    .openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Host", urlString);
            con.connect();

            DataOutputStream printout = new DataOutputStream(con.getOutputStream());
            printout.writeBytes(jsonParam.toString());
            printout.flush();
            printout.close();

            int code = con.getResponseCode();
            if (con.getResponseCode() == con.HTTP_OK) {
                response = readStream(con.getInputStream());
            } else {
                response = readStream(con.getErrorStream());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
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
