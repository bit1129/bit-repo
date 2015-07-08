package com.app;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


///Use HttpClient 3.x
public class HttpClientUtil {

    private static final HttpClientUtil INSTANCE = new HttpClientUtil();

    private static final int TIMEOUT = 10 * 1000; //10s

    private HttpClientUtil() {
    }

    ///Actually, this class is more like a static class than Singleton
    public static HttpClientUtil getInstance() {
        return INSTANCE;
    }

    //brand new http client per request
    private HttpClient newHttpClient() {
        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT);
        client.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT);
        return client;
    }

    public static String responseBodyAsString(HttpMethod method) throws IOException {
        BufferedReader br = null;
        String lsr = System.getProperty("line.separator");
        try {
            InputStream in = method.getResponseBodyAsStream();
            br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append(lsr);
            }
            return sb.toString();
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    public String get(String url) throws IOException {
        GetMethod pm = new GetMethod(url);
        pm.setRequestHeader("Connection", "close");
        HttpClient client = newHttpClient();
        try {
            client.executeMethod(pm);
            String response = responseBodyAsString(pm);
            return response;
        } finally {
            pm.releaseConnection();
        }
    }
}
