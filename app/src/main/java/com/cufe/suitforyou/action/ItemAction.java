package com.cufe.suitforyou.action;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.cufe.suitforyou.commons.AppConfig;
import com.cufe.suitforyou.http.SimpleHttpCallback;
import com.cufe.suitforyou.http.SimpleHttpURLConnection;

import java.io.InputStream;

/**
 * Created by Victor on 2016-09-16.
 */
public class ItemAction {

    public void getComments(final int id, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = AppConfig.COMMENT_URL + "?clothesId=" + id;
                SimpleHttpURLConnection connection = new SimpleHttpURLConnection();
                connection.setMethod("GET");
                connection.archiveInfo(url, new SimpleHttpCallback() {
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
                    }

                    @Override
                    public void onFinally() {

                    }
                });
            }
        }).start();
    }

}
