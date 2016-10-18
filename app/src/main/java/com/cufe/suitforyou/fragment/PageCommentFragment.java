package com.cufe.suitforyou.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cufe.suitforyou.R;
import com.cufe.suitforyou.model.Comment;
import com.cufe.suitforyou.databinding.FragmentPage2CommentBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by Victor on 2016-09-12.
 */
public class PageCommentFragment extends Fragment {

    private ArrayList<Comment> comments;

    public PageCommentFragment() {
    }

    public static PageCommentFragment newInstance(ArrayList<Comment> comments) {
        PageCommentFragment fragment = new PageCommentFragment();
        String data = new Gson().toJson(comments);
        Bundle bundle = new Bundle();
        bundle.putString("data", data);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentPage2CommentBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_page_2_comment, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            comments = new Gson().fromJson(bundle.getString("data"),
                    new TypeToken<ArrayList<Comment>>() {
                    }.getType());
            binding.setComments(comments);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
