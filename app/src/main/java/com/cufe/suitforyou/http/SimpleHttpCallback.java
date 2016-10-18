package com.cufe.suitforyou.http;

import java.io.InputStream;

/**
 * Created by Victor on 2016-08-31.
 */
public interface SimpleHttpCallback {

    /**
     * 连接成功的Callback
     *
     * @param response
     */
    void onSuccess(String response);

    /**
     * 连接成功的Callback
     *
     * @param stream
     */
    void onSuccess(InputStream stream);

    /**
     * 连接失败的Callback
     *
     * @param response
     */
    void onFailure(String response);

    /**
     * 连接出现异常的Callback
     */
    void onException(Exception e);

    /**
     * 该次请求最后的执行
     */
    void onFinally();
}
