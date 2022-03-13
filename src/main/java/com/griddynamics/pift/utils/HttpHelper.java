package com.griddynamics.pift.utils;

import com.sun.jndi.toolkit.url.Uri;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class HttpHelper {

    private final String domain;
    private final boolean useSSL;
    private final int port;

    public HttpHelper(String domain, int port, boolean useSSL) {
        this.domain = domain;
        this.useSSL = useSSL;
        this.port = port;
    }

    /**
     * Make POST request with json body.
     */
    public String post(String method, String requestBodyJson) {
        URL url = getUrl(method);
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = requestBodyJson.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            return response(con);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Make GET request with json body.
     */
    public String get(String method, Map<String, String> params) {
        try {
            if (params.size() > 0) {
                method = method + "?" + getParamsString(params);
            }
            URL url = getUrl(method);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            return response(con);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private String response(HttpURLConnection con) throws IOException {
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        Reader streamReader;
        if (con.getResponseCode() > 299) {
            streamReader = new InputStreamReader(con.getErrorStream());
        } else {
            streamReader = new InputStreamReader(con.getInputStream());
        }
        BufferedReader in = new BufferedReader(streamReader);
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        return content.toString();
    }

    private URL getUrl(String method) {
        try {
            return new URL(getProtocol(), domain, port, method);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Illegal url for method: " + method, e);
        }
    }

    private static String getParamsString(Map<String, String> params) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }

    private String getProtocol() {
        return useSSL ? "https" : "http";
    }

}
