package com.cufe.suitforyou.utils;

import android.animation.Animator;
import android.content.Context;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;

import com.cufe.suitforyou.commons.ScreenManager;

/**
 * Created by Victor on 2016-09-06.
 */
public class AnimationUtil {

    public static void toggleViewInReveal(final View view) {
        if (view.getVisibility() == View.INVISIBLE) {
            Animator animator = ViewAnimationUtils.createCircularReveal(
                    view,
                    view.getWidth(),
                    view.getHeight(),
                    0,
                    (float) Math.hypot(view.getWidth(), view.getHeight()));
            view.setVisibility(View.VISIBLE);
            view.bringToFront();
            animator.setDuration(300);
            animator.start();
        } else {
            Animator animator = ViewAnimationUtils.createCircularReveal(
                    view,
                    view.getWidth(),
                    view.getHeight(),
                    (float) Math.hypot(view.getWidth(), view.getHeight()),
                    0);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.setDuration(300);
            animator.start();
        }
    }

    public static void RecyleViewItemEnterAnimHorizontal(final View view, int position) {
        view.setAlpha(0);
        view.setTranslationX(-DeviceInfoUtil.getDeviceWidth());
        view.animate()
                .alpha(1)
                .translationX(0)
                .setInterpolator(new AnticipateOvershootInterpolator(0.5f))
                .setStartDelay(50 * position)
                .setDuration(400)
                .start();
    }

    public static void RecyleViewItemEnterAnimVertical(final View view, int position) {
        Context context = ScreenManager.getInstance().currentActivity();
        view.setAlpha(0);
        view.setTranslationY(-MyUtil.dpToPx(context, 12));
        view.animate()
                .alpha(1)
                .translationY(0)
                .setInterpolator(new AnticipateOvershootInterpolator(0.5f))
                .setStartDelay(50 * position)
                .setDuration(400)
                .start();
    }
}
