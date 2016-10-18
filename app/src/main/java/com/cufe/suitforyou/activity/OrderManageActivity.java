package com.cufe.suitforyou.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;

import com.cufe.suitforyou.R;
import com.cufe.suitforyou.action.ItemAction;
import com.cufe.suitforyou.action.OrderAction;
import com.cufe.suitforyou.action.UserAction;
import com.cufe.suitforyou.adapter.OrderManageListAdapter;
import com.cufe.suitforyou.model.Order;
import com.cufe.suitforyou.commons.CommonCodes;
import com.cufe.suitforyou.commons.LoginStatus;
import com.cufe.suitforyou.commons.ScreenManager;
import com.cufe.suitforyou.databinding.ActivityOrderManageBinding;
import com.cufe.suitforyou.utils.MyUtil;
import com.cufe.suitforyou.utils.RegexUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

public class OrderManageActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityOrderManageBinding binding;

    private boolean customTime = false;

    private int statusIndex = 0;

    private String[] timeStrings = new String[]{"全部时间", "自定义时间"};

    private String[] statusStrings = new String[]{"全部状态", "未支付", "已签收", "确认收货", "已评价"};

    private String startString;

    private String endString;

    private boolean hasData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ScreenManager.getInstance().pushActivity(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_manage);

        initUI();

        binding.orderManageTimeSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, timeStrings));
        binding.orderManageStatusSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, statusStrings));
        binding.orderManageTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                binding.orderManageTimeConfig.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
                customTime = position != 0;
                query();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.orderManageStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                statusIndex = position;
                query();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case CommonCodes.PAY_CODE:
                query();
                break;
            case CommonCodes.COMMENT_CODE:
                query();
                break;
        }
    }

    private void initUI() {
        Toolbar toolbar = binding.orderManageToolbar;
        setSupportActionBar(toolbar);
        setTitle("我的订单");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void query() {
        if (customTime) {
            if (confirmTime())
                queryOrder(startString, endString, statusIndex == 0 ? null : statusStrings[statusIndex]);
        } else
            queryOrder(null, null, statusIndex == 0 ? null : statusStrings[statusIndex]);
    }

    private void queryOrder(@Nullable final String startTime, @Nullable final String endTime, @Nullable final String status) {
        HashMap<String, Object> parameters = new HashMap<String, Object>() {{
            if (customTime && startTime != null && endTime != null) {
                put("startTime", startTime);
                put("endTime", endTime);
            }
            if (status != null)
                put("status", status);
        }};
        MyUtil.showProgressDialog(this, true, "正在加载...");
        new OrderAction().getOrders(parameters, LoginStatus.getInstance(this).getToken(), new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                try {
                    if (msg.what < 0) {
                        MyUtil.ShowToast("连接服务器失败");
                    } else {
                        Bundle bundle = msg.getData();
                        if (bundle != null) {
                            String response = bundle.getString("response");
                            JSONArray data = new JSONObject(response).getJSONArray("data");
                            ArrayList<Order> orders =
                                    new Gson().fromJson(String.valueOf(data),
                                            new TypeToken<ArrayList<Order>>() {
                                            }.getType());
                            binding.orderManageRv.setLayoutManager(new LinearLayoutManager(OrderManageActivity.this));
                            binding.orderManageRv.setAdapter(new OrderManageListAdapter(orders));
                            hasData = orders != null && orders.size() > 0;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    MyUtil.showProgressDialog(null, false, null);
                    updateTips();
                }
                return false;
            }
        }));
    }

    public void queryOrderByTime(View view) {
        startString = binding.orderManageStartTime.getText().toString();
        endString = binding.orderManageEndTime.getText().toString();

        if (confirmTime())
            queryOrder(startString, endString, statusIndex == 0 ? null : statusStrings[statusIndex]);
    }

    private boolean confirmTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String dateRegex = "[0-9]{4}-(0[1-9]{1}|1[0-2]{1})-(0[1-9]{1}|[1-3][0-9]{1})";
        if (!RegexUtil.matches(dateRegex, startString)) {
            MyUtil.ShowToast("请选择开始日期");
            return false;
        } else if (!RegexUtil.matches(dateRegex, endString)) {
            MyUtil.ShowToast("请选择结束日期");
            return false;
        } else {
            try {
                Date startDate = format.parse(startString);
                Date endDate = format.parse(endString);
                if (startDate.after(endDate)) {
                    MyUtil.ShowToast("开始日期大于结束日期");
                    return false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public void setStartTime(View view) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String ms = month + 1 < 10 ? "0" + String.valueOf(month + 1) : String.valueOf(month + 1);
                        String ds = dayOfMonth < 10 ? "0" + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
                        String date = String.format("%s-%s-%s", year, ms, ds);

                        binding.orderManageStartTime.setText(date);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    public void setEndTime(View view) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String ms = month + 1 < 10 ? "0" + String.valueOf(month + 1) : String.valueOf(month + 1);
                        String ds = dayOfMonth < 10 ? "0" + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
                        String date = String.format("%s-%s-%s", year, ms, ds);
                        binding.orderManageEndTime.setText(date);

                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void updateTips() {
        binding.orderManageTips.setVisibility(hasData ? View.GONE : View.VISIBLE);
        binding.orderManageTips.setAlpha(0);
        binding.orderManageTips.setTranslationY(MyUtil.dpToPx(this, -12));
        binding.orderManageTips.animate().alpha(1).translationY(0).start();
    }

    public void changeStatus(View view) {
        int orderId = (int) view.getTag();
        TextView textView = (TextView) view.findViewById(R.id.order_manage_status_change);
        switch (textView.getText().toString().trim()) {
            case "去支付":
                Intent intentPay = new Intent(this, PayActivity.class);
                intentPay.putExtra("orderId", orderId);
                startActivityForResult(intentPay, CommonCodes.PAY_CODE);
                break;
            case "确认收货":
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("fromStatus", "已签收");
                    jsonObject.accumulate("toStatus", "确认收货");
                    String token = LoginStatus.getInstance(this).getToken();
                    new OrderAction().changeStatus(orderId, token, jsonObject, new Handler(new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            if (msg.what > 0) {
                                MyUtil.ShowToast("确认收货成功！");
                                query();
                            } else {
                                MyUtil.ShowToast("好像哪里不对...");
                            }
                            return false;
                        }
                    }));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "去评价":
                Intent intent = new Intent(this, AddCommentActivity.class);
                intent.putExtra("orderId", orderId);
                startActivityForResult(intent, CommonCodes.COMMENT_CODE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(final View view) {
        if (view.getTag() instanceof Integer) {
            final int id = (int) view.getTag();
            final View imageView = view.findViewById(R.id.order_list_image);
            MyUtil.showProgressDialog(this, true, "正在加载...");
            new UserAction().searchDetailItem(id, new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    try {
                        if (msg.what < 0) {
                            MyUtil.ShowToast("无法连接到服务器");
                            return false;
                        } else {
                            String response = msg.getData().getString("response");
                            JSONObject data = new JSONObject(response).getJSONObject("data");

                            searchComments(id, data.toString(), imageView);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            }));

        }
    }

    public void searchComments(int clothesId, final String clothes, final View view) {
        new ItemAction().getComments(clothesId, new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                try {
                    if (msg.what < 0) {
                        MyUtil.ShowToast("无法连接到服务器");
                    } else {
                        Bundle commentBundle = msg.getData();
                        if (commentBundle != null) {
                            String response = commentBundle.getString("response");
                            JSONArray data = new JSONObject(response).getJSONArray("data");

                            Intent intent = new Intent(OrderManageActivity.this, ItemActivity.class);
                            intent.putExtra("item", clothes);
                            intent.putExtra("comments", data.toString());

                            ActivityOptionsCompat activityOptionsCompat =
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(OrderManageActivity.this, view, getString(R.string.item_image));
                            ActivityCompat.startActivity(OrderManageActivity.this, intent, activityOptionsCompat.toBundle());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return false;
            }
        }));
    }
}
