package com.cufe.suitforyou.action;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.cufe.suitforyou.commons.AppConfig;
import com.cufe.suitforyou.http.SimpleHttpCallback;
import com.cufe.suitforyou.http.SimpleHttpURLConnection;
import com.cufe.suitforyou.utils.MyUtil;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Victor on 2016-09-17.
 */
public class OrderAction {

    public void sendOrder(final JSONObject jsonObject, final String token, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = AppConfig.ORDER_URL;
                SimpleHttpURLConnection connection = new SimpleHttpURLConnection();
                connection.setMethod("POST");
                connection.send(url, jsonObject, token, new SimpleHttpCallback() {
                    @Override
                    public void onSuccess(String response) {
                        handler.sendEmptyMessage(1);
                    }

                    @Override
                    public void onSuccess(InputStream stream) {

                    }

                    @Override
                    public void onFailure(String response) {
                        Bundle bundle = new Bundle();
                        bundle.putString("response", response);
                        Message message = new Message();
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onException(Exception e) {
                        handler.sendEmptyMessage(-1);
                        e.printStackTrace();
                    }

                    @Override
                    public void onFinally() {

                    }
                });
            }
        }).start();
    }

    public void getOrders(final HashMap<String, Object> parameters, final String token, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = AppConfig.ORDER_URL + MyUtil.parseParameters(parameters);
                SimpleHttpURLConnection connection = new SimpleHttpURLConnection();
                connection.setMethod("GET");
                connection.send(url, token, new SimpleHttpCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Bundle bundle = new Bundle();
                        bundle.putString("response", response);
                        Message message = new Message();
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onSuccess(InputStream stream) {

                    }

                    @Override
                    public void onFailure(String response) {
                        handler.sendEmptyMessage(-1);
                    }

                    @Override
                    public void onException(Exception e) {
                        handler.sendEmptyMessage(-1);
                        e.printStackTrace();
                    }

                    @Override
                    public void onFinally() {

                    }
                });
            }
        }).start();
    }

    public void getOrder(final int id, final String token, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = AppConfig.ORDER_URL + "/" + id;
                SimpleHttpURLConnection connection = new SimpleHttpURLConnection();
                connection.setMethod("GET");
                connection.send(url, token, new SimpleHttpCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Bundle bundle = new Bundle();
                        bundle.putString("response", response);
                        Message message = new Message();
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onSuccess(InputStream stream) {

                    }

                    @Override
                    public void onFailure(String response) {
                        handler.sendEmptyMessage(-1);
                    }

                    @Override
                    public void onException(Exception e) {
                        handler.sendEmptyMessage(-1);
                        e.printStackTrace();
                    }

                    @Override
                    public void onFinally() {

                    }
                });
            }
        }).start();
    }

    public void changeStatus(final int id, final String token, final JSONObject jsonObject, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = String.format(Locale.CHINA, "%s/%d/status", AppConfig.ORDER_URL, id);
                SimpleHttpURLConnection connection = new SimpleHttpURLConnection();
                connection.setMethod("PUT");
                connection.send(url, jsonObject, token, new SimpleHttpCallback() {
                    @Override
                    public void onSuccess(String response) {
                        handler.sendEmptyMessage(1);
                    }

                    @Override
                    public void onSuccess(InputStream stream) {

                    }

                    @Override
                    public void onFailure(String response) {
                        handler.sendEmptyMessage(-1);
                    }

                    @Override
                    public void onException(Exception e) {
                        handler.sendEmptyMessage(-1);
                        e.printStackTrace();
                    }

                    @Override
                    public void onFinally() {

                    }
                });
            }
        }).start();
    }

    public void addComment(final String token, final JSONObject jsonObject, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = AppConfig.COMMENT_URL;
                SimpleHttpURLConnection connection = new SimpleHttpURLConnection();
                connection.setMethod("POST");
                connection.send(url, jsonObject, token, new SimpleHttpCallback() {
                    @Override
                    public void onSuccess(String response) {
                        handler.sendEmptyMessage(1);
                    }

                    @Override
                    public void onSuccess(InputStream stream) {

                    }

                    @Override
                    public void onFailure(String response) {
                        handler.sendEmptyMessage(-1);
                    }

                    @Override
                    public void onException(Exception e) {
                        handler.sendEmptyMessage(-1);
                        e.printStackTrace();
                    }

                    @Override
                    public void onFinally() {

                    }
                });
            }
        }).start();
    }
}
