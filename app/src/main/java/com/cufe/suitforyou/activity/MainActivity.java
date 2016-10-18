package com.cufe.suitforyou.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cufe.suitforyou.R;
import com.cufe.suitforyou.action.ItemAction;
import com.cufe.suitforyou.action.UserAction;
import com.cufe.suitforyou.adapter.ImagePagerAdapter;
import com.cufe.suitforyou.adapter.MiniListAdapter;
import com.cufe.suitforyou.model.SimpleItem;
import com.cufe.suitforyou.commons.LoginStatus;
import com.cufe.suitforyou.commons.ScreenManager;
import com.cufe.suitforyou.customclass.MyImageTag;
import com.cufe.suitforyou.databinding.ActivityMainBinding;
import com.cufe.suitforyou.databinding.NavHeaderMainBinding;
import com.cufe.suitforyou.utils.MyUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private ActivityMainBinding binding;

    private ViewPager viewPager;

    private ViewGroup vpIndicatorsContainer;

    private ImageView[] points;

    private Thread cycleThread;

    private volatile boolean flag = true;

    private RecyclerView[] rvs;

    private RecyclerView recommendRv;

    private String[] keys = new String[]{"男", "女", "羽绒"};

    private LinearLayout[] containers;

    private static String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ScreenManager.getInstance().pushActivity(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        if (!MyUtil.CheckPermissions(PERMISSIONS)) {
            new AlertDialog.Builder(this)
                    .setTitle("温馨提示")
                    .setMessage("本应用需要加载网络图片，本着节流的原则，我们希望使用缓存图片，对此我们需要您允许存储权限。（不行的话，我下次再来问。）")
                    .setPositiveButton("好的哥", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MyUtil.GetPermissions(PERMISSIONS);
                        }
                    })
                    .show();
        }
        initUI();
    }

    @Override
    protected void onResume() {
        initHeader();
        initHotPager();
        initClassRv();
        initRecommend();
        if (cycleThread != null && !cycleThread.isAlive()) {
            cycleThread = initThread();
            cycleThread.start();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        MyUtil.showProgressDialog(null, false, null);
        if (cycleThread != null && cycleThread.isAlive())
            cycleThread.interrupt();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        ScreenManager.getInstance().popActivity(this);
        MyUtil.showProgressDialog(null, false, null);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_shopping_cart:
                LoginStatus.getInstance(this).actionWithUserLogin(this, CartActivity.class, findViewById(item.getItemId()), this);
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.nav_header_userImg:
                LoginStatus.getInstance(this).actionWithUserLogin(MainActivity.this, UserInfoActivity.class);
                break;
            case R.id.nav_header_userNickname:
                LoginStatus.getInstance(this).actionWithUserLogin(MainActivity.this, UserInfoActivity.class);
            default:
                if (v instanceof ImageView && v.getTag() instanceof String) {
                    final int id = Integer.parseInt(String.valueOf(v.getTag()));
                    final View imageView = v;
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

                                    searchComments(id, data.toString(), imageView, false);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            return false;
                        }
                    }));
                }
                break;
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_menu_user_info:
                LoginStatus.getInstance(this).actionWithUserLogin(MainActivity.this, UserInfoActivity.class);
                break;
            case R.id.nav_menu_order:
                LoginStatus.getInstance(this).actionWithUserLogin(MainActivity.this, OrderManageActivity.class);
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        toolbar.bringToFront();
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        MyUtil.fixTopMargin(toolbar);
    }

    /**
     * 动态加载侧边栏的HeaderView，进行用户数据绑定
     */
    private void initHeader() {
        NavigationView navigationView = binding.navView;
        NavHeaderMainBinding headerMainBinding = NavHeaderMainBinding.inflate((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE));
        headerMainBinding.setUser(LoginStatus.getInstance(this).getUser());
        if (navigationView != null) {
            if (navigationView.getHeaderView(0) != null) {
                navigationView.removeHeaderView(navigationView.getHeaderView(0));
            }
            navigationView.addHeaderView(headerMainBinding.navHeaderContainer);
            // 绑定侧边栏Header点击事件
            headerMainBinding.navHeaderUserImg.setOnClickListener(this);
            headerMainBinding.navHeaderUserNickname.setOnClickListener(this);
            // 绑定侧边栏Layout点击事件
            navigationView.setNavigationItemSelectedListener(this);
        }
    }

    /**
     * 请求数据初始化热门ViewPager
     */
    private void initHotPager() {
        viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        vpIndicatorsContainer = (ViewGroup) findViewById(R.id.main_viewpager_indicators_container);
        if (viewPager != null && viewPager.getAdapter() != null) {
            return;
        }
        int page = new Random().nextInt(50) + 1;
        new UserAction().searchItems("装", page, 5, new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what < 0) {
                    MyUtil.ShowToast("无法连接到服务器");
                } else {
                    String response = msg.getData().getString("response");
                    try {
                        JSONArray data = new JSONObject(response).getJSONObject("data").getJSONArray("data");
                        final MyImageTag[] urls = new MyImageTag[data.length()];
                        for (int index = 0; index < urls.length; index++) {
                            JSONObject object = data.getJSONObject(index);
                            SimpleItem item = new Gson().fromJson(String.valueOf(object), SimpleItem.class);
                            urls[index] = new MyImageTag();
                            urls[index].setTag(String.valueOf(item.getClothesId()));
                            urls[index].setUrl(item.getPhoto());
                        }
                        viewPager.setAdapter(new ImagePagerAdapter(getSupportFragmentManager(), urls));

                        points = new ImageView[urls.length];
                        Drawable dot = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_circyle_fill, null);
                        vpIndicatorsContainer.removeAllViews();
                        for (int index = 0; index < urls.length; index++) {
                            points[index] = new ImageView(MainActivity.this);
                            points[index].setImageDrawable(dot);
                            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            );
                            int margin = MyUtil.dpToPx(MainActivity.this, 6);
                            params.setMargins(margin, margin, margin, margin);
                            points[index].setLayoutParams(params);
                            vpIndicatorsContainer.addView(points[index]);
                        }

                        vpIndicatorsContainer.setAlpha(0);
                        vpIndicatorsContainer.animate().alpha(1).start();

                        setViewPagerListener();
                        if (cycleThread == null || !cycleThread.isAlive()) {
                            cycleThread = initThread();
                            cycleThread.start();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        }));
    }

    /**
     * 初始化页面多个分类
     */
    private void initClassRv() {
        rvs = new RecyclerView[]{
                (RecyclerView) findViewById(R.id.main_rv_1),
                (RecyclerView) findViewById(R.id.main_rv_2),
                (RecyclerView) findViewById(R.id.main_rv_3)
        };
        containers = new LinearLayout[]{
                (LinearLayout) findViewById(R.id.main_class_1),
                (LinearLayout) findViewById(R.id.main_class_2),
                (LinearLayout) findViewById(R.id.main_class_3)
        };
        MyUtil.showProgressDialog(this, true, "正在加载...");
        boolean flag = true;
        for (int index = 0; index < keys.length; index++) {
            if (rvs[index].getAdapter() == null) {
                getClassRvData(keys[index], index);
                flag = false;
            }
        }
        if (flag)
            MyUtil.showProgressDialog(null, false, null);
    }

    /**
     * 初始化协同过滤推荐
     */
    private void initRecommend() {
        recommendRv = (RecyclerView) findViewById(R.id.main_rv_recommend);
        final LinearLayout recommendLayout = (LinearLayout) findViewById(R.id.main_class_recommend);
        if (LoginStatus.getInstance(this).isLogin()) {
            String token = LoginStatus.getInstance(this).getToken();
            new UserAction().getFilterItems(token, new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    try {
                        if (msg.what < 0) {
                            MyUtil.ShowToast("无法连接到服务器");
                        } else {
                            Bundle bundle = msg.getData();
                            if (bundle != null) {
                                String response = bundle.getString("response");
                                JSONArray array = new JSONObject(response).getJSONArray("data");
                                ArrayList<SimpleItem> items = new ArrayList<>();
                                for (int index = 0; index < array.length(); index++) {
                                    JSONObject jsonObject = array.getJSONObject(index);
                                    String tmp = jsonObject.getJSONObject("clothes").toString();
                                    SimpleItem item = new Gson().fromJson(tmp, SimpleItem.class);
                                    items.add(item);
                                }
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                                linearLayoutManager.setOrientation(OrientationHelper.HORIZONTAL);
                                recommendRv.setLayoutManager(linearLayoutManager);
                                recommendRv.setAdapter(new MiniListAdapter(items));

                                if (recommendLayout.getVisibility() != View.VISIBLE) {
                                    recommendLayout.setVisibility(View.VISIBLE);
                                    recommendLayout.setAlpha(0);
                                    recommendLayout.animate().alpha(1).start();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            }));
        } else {
            if (recommendLayout != null)
                recommendLayout.setVisibility(View.GONE);
            if (recommendRv != null)
                recommendRv.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化页面多个分类
     */
    private void getClassRvData(String key, final int index) {
        int page = new Random().nextInt(5) + 1;
        new UserAction().searchItems(key, page, 10, new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                try {
                    if (msg.what < 0) {
                        MyUtil.ShowToast("无法连接到服务器");
                    } else {
                        String response = msg.getData().getString("response");
                        JSONArray array = new JSONObject(response).getJSONObject("data").getJSONArray("data");
                        ArrayList<SimpleItem> list =
                                new Gson().fromJson(String.valueOf(array),
                                        new TypeToken<ArrayList<SimpleItem>>() {
                                        }.getType());
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                        linearLayoutManager.setOrientation(OrientationHelper.HORIZONTAL);
                        rvs[index].setLayoutManager(linearLayoutManager);
                        rvs[index].setAdapter(new MiniListAdapter(list));

                        if (containers[index].getVisibility() != View.VISIBLE) {
                            containers[index].setVisibility(View.VISIBLE);
                            containers[index].setAlpha(0);
                            containers[index].animate().alpha(1).start();
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

    /**
     * 设置HotViewPager监听器，改变指示器
     */
    private void setViewPagerListener() {
        if (viewPager != null) {
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    clearDots();
                    points[position].animate()
                            .setInterpolator(new DecelerateInterpolator())
                            .scaleX(1.3f)
                            .scaleY(1.3f)
                            .start();
                }

                @Override
                public void onPageSelected(int position) {
                    clearDots();
                    points[position].animate()
                            .setInterpolator(new DecelerateInterpolator())
                            .scaleX(1.3f)
                            .scaleY(1.3f)
                            .start();
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == 1) {
                        flag = false;
                        cycleThread.interrupt();
                    } else {
                        if (!cycleThread.isAlive()) {
                            flag = true;
                            cycleThread = initThread();
                            cycleThread.start();
                        }
                    }
                }
            });
        }
    }

    /**
     * 清空轮播下方指示器ON状态
     */
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

    /**
     * 轮播线程（中断后）初始化
     *
     * @return
     */
    private Thread initThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (flag && !Thread.interrupted()) {
                        Thread.sleep(3000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (points != null && points.length > 0 && viewPager != null) {
                                    int count = points.length;
                                    int current = viewPager.getCurrentItem();
                                    int next = (current + 1) % count;
                                    viewPager.setCurrentItem(next, true);
                                }
                            }
                        });
                    }
                } catch (InterruptedException ignored) {

                }
            }
        });
    }

    public void more(View view) {
        if (view.getTag() != null && view.getTag() instanceof String) {
            String key = (String) view.getTag();
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("key", key);
            startActivity(intent);
        }
    }

    public void anothers(View view) {
        if (view.getTag() != null) {
            int index = Integer.parseInt(String.valueOf(view.getTag()));
            getClassRvData(keys[index], index);
        }
    }

    public void miniClick(View view) {
        if (view.getTag() instanceof Integer) {
            final int id = (int) view.getTag();
            final View imageView = view.findViewById(R.id.mini_item_image);

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

                            searchComments(id, data.toString(), imageView, true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            }));

        }
    }

    public void searchComments(int clothesId, final String clothes, final View view, final boolean effect) {
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

                            Intent intent = new Intent(MainActivity.this, ItemActivity.class);
                            intent.putExtra("item", clothes);
                            intent.putExtra("comments", data.toString());

                            if (effect) {
                                ActivityOptionsCompat activityOptionsCompat =
                                        ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, view, getString(R.string.item_image));
                                ActivityCompat.startActivity(MainActivity.this, intent, activityOptionsCompat.toBundle());
                            } else {
                                startActivity(intent);
                            }

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
