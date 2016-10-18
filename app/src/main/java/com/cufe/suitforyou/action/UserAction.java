package com.cufe.suitforyou.action;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.cufe.suitforyou.commons.AppConfig;
import com.cufe.suitforyou.commons.LoginStatus;
import com.cufe.suitforyou.commons.ScreenManager;
import com.cufe.suitforyou.http.SimpleHttpCallback;
import com.cufe.suitforyou.http.SimpleHttpURLConnection;
import com.cufe.suitforyou.utils.MyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by Victor on 2016-09-01.
 */
public class UserAction {
    private final String loginURI = AppConfig.LOGIN_URL + "login";
    private final String userInfoURI = AppConfig.LOGIN_URL + "userinfo";

    /**
     * 用户登录动作
     *
     * @param context      操作上下文
     * @param jos          JSONObject数组
     *                     ${jos[0]} HTTP发送对象
     *                     ${jos[1]} 用户账号密码原始对象
     * @param loginHandler 登录动作后的回调
     */
    public void login(final Context context, final JSONObject[] jos, final Handler loginHandler, final boolean welcome) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SimpleHttpURLConnection connection = new SimpleHttpURLConnection();
                connection.setMethod("POST");
                connection.send(loginURI, jos[0], new SimpleHttpCallback() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            /**
                             * 状态码${status}
                             * 0 登录成功
                             * 1 登录失败（服务器）
                             * 2 请求超时（服务器）
                             * 3 用户名密码错误
                             */
                            int statusCode = Integer.parseInt(jsonObject.getString("status"));
                            if (statusCode == 0) {
                                /**
                                 * 一次登录成功对应三个操作
                                 * 1.记录登录状态和token
                                 * 2.记住用户名和密码
                                 * 3.获取用户基本信息（欢迎登陆不需要获取）
                                 */
                                LoginStatus loginStatus = LoginStatus.getInstance(context);
                                loginStatus.logForLogin(jsonObject);
                                loginStatus.logUserAccount(jos[1]);
                                if (!welcome)
                                    getUserInfo(loginStatus.getToken(), loginHandler);
                                else
                                    loginHandler.sendEmptyMessage(0);
                            } else {
                                loginHandler.sendEmptyMessage(statusCode);
                            }
                        } catch (JSONException e) {
                            loginHandler.sendEmptyMessage(-1);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSuccess(InputStream stream) {

                    }

                    @Override
                    public void onFailure(String response) {
                        loginHandler.sendEmptyMessage(-1);
                    }

                    @Override
                    public void onException(Exception e) {
                        loginHandler.sendEmptyMessage(-1);
                    }

                    @Override
                    public void onFinally() {

                    }
                });
            }
        }).start();
    }

    /**
     * 查询用户信息动作，一般为用户登录后进行查询
     *
     * @param token 用户登陆成功后返回的Token字符串
     */
    private void getUserInfo(final String token, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SimpleHttpURLConnection connection = new SimpleHttpURLConnection();
                connection.setMethod("GET");
                connection.send(userInfoURI, token, new SimpleHttpCallback() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject jo = new JSONObject(response).getJSONObject("data");
                            LoginStatus.getInstance(ScreenManager.getInstance().currentActivity().getBaseContext()).logUserInfo(jo);
                            handler.sendEmptyMessage(0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSuccess(InputStream stream) {

                    }

                    @Override
                    public void onFailure(String response) {
                        System.out.println("Get User Info onFailure: " + response);
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

    /**
     * 修改用户信息动作
     *
     * @param jo
     */
    public void updateUserInfo(final JSONObject jo, final String token, final Handler updateHandler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SimpleHttpURLConnection connection = new SimpleHttpURLConnection();
                connection.setMethod("PUT");
                connection.send(userInfoURI, jo, token, new SimpleHttpCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Bundle bundle = new Bundle();
                        bundle.putString("jo", jo.toString());
                        Message message = new Message();
                        message.setData(bundle);
                        updateHandler.sendMessage(message);
                    }

                    @Override
                    public void onSuccess(InputStream stream) {

                    }

                    @Override
                    public void onFailure(String response) {
                        updateHandler.sendEmptyMessage(0);
                    }

                    @Override
                    public void onException(Exception e) {
                        updateHandler.sendEmptyMessage(-1);
                        e.printStackTrace();
                    }

                    @Override
                    public void onFinally() {
                    }
                });
            }
        }).start();
    }

    /**
     * 搜索商品，返回列表
     */
    public void searchItems(final String key, final int page, final int size, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = AppConfig.ITEM_URL +
                        MyUtil.parseParameters(new HashMap<String, Object>() {{
                            put("key", key);
                            put("page", page);
                            put("size", size);
                        }});
                SimpleHttpURLConnection connection = new SimpleHttpURLConnection();
                connection.setMethod("GET");
                connection.setTimeout(5000);
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
                        e.printStackTrace();
                    }

                    @Override
                    public void onFinally() {

                    }
                });
            }
        }).start();
    }

    /**
     * 获取单品详细
     */
    public void searchDetailItem(final int clothesId, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = AppConfig.ITEM_URL + '/' + clothesId;
                SimpleHttpURLConnection connection = new SimpleHttpURLConnection();
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

    /**
     * 获取用户购物车商品skuID列表
     */
    public void searchCartItemsSkuIds(final String token, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = AppConfig.SHOPPING_CART_URL + "/sku";
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
                    }

                    @Override
                    public void onFinally() {

                    }
                });
            }
        }).start();
    }

    /**
     * 获取用户购物车列表
     */
    public void searchCartItems(final String token, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SimpleHttpURLConnection connection = new SimpleHttpURLConnection();
                connection.setMethod("GET");
                connection.send(AppConfig.SHOPPING_CART_URL, token, new SimpleHttpCallback() {
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

    /**
     * 添加商品进购物车
     *
     * @param jo {
     *           "skuId":1,
     *           "clothesId":1,
     *           "number":2
     *           }
     */
    public void addCartItem(final JSONObject jo, final String token, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SimpleHttpURLConnection connection = new SimpleHttpURLConnection();
                connection.setMethod("POST");
                connection.send(AppConfig.SHOPPING_CART_URL, jo, token, new SimpleHttpCallback() {
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
                    }

                    @Override
                    public void onFinally() {

                    }
                });
            }
        }).start();
    }

    /**
     * 更新购物车商品
     *
     * @param id 购物车ID
     * @param jo {
     *           "number":2
     *           }
     */
    public void updateCartItem(final int id, final JSONObject jo, final String token, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = AppConfig.SHOPPING_CART_URL + "/" + id;
                SimpleHttpURLConnection connection = new SimpleHttpURLConnection();
                connection.setMethod("PUT");
                connection.send(url, jo, token, new SimpleHttpCallback() {
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
                    }

                    @Override
                    public void onFinally() {

                    }
                });
            }
        }).start();
    }

    /**
     * 删除购物车商品
     *
     * @param id 购物车ID
     */
    public void deleteCartItem(final int id, final String token, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = AppConfig.SHOPPING_CART_URL + "/" + id;
                SimpleHttpURLConnection connection = new SimpleHttpURLConnection();
                connection.setMethod("DELETE");
                connection.send(url, token, new SimpleHttpCallback() {
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
                    }

                    @Override
                    public void onFinally() {

                    }
                });
            }
        }).start();
    }

    public void getAddress(final String token, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = AppConfig.ADDRESS_URL;
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
                    }

                    @Override
                    public void onFinally() {

                    }
                });
            }
        }).start();
    }

    public void getFilterItems(final String token, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = AppConfig.FILTER_URL;
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


}
