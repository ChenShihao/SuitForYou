package com.cufe.suitforyou.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.cufe.suitforyou.R;
import com.cufe.suitforyou.action.UserAction;
import com.cufe.suitforyou.model.DetailItem;
import com.cufe.suitforyou.commons.LoginStatus;
import com.cufe.suitforyou.customview.NumberPickView;
import com.cufe.suitforyou.databinding.FragmentPage1DetailBinding;
import com.cufe.suitforyou.utils.MyUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Victor on 2016-09-08.
 */
public class PageDetailFragment extends Fragment {

    private DetailItem item;

    public PageDetailFragment() {
    }

    public static PageDetailFragment newInstance(DetailItem item) {
        PageDetailFragment fragment = new PageDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("item", new Gson().toJson(item));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentPage1DetailBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_page_1_detail, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            item = new Gson().fromJson(bundle.getString("item"), DetailItem.class);
            binding.setDetailItem(item);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Spinner spinner = (Spinner) view.findViewById(R.id.detail_item_sku_spinner);
        if (spinner != null) {
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    final int skuId = item.getSkus()[position].getId();
                    int stock = item.getSkus()[position].getStock();
                    final View rootView = parent.getRootView();
                    NumberPickView pickView = (NumberPickView) rootView.findViewById(R.id.detail_item_number_pick);
                    pickView.setMax(stock);
                    pickView.setVisibility(stock > 0 ? View.VISIBLE : View.GONE);

                    TextView stockTv = (TextView) rootView.findViewById(R.id.detail_item_stock_num);
                    stockTv.setText(String.valueOf(stock));
                    stockTv.setTag(stock > 0 ? 1 : 0);

                    String token = LoginStatus.getInstance(getActivity()).getToken();
                    new UserAction().searchCartItemsSkuIds(token, new Handler(new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.item_fab_add_to_cart);
                            boolean flag = true;
                            try {
                                if (msg.what < 0) {
                                    MyUtil.ShowToast("连接服务器失败");
                                    fab.setBackgroundColor(ResourcesCompat.getColor(getResources(), android.R.color.darker_gray, null));
                                    fab.setEnabled(false);
                                } else {
                                    Bundle bundle = msg.getData();
                                    if (bundle != null) {
                                        JSONObject response = new JSONObject(bundle.getString("response"));
                                        JSONArray data = response.getJSONArray("data");
                                        int[] skuIds = new Gson().fromJson(String.valueOf(data), int[].class);
                                        for (int foo : skuIds) {
                                            if (foo == skuId) {
                                                flag = false;
                                                break;
                                            }
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } finally {
                                fab.setTag(flag);
                            }
                            return false;
                        }
                    }));


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }
}
