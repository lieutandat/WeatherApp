package com.dat.stormy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;
import android.app.Activity;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import java.lang.String;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Phong on 04/07/2015.
 */
public class GetXml {
    public String getXmlFromUrl(String urlString) {
        String responseBody = "Wrong";
            DefaultHttpClient http = new DefaultHttpClient();
            HttpGet httpMethod = new HttpGet();
            try {
                httpMethod.setURI(new URI(urlString));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            HttpResponse response = null;
            try {
                response = http.execute(httpMethod);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int responseCode = response.getStatusLine().getStatusCode();
            switch (responseCode) {
                case 200:
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        try {
                            responseBody = EntityUtils.toString(entity);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        return responseBody;
    }
}
