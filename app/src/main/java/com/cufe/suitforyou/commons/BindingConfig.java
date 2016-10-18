package com.cufe.suitforyou.commons;

import android.app.Activity;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.cufe.suitforyou.adapter.CommentListAdapter;
import com.cufe.suitforyou.adapter.OrderListAdapter;
import com.cufe.suitforyou.model.CartItem;
import com.cufe.suitforyou.model.Comment;
import com.cufe.suitforyou.model.SKU;
import com.cufe.suitforyou.customview.NumberPickMiniView;
import com.cufe.suitforyou.customview.NumberPickView;
import com.cufe.suitforyou.http.SimpleHttpCallback;
import com.cufe.suitforyou.http.SimpleHttpURLConnection;
import com.cufe.suitforyou.utils.ImageUtil;
import com.cufe.suitforyou.utils.MD5Util;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by Victor on 2016-09-07.
 */

public class BindingConfig {

    @BindingAdapter("android:rating")
    public static void setRating(View v, Object o) {
        ((RatingBar) v).setRating(Float.valueOf(o.toString()));
    }

    @BindingAdapter("android:src")
    public static void setSrc(final View v, Object o) {
        if (v instanceof ImageView) {
            if (o instanceof Bitmap) {
                ((ImageView) v).setImageBitmap((Bitmap) o);
            } else if (o instanceof Drawable) {
                ((ImageView) v).setImageDrawable((Drawable) o);
            }
        }
    }

    @BindingAdapter("imageUrl")
    public static void handleImageUrl(final View view, final Object o) {
        String md5 = null;
        try {
            md5 = MD5Util.encode((String) o);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        final String filePath = AppConfig.PHOTO_FILE_PATH + md5 + ".jpg";
        final File file = new File(filePath);
        if (file.exists()) {
            final Handler localHandler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    Bundle bundle = msg.getData();
                    if (bundle != null) {
                        Bitmap bitmap = bundle.getParcelable("bitmap");
                        if (bitmap != null) {
                            ImageView imageView = (ImageView) view;
                            view.setAlpha(0);
                            if ("FILL".equals(imageView.getTag())) {
                                int width = imageView.getWidth();
                                int bitmapWidth = bitmap.getWidth();
                                int bitmapHeight = bitmap.getHeight();
                                int height = width * bitmapHeight / bitmapWidth;
                                imageView.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT, height));
                            }
                            imageView.setImageBitmap(bitmap);
                            view.animate()
                                    .setDuration(300)
                                    .setInterpolator(new AnticipateInterpolator())
                                    .alpha(1)
                                    .start();
                        }
                    }
                    return false;
                }
            });
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("bitmap", bitmap);
                    Message message = new Message();
                    message.setData(bundle);
                    localHandler.sendMessage(message);
                }
            }).start();
        } else {
            final Handler httpHandler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    Bundle bundle = msg.getData();
                    if (bundle != null) {
                        Bitmap bitmap = bundle.getParcelable("bitmap");
                        if (bitmap != null) {
                            ImageView imageView = (ImageView) view;
                            view.setAlpha(0);
                            // 缓存至本地文件夹
                            ImageUtil.saveToLocal(bitmap, file);

                            if ("FILL".equals(imageView.getTag())) {
                                int width = imageView.getWidth();
                                int bitmapWidth = bitmap.getWidth();
                                int bitmapHeight = bitmap.getHeight();
                                int height = width * bitmapHeight / bitmapWidth;
                                imageView.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT, height));
                            }
                            imageView.setImageBitmap(bitmap);
                            view.animate()
                                    .setDuration(300)
                                    .setInterpolator(new AnticipateInterpolator())
                                    .alpha(1)
                                    .start();
                        }
                    }
                    return false;
                }
            });
            if (view instanceof ImageView) {
                final String url = String.valueOf(o);
                final int viewId = view.getId();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SimpleHttpURLConnection connection = new SimpleHttpURLConnection();
                        connection.archiveFile(url, new SimpleHttpCallback() {
                            @Override
                            public void onSuccess(String response) {

                            }

                            @Override
                            public void onSuccess(InputStream stream) {
                                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("bitmap", bitmap);
                                bundle.putInt("viewId", viewId);
                                Message message = new Message();
                                message.setData(bundle);
                                httpHandler.sendMessage(message);
                            }

                            @Override
                            public void onFailure(String response) {

                            }

                            @Override
                            public void onException(Exception e) {

                            }

                            @Override
                            public void onFinally() {

                            }
                        });
                    }
                }).start();
            }
        }
    }

    @BindingAdapter("imageUrlList")
    public static void handleImageUrlList(View view, Object o) {
        if (view instanceof ViewGroup && o instanceof String[]) {
            ViewGroup container = (ViewGroup) view;
            String[] urls = (String[]) o;
            Activity activity = ScreenManager.getInstance().currentActivity();
            ImageView imageView;
            for (String url : urls) {
                imageView = new ImageView(activity);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setTag("FILL");
                handleImageUrl(imageView, url);
                container.addView(imageView);
            }
        }
    }

    @BindingAdapter("item_price")
    public static void handlePrice(View view, Object o) {
        if (view instanceof NumberPickView) {
            NumberPickView numberPickView = (NumberPickView) view;
            BigDecimal decimal = BigDecimal.valueOf((double) o);
            numberPickView.setPrice(decimal.floatValue());
        }
    }

    @BindingAdapter("item_max")
    public static void handleStock(View view, Object o) {
        if (view instanceof NumberPickView) {
            NumberPickView numberPickView = (NumberPickView) view;
            numberPickView.setMax((int) o);
        }
    }

    @BindingAdapter("mini_item_price")
    public static void handleMiniPrice(View view, Object o) {
        if (view instanceof NumberPickMiniView) {
            NumberPickMiniView numberPickView = (NumberPickMiniView) view;
            BigDecimal decimal = BigDecimal.valueOf((double) o);
            numberPickView.setPrice(decimal.floatValue());
        }
    }

    @BindingAdapter("mini_item_max")
    public static void handleMiniStock(View view, Object o) {
        if (view instanceof NumberPickMiniView) {
            NumberPickMiniView numberPickView = (NumberPickMiniView) view;
            numberPickView.setMax((int) o);
        }
    }

    @BindingAdapter("mini_item_num")
    public static void handleMiniNum(View view, Object o) {
        if (view instanceof NumberPickMiniView) {
            NumberPickMiniView numberPickView = (NumberPickMiniView) view;
            numberPickView.setNum((int) o);
        }
    }

    @BindingAdapter("skuSpinnerList")
    public static void handleSkuList(View view, Object o) {
        if (view instanceof Spinner) {
            Spinner spinner = (Spinner) view;
            SKU[] skus = (SKU[]) o;
            String[] array = new String[skus.length];

            for (int index = 0; index < skus.length; index++) {
                array[index] = skus[index].toString();
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, array);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }
    }

    @BindingAdapter("orderItemlist")
    public static void handleOrderItemList(View view, Object o) {
        if (view instanceof RecyclerView && o instanceof CartItem[]) {
            CartItem[] items = (CartItem[]) o;
            RecyclerView recyclerView = (RecyclerView) view;
            Context context = ScreenManager.getInstance().currentActivity();
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new OrderListAdapter(items));
        }
    }

    @BindingAdapter("commentList")
    public static void handleCommentLst(View view, Object o) {
        if (view instanceof RecyclerView && o instanceof ArrayList) {
            ArrayList<Comment> items = (ArrayList<Comment>) o;
            RecyclerView recyclerView = (RecyclerView) view;
            Context context = ScreenManager.getInstance().currentActivity();
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new CommentListAdapter(items));
        }
    }
}
