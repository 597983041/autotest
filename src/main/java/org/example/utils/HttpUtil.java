package org.example.utils;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.config.*;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.example.exception.HttpRequestException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http链接池，支持https
 */
@Slf4j
public class HttpUtil {

    private final static Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    private final static String encoding = "UTF-8";

    private final static int connectTimeout = 10000;

    private static CloseableHttpClient httpClient = null;

    private static TrustManager manager = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    };

    private static SSLConnectionSocketFactory socketFactory;

    private static void enableSSL() {
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{manager}, null);
            socketFactory = new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            logger.error(e.getMessage());
        }
    }

    static {
        try {
            enableSSL();
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("https", socketFactory).register("http", PlainConnectionSocketFactory.INSTANCE).build();

            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            httpClient = HttpClients.custom().setConnectionManager(connManager).build();
            // Create socket configuration
            SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
            connManager.setDefaultSocketConfig(socketConfig);
            // Create message constraints
            MessageConstraints messageConstraints = MessageConstraints.custom().setMaxHeaderCount(200).setMaxLineLength(2000).build();
            // Create connection configuration
            ConnectionConfig connectionConfig = ConnectionConfig.custom().setMalformedInputAction(CodingErrorAction.IGNORE).setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8).setMessageConstraints(messageConstraints).build();
            connManager.setDefaultConnectionConfig(connectionConfig);
            connManager.setMaxTotal(200);
            connManager.setDefaultMaxPerRoute(100);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    public static String sendPostRequest(String url, String body) throws HttpRequestException {
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        return sendPostRequest(url, header, body);
    }

    public static String sendPostRequest(String url, Map<String, String> header, String body) throws HttpRequestException {
        HttpPost http = new HttpPost(url);
        try {
            initWithStringEntity(http, header, body);
            logger.debug("[HttpUtils post] begin invoke url:" + url + " , params:" + body);
            return execute(http);
        } catch (HttpRequestException e) {
            logger.error("Exception", ExceptionUtils.getStackTrace(e));
            throw e;//抛出异常
        } finally {
            http.releaseConnection();
        }
    }

    public static String sendPostRequestWithForm(String url, Map<String, String> header, Map<String, String> param) throws HttpRequestException {
        HttpPost http = new HttpPost(url);
        try {
            initWithFormEntity(http, header, param);
            logger.debug("[HttpUtils post] begin invoke url:" + url + " , params:" + param);
            return execute(http);
        } catch (HttpRequestException e) {
            logger.error("Exception", ExceptionUtils.getMessage(e));
            throw e;//抛出异常
        } catch (UnsupportedEncodingException e) {
            logger.error("Exception", ExceptionUtils.getMessage(e));
            throw new HttpRequestException(HttpRequestException.FAIL, e.getMessage());//抛出异常
        } finally {
            http.releaseConnection();
        }
    }

    public static String sendPutRequest(String url, Map<String, String> authHeader, String body) throws HttpRequestException {
        HttpPut http = new HttpPut(url);
        try {
            initWithStringEntity(http, authHeader, body);
            logger.debug("[HttpUtils put] begin invoke url:" + url + " , params:" + body);
            return execute(http);
        } catch (HttpRequestException e) {
            logger.error("Exception", ExceptionUtils.getMessage(e));
            throw e;//跑出异常
        } finally {
            http.releaseConnection();
        }
    }

    public static String sendGetRequest(String url, Map<String, String> header, Map<String, String> params) throws HttpRequestException {
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        int i = 0;
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (i == 0 && !url.contains("?")) {
                    sb.append("?");
                } else {
                    sb.append("&");
                }
                sb.append(entry.getKey());
                sb.append("=");
                String value = entry.getValue();
                try {
                    sb.append(URLEncoder.encode(value, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new HttpRequestException(HttpRequestException.EXCEPTION, e.getMessage());
                }
                i++;
            }
        }
        logger.info("[HttpUtils Get] begin invoke:" + sb.toString());
        HttpGet get = new HttpGet(sb.toString());
        if (header != null) {
            for (String key : header.keySet()) {
                get.setHeader(key, header.get(key));
            }
        }
        setConfig(get);
        return execute(get);
    }

    public static String sendGetRequestWithMap(String url, Map<String, String> header, Map<String, Object> params) throws HttpRequestException {
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        int i = 0;
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (i == 0 && !url.contains("?")) {
                    sb.append("?");
                } else {
                    sb.append("&");
                }
                sb.append(entry.getKey());
                sb.append("=");
                String value = entry.getValue().toString();
                try {
                    sb.append(URLEncoder.encode(value, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new HttpRequestException(HttpRequestException.EXCEPTION, e.getMessage());
                }
                i++;
            }
        }
        logger.info("[HttpUtils Get] begin invoke:" + sb.toString());
        HttpGet get = new HttpGet(sb.toString());
        if (header != null) {
            for (String key : header.keySet()) {
                get.setHeader(key, header.get(key));
            }
        }
        setConfig(get);
        return execute(get);
    }

    private static void initWithFormEntity(HttpEntityEnclosingRequestBase http, Map<String, String> header, Map<String, String> param) throws HttpRequestException, UnsupportedEncodingException {
        boolean hasSetContentType = processContentType(http, header);
        if (!hasSetContentType) {
            http.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
        }
        setConfig(http);

        List<NameValuePair> params = Lists.newArrayList();
        //添加参数
        if (param != null) {
            for (Map.Entry<String, String> entry : param.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, encoding);
        formEntity.setContentType(ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
        http.setEntity(formEntity);
    }

    /**
     * 本方法供post与put使用
     */
    private static void initWithStringEntity(HttpEntityEnclosingRequestBase http, Map<String, String> header, String body) throws HttpRequestException {
        boolean hasSetContentType = processContentType(http, header);
        //使用 Content-Type:application/json 的header
        if (!hasSetContentType) {
            http.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        }
        setConfig(http);

        StringEntity stringEntity = new StringEntity(body, encoding);
//        stringEntity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        http.setEntity(stringEntity);
    }

    private static boolean processContentType(HttpEntityEnclosingRequestBase http, Map<String, String> header) {
        boolean hasSetContentType = false;
        if (header != null) {
            for (String key : header.keySet()) {
                http.setHeader(key, header.get(key));
                if (HttpHeaders.CONTENT_TYPE.equals(key)) {
                    hasSetContentType = true;
                }
            }
        }
        return hasSetContentType;
    }

    /**
     * 本方法供get,post与put也会用到
     */
    private static void setConfig(HttpRequestBase http) {
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(HttpUtil.connectTimeout)
                .setConnectTimeout(HttpUtil.connectTimeout).setConnectionRequestTimeout(HttpUtil.connectTimeout).setExpectContinueEnabled(false).build();
        http.setConfig(requestConfig);
    }

    private static String execute(HttpUriRequest request) throws HttpRequestException {
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            try {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    throw new HttpRequestException(statusCode, response.getStatusLine() + response.getStatusLine().getReasonPhrase());
                } else if (entity != null) {
                    String str = EntityUtils.toString(entity, encoding);
                    logger.debug("[HttpUtils http]Debug response." + "response string :" + str);
                    return str;
                } else {
                    throw new HttpRequestException(HttpRequestException.EXCEPTION, "NO Response dto get");
                }
            } finally {
                if (entity != null) {
                    entity.getContent().close();
                }
            }
        } catch (IOException e) {
            throw new HttpRequestException(HttpRequestException.EXCEPTION, e.getMessage());
        }
        //ignore
    }

    public static String sendFormPostWithRetry(String url, Map<String, String> header, Map<String, String> param,
                                               int maxTryTimes) throws HttpRequestException {
        int tryTimes = maxTryTimes;
        while (tryTimes > 0) {
            tryTimes--;
            try {
                return sendPostRequestWithForm(url, header, param);
            } catch (HttpRequestException e) {
                if (tryTimes <= 0) {
                    throw e;
                }
            }
        }
        return "";//never arrive
    }
}
