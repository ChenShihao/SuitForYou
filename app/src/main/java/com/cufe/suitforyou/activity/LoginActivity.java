package com.cufe.suitforyou.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.cufe.suitforyou.R;
import com.cufe.suitforyou.action.UserAction;
import com.cufe.suitforyou.commons.CommonCodes;
import com.cufe.suitforyou.commons.ScreenManager;
import com.cufe.suitforyou.utils.AESUtil;
import com.cufe.suitforyou.utils.DeviceInfoUtil;
import com.cufe.suitforyou.utils.MD5Util;
import com.cufe.suitforyou.utils.MyUtil;
import com.cufe.suitforyou.utils.RegexUtil;

import org.json.JSONObject;

/**
 * 登录页面Activity
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ScreenManager.getInstance().pushActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initToolbar();

        Button loginButton = (Button) findViewById(R.id.login_login_btn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = validateInfo();
                switch (result) {
                    case "validation":
                        httpConfirmInfo();
                        break;
                    default:
                        MyUtil.ShowToast(result);
                        break;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        ScreenManager.getInstance().popActivity(this);
        MyUtil.showProgressDialog(null, false, null);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        MyUtil.showProgressDialog(null, false, null);
        super.onPause();
    }

    /**
     * 初始化Login活动Toolbar
     */
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitle(R.string.login_str);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 验证用户表单输入是否合法
     *
     * @return
     */
    private String validateInfo() {
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) LoginActivity.this.findViewById(R.id.login_username_tv);
        String username = autoCompleteTextView.getText().toString();
        EditText passwordEditText = (EditText) LoginActivity.this.findViewById(R.id.login_password_et);
        String password = passwordEditText.getText().toString();
        if (username.length() <= 0)
            return "用户名不得为空";
        else if (!RegexUtil.matches("([a-z]|[A-Z]|[0-9]|_){1,}", username))
            return "用户名仅支持大小写字母、数字以及下划线";
        else if (!RegexUtil.matches("([a-z]|[A-Z]|[0-9]|_){3,16}", username))
            return "用户名长度应为3至16位";
        else if (password.length() <= 0)
            return "密码不得为空";
        else
            return "validation";
    }

    /**
     * 预处理用户表单数据
     *
     * @return JSONObject
     * {
     * "value":${Username}和MD5加密后的${password}组成的JSON字符串进行AES加密后的结果
     * "token":${SerialNumber}与${TimeStamp}组成的JSON字符串进行AES加密后的结果
     * }
     */
    private JSONObject[] preTreatInfo() throws Exception {

        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) LoginActivity.this.findViewById(R.id.login_username_tv);
        String username = autoCompleteTextView.getText().toString();
        EditText passwordEditText = (EditText) LoginActivity.this.findViewById(R.id.login_password_et);
        String password = MD5Util.encode(passwordEditText.getText().toString());

        JSONObject jsonObjectForValue = new JSONObject();
        jsonObjectForValue.accumulate("username", username);
        jsonObjectForValue.accumulate("password", password);

        JSONObject jsonObjectForToken = new JSONObject();
        jsonObjectForToken.accumulate("serialnumber", DeviceInfoUtil.getSerialNumber());
        jsonObjectForToken.accumulate("timestamp", System.currentTimeMillis());

        JSONObject postJO = new JSONObject();
        postJO.accumulate("token", AESUtil.Encrypt(jsonObjectForToken.toString(), CommonCodes.ENCRYPT_KEY));
        postJO.accumulate("value", AESUtil.Encrypt(jsonObjectForValue.toString(), CommonCodes.ENCRYPT_KEY));

        //返回JSON对象数组存放[POST数据对象,原始数据对象]
        return new JSONObject[]{postJO, jsonObjectForValue};
    }

    /**
     * 发送http请求验证账号密码是否正确
     */
    private void httpConfirmInfo() {
        try {
            MyUtil.showProgressDialog(this, true, "正在登录");
            new UserAction().login(this, preTreatInfo(), loginHandler, false);
        } catch (Exception e) {
            MyUtil.showProgressDialog(this, false, null);
            e.printStackTrace();
        }
    }

    /**
     * 登录后的回调handler，用于操作主线程
     */
    private Handler loginHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    redirectToMain();
                    break;
                case 1:
                    MyUtil.ShowToast("登录失败");
                    break;
                case 2:
                    MyUtil.ShowToast("连接超时");
                    break;
                case 3:
                    MyUtil.ShowToast("用户名或密码错误");
                    break;
                case -1:
                    MyUtil.ShowToast("连接失败");
                    break;
                default:
                    MyUtil.ShowToast("连接失败");
                    break;
            }
            MyUtil.showProgressDialog(null, false, null);
            return false;
        }
    });

    /**
     * 验证成功后跳转至主页面
     */
    private void redirectToMain() {
        finish();
    }

}
