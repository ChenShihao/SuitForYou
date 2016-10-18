package com.cufe.suitforyou.http;

import android.support.annotation.Nullable;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Victor on 2016-08-31.
 */
public class SimpleHttpURLConnection {

    private int timeout = 5000;
    private String charset = "UTF-8";
    private String method = "GET";

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * 发送HTTP请求获取文件
     *
     * @param uri                访问路径
     * @param simpleHttpCallback HTTP回调函数
     */
    public void archiveFile(String uri, SimpleHttpCallback simpleHttpCallback) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(uri).openConnection();

            int status = connection.getResponseCode();
            InputStream inputStream = connection.getInputStream();
            switch (status) {
                case HttpURLConnection.HTTP_OK:
                    simpleHttpCallback.onSuccess(inputStream);
                    break;
                default:
                    simpleHttpCallback.onFailure(null);
                    break;
            }
            connection.disconnect();
        } catch (Exception e) {
            simpleHttpCallback.onException(e);
            e.printStackTrace();
        } finally {
            simpleHttpCallback.onFinally();
        }
    }

    /**
     * 发送HTTP请求获取信息
     *
     * @param uri                访问路径
     * @param simpleHttpCallback HTTP回调函数
     */
    public void archiveInfo(String uri, SimpleHttpCallback simpleHttpCallback) {
        try {
            HttpURLConnection connection = getConnection(new URL(uri), null);

            int status = connection.getResponseCode();
            String response = getInput(connection.getInputStream());
            switch (status) {
                case HttpURLConnection.HTTP_OK:
                    simpleHttpCallback.onSuccess(response);
                    break;
                default:
                    simpleHttpCallback.onFailure(response);
                    break;
            }
            connection.disconnect();
        } catch (Exception e) {
            simpleHttpCallback.onException(e);
            e.printStackTrace();
        } finally {
            simpleHttpCallback.onFinally();
        }
    }

    /**
     * 发送HTTP请求，仅包括JSON数据对象，如登陆操作
     *
     * @param url                访问路径
     * @param jo                 JSON对象
     * @param simpleHttpCallback HTTP回调函数
     */
    public void send(String url, JSONObject jo, SimpleHttpCallback simpleHttpCallback) {
        try {
            HttpURLConnection connection = getConnection(new URL(url), getJsonConfigMap());
            setOutput(jo, connection);

            int status = connection.getResponseCode();
            String response = getInput(connection.getInputStream());
            switch (status) {
                case HttpURLConnection.HTTP_OK:
                    simpleHttpCallback.onSuccess(response);
                    break;
                default:
                    simpleHttpCallback.onFailure(response);
                    break;
            }
            connection.disconnect();
        } catch (Exception e) {
            simpleHttpCallback.onException(e);
            e.printStackTrace();
        } finally {
            simpleHttpCallback.onFinally();
        }
    }

    /**
     * 发送HTTP请求，仅包括Token数据
     *
     * @param uri                访问路径
     * @param token              用户token
     * @param simpleHttpCallback HTTP回调函数
     */
    public void send(String uri, final String token, SimpleHttpCallback simpleHttpCallback) {
        try {
            HttpURLConnection connection =
                    getConnection(new URL(uri), new HashMap<String, String>() {{
                        put("token", token);
                    }});

            int status = connection.getResponseCode();
            String response = getInput(connection.getInputStream());
            switch (status) {
                case HttpURLConnection.HTTP_OK:
                    simpleHttpCallback.onSuccess(response);
                    break;
                case HttpURLConnection.HTTP_CREATED:
                    simpleHttpCallback.onSuccess(response);
                    break;
                case HttpURLConnection.HTTP_ACCEPTED:
                    simpleHttpCallback.onSuccess(response);
                    break;
                default:
                    simpleHttpCallback.onFailure(response);
                    break;
            }
            connection.disconnect();
        } catch (Exception e) {
            simpleHttpCallback.onException(e);
            e.printStackTrace();
        } finally {
            simpleHttpCallback.onFinally();
        }
    }

    /**
     * 发送HTTP，需携带TOKEN以及JSON数据对象，大部分数据交互使用该函数
     *
     * @param uri                访问路径
     * @param jo                 JSON对象
     * @param token              用户token
     * @param simpleHttpCallback HTTP回调函数
     */
    public void send(String uri, JSONObject jo, String token, SimpleHttpCallback simpleHttpCallback) {
        try {
            Map<String, String> properties = getJsonConfigMap();
            properties.put("token", token);
            HttpURLConnection connection = getConnection(new URL(uri), properties);
            setOutput(jo, connection);

            int statusCode = connection.getResponseCode();
            switch (statusCode) {
                case HttpURLConnection.HTTP_OK:
                    simpleHttpCallback.onSuccess(getInput(connection.getInputStream()));
                    break;
                case HttpURLConnection.HTTP_NO_CONTENT:
                    simpleHttpCallback.onSuccess(getInput(connection.getInputStream()));
                    break;
                case HttpURLConnection.HTTP_CREATED:
                    simpleHttpCallback.onSuccess(getInput(connection.getInputStream()));
                    break;
                case HttpURLConnection.HTTP_ACCEPTED:
                    simpleHttpCallback.onSuccess(getInput(connection.getInputStream()));
                    break;
                default:
                    simpleHttpCallback.onFailure(getInput(connection.getInputStream()));
                    break;
            }
            connection.disconnect();
        } catch (Exception e) {
            simpleHttpCallback.onException(e);
        } finally {
            simpleHttpCallback.onFinally();
        }
    }

    private Map<String, String> getJsonConfigMap() {
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json;charset=" + charset);
        map.put("Accept", "application/json");
        return map;
    }

    private HttpURLConnection getConnection(URL url, @Nullable Map<String, String> properties) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if (properties != null) {
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        connection.setRequestMethod(method);
        connection.setConnectTimeout(timeout);
        return connection;
    }

    private void setOutput(JSONObject jo, HttpURLConnection connection) throws IOException {
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(jo.toString().getBytes());
        outputStream.flush();
        outputStream.close();
    }

    private String getInput(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, charset));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        if ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }
}
