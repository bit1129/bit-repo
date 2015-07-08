package com.app;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;

import java.util.Map;

///HttpClient 4.3.6
public class HttpUtils {

    //HTTP GET
    public static String httpInvoke(String httpUrl, Map<String, Object> parameters, RequestConfig config) {
        HttpClientManager httpClientConnectionManager = HttpClientManager.getHttpClientConnectionManagerInstance();
        HttpGet httpGet = new HttpGet(httpUrl);
        if (config != null) {
            httpGet.setConfig(config);
        }

        String result = httpClientConnectionManager.execute(httpGet);
        return result;
    }

}
