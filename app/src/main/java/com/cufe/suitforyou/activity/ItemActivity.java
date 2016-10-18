package com.cufe.suitforyou.activity;

import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.Spinner;

import com.cufe.suitforyou.R;
import com.cufe.suitforyou.action.UserAction;
import com.cufe.suitforyou.adapter.DetailPageAdapter;
import com.cufe.suitforyou.adapter.ImagePagerAdapter;
import com.cufe.suitforyou.model.Comment;
import com.cufe.suitforyou.model.DetailItem;
import com.cufe.suitforyou.model.SKU;
import com.cufe.suitforyou.commons.LoginStatus;
import com.cufe.suitforyou.commons.ScreenManager;
import com.cufe.suitforyou.customclass.MyImageTag;
import com.cufe.suitforyou.customview.NumberPickView;
import com.cufe.suitforyou.databinding.ActivityItemBinding;
import com.cufe.suitforyou.fragment.ImageContainerFragment;
import com.cufe.suitforyou.fragment.PageDetailFragment;
import com.cufe.suitforyou.utils.DeviceInfoUtil;
import com.cufe.suitforyou.utils.MyUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityItemBinding binding;

    private DetailItem detailItem;

    private ArrayList<Comment> comments;

    private int clothesId;

    private SKU[] skus;

    private ImageView[] points;

    private int appBarOffset = 0;

    private int scrimTriggerHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ScreenManager.getInstance().pushActivity(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_item);
        initUI(binding);

        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            detailItem = new Gson().fromJson(bundle.getString("item"), DetailItem.class);
            comments = new Gson().fromJson(bundle.getString("comments"),
                    new TypeToken<ArrayList<Comment>>() {
                    }.getType());

            clothesId = detailItem.getId();
            skus = detailItem.getSkus();
            initTopViewPager();
            binding.itemVp.setTranslationY(DeviceInfoUtil.getDeviceHeight() - getResources().getDimensionPixelOffset(R.dimen.app_bar_height));
            binding.itemVp.setAdapter(new DetailPageAdapter(getSupportFragmentManager(), detailItem, comments));
            binding.itemTab.setupWithViewPager(binding.itemVp);

            binding.itemTab.setAlpha(0);
            binding.itemVp.animate()
                    .translationY(0)
                    .setInterpolator(new OvershootInterpolator(0.5f))
                    .setDuration(300)
                    .setStartDelay(800)
                    .start();
            binding.itemTab.animate()
                    .alpha(1)
                    .setInterpolator(new OvershootInterpolator(0.5f))
                    .setDuration(300)
                    .setStartDelay(800)
                    .start();

        }

        scrimTriggerHeight = Math.round(getResources().getDimension(R.dimen.app_bar_height) - getResources().getDimension(R.dimen.scrim_visible_trigger_height));
        binding.itemAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                appBarOffset = verticalOffset;
            }
        });

        binding.itemFabAddToCart.setOnClickListener(this);

    }

    private void initTopViewPager() {
        // 获取商品样板图URL数组
        String[] urls = detailItem.getPhotos();
        if (urls == null || urls.length == 0)
            return;
        MyImageTag[] myUrls = new MyImageTag[urls.length];
        for (int index = 0; index < myUrls.length; index++) {
            myUrls[index] = new MyImageTag();
            myUrls[index].setUrl(urls[index]);
        }
        binding.itemViewpager.setAdapter(new ImagePagerAdapter(getSupportFragmentManager(), myUrls));

        points = new ImageView[urls.length];
        Drawable dot = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_circyle_fill, null);
        for (int index = 0; index < urls.length; index++) {
            points[index] = new ImageView(this);
            points[index].setImageDrawable(dot);
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            int margin = MyUtil.dpToPx(ItemActivity.this, 6);
            params.setMargins(margin, margin, margin, margin);
            points[index].setLayoutParams(params);
            binding.itemViewpagerIndicatorsContainer.addView(points[index]);
        }

        binding.itemViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                clearDots();
                points[position].animate()
                        .setInterpolator(new DecelerateInterpolator())
                        .scaleX(1.5f)
                        .scaleY(1.5f)
                        .start();
            }

            @Override
            public void onPageSelected(int position) {
                clearDots();
                points[position].animate()
                        .setInterpolator(new DecelerateInterpolator())
                        .scaleX(1.5f)
                        .scaleY(1.5f)
                        .start();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_menu_cart:
                LoginStatus.getInstance(this).actionWithUserLogin(this, CartActivity.class, findViewById(item.getItemId()), this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        binding.itemTab.setAlpha(0);
        if (Math.abs(appBarOffset) < scrimTriggerHeight) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            ArrayList<View> list = new ArrayList<>();
            for (Fragment fragment : fragments) {
                if (fragment instanceof ImageContainerFragment)
                    list.add(fragment.getView());
            }
            int currentItem = binding.itemViewpager.getCurrentItem();
            View view = list.get(currentItem);
            if (view != null)
                view.setTransitionName(getString(R.string.item_image));
        }

        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        MyUtil.showProgressDialog(null, false, null);
        super.onPause();
    }

    private void initUI(ActivityItemBinding binding) {
        Toolbar toolbar = binding.itemToolbar;
        setSupportActionBar(toolbar);
        setTitle(null);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        MyUtil.fixTopMargin(toolbar);
        toolbar.bringToFront();
        MyUtil.fixBottomMargin(binding.itemFabAddToCart);
    }

    private void clearDots() {
        if (points != null) {
            for (ImageView view : points) {
                view.animate()
                        .setInterpolator(new DecelerateInterpolator())
                        .scaleX(1)
                        .scaleY(1)
                        .start();
            }
        }
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.item_fab_add_to_cart:
                if (!LoginStatus.getInstance(this).actionWithUserLogin(this, null)) {
                    return;
                }
                if (v.getTag() != null && !(Boolean) v.getTag()) {
                    MyUtil.ShowToast("已经添加到购物车");
                    return;
                }
                Spinner spinner = null;
                NumberPickView pickView = null;
                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    if (fragment instanceof PageDetailFragment) {
                        View fragmentView = fragment.getView();
                        pickView = fragmentView != null ? (NumberPickView) fragmentView.findViewById(R.id.detail_item_number_pick) : null;
                        spinner = fragmentView != null ? (Spinner) fragmentView.findViewById(R.id.detail_item_sku_spinner) : null;
                        break;
                    }
                }
                if (pickView == null || spinner == null)
                    return;
                if (pickView.getVisibility() != View.VISIBLE) {
                    MyUtil.ShowToast("没货啦！...");
                    return;
                }
                final int num = pickView.getNum();
                final SKU sku = skus[spinner.getSelectedItemPosition()];

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("skuId", sku.getId());
                    jsonObject.accumulate("clothesId", detailItem.getId());
                    jsonObject.accumulate("number", num);
                    new UserAction().addCartItem(jsonObject, LoginStatus.getInstance(this).getToken(), new Handler(new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            if (msg.what < 0) {
                                MyUtil.ShowToast("连接异常，添加商品失败");
                            } else {
                                MyUtil.ShowToast(String.format(Locale.CHINA, "已添加至购物车\n%s\t×%d", sku.toString(), num));
                                confirmSku(v, sku);
                            }
                            return false;
                        }
                    }));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private void confirmSku(final View v, final SKU sku) {
        String token = LoginStatus.getInstance(ItemActivity.this).getToken();
        new UserAction().searchCartItemsSkuIds(token, new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                boolean flag = true;
                try {
                    if (msg.what < 0) {
                        MyUtil.ShowToast("连接服务器失败");
                    } else {
                        Bundle bundle = msg.getData();
                        if (bundle != null) {
                            JSONObject response = new JSONObject(bundle.getString("response"));
                            JSONArray data = response.getJSONArray("data");

                            int[] skuIds = new Gson().fromJson(String.valueOf(data), int[].class);
                            for (int foo : skuIds) {
                                if (foo == sku.getId()) {
                                    flag = false;
                                    break;
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    v.setTag(flag);
                }
                return false;
            }
        }));
    }
}
