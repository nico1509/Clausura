package de.nico_assfalg.apps.android.clausura.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class UpdateHelper {

    public static String getUpdateVersion(String url, String thisVersion) throws IOException {
        URL versionUrl;
        try {
            versionUrl = new URL(url + thisVersion);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new IOException("ERROR: Malformed URL...");
        }

        HttpsURLConnection connection = (HttpsURLConnection) versionUrl.openConnection();

        // Creating an SSL Connection
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new IOException("ERROR: TLS is not an algorithm...");
        }
        try {
            sslContext.init(null, null, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
            throw new IOException("ERROR: Key Management Exception");
        }

        // Set Timeout and Method
        connection.setReadTimeout(7000);
        connection.setConnectTimeout(7000);
        connection.setRequestMethod("GET");
        //connection.setDoInput(false);

        // Connect and Receive
        connection.connect();
        String result;
        InputStream is = connection.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        if ((result = in.readLine()) != null) {
            return result;
        }
        return "-1";
    }
}
