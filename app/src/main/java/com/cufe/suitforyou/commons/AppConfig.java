package com.cufe.suitforyou.commons;

import android.os.Environment;

import com.cufe.suitforyou.utils.MyUtil;

/**
 * Created by Victor on 2016-08-30.
 */
public class AppConfig {

    public static final String PHOTO_FILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/SuitForYou/";

    public static String LOGIN_URL = "http://jal.website:8080/";

    public static String ITEM_URL = "http://jal.website:8082/clothes";

    public static String ORDER_URL = "http://jal.website:8081/orders";

    public static String COMMENT_URL = "http://jal.website:8081/comments";

    public static String FILTER_URL = "http://jal.website:8082/filter";

    public static String PHOTO_URL = "http://192.168.191.1:8085/photoSearch";

    public static String SHOPPING_CART_URL = "http://jal.website:8083/carts";

    public static String ADDRESS_URL = "http://jal.website:8084/receives";

    static {
        MyUtil.makeDir(PHOTO_FILE_PATH);
    }

}
