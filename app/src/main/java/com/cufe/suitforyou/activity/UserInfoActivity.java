package com.cufe.suitforyou.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cufe.suitforyou.R;
import com.cufe.suitforyou.action.UserAction;
import com.cufe.suitforyou.commons.AppConfig;
import com.cufe.suitforyou.commons.CommonCodes;
import com.cufe.suitforyou.commons.LoginStatus;
import com.cufe.suitforyou.commons.ScreenManager;
import com.cufe.suitforyou.customview.EditTextDialogView;
import com.cufe.suitforyou.databinding.ActivityUserInfoBinding;
import com.cufe.suitforyou.utils.ImageUtil;
import com.cufe.suitforyou.utils.MyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private String[] PERMISSIONS = {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private Uri imageUri;

    private File outputImageFile;

    private ActivityUserInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ScreenManager.getInstance().pushActivity(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_info);
        binding.setUser(LoginStatus.getInstance(this).getUser());
    }

    @Override
    protected void onDestroy() {
        ScreenManager.getInstance().popActivity(this);
        MyUtil.showProgressDialog(null, false, null);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_user_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.user_info_logout:
                new AlertDialog.Builder(this)
                        .setTitle("温馨提示")
                        .setMessage("是否退出账号？")
                        .setPositiveButton("是哒", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LoginStatus.getInstance(UserInfoActivity.this).logForLogout(UserInfoActivity.this);
                            }
                        })
                        .setNegativeButton("点错了", null)
                        .show();
                break;
            default:
                break;
        }
        return false;
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
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_info_img:
                new AlertDialog.Builder(UserInfoActivity.this)
                        .setTitle("修改头像")
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
                break;
            case R.id.user_info_nickName:
                showEditDialog(v, "修改昵称", "nickname");
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.user_info_name:
                showEditDialog(v, "修改姓名", "name");
                break;
            case R.id.user_info_desc:
                showEditDialog(v, "修改剁手宣言", "userDesc");
                break;
            case R.id.user_info_phone:
                showEditDialog(v, "修改手机号码", "phone");
                break;
            case R.id.user_info_birthday:
                Date date = null;
                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                    date = format.parse(((TextView) v).getText().toString().trim());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(date == null ? new Date() : date);
                new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String ms = month + 1 < 10 ? "0" + String.valueOf(month + 1) : String.valueOf(month + 1);
                                String ds = dayOfMonth < 10 ? "0" + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
                                String date = String.format("%s-%s-%s", year, ms, ds);
                                try {
                                    JSONObject jo = new JSONObject();
                                    jo.accumulate("birthday", date);
                                    uploadNewUserInfo(jo);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
                break;
            case R.id.user_info_sex:
                TextView sexTv = (TextView) v;
                String sex = sexTv.getText().toString().trim();
                RadioButton manBtn = new RadioButton(this);
                RadioButton womanBtn = new RadioButton(this);
                final int manId = View.generateViewId();
                manBtn.setId(manId);
                manBtn.setText("男");
                int womanId = View.generateViewId();
                womanBtn.setId(womanId);
                womanBtn.setText("女");

                int padding = MyUtil.dpToPx(this, 12);
                manBtn.setPadding(padding, padding, padding, padding);
                womanBtn.setPadding(padding, padding, padding, padding);

                final RadioGroup radioGroup = new RadioGroup(this);
                radioGroup.addView(manBtn);
                radioGroup.addView(womanBtn);
                if (sex.equals("男"))
                    manBtn.setSelected(true);
                else
                    womanBtn.setSelected(true);
                radioGroup.setOrientation(LinearLayout.HORIZONTAL);
                radioGroup.setPadding(padding, padding, padding, 0);
                new AlertDialog.Builder(this)
                        .setTitle("修改性别")
                        .setView(radioGroup)
                        .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    JSONObject jo = new JSONObject();
                                    int id = radioGroup.getCheckedRadioButtonId();
                                    jo.accumulate("sex", id == manId ? "男" : "女");
                                    uploadNewUserInfo(jo);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case R.id.user_info_email:
                showEditDialog(v, "修改E-mail", "email");
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onPause() {
        MyUtil.showProgressDialog(null, false, null);
        super.onPause();
    }

    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.user_info_toolbar);
        toolbar.bringToFront();
        setSupportActionBar(toolbar);
        setTitle(null);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ImageUtil.setElevation(this, binding.userInfoImg, 12);

        binding.userInfoName.setOnLongClickListener(this);
        binding.userInfoDesc.setOnLongClickListener(this);
        binding.userInfoPhone.setOnLongClickListener(this);
        binding.userInfoBirthday.setOnLongClickListener(this);
        binding.userInfoSex.setOnLongClickListener(this);
        binding.userInfoEmail.setOnLongClickListener(this);
    }

    /**
     * 点击头像后上下文菜单中拍照选项
     */
    private void cameraOnClick() {
        String fileName = String.format("USER_IMG_%s.png", new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(new Date()));
        outputImageFile = new File(AppConfig.PHOTO_FILE_PATH, fileName);
        /**
         * Android N 以上需使用Provider，其余版本使用Uri.fromFile
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            imageUri = Uri.fromFile(outputImageFile);
        } else {
            imageUri = FileProvider.getUriForFile(UserInfoActivity.this, UserInfoActivity.this.getApplicationContext().getPackageName() + ".provider", outputImageFile);
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CommonCodes.CAMERA_REQUEST_CODE);
    }

    /**
     * 点击头像后上下文菜单中相册选项
     */
    private void photoOnClick() {
        String fileName = String.format("USER_IMG_%s.png", new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(new Date()));
        outputImageFile = new File(AppConfig.PHOTO_FILE_PATH, fileName);
        imageUri = Uri.fromFile(outputImageFile);
        Intent intent_pick = new Intent(Intent.ACTION_PICK, null);
        intent_pick.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent_pick.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent_pick, CommonCodes.PHOTO_REQUEST_CODE);
    }

    private void showEditDialog(View v, String title, final String key) {
        final EditTextDialogView view = new EditTextDialogView(UserInfoActivity.this);
        view.getAfter().setText(((TextView) v).getText());
        new AlertDialog.Builder(UserInfoActivity.this)
                .setTitle(title)
                .setView(view)
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            JSONObject jo = new JSONObject();
                            jo.accumulate(key, view.getAfter().getText().toString().trim());
                            uploadNewUserInfo(jo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 用户图像上传异步任务类
     */
    private class uploadPhotoAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Bitmap bitmap = ImageUtil.resizeImage(BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri)), 512, 512);
                String userImgBase64 = ImageUtil.encodeToBase64(bitmap, Bitmap.CompressFormat.PNG, 100);
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("photo", userImgBase64);
                new UserAction().updateUserInfo(jsonObject, LoginStatus.getInstance(UserInfoActivity.this).getToken(), uploadNewUserInfoHandler);
            } catch (FileNotFoundException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * 用户更新信息上传
     *
     * @param userInfoJO 包含需要修改的用户信息键值对
     */
    private void uploadNewUserInfo(JSONObject userInfoJO) {
        MyUtil.showProgressDialog(this, true, "正在上传...");
        new UserAction().updateUserInfo(userInfoJO, LoginStatus.getInstance(UserInfoActivity.this).getToken(), uploadNewUserInfoHandler);
    }

    /**
     * 用户信息修改更新的Handler
     */
    private Handler uploadNewUserInfoHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            if (!bundle.isEmpty()) {
                try {
                    MyUtil.ShowToast("修改成功");
                    JSONObject jsonObject = new JSONObject(bundle.getString("jo"));
                    LoginStatus.getInstance(UserInfoActivity.this).logUserInfo(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                switch (msg.what) {
                    case 0:
                        MyUtil.ShowToast("服务器异常，修改失败");
                        break;
                    case -1:
                        MyUtil.ShowToast("网络异常，修改失败");
                        break;
                    default:
                        MyUtil.ShowToast("出现异常，修改失败");
                        break;
                }
            }
            MyUtil.showProgressDialog(null, false, null);
            return true;
        }

    });
}