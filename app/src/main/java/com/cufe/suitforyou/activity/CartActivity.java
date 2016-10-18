package com.cufe.suitforyou.activity;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.cufe.suitforyou.R;
import com.cufe.suitforyou.action.ItemAction;
import com.cufe.suitforyou.action.UserAction;
import com.cufe.suitforyou.adapter.CartListAdapter;
import com.cufe.suitforyou.model.CartItem;
import com.cufe.suitforyou.commons.CommonCodes;
import com.cufe.suitforyou.commons.LoginStatus;
import com.cufe.suitforyou.commons.ScreenManager;
import com.cufe.suitforyou.customclass.MyInteger;
import com.cufe.suitforyou.customview.NumberPickMiniView;
import com.cufe.suitforyou.databinding.ActivityCartBinding;
import com.cufe.suitforyou.utils.MyUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private ActivityCartBinding binding;

    private CartListAdapter listAdapter;

    private int itemNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ScreenManager.getInstance().pushActivity(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart);

        setSupportActionBar(binding.cartToolbar);
        setTitle("我的购物车");
        binding.cartToolbar.setNavigationIcon(R.drawable.ic_back);
        binding.cartToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        MyUtil.setSwipeRefreshColor(binding.cartSwipeRefresh);
        binding.cartSwipeRefresh.setOnRefreshListener(this);
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
    protected void onResume() {
        onRefresh();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.cart_list_item_title:
                Handler itemHandler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        if (msg.what < 0) {
                            MyUtil.ShowToast("无法连接到服务器");
                            return false;
                        } else {
                            try {
                                ViewGroup viewGroup = (ViewGroup) v.getParent().getParent();
                                View view = viewGroup.findViewById(R.id.cart_list_item_image);
                                String response = msg.getData().getString("response");
                                JSONObject data = new JSONObject(response).getJSONObject("data");

                                searchComments((Integer) v.getTag(), data.toString(), view);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        return false;
                    }
                });

                MyUtil.showProgressDialog(this, true, "正在加载...");

                /** 根据ID查找商品详情 **/
                if (v.getTag() != null)
                    new UserAction().searchDetailItem((Integer) v.getTag(), itemHandler);
                else
                    MyUtil.showProgressDialog(null, false, null);
                break;
            case R.id.cart_list_item_delete:
                final int position = (int) v.getTag();
                final ViewGroup card = (ViewGroup) v.getParent().getParent();
                String title = ((TextView) card.findViewById(R.id.cart_list_item_title)).getText().toString().trim();
                String sku = ((TextView) card.findViewById(R.id.cart_list_item_sku)).getText().toString().trim();
                new AlertDialog.Builder(this)
                        .setTitle("是否删除")
                        .setMessage(title + "\n" + sku)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Object tag = card.findViewById(R.id.cart_list_item).getTag();
                                int id = -1;
                                if (tag instanceof Integer)
                                    id = (int) tag;
                                if (id > 0) {
                                    MyUtil.showProgressDialog(CartActivity.this, true, "请稍后...");
                                    new UserAction().deleteCartItem(id, LoginStatus.getInstance(CartActivity.this).getToken(), new Handler(new Handler.Callback() {
                                        @Override
                                        public boolean handleMessage(Message msg) {
                                            if (msg.what < 0) {
                                                MyUtil.ShowToast("连接异常，删除失败");
                                            } else {
                                                card.animate()
                                                        .alpha(0)
                                                        .translationX(card.getWidth())
                                                        .setInterpolator(new AccelerateInterpolator())
                                                        .setListener(new Animator.AnimatorListener() {
                                                            @Override
                                                            public void onAnimationStart(Animator animation) {

                                                            }

                                                            @Override
                                                            public void onAnimationEnd(Animator animation) {

                                                                listAdapter.removeItem(position);
                                                                itemNumber--;
                                                                updateTips();
                                                            }

                                                            @Override
                                                            public void onAnimationCancel(Animator animation) {

                                                            }

                                                            @Override
                                                            public void onAnimationRepeat(Animator animation) {

                                                            }
                                                        })
                                                        .setDuration(300)
                                                        .start();
                                            }
                                            MyUtil.showProgressDialog(null, false, null);
                                            return false;
                                        }
                                    }));
                                }
                            }
                        })
                        .show();
                break;

            case R.id.number_pick_mini_add:

                final NumberPickMiniView numberPickAdd = (NumberPickMiniView) v.getParent().getParent();
                int numAdd = numberPickAdd.getNum();
                if (++numAdd > numberPickAdd.getMax()) {
                    MyUtil.ShowToast("没这么多货啦");
                    return;
                }
                Object tagAdd = numberPickAdd.getTag();
                if (tagAdd instanceof Integer) {
                    int id = (int) tagAdd;
                    uploadNumData(numberPickAdd, id, numAdd);
                }
                break;

            case R.id.number_pick_mini_drop:

                final NumberPickMiniView numberPickDrop = (NumberPickMiniView) v.getParent().getParent();
                int numDrop = numberPickDrop.getNum();
                if (numDrop <= 1)
                    return;
                numDrop--;
                Object tagDrop = numberPickDrop.getTag();
                if (tagDrop instanceof Integer) {
                    int id = (int) tagDrop;
                    uploadNumData(numberPickDrop, id, numDrop);
                }
                break;

            case R.id.number_pick_mini_tv:

                final NumberPickMiniView numberPickMiniView = (NumberPickMiniView) v.getParent().getParent();
                final MyInteger num = new MyInteger(numberPickMiniView.getNum());
                final NumberPicker numberPicker = new NumberPicker(v.getContext());
                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(numberPickMiniView.getMax());
                numberPicker.setValue(num.getValue());
                numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        num.setValue(newVal);
                    }
                });
                new AlertDialog.Builder(v.getContext())
                        .setTitle("选择数量")
                        .setView(numberPicker)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Object tag = numberPickMiniView.getTag();
                                if (tag instanceof Integer)
                                    uploadNumData(numberPickMiniView, (int) tag, num.getValue());
                            }
                        })
                        .show();
                break;
        }
    }

    private void uploadNumData(final NumberPickMiniView numberPick, int id, final int num) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("number", num);
            new UserAction().updateCartItem(id, jsonObject, LoginStatus.getInstance(CartActivity.this).getToken(), new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    if (msg.what < 0) {
                        MyUtil.ShowToast("连接异常，更改失败");
                    } else {
                        updateNumTv(numberPick, num);
                    }
                    return false;
                }
            }));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateNumTv(NumberPickMiniView numberPickMiniView, int num) {
        TextView textView = (TextView) numberPickMiniView.findViewById(R.id.number_pick_mini_tv);
        if (textView != null) {
            textView.setText(String.valueOf(num));
            numberPickMiniView.setNum(num);
        }
    }

    @Override
    public void onRefresh() {
        binding.cartSwipeRefresh.setRefreshing(true);
        new UserAction().searchCartItems(LoginStatus.getInstance(this).getToken(), new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                try {
                    if (msg.what < 0) {
                        MyUtil.ShowToast("获取购物车信息失败");
                    } else {
                        Bundle bundle = msg.getData();
                        if (bundle != null) {
                            JSONObject response = new JSONObject(bundle.getString("response"));
                            JSONArray data = response.getJSONArray("data");

                            ArrayList<CartItem> cartItems =
                                    new Gson().fromJson(String.valueOf(data),
                                            new TypeToken<ArrayList<CartItem>>() {
                                            }.getType());
                            itemNumber = cartItems.size();
                            binding.cartRv.setLayoutManager(new LinearLayoutManager(CartActivity.this));
                            listAdapter = new CartListAdapter(cartItems);
                            binding.cartRv.setAdapter(listAdapter);
                            binding.cartRv.setItemAnimator(new DefaultItemAnimator());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    binding.cartSwipeRefresh.setRefreshing(false);
                    updateTips();
                }
                return false;
            }
        }));
    }

    public void updateTips() {
        if (itemNumber > 0) {
            binding.cartTips.setVisibility(View.GONE);
        } else {
            binding.cartTips.setVisibility(View.VISIBLE);
        }
    }

    public void GoToOrder(final View v) {
        if (itemNumber <= 0) {
            MyUtil.ShowToast("Umm...你好像没买东西...");
            return;
        }
        MyUtil.showProgressDialog(this, true, "正在验证商品...");
        new UserAction().searchCartItems(LoginStatus.getInstance(this).getToken(), new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                try {
                    if (msg.what < 0) {
                        MyUtil.ShowToast("连接失败");
                    } else {
                        Bundle bundle = msg.getData();
                        if (bundle != null) {
                            JSONObject response = new JSONObject(bundle.getString("response"));
                            JSONArray data = response.getJSONArray("data");
                            if (data.length() > 0) {
                                Intent intent = new Intent(CartActivity.this, OrderActivity.class);
                                intent.putExtra("data", data.toString());
                                ActivityOptionsCompat activityOptionsCompat =
                                        ActivityOptionsCompat.makeClipRevealAnimation(v, 0, 0, 0, 0);
                                ActivityCompat.startActivityForResult(CartActivity.this, intent, CommonCodes.ORDER_CODE, activityOptionsCompat.toBundle());
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    MyUtil.showProgressDialog(null, false, null);
                }
                return false;
            }
        }));
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

                            Intent intent = new Intent(CartActivity.this, ItemActivity.class);
                            intent.putExtra("item", clothes);
                            intent.putExtra("comments", data.toString());

                            ActivityOptionsCompat activityOptionsCompat =
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(CartActivity.this, view, getString(R.string.item_image));
                            ActivityCompat.startActivity(CartActivity.this, intent, activityOptionsCompat.toBundle());
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
