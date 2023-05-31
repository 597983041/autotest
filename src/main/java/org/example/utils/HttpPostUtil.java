package org.example.utils;

import org.apache.commons.codec.CharEncoding;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class HttpPostUtil {
    private static final HttpClient httpClient = HttpClientBuilder.create().build();
    private static final Logger logger = LoggerFactory.getLogger(HttpPostUtil.class);

    /**
     * 发送POST请求
     *
     * @param url
     * @return
     */
    public static String post(String url, Map<String, String> header, String body) {
        try {
            HttpPost request = new HttpPost(url);
            if (header != null) {
                for (String key : header.keySet()) {
                    request.setHeader(key, header.get(key));
                }
            }
            request.setEntity(new StringEntity(body));
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                logger.debug("post httpRequest url info:{},response info:{}", url, response);
                return EntityUtils.toString(entity, CharEncoding.UTF_8);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
