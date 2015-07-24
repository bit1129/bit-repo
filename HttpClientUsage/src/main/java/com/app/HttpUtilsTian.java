/**
 * 
 */
package com.app;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author petertian
 *
 */
public class HttpUtilsTian {

	private static final Logger logger = Logger.getLogger(HttpUtils.class);
	private static final RequestConfig defaultRequestConfig = 
			RequestConfig.custom()
			.setSocketTimeout(30000)
			.setConnectTimeout(15000)
			.setConnectionRequestTimeout(10000)
			.build();
	
	public static String get(final String url) {
		
		CloseableHttpClient httpclient = HttpClients.custom()
				.setDefaultRequestConfig(defaultRequestConfig)
				.build();
		
        try {
            HttpGet httpget = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig).build();
            httpget.setConfig(requestConfig);

            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            	
                public String handleResponse(final HttpResponse response) throws IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                    	logger.warn("Unexpected response status: " + status + " url="+url);
                    	return null;
                    }
                }

            };
            String responseBody = httpclient.execute(httpget, responseHandler);
            return responseBody;
            
        }catch(Exception e) {
        	logger.error("HttpGet Failed! url="+url, e);
        	return null;
        }finally {
            try {
				httpclient.close();
			} catch (IOException e) {
                e.printStackTrace();
			}
        }
	}
	
	public static String post(final String url, Map<String, String> map) {
		
		CloseableHttpClient httpclient = HttpClients.custom()
				.setDefaultRequestConfig(defaultRequestConfig)
				.build();
		try {	
        	HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig).build();
            httpPost.setConfig(requestConfig);
            
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            for(Map.Entry<String, String> entry : map.entrySet()) {
        		nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                public String handleResponse(final HttpResponse response) throws IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        logger.warn("Unexpected response status: " + status + " url="+url);
                        return null;
                    }
                }

            };
            String responseBody = httpclient.execute(httpPost, responseHandler);
            return responseBody;
            
        } catch(Exception e){
        	logger.error("HttpPost Failed! url="+url, e);
        	return null;
        }finally {
            try {
				httpclient.close();
			} catch (IOException e) {
			}
        }
	}

}
