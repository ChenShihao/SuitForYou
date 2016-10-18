package com.cufe.suitforyou.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cufe.suitforyou.R;
import com.cufe.suitforyou.action.AddressAction;
import com.cufe.suitforyou.action.OrderAction;
import com.cufe.suitforyou.action.UserAction;
import com.cufe.suitforyou.adapter.AddressAdapter;
import com.cufe.suitforyou.adapter.AreaAdapter;
import com.cufe.suitforyou.adapter.CityAdapter;
import com.cufe.suitforyou.adapter.OrderListAdapter;
import com.cufe.suitforyou.adapter.ProvinceAdapter;
import com.cufe.suitforyou.model.Address;
import com.cufe.suitforyou.model.Area;
import com.cufe.suitforyou.model.CartItem;
import com.cufe.suitforyou.model.City;
import com.cufe.suitforyou.model.Province;
import com.cufe.suitforyou.commons.LoginStatus;
import com.cufe.suitforyou.commons.ScreenManager;
import com.cufe.suitforyou.databinding.ActivityOrderBinding;
import com.cufe.suitforyou.utils.MyUtil;
import com.cufe.suitforyou.utils.RegexUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityOrderBinding binding;

    private CartItem[] cartItems;

    private float totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ScreenManager.getInstance().pushActivity(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order);
        initUI();
        setAddressSpinner();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String data = bundle.getString("data");
            cartItems = new Gson().fromJson(data, CartItem[].class);
            binding.orderRv.setLayoutManager(new LinearLayoutManager(this));
            binding.orderRv.setAdapter(new OrderListAdapter(cartItems));
            binding.orderRv.setNestedScrollingEnabled(false);
            binding.orderRv.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    MyUtil.dpToPx(this, 112) * cartItems.length
            ));
            for (CartItem item : cartItems) {
                totalPrice += item.getNumber() * item.getPrice();
            }
            binding.orderTotalPrice.append(String.valueOf(totalPrice));
            binding.orderScrollView.smoothScrollTo(0, 0);
        }

    }

    @Override
    protected void onDestroy() {
        ScreenManager.getInstance().popActivity(this);
        MyUtil.showProgressDialog(null, false, null);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("提示信息")
                .setMessage("确定退出订单页面？")
                .setPositiveButton("嗯呢", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("点错了", null)
                .show();
    }

    @Override
    protected void onPause() {
        MyUtil.showProgressDialog(null, false, null);
        super.onPause();
    }

    private void initUI() {
        Toolbar toolbar = binding.orderToolbar;
        setSupportActionBar(toolbar);
        setTitle("填写订单");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setAddressSpinner() {
        Province[] provinces = new AddressAction(this).queryProvince();
        binding.orderSpinnerProvince.setAdapter(new ProvinceAdapter(provinces));
        binding.orderSpinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Province province = (Province) parent.getSelectedItem();
                City[] cities = new AddressAction(parent.getContext()).queryCity(province.getPROVINCE_ID());
                binding.orderSpinnerCity.setAdapter(new CityAdapter(cities));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.orderSpinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                City city = (City) parent.getSelectedItem();
                Area[] areas = new AddressAction(parent.getContext()).queryArea(city != null ? city.getCITY_ID() : "");
                binding.orderSpinnerArea.setAdapter(new AreaAdapter(areas));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        new UserAction().getAddress(LoginStatus.getInstance(this).getToken(), new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                try {
                    Bundle data = msg.getData();
                    if (data != null) {
                        String response = data.getString("response");
                        JSONArray dataArray = new JSONObject(response).getJSONArray("data");
                        Address[] addresses = new Gson().fromJson(String.valueOf(dataArray), Address[].class);
                        binding.orderAddressSpinner.setAdapter(new AddressAdapter(addresses));
                        binding.orderAddressSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position == parent.getCount() - 1) {
                                    binding.orderAddAddressTable.setAlpha(0);
                                    binding.orderAddAddressTable.setVisibility(View.VISIBLE);
                                    binding.orderAddAddressTable.animate()
                                            .alpha(1).start();
                                } else {
                                    binding.orderAddAddressTable.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return false;
            }
        }));

    }

    public void GoToPay(View view) {
        Object selectedItem = binding.orderAddressSpinner.getSelectedItem();
        JSONObject address = new JSONObject();
        try {
            if (selectedItem != null) {
                Address item = (Address) selectedItem;
                address.accumulate("addressId", item.getAddressId());
            } else {
                String detail = binding.orderAddAddressDetail.getText().toString().trim();
                String name = binding.orderAddAddressName.getText().toString().trim();
                String phone = binding.orderAddAddressPhone.getText().toString().trim();
                if (detail.isEmpty()) {
                    MyUtil.ShowToast("详细地址不能为空");
                    return;
                } else if (name.isEmpty()) {
                    MyUtil.ShowToast("收货人不能为空");
                    return;
                } else if (phone.isEmpty()) {
                    MyUtil.ShowToast("联系电话不能为空");
                    return;
                } else if (!RegexUtil.matches("[0-9]{11}", phone)) {
                    MyUtil.ShowToast("联系电话格式不正确(应为11位数字)");
                    return;
                } else {
                    TextView provinceView = (TextView) binding.orderSpinnerProvince.getSelectedView();
                    address.accumulate("province", provinceView.getText().toString().trim());
                    TextView cityView = (TextView) binding.orderSpinnerCity.getSelectedView();
                    address.accumulate("city", cityView.getText().toString().trim());
                    TextView areaView = (TextView) binding.orderSpinnerArea.getSelectedView();
                    address.accumulate("area", areaView.getText().toString().trim());
                    address.accumulate("addressInfo", detail);
                    address.accumulate("receiverName", name);
                    address.accumulate("receiverPhone", phone);
                }
            }
            MyUtil.showProgressDialog(this, true, "正在创建订单...");
            new OrderAction().sendOrder(address, LoginStatus.getInstance(this).getToken(), new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    try {
                        if (msg.what > 0) {
                            MyUtil.ShowToast("订单创建成功");
                            Intent intent = new Intent(OrderActivity.this, OrderManageActivity.class);
                            startActivity(intent);
                        } else {
                            Bundle bundle = msg.getData();
                            if (bundle != null && !bundle.isEmpty()) {
                                String response = bundle.getString("response");
                                JSONObject jsonObject = new JSONObject(response);
                                switch (jsonObject.getString("status")) {
                                    case "3":
                                        MyUtil.ShowToast("出错啦！订单为空");
                                        break;
                                    case "5":
                                        int id = jsonObject.getInt("data");
                                        CartItem target = cartItems[0];
                                        for (CartItem item : cartItems) {
                                            if (item.getSku().getId() == id)
                                                target = item;
                                        }
                                        MyUtil.ShowToast(String.format("%s\n%s\n%s", target.getTitle(), target.getSku().toString(), "该商品库存不足！"));
                                        break;
                                    default:
                                        MyUtil.ShowToast("出错啦！");
                                        break;
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        MyUtil.showProgressDialog(null, false, null);
                        finish();
                    }
                    return false;
                }
            }));
        } catch (JSONException e) {
            e.printStackTrace();
            MyUtil.showProgressDialog(null, false, null);
        }


    }

    @Override
    public void onClick(View v) {

    }
}
