package com.app;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

//The class is not used, but just to demonstrate how to configure MultiThreadedHttpConnectionManager
public class HttpClientUtilV2 {
    private  HttpClient httpClient;
    private static HttpClientUtilV2 httpClientUtil = new HttpClientUtilV2();
    private HttpClientUtilV2() {
        MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        params.setConnectionTimeout(10*000);
        params.setSoTimeout(10*000);
        params.setMaxTotalConnections(500);
        params.setDefaultMaxConnectionsPerHost(500);
        params.setStaleCheckingEnabled(true);
        connectionManager.setParams(params);
        httpClient = new HttpClient(connectionManager);

    }
    public static HttpClientUtilV2 getInstance() {
        return httpClientUtil;
    }

    public String get(String url) throws IOException{
        GetMethod pm = new GetMethod(url);
        try {
            httpClient.executeMethod(pm);
            String response =  responseBodyAsString(pm);
            return response;
        } finally {
            pm.releaseConnection();
        }
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
}
