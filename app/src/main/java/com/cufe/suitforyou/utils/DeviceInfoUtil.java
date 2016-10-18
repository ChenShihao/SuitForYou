package com.cufe.suitforyou.utils;

import android.os.Build;
import android.util.DisplayMetrics;

import com.cufe.suitforyou.commons.ScreenManager;

/**
 * Created by Victor on 2016-08-30.
 */
public class DeviceInfoUtil {

    public static String getSerialNumber() {
        return Build.SERIAL;
    }

    public static int getDeviceWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ScreenManager.getInstance().currentActivity()
                .getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static int getDeviceHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ScreenManager.getInstance().currentActivity()
                .getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }
}
