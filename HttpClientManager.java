package com.app;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.pool.PoolStats;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.Collection;


public class HttpClientManager {
	private static Logger logger = LoggerFactory.getLogger(HttpClientManager.class);
    private static int defaultConnectionTimeout = 10*1000;
    private static int defaultSocketTimeout = 10*1000;
    private static int connectionRequestTimeout = 10*1000;
    
    private static int defaultMaxRouteConnections = 128;
    private static int defaultMaxTotalConnections = 1024;
    
    private static int defaultMaxHeaderCount = 200;
    private static int defaultMaxLineLength = 2000;
    
    private static String Charset = "utf-8";
    
    private String proxyHost = null;
    private String proxyPort = null;
    
    private PoolingHttpClientConnectionManager cm;
    
    private final static HttpClientManager httpClientConnectionManager = new HttpClientManager();


    private HttpClientManager() {
		logger.info("HttpClientManager initial!");
		cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(defaultMaxTotalConnections);
		cm.setDefaultMaxPerRoute(defaultMaxRouteConnections);
		/*
        new Thread(){
            @Override
            public void run() {
                while (true) {
                    PoolStats stats = cm.getTotalStats();
                    System.out.println("available: " + stats.getAvailable() + ",leased: " + stats.getLeased() + ", max: " + stats.getMax() + ",pending: " + stats.getPending());
                    try {
                        sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
		*/
	}
	
	private CloseableHttpClient getHttpclient(){
	
		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		httpClientBuilder.setConnectionManager(cm);

		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
				.setConnectTimeout(defaultConnectionTimeout)
				.setSocketTimeout(defaultSocketTimeout)
				.setConnectionRequestTimeout(connectionRequestTimeout)
				.setExpectContinueEnabled(false)          
				.setStaleConnectionCheckEnabled(true);

		if(StringUtils.isNotBlank(proxyHost) && StringUtils.isNotBlank(proxyPort)){
			try{
        		logger.info("using proxy, proxyHost:{}, proxyPort:{}", proxyHost, proxyPort);
        		int proxyPortInt = Integer.parseInt(proxyPort);
        		requestConfigBuilder.setProxy(new HttpHost(proxyHost, proxyPortInt));
        	} catch(Exception e){
        		logger.error("parseInt proxyPort:{}", proxyPort, e);
        	}
		}
		
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

        return  httpClientBuilder.build();
        
	}

	public String execute(HttpGet httpGet) {
		String body = "";
		try{
			CloseableHttpClient httpclient = this.getHttpclient();
			CloseableHttpResponse response = httpclient.execute(httpGet);
			int status = response.getStatusLine().getStatusCode();
			try {
				if (status == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						Header header = (Header) entity.getContentEncoding();
						if(header != null && "gzip".equals(header.getValue())){
							body = EntityUtils.toString(new GzipDecompressingEntity(entity), Charset);
						} else {
							body = EntityUtils.toString(entity, Charset);
						}
					}
				} else {
					logger.error("[httpClientManager] [fail] [httpGet:{}] [status:{}]", httpGet, status);
				}
			} finally {
				response.close();
			}
		} catch(Exception e) {
			logger.error("[module:httpClientManager] [action:execute] [httpGet:{}] [error:{}] ", httpGet, e.getMessage(), e);
		}
		return body;
	}

	public static HttpClientManager getHttpClientConnectionManagerInstance(){
		return httpClientConnectionManager;
	}

}
