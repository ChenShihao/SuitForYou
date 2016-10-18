package com.cufe.suitforyou.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.cufe.suitforyou.R;
import com.cufe.suitforyou.action.OrderAction;
import com.cufe.suitforyou.commons.CommonCodes;
import com.cufe.suitforyou.commons.LoginStatus;
import com.cufe.suitforyou.commons.ScreenManager;
import com.cufe.suitforyou.utils.MyUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class PayActivity extends AppCompatActivity {

    private int id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ScreenManager.getInstance().pushActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        Toolbar toolbar = (Toolbar) findViewById(R.id.pay_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getInt("orderId");
        }

    }

    @Override
    protected void onDestroy() {
        ScreenManager.getInstance().popActivity(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        MyUtil.showProgressDialog(null, false, null);
        super.onPause();
    }

    public void pay(View view) {
        if (id < 0)
            return;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("fromStatus", "未支付");
            jsonObject.accumulate("toStatus", "已支付");
            String token = LoginStatus.getInstance(this).getToken();
            new OrderAction().changeStatus(id, token, jsonObject, new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    if (msg.what > 0) {
                        MyUtil.ShowToast("支付成功！");
                        setResult(CommonCodes.PAY_CODE);
                        finish();
                    } else {
                        MyUtil.ShowToast("好像哪里不对...");
                    }
                    return false;
                }
            }));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
