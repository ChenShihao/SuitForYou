package com.cufe.suitforyou.commons;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.VectorDrawable;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import com.cufe.suitforyou.R;
import com.cufe.suitforyou.action.UserAction;
import com.cufe.suitforyou.activity.LoginActivity;
import com.cufe.suitforyou.model.User;
import com.cufe.suitforyou.utils.AESUtil;
import com.cufe.suitforyou.utils.DeviceInfoUtil;
import com.cufe.suitforyou.utils.ImageUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Victor on 2016-08-31.
 */
public class LoginStatus {

    private static User user;
    private static LoginStatus instance;
    private static SharedPreferences sharedPreferences;
    private static final String INFO_KEY = "LOGIN_INFO";
    private static final String IS_LOGIN_FLAG_KEY = "isLogin";
    private static final String LOGIN_TOKEN_KEY = "token";
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";

    public LoginStatus(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(INFO_KEY, Context.MODE_PRIVATE);
    }

    public static LoginStatus getInstance(Context context) {
        if (instance == null) {
            synchronized (LoginStatus.class) {
                if (instance == null)
                    instance = new LoginStatus(context);
            }
        }
        return instance;
    }

    /**
     * 需要用户登录的操作在页面跳转前进行登录状态验证
     */
    public boolean actionWithUserLogin(Context context, final Class<?> cls) {
        if (cls != null) {
            context.startActivity(new Intent(context,
                    getInstance(context).isLogin() ? cls : LoginActivity.class));
        } else {
            if (!getInstance(context).isLogin()) {
                context.startActivity(new Intent(context, LoginActivity.class));
                return false;
            }
        }
        return true;
    }

    /**
     * 需要用户登录的操作在页面跳转前进行登录状态验证
     * With Reveal Effect
     */
    public void actionWithUserLogin(Context context, final Class<?> cls, View view, Activity activity) {
        boolean login = getInstance(context).isLogin();
        if (login) {
            ActivityOptionsCompat activityOptionsCompat =
                    ActivityOptionsCompat.makeClipRevealAnimation(view, 0, 0, 0, 0);
            ActivityCompat.startActivity(activity, new Intent(context, cls), activityOptionsCompat.toBundle());
        } else {
            context.startActivity(new Intent(context, LoginActivity.class));
        }
    }

    /**
     * 获取客户端用户是否已经登录
     *
     * @return
     */
    public boolean isLogin() {
        return sharedPreferences.getBoolean(IS_LOGIN_FLAG_KEY, false);
    }

    /**
     * 记录用户TOKEN
     */
    public void logForLogin(JSONObject jo) throws JSONException {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // 标识是否登录
        editor.putBoolean(IS_LOGIN_FLAG_KEY, true);
        // 标识用户TOKEN
        editor.putString(LOGIN_TOKEN_KEY, jo.getString(LOGIN_TOKEN_KEY));
        editor.apply();
    }

    /**
     * 记录用户基本信息
     */
    public void logUserInfo(JSONObject jo) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (user == null)
            user = new User();
        try {
            if (jo.has("name")) {
                editor.putString("name", jo.getString("name"));
                user.setName(jo.getString("name"));
            }
            if (jo.has("birthday")) {
                editor.putString("birthday", jo.getString("birthday"));
                user.setBirthday(jo.getString("birthday"));
            }
            if (jo.has("phone")) {
                editor.putString("phone", jo.getString("phone"));
                user.setPhone(jo.getString("phone"));
            }
            if (jo.has("nickname")) {
                editor.putString("nickname", jo.getString("nickname"));
                user.setNickname(jo.getString("nickname"));
            }
            if (jo.has("userDesc")) {
                editor.putString("userDesc", jo.getString("userDesc"));
                user.setUserDesc(jo.getString("userDesc"));
            }
            if (jo.has("sex")) {
                editor.putString("sex", jo.getString("sex"));
                user.setSex(jo.getString("sex"));
            }
            if (jo.has("photo")) {
                final String BASE64 = jo.getString("photo");
                editor.putString("photo", BASE64);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap photo;
                        if (BASE64 == null || BASE64.isEmpty())
                            photo = ImageUtil.getBitmapFromDrawable(
                                    (VectorDrawable) ScreenManager.getInstance().currentActivity().getResources().getDrawable(R.drawable.ic_account));
                        else
                            photo = ImageUtil.decodeBase64(BASE64);
                        user.setPhoto(photo);
                    }
                }).start();
            }
            if (jo.has("email")) {
                editor.putString("email", jo.getString("email"));
                user.setEmail(jo.getString("email"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.apply();
    }

    /**
     * 记住用户账号密码
     */
    public void logUserAccount(JSONObject jo) throws JSONException {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME_KEY, jo.getString(USERNAME_KEY));
        editor.putString(PASSWORD_KEY, jo.getString(PASSWORD_KEY));
        editor.apply();
    }

    /**
     * 注销用户信息
     *
     * @param context 操作上下文
     */
    public void logForLogout(Context context) {
        sharedPreferences.edit().clear().apply();
        user = null;
        ((Activity) context).finish();
    }

    /**
     * 返回当前登录用户TOKEN
     *
     * @return
     */
    public String getToken() {
        return sharedPreferences.getString(LOGIN_TOKEN_KEY, null);
    }

    public void welcomeLogin(Context context, Handler handler) throws Exception {
        new UserAction().login(context, generateLoginInfo(), handler, true);
    }

    private JSONObject[] generateLoginInfo() throws Exception {
        JSONObject jsonObjectForValue = new JSONObject();
        jsonObjectForValue.accumulate("username", sharedPreferences.getString(USERNAME_KEY, null));
        jsonObjectForValue.accumulate("password", sharedPreferences.getString(PASSWORD_KEY, null));

        JSONObject jsonObjectForToken = new JSONObject();
        jsonObjectForToken.accumulate("serialnumber", DeviceInfoUtil.getSerialNumber());
        jsonObjectForToken.accumulate("timestamp", System.currentTimeMillis());

        JSONObject postJO = new JSONObject();
        postJO.accumulate("token", AESUtil.Encrypt(jsonObjectForToken.toString(), CommonCodes.ENCRYPT_KEY));
        postJO.accumulate("value", AESUtil.Encrypt(jsonObjectForValue.toString(), CommonCodes.ENCRYPT_KEY));

        //返回JSON对象数组存放[POST数据对象,原始数据对象]
        return new JSONObject[]{postJO, jsonObjectForValue};
    }

    public User getUser() {
        if (user == null) {
            user = new User(
                    sharedPreferences.getString("name", null),
                    sharedPreferences.getString("birthday", null),
                    sharedPreferences.getString("phone", null),
                    sharedPreferences.getString("nickname",
                            ScreenManager.getInstance().currentActivity().getApplicationContext().getString(R.string.loginOrReg)),
                    sharedPreferences.getString("userDesc", null),
                    sharedPreferences.getString("sex", null),
                    sharedPreferences.getString("email", null),
                    null
            );
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String photoBase64 = sharedPreferences.getString("photo", null);
                    Bitmap photo;
                    if (photoBase64 == null || photoBase64.isEmpty())
                        photo = ImageUtil.getBitmapFromDrawable(
                                ScreenManager.getInstance().currentActivity().getResources().getDrawable(R.drawable.ic_account));
                    else
                        photo = ImageUtil.decodeBase64(photoBase64);
                    user.setPhoto(photo);
                }
            }).start();
        }
        return user;
    }
}
