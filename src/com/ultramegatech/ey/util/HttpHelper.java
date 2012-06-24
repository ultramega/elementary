/*
 * The MIT License (MIT)
 * Copyright © 2012 Steve Guidetti
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.ultramegatech.ey.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Helper for communicating with the web server for data updates.
 * 
 * @author Steve Guidetti
 */
public class HttpHelper {
    private static final String TAG = "HttpHelper";
    
    /* Base URL */
    private static final String URL = "http://ey.ultramegatech.com/";
    
    /* Output buffer */
    private static byte[] sBuffer = new byte[512];
    
    /**
     * Test for internet connectivity.
     * 
     * @param context
     * @return True if there is a connection available
     */
    public static boolean isConnected(Context context) {
        final ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }
    
    /**
     * Get the current version of element data on the remote server.
     * 
     * @return The version number, or -1 on failure
     */
    public static int getVersion() {
        final String response = makeRequest("version");
        if(response != null) {
            return Integer.valueOf(response);
        }
        return -1;
    }
    
    /**
     * Download element data from the remote server.
     * 
     * @param version Version of data to request
     * @return Raw response body from server
     */
    public static String getElementData(int version) {
        return makeRequest(String.format("elements_v%04d.json", version));
    }
    
    /**
     * Make an arbitrary GET request to the remote server.
     * 
     * @param path Path to request
     * @return The response body
     */
    private static synchronized String makeRequest(String path) {
        try {
            final URL url = new URL(URL + path);
            final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            try {
                conn.setDoInput(true);
                conn.setUseCaches(true);
                conn.setConnectTimeout(30000);
                
                final int code = conn.getResponseCode();
                if(code != HttpURLConnection.HTTP_OK) {
                    return null;
                }
                
                final InputStream inputStream = conn.getInputStream();
                final ByteArrayOutputStream content = new ByteArrayOutputStream();
                try {
                    int readBytes = 0;
                    while((readBytes = inputStream.read(sBuffer)) != -1) {
                        content.write(sBuffer, 0, readBytes);
                    }
                    return new String(content.toByteArray());
                } finally {
                    inputStream.close();
                    content.close();
                }
            } catch(IOException e) {
                Log.d(TAG, e.getMessage(), e);
            } finally {
                conn.disconnect();
            }
        } catch(IOException e) {
            Log.d(TAG, e.getMessage(), e);
        }
        return null;
    }
}