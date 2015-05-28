package net.unverschaemt.pinfever;

import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by D060338 on 28.05.2015.
 */
public abstract class Connector extends AsyncTask<Object, Void, String> {

    @Override
    protected String doInBackground(Object... params) {
        String urlString = (String) params[0];
        JSONObject jsonParam = (JSONObject) params[1];
        RequestMethod requestMethod = (RequestMethod) params[2];
        String token = (String) params[3];
        String response = "";
        try {
            URL url = new URL(urlString);
            HttpURLConnection con = null;
            switch (requestMethod) {
                case GET:
                    con = getConnectorWithTypeGET(url, token);
                    break;
                case POST:
                    con = getConnectorWithTypePOST(url, jsonParam, token);
                    break;
            }
            con.connect();

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

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected abstract void onPostExecute(String resultString);

    private HttpURLConnection getConnectorWithTypeGET(URL url, String token) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url
                .openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty(ServerAPI.paramAuthToken, token);
        return con;
    }

    private HttpURLConnection getConnectorWithTypePOST(URL url, JSONObject jsonParam, String token) throws IOException {
        HttpURLConnection con;
        con = (HttpURLConnection) url
                .openConnection();
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty(ServerAPI.paramAuthToken, token);
        DataOutputStream printout = new DataOutputStream(con.getOutputStream());
        printout.writeBytes(jsonParam.toString());
        printout.flush();
        printout.close();

        return con;
    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
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