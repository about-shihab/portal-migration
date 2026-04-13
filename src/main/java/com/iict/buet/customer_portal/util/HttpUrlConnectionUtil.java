package com.iict.buet.customer_portal.util;

import com.google.gson.stream.JsonReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class HttpUrlConnectionUtil {
    private static final Logger logger = LogManager.getLogger(HttpUrlConnectionUtil.class.getName());

    private HttpUrlConnectionUtil(){}

    public static HttpURLConnection getHttpURLConnection(String url, String method, StringBuilder postDataBuilder, Map<String, String> requestPropertyMap) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(method);
        connection.setReadTimeout(7000);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        for (Map.Entry<String, String> entry : requestPropertyMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            connection.setRequestProperty(key, value);
        }
        if(postDataBuilder != null && !postDataBuilder.equals("")){
            byte[] postData = postDataBuilder.toString().getBytes("UTF-8");
            connection.getOutputStream().write(postData);
        }
        connection.connect();
        return connection;
    }

    public static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    public static JsonReader getReader(HttpURLConnection connection) throws IOException {
        JsonReader reader = new JsonReader(new StringReader(getOkContent(connection)));
        reader.setLenient(true);
        return reader;
    }

    public static String getOkContent(HttpURLConnection connection) throws IOException {
        return getStringToInputSteam(connection.getInputStream());
    }

    public static String getErrorContent(HttpURLConnection connection) throws IOException {
        return getStringToInputSteam(connection.getErrorStream());
    }
     private static String getStringToInputSteam(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
        String line;
        StringBuilder sb = new StringBuilder("");
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    public static String getContent(String url, String method, String basicAuth) {
        Map<String, String> params = new HashMap<>();
        params.put("contentType", "application/x-www-form-urlencoded");
        params.put("Authorization", basicAuth);
        HttpURLConnection connection = null;
        try {
            connection = HttpUrlConnectionUtil.getHttpURLConnection(url, method, null, params);
            return HttpUrlConnectionUtil.getOkContent(connection);
        } catch (Exception e) {
            try {
                return HttpUrlConnectionUtil.getErrorContent(connection);
            } catch (IOException ioException) {
                logger.error(e.getMessage());
                return null;
            }
        }
    }


}