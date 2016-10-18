package com.cufe.suitforyou.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by Victor on 2016-09-02.
 */
public class ImageUtil {


    public static String encodeToBase64(final Bitmap image, final Bitmap.CompressFormat compressFormat, final int quality) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOutputStream);
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input) {
        byte[] bytes = Base64.decode(input, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static Bitmap resizeImage(Resources res, int resId, float targetWidth, float targetHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;
        float srcRatio = srcWidth / srcHeight;
        float targetRatio = targetWidth / targetHeight;
        float actualOutWidth = srcWidth;
        float actualOutHeight = srcHeight;

        if (srcWidth > targetWidth || srcHeight > targetHeight) {
            if (srcRatio < targetRatio) {
                actualOutWidth = targetWidth;
                actualOutHeight = targetWidth / targetRatio;
            } else if (srcRatio > targetRatio) {
                actualOutHeight = targetHeight;
                actualOutWidth = targetHeight * targetRatio;
            } else {
                actualOutHeight = targetHeight;
                actualOutWidth = targetWidth;
            }
        }
        options.inSampleSize = computSampleSize(options, actualOutWidth, actualOutHeight);
        options.inJustDecodeBounds = false;
        Bitmap scaledBitmap = null;
        try {
            scaledBitmap = BitmapFactory.decodeResource(res, resId, options);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        if (scaledBitmap == null) {
            return null;//压缩失败
        }
        Bitmap actualOutBitmap = Bitmap.createScaledBitmap(scaledBitmap, (int) actualOutWidth, (int) actualOutHeight, true);
        if (actualOutBitmap != scaledBitmap)
            scaledBitmap.recycle();
        return actualOutBitmap;
    }

    public static Bitmap resizeImage(String path, float targetWidth, float targetHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;
        float srcRatio = srcWidth / srcHeight;
        float targetRatio = targetWidth / targetHeight;
        float actualOutWidth = srcWidth;
        float actualOutHeight = srcHeight;

        if (srcWidth > targetWidth || srcHeight > targetHeight) {
            if (srcRatio < targetRatio) {
                actualOutWidth = targetWidth;
                actualOutHeight = targetWidth / targetRatio;
            } else if (srcRatio > targetRatio) {
                actualOutHeight = targetHeight;
                actualOutWidth = targetHeight * targetRatio;
            } else {
                actualOutHeight = targetHeight;
                actualOutWidth = targetWidth;
            }
        }
        options.inSampleSize = computSampleSize(options, actualOutWidth, actualOutHeight);
        options.inJustDecodeBounds = false;
        Bitmap scaledBitmap = null;
        try {
            scaledBitmap = BitmapFactory.decodeFile(path, options);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        if (scaledBitmap == null) {
            return null;//压缩失败
        }
        Bitmap actualOutBitmap = Bitmap.createScaledBitmap(scaledBitmap, (int) actualOutWidth, (int) actualOutHeight, true);
        if (actualOutBitmap != scaledBitmap)
            scaledBitmap.recycle();
        return actualOutBitmap;
    }

    public static Bitmap resizeImage(Bitmap bitmap, float targetWidth, float targetHeight) {
        float srcWidth = bitmap.getWidth();
        float srcHeight = bitmap.getHeight();
        float srcRatio = srcWidth / srcHeight;
        float targetRatio = targetWidth / targetHeight;
        float actualOutWidth = srcWidth;
        float actualOutHeight = srcHeight;

        if (srcWidth > targetWidth || srcHeight > targetHeight) {
            if (srcRatio < targetRatio) {
                actualOutWidth = targetWidth;
                actualOutHeight = targetWidth / targetRatio;
            } else if (srcRatio > targetRatio) {
                actualOutHeight = targetHeight;
                actualOutWidth = targetHeight * targetRatio;
            } else {
                actualOutHeight = targetHeight;
                actualOutWidth = targetWidth;
            }
        }
        return Bitmap.createScaledBitmap(bitmap, (int) actualOutWidth, (int) actualOutHeight, true);
    }

    private static int computSampleSize(BitmapFactory.Options options, float reqWidth, float reqHeight) {
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;
        int sampleSize = 1;
        if (srcWidth > reqWidth || srcHeight > reqHeight) {
            int withRatio = Math.round(srcWidth / reqWidth);
            int heightRatio = Math.round(srcHeight / reqHeight);
            sampleSize = Math.min(withRatio, heightRatio);
        }
        return sampleSize;
    }

    /**
     * Drawable图转化为位图
     *
     * @param drawable 目标图
     * @return 返回位图
     */
    public static Bitmap getBitmapFromDrawable(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static void setElevation(Context context, ImageView imageView, int elevation) {
        imageView.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        });
        imageView.setElevation(MyUtil.dpToPx(context, elevation));
    }

    public static void saveToLocal(final Bitmap bitmap, final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!file.exists()) {
                        FileOutputStream outputStream = new FileOutputStream(file);
                        if (bitmap != null) {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
