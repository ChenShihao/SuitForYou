package com.cufe.suitforyou.commons;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by Victor on 2016-08-30.
 */
public class ScreenManager {

    private static Stack<Activity> activityStack;
    private static ScreenManager instance = null;

    public ScreenManager() {
    }

    public static ScreenManager getInstance() {
        if (instance == null) {
            synchronized (ScreenManager.class) {
                if (instance == null)
                    instance = new ScreenManager();
            }
        }
        return instance;
    }

    public Activity currentActivity() {
        return activityStack.peek();
    }

    public void popActivity() {
        Activity activity = activityStack.peek();
        if (activity != null) {
            activity.finish();
            activityStack.pop();
        }
    }

    public void popActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            activityStack.remove(activity);
        }
    }

    public void pushActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        if (activity != null) {
            activityStack.push(activity);
        }
    }

    public void popAll() {
        while (!activityStack.empty()) {
            popActivity(activityStack.peek());
        }
    }
}
