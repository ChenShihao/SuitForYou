package com.cufe.suitforyou.customview;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.cufe.suitforyou.R;

/**
 * Created by Victor on 2016-09-04.
 */
public class EditTextDialogView extends LinearLayout {

    private EditText after;

    public EditTextDialogView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.customview_userinfo_dialog, this, true);
        after = (EditText) findViewById(R.id.userinfo_dialog_et);
    }

    public EditText getAfter() {
        return after;
    }

}
