package com.cufe.suitforyou.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cufe.suitforyou.R;
import com.cufe.suitforyou.customclass.MyImageTag;
import com.cufe.suitforyou.databinding.FragmentImageContainerBinding;
import com.google.gson.Gson;

/**
 * Created by Victor on 2016-09-03.
 */
public class ImageContainerFragment extends Fragment {

    public ImageContainerFragment() {
    }

    public static ImageContainerFragment newInstance(MyImageTag url) {
        ImageContainerFragment fragment = new ImageContainerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", new Gson().toJson(url));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentImageContainerBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_image_container, container, false);
        Bundle bundle = getArguments();
        if (bundle != null && binding != null) {
            MyImageTag url = new Gson().fromJson(bundle.getString("url"), MyImageTag.class);
            binding.setImage(url);
        }
        return binding != null ? binding.getRoot() : null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
