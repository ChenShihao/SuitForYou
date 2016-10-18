package com.cufe.suitforyou.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.cufe.suitforyou.R;
import com.cufe.suitforyou.action.ItemAction;
import com.cufe.suitforyou.action.UserAction;
import com.cufe.suitforyou.adapter.SearchResultAdapter;
import com.cufe.suitforyou.model.SimpleItem;
import com.cufe.suitforyou.commons.AppConfig;
import com.cufe.suitforyou.commons.CommonCodes;
import com.cufe.suitforyou.commons.ScreenManager;
import com.cufe.suitforyou.customclass.MyBoolean;
import com.cufe.suitforyou.databinding.ActivitySearchBinding;
import com.cufe.suitforyou.utils.MyUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private String[] PERMISSIONS = {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private ActivitySearchBinding binding;

    private String key = "";

    private int page = 1;

    private MyBoolean hasData = new MyBoolean(false);

    private SearchResultAdapter searchResultAdapter;

    private LinearLayoutManager linearLayoutManager;

    private int lastVisiblePosition = 0;

    private boolean secondScroll = false;

    private Uri imageUri;

    private File outputImageFile;

    private String photoResponse;

    private Handler searchHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                if (msg.what < 0) {
                    page = 1;
                    hasData.setBool(false);
                    MyUtil.ShowToast("无法连接到服务器");
                    if (searchResultAdapter != null)
                        searchResultAdapter.clearItems();
                    return false;
                } else {
                    String response = msg.getData().getString("response");
                    JSONArray array = new JSONObject(response).getJSONObject("data").getJSONArray("data");
                    final ArrayList<SimpleItem> searchResultList = new ArrayList<>();
                    for (int index = 0; index < array.length(); index++) {
                        JSONObject item = (JSONObject) array.get(index);
                        final SimpleItem listItem = new Gson().fromJson(String.valueOf(item), SimpleItem.class);
                        searchResultList.add(listItem);
                    }
                    hasData.setBool(!searchResultList.isEmpty());
                    updateTips();
                    if (page <= 1) {
                        RecyclerView recyclerView = binding.searchResultRv;
                        searchResultAdapter = new SearchResultAdapter(searchResultList);
                        linearLayoutManager = new LinearLayoutManager(SearchActivity.this);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(searchResultAdapter);
                        recyclerView.clearOnScrollListeners();
                        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if (newState == RecyclerView.SCROLL_STATE_IDLE
                                        && lastVisiblePosition + 1 == searchResultAdapter.getItemCount()) {
                                    if (secondScroll) {
                                        TextView textView = (TextView) findViewById(R.id.tip_tv);
                                        if (textView != null)
                                            getMore(textView);
                                    }
                                    secondScroll = !secondScroll;
                                }
                            }

                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition();
                                secondScroll = false;
                            }
                        });
                    } else {
                        searchResultAdapter.addItem(searchResultList, 10);
                    }
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            } finally {
                if (binding.searchSwipeRefresh.isRefreshing())
                    binding.searchSwipeRefresh.setRefreshing(false);
                updateTips();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ScreenManager.getInstance().pushActivity(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        binding.setHasData(hasData);
        binding.searchBackIv.setOnClickListener(this);
        binding.searchSubmitBtn.setOnClickListener(this);
        binding.searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                onClick(binding.searchSubmitBtn);
                return false;
            }
        });

        MyUtil.setSwipeRefreshColor(binding.searchSwipeRefresh);
        binding.searchSwipeRefresh.setOnRefreshListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            binding.searchEditText.setText(bundle.getString("key"));
            binding.searchSubmitBtn.performClick();
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
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_back_iv:
                onBackPressed();
                break;
            case R.id.search_submit_btn:
                String tmp = binding.searchEditText.getText().toString().trim();
                if (tmp.equals(key) || tmp.isEmpty())
                    break;
                else {
                    MyUtil.hideIME();
                    hasData.setBool(true);
                    key = tmp;
                    page = 1;
                    onRefresh();
                }
                break;
            case R.id.search_list_item:
                final Integer id = (Integer) v.getTag();
                final View view = v.findViewById(R.id.search_list_item_image);
                Handler itemHandler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        if (msg.what < 0) {
                            MyUtil.ShowToast("无法连接到服务器");
                            return false;
                        } else {
                            try {
                                String response = msg.getData().getString("response");
                                JSONObject data = new JSONObject(response).getJSONObject("data");

                                searchComments(id, data.toString(), view);
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
                    new UserAction().searchDetailItem(id, itemHandler);
                else
                    MyUtil.showProgressDialog(null, false, null);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRefresh() {
        binding.searchSwipeRefresh.setRefreshing(true);
        new UserAction().searchItems(key, page, 10, searchHandler);
    }

    @Override
    protected void onPause() {
        MyUtil.showProgressDialog(null, false, null);
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CommonCodes.CAMERA_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    MyUtil.showProgressDialog(this, true, "正在上传...");
                    new uploadPhotoAsyncTask().execute();
                }
                break;
            case CommonCodes.PHOTO_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(data.getData(), "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CommonCodes.SHOW_PHOTO_CODE);
                }
                break;
            case CommonCodes.SHOW_PHOTO_CODE:
                if (resultCode == RESULT_OK) {
                    MyUtil.showProgressDialog(this, true, "正在上传...");
                    new uploadPhotoAsyncTask().execute();
                }
                break;
            default:
                break;
        }
    }

    public void updateTips() {
        if (hasData.isBool()) {
            binding.searchTips.setVisibility(View.GONE);
        } else {
            binding.searchTips.setVisibility(View.VISIBLE);
            binding.searchTips.setAlpha(0);
            binding.searchTips.setTranslationY(MyUtil.dpToPx(this, -24));
            binding.searchTips.animate()
                    .alpha(1)
                    .translationY(0)
                    .setInterpolator(new OvershootInterpolator(0.5f))
                    .start();
        }
    }

    public void getMore(View view) {
        if (view instanceof TextView && view.getTag().equals(1)) {
            TextView textView = (TextView) view;
            textView.setText("正在加载...");
            page++;
            onRefresh();
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

                            Intent intent = new Intent(SearchActivity.this, ItemActivity.class);
                            intent.putExtra("item", clothes);
                            intent.putExtra("comments", data.toString());

                            ActivityOptionsCompat activityOptionsCompat =
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(SearchActivity.this, view, getString(R.string.item_image));
                            ActivityCompat.startActivity(SearchActivity.this, intent, activityOptionsCompat.toBundle());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return false;
            }
        }));
    }

    public void searchPhoto(View view) {
        new AlertDialog.Builder(this)
                .setTitle("选择图片")
                .setItems(new CharSequence[]{"拍照", "从相册中选取"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!MyUtil.CheckPermissions(PERMISSIONS)) {
                            MyUtil.GetPermissions(PERMISSIONS);
                        } else if (!MyUtil.makeDir(AppConfig.PHOTO_FILE_PATH)) {
                            MyUtil.ShowToast("文件夹创建失败");
                        } else {
                            switch (which) {
                                case 0:
                                    cameraOnClick();
                                    break;
                                case 1:
                                    photoOnClick();
                                    break;
                                default:
                                    break;
                            }
                            dialog.dismiss();
                        }
                    }
                }).show();
    }

    /**
     * 点击头像后上下文菜单中拍照选项
     */
    private void cameraOnClick() {
        String fileName = String.format("SEARCH_IMG_%s.png", new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(new Date()));
        outputImageFile = new File(AppConfig.PHOTO_FILE_PATH, fileName);
        /**
         * Android N 以上需使用Provider，其余版本使用Uri.fromFile
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            imageUri = Uri.fromFile(outputImageFile);
        } else {
            imageUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", outputImageFile);
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CommonCodes.CAMERA_REQUEST_CODE);
    }

    /**
     * 点击头像后上下文菜单中相册选项
     */
    private void photoOnClick() {
        String fileName = String.format("SEARCH_IMG_%s.png", new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(new Date()));
        outputImageFile = new File(AppConfig.PHOTO_FILE_PATH, fileName);
        imageUri = Uri.fromFile(outputImageFile);
        Intent intent_pick = new Intent(Intent.ACTION_PICK, null);
        intent_pick.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent_pick.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent_pick, CommonCodes.PHOTO_REQUEST_CODE);
    }

    /**
     * 用户图像上传异步任务类
     */
    private class uploadPhotoAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", outputImageFile.getName(),
                            RequestBody.create(MediaType.parse("text/plain"), outputImageFile))
                    .build();
            String url = AppConfig.PHOTO_URL;
            Request request = new Request.Builder().url(url).post(requestBody).build();
            try {
                photoResponse = okHttpClient.newCall(request).execute().body().string();
            } catch (IOException e) {
                e.printStackTrace();
                MyUtil.ShowToast("出错啦");
                MyUtil.showProgressDialog(null, false, null);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            MyUtil.showProgressDialog(null, false, null);

            if (photoResponse != null) {
                try {
                    JSONObject jsonObject = new JSONObject(photoResponse);
                    JSONArray data = jsonObject.getJSONArray("data");
                    ArrayList<SimpleItem> items =
                            new Gson().fromJson(String.valueOf(data),
                                    new TypeToken<ArrayList<SimpleItem>>() {
                                    }.getType());
                    hasData.setBool(items != null && items.size() > 0);
                    RecyclerView recyclerView = binding.searchResultRv;
                    searchResultAdapter = new SearchResultAdapter(items, items.size() + 1);
                    linearLayoutManager = new LinearLayoutManager(SearchActivity.this);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(searchResultAdapter);
                    recyclerView.clearOnScrollListeners();
                    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if (newState == RecyclerView.SCROLL_STATE_IDLE
                                    && lastVisiblePosition + 1 == searchResultAdapter.getItemCount()) {
                                if (secondScroll) {
                                    TextView textView = (TextView) findViewById(R.id.tip_tv);
                                    if (textView != null)
                                        getMore(textView);
                                }
                                secondScroll = !secondScroll;
                            }
                        }

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition();
                            secondScroll = false;
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            updateTips();
            super.onPostExecute(aVoid);
        }
    }
}
