package com.cufe.suitforyou.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import com.cufe.suitforyou.R;
import com.cufe.suitforyou.action.OrderAction;
import com.cufe.suitforyou.adapter.AddCommentListAdapter;
import com.cufe.suitforyou.model.CartItem;
import com.cufe.suitforyou.model.CommentPost;
import com.cufe.suitforyou.commons.CommonCodes;
import com.cufe.suitforyou.commons.LoginStatus;
import com.cufe.suitforyou.commons.ScreenManager;
import com.cufe.suitforyou.databinding.ActivityAddCommentBinding;
import com.cufe.suitforyou.utils.MyUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddCommentActivity extends AppCompatActivity {

    private ActivityAddCommentBinding binding;

    private int orderId = -1;

    private ArrayList<CartItem> items;

    private ArrayList<CommentPost> commentPosts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ScreenManager.getInstance().pushActivity(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_comment);

        Toolbar toolbar = binding.addCommentToolbar;
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null && (orderId = extras.getInt("orderId")) > 0) {
            String token = LoginStatus.getInstance(this).getToken();
            new OrderAction().getOrder(orderId, token, new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    try {
                        if (msg.what < 0) {
                            MyUtil.ShowToast("连接服务器失败");
                            finish();
                        } else {
                            Bundle data = msg.getData();
                            if (data != null) {
                                String response = data.getString("response");
                                JSONObject order = new JSONObject(response).getJSONObject("data");
                                JSONArray array = order.getJSONArray("skus");
                                items = new Gson().fromJson(String.valueOf(array),
                                        new TypeToken<ArrayList<CartItem>>() {
                                        }.getType());
                                binding.addCommentRv.setLayoutManager(new LinearLayoutManager(AddCommentActivity.this));
                                binding.addCommentRv.setAdapter(new AddCommentListAdapter(items));
                                commentPosts = initCommentPosts(items);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            }));
        } else {
            MyUtil.ShowToast("好像哪里不对...");
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        ScreenManager.getInstance().popActivity(this);
        MyUtil.showProgressDialog(null, false, null);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        MyUtil.showProgressDialog(null, false, null);
        super.onPause();
    }

    private ArrayList<CommentPost> initCommentPosts(ArrayList<CartItem> items) {
        int size = items.size();
        ArrayList<CommentPost> commentPosts = new ArrayList<>();
        for (int index = 0; index < size; index++) {
            CartItem item = items.get(index);
            CommentPost template = new CommentPost(orderId, item.getSku().getId(), "系统默认好评", 5);
            commentPosts.add(template);
        }
        return commentPosts;
    }

    public void editScore(View view) {
        if (view instanceof RatingBar && view.getTag() instanceof Integer) {
            int position = (int) view.getTag();
            RatingBar ratingBar = (RatingBar) view;
            float rating = ratingBar.getRating();
            commentPosts.get(position).setScore(rating);
        }
    }

    public void editComment(View view) {
        if (view instanceof EditText && view.getTag() instanceof Integer) {
            int position = (int) view.getTag();
            EditText editText = (EditText) view;
            String comment = editText.getText().toString().trim();
            commentPosts.get(position).setComment(comment);
        }
    }

    public void submitComment(View view) {
        if (commentPosts != null) {

            Handler handler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    if (msg.what > 0) {
                        MyUtil.ShowToast("评论成功");
                    } else {
                        MyUtil.ShowToast("评论失败");
                    }
                    AddCommentActivity.this.setResult(CommonCodes.COMMENT_CODE);
                    finish();
                    return false;
                }
            });
            try {
                String token = LoginStatus.getInstance(this).getToken();
                JSONArray array = new JSONArray(new Gson().toJson(commentPosts));
                for (int index = 0; index < array.length(); index++) {
                    JSONObject object = array.getJSONObject(index);
                    MyUtil.showProgressDialog(this, true, "正在上传...");
                    new OrderAction().addComment(token, object, handler);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
