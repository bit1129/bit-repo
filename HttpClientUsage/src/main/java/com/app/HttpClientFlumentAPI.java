package com.app;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.Collection;


public class HttpClientFlumentAPI {
    private static int defaultConnectionTimeout = 10 * 1000;
    private static int defaultSocketTimeout = 10 * 1000;
    private static int connectionRequestTimeout = 10 * 1000;

    private static int defaultMaxRouteConnections = 128;
    private static int defaultMaxTotalConnections = 1024;

    private static int defaultMaxHeaderCount = 200;
    private static int defaultMaxLineLength = 2000;

    private static String Charset = "utf-8";

    private String proxyHost = null;
    private String proxyPort = null;

    private PoolingHttpClientConnectionManager cm;

    private final static HttpClientFlumentAPI httpClientConnectionManager = new HttpClientFlumentAPI();

    private CloseableHttpClient HTTP_CLIENT = null;


    private HttpClientFlumentAPI() {
        cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(defaultMaxTotalConnections);
        cm.setDefaultMaxPerRoute(defaultMaxRouteConnections);
        HTTP_CLIENT = createHttpClientSingleton();
    }

    private CloseableHttpClient createHttpClientSingleton() {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();

        ///If following code(set pool connection manager) is commented out, the response time will make sense
//        httpClientBuilder.setConnectionManager(cm);

        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                .setConnectTimeout(defaultConnectionTimeout)
                .setSocketTimeout(defaultSocketTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setExpectContinueEnabled(false)
                .setStaleConnectionCheckEnabled(true);
        SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();

        MessageConstraints messageConstraints = MessageConstraints.custom().setMaxHeaderCount(defaultMaxHeaderCount).setMaxLineLength(defaultMaxLineLength).build();

        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE)
                .setCharset(Consts.UTF_8)
                .setMessageConstraints(messageConstraints).build();

        Collection<BasicHeader> collection = new ArrayList<BasicHeader>();
        collection.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3"));
        collection.add(new BasicHeader("Accept-Language", "zh-cn,zh,en-US,en;q=0.5"));
        collection.add(new BasicHeader("Accept-Charset", Charset));
        collection.add(new BasicHeader("Accept-Encoding", "gzip"));

        httpClientBuilder.setDefaultRequestConfig(requestConfigBuilder.build());
        httpClientBuilder.setDefaultSocketConfig(socketConfig);
        httpClientBuilder.setDefaultConnectionConfig(connectionConfig);
        httpClientBuilder.setDefaultHeaders(collection);

        return httpClientBuilder.build();
    }

    public String execute(String url) {
        String body = "";
        try {
            Request.Get(url)
                    .connectTimeout(10*1000)
                    .socketTimeout(10*1000)
                    .execute().returnContent().asString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body;
    }

    public static HttpClientFlumentAPI getHttpClientConnectionManagerInstance() {
        return httpClientConnectionManager;
    }

}
