package com.cufe.suitforyou.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cufe.suitforyou.R;

/**
 * Created by Victor on 2016-09-10.
 */
public class NumberPickMiniView extends LinearLayout {

    private int num = 1;
    private int max = 999;
    private float price = 0;
    private TextView numTv;
    private Context context;

    public NumberPickMiniView(Context context, int num, int max) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.customview_number_pick_mini, this, true);
        this.context = context;
        if (num >= 1)
            this.num = num;
        if (max >= 1)
            this.max = max;
    }

    public NumberPickMiniView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.customview_number_pick_mini, this, true);
        this.context = context;
    }

    public NumberPickMiniView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.customview_number_pick_mini, this, true);
        this.context = context;
        setCustomAttributes(attrs);
    }

    public NumberPickMiniView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.customview_number_pick_mini, this, true);
        this.context = context;
        setCustomAttributes(attrs);
    }

    public NumberPickMiniView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LayoutInflater.from(context).inflate(R.layout.customview_number_pick_mini, this, true);
        this.context = context;
        setCustomAttributes(attrs);
    }

    private void setCustomAttributes(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NumberPickView);
        num = typedArray.getInt(R.styleable.NumberPickMiniView_mini_item_num, 1);
        max = typedArray.getInt(R.styleable.NumberPickMiniView_mini_item_max, 999);
        price = typedArray.getFloat(R.styleable.NumberPickMiniView_mini_item_price, 0);
        typedArray.recycle();
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        numTv = (TextView) findViewById(R.id.number_pick_mini_tv);
        updateNumberTv();
    }

    private void updateNumberTv() {
        if (numTv != null) {
            numTv.setText(String.valueOf(num));
        }
    }

    public void setNum(int num) {
        this.num = num;
        updateNumberTv();
    }

    public void setMax(int max) {
        this.max = max;
        num = 1;
        updateNumberTv();
    }

    public void setPrice(float price) {
        this.price = price;
        updateNumberTv();
    }

    public int getNum() {
        return num;
    }

    public int getMax() {
        return max;
    }

    public float getPrice() {
        return price;
    }
}
