package com.cufe.suitforyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.cufe.suitforyou.R;
import com.cufe.suitforyou.model.User;
import com.cufe.suitforyou.commons.LoginStatus;
import com.cufe.suitforyou.commons.ScreenManager;
import com.cufe.suitforyou.utils.MyUtil;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ScreenManager.getInstance().pushActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        try {
            final LoginStatus loginStatus = LoginStatus.getInstance(this);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (loginStatus.isLogin()) {
                            loginStatus.welcomeLogin(WelcomeActivity.this, welcomeHandler);
                        } else {
                            Thread.sleep(1000);
                            redirectToMain();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        ScreenManager.getInstance().popActivity(this);
        MyUtil.showProgressDialog(null, false, null);
        super.onDestroy();
    }

    private Handler welcomeHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    User user = LoginStatus.getInstance(WelcomeActivity.this).getUser();
                    MyUtil.ShowToast(String.format("欢迎回来！%s", user.getNickname()), Toast.LENGTH_LONG);
                    redirectToMain();
                    break;
                default:
                    LoginStatus.getInstance(WelcomeActivity.this).logForLogout(WelcomeActivity.this);
                    redirectToMain();
                    break;
            }
            return false;
        }
    });

    private void redirectToMain() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
