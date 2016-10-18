package com.cufe.suitforyou.customview;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.cufe.suitforyou.R;
import com.cufe.suitforyou.utils.MyUtil;

/**
 * Created by Victor on 2016-09-10.
 */
public class NumberPickView extends LinearLayout {

    private int num = 1;
    private int max = 999;
    private float price = 0;
    private TextView add;
    private TextView drop;
    private TextView numTv;
    private TextView totalPriceTv;
    private Context context;

    public NumberPickView(Context context, int num, int max) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.customview_number_pick, this, true);
        this.context = context;
        if (num >= 1)
            this.num = num;
        if (max >= 1)
            this.max = max;
    }

    public NumberPickView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.customview_number_pick, this, true);
        this.context = context;
    }

    public NumberPickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.customview_number_pick, this, true);
        this.context = context;
        setCustomAttributes(attrs);
    }

    public NumberPickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.customview_number_pick, this, true);
        this.context = context;
        setCustomAttributes(attrs);
    }

    public NumberPickView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LayoutInflater.from(context).inflate(R.layout.customview_number_pick, this, true);
        this.context = context;
        setCustomAttributes(attrs);
    }

    private void setCustomAttributes(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NumberPickView);
        num = typedArray.getInt(R.styleable.NumberPickView_item_num, 1);
        max = typedArray.getInt(R.styleable.NumberPickView_item_max, 999);
        price = typedArray.getFloat(R.styleable.NumberPickView_item_price, 0);
        typedArray.recycle();
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        numTv = (TextView) findViewById(R.id.number_pick_tv);
        totalPriceTv = (TextView) findViewById(R.id.number_pick_total_price);
        drop = (TextView) findViewById(R.id.number_picker_drop);
        add = (TextView) findViewById(R.id.number_pick_add);
        updateNumberTv();
        add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (num >= max) {
                    MyUtil.ShowToast("没那么多货啦");
                    return;
                }
                num++;
                updateNumberTv();
            }
        });
        drop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (num >= 2)
                    num--;
                else
                    num = 1;
                updateNumberTv();
            }
        });
        numTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPicker numberPicker = new NumberPicker(v.getContext());
                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(max);
                numberPicker.setValue(num);
                numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        num = newVal;
                    }
                });
                new AlertDialog.Builder(v.getContext())
                        .setTitle("选择数量")
                        .setView(numberPicker)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updateNumberTv();
                            }
                        })
                        .show();
            }
        });
    }

    private void updateNumberTv() {
        if (numTv != null) {
            numTv.setText(String.valueOf(num));
        }
        if (totalPriceTv != null) {
            totalPriceTv.setText(String.valueOf(num * price));
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
}
