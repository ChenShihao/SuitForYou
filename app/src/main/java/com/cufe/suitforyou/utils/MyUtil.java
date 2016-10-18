package com.cufe.suitforyou.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cufe.suitforyou.commons.CommonCodes;
import com.cufe.suitforyou.commons.ScreenManager;

import java.io.File;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Created by Victor on 2016-09-03.
 */
public class MyUtil {

    private static Toast toast;
    private static ProgressDialog progressDialog;

    public static void ShowToast(Object object) {
        String message = String.valueOf(object);
        if (toast == null) {
            toast = Toast.makeText(ScreenManager.getInstance().currentActivity().getApplicationContext(), message, Toast.LENGTH_SHORT);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

    public static void ShowToast(String message, int time) {
        if (toast == null) {
            toast = Toast.makeText(ScreenManager.getInstance().currentActivity().getApplicationContext(), message, time);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

    public static void GetPermissions(String[] permissions) {
        if (permissions != null && permissions.length >= 1) {
            Activity activity = ScreenManager.getInstance().currentActivity();
            LinkedList<Integer> flags = new LinkedList<>();
            for (String permission : permissions) {
                flags.push(ActivityCompat.checkSelfPermission(activity, permission));
            }
            while (!flags.isEmpty()) {
                if (flags.pop() != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, permissions, CommonCodes.CAMERA_WRITE_READ_PERMISSION_CODE);
                    break;
                }
            }
        }
    }

    public static boolean CheckPermissions(String[] permissions) {
        Activity activity = ScreenManager.getInstance().currentActivity();
        LinkedList<Integer> flags = new LinkedList<>();
        for (String permission : permissions) {
            flags.push(ActivityCompat.checkSelfPermission(activity, permission));
        }
        while (!flags.isEmpty()) {
            if (flags.pop() != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void showProgressDialog(Context context, boolean show, @Nullable String msg) {
        if (show) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage(msg);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        } else {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public static void showProgressDialog(Context context, boolean show, @Nullable String msg, boolean cancelable) {
        if (show) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage(msg);
                progressDialog.setCancelable(cancelable);
                progressDialog.show();
            }
        } else {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public static void showProgressBar(Context context, boolean show, ViewGroup container) {
        if (show) {
            int count = container.getChildCount();
            View view;
            for (int index = 0; index < count; index++) {
                view = container.getChildAt(index);
                if (view instanceof ProgressBar)
                    container.removeView(view);
            }
            ProgressBar bar = new ProgressBar(context);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            bar.setVisibility(View.VISIBLE);
            bar.setLayoutParams(params);
            container.addView(bar, 0);
        } else {
            int count = container.getChildCount();
            View view;
            for (int index = 0; index < count; index++) {
                view = container.getChildAt(index);
                if (view instanceof ProgressBar)
                    container.removeView(view);
            }
        }
    }

    public static boolean makeDir(String path) {
        File file = new File(path);
        return file.exists() || file.mkdirs();
    }

    public static int dpToPx(Context context, float dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    public static String parseParameters(Map<String, Object> map) {
        StringBuilder result = new StringBuilder();
        if (map != null && map.size() > 0) {
            result.append("?");
            Set<Map.Entry<String, Object>> entries = map.entrySet();

            for (Map.Entry<String, Object> entry : entries) {
                String key = entry.getKey();
                String value = entry.getValue().toString().replaceAll(" ", "%20");
                result.append(key).append("=").append(value).append("&&");
            }
            int index = result.lastIndexOf("&&");
            if (index >= 0)
                result.delete(index, index + 2);
        }
        return result.toString();

    }

    public static void hideIME() {
        try {
            Activity activity = ScreenManager.getInstance().currentActivity();
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            View view = activity.getCurrentFocus();
            if (view != null) {
                IBinder binder = view.getWindowToken();
                inputMethodManager.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getNetWorkType() {
        ConnectivityManager manager =
                (ConnectivityManager) ScreenManager.getInstance().currentActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        if (info != null && info.isConnected()) {
            String typeName = info.getTypeName();
            if (typeName.equalsIgnoreCase("WIFI"))
                return 1;
            else
                return 0;
        }

        return -1;
    }

    public static int getStatusBarHeight() {
        Context context = ScreenManager.getInstance().currentActivity();
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = 0;
        if (resourceId > 0) {
            height = resources.getDimensionPixelSize(resourceId);
        }
        return height;
    }

    public static void fixTopMargin(View view) {
        int height = getStatusBarHeight();
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        int margin = layoutParams.topMargin;
        layoutParams.setMargins(margin, margin + height, margin, margin);
        view.setLayoutParams(layoutParams);
    }

    public static int getNavBarHeight() {
        Context context = ScreenManager.getInstance().currentActivity();
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = 0;
        if (resourceId > 0) {
            height = resources.getDimensionPixelSize(resourceId);
        }
        return height;
    }

    public static void fixBottomMargin(View view) {
        if (checkDeviceHasNavigationBar()) {
            int height = getNavBarHeight();
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            int margin = layoutParams.topMargin;
            layoutParams.setMargins(margin, margin, margin, margin + height);
            view.setLayoutParams(layoutParams);
        }
    }

    public static boolean checkDeviceHasNavigationBar() {
        boolean hasMenuKey =
                ViewConfiguration.get(ScreenManager.getInstance().currentActivity()).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        return !hasMenuKey && !hasBackKey;
    }

    public static void setSwipeRefreshColor(SwipeRefreshLayout layout) {
        Context context = ScreenManager.getInstance().currentActivity();
        layout.setColorSchemeColors(
                ActivityCompat.getColor(context, android.R.color.holo_green_light),
                ActivityCompat.getColor(context, android.R.color.holo_red_light),
                ActivityCompat.getColor(context, android.R.color.holo_blue_light));
    }
}
