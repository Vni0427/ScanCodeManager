package com.example.scancodemanager.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.scancodemanager.Adapter.GoodsAdapter;
import com.example.scancodemanager.Other.CodeMsgGoodsList;
import com.example.scancodemanager.Other.Goods;
import com.example.scancodemanager.R;
import com.example.scancodemanager.util.JsonCallback;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

public class TableFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewTable;
    private List<Goods> goodsList = new ArrayList<>();
    private GoodsAdapter adapter;
    private LinearLayoutManager layoutManager;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_table, container, false);

        //网络请求获取商品数据
        initGoods();

        //下拉刷新数据
        refreshConfig(view);

        return view;
    }

    /**
     * 配置下滑刷新
     * @param view
     */
    private void refreshConfig(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initGoods();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * 网络请求获得商品数据
     */
    private void initGoods() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String requestUrl = "http://llp.free.idcfengye.com/goods/appgoodslist";
                OkGo.<CodeMsgGoodsList>get(requestUrl)
                        .tag(this)
                        .execute(new JsonCallback<CodeMsgGoodsList>() {
                            @Override
                            public void onSuccess(Response<CodeMsgGoodsList> response) {
                                super.onSuccess(response);
                                recyclerViewTable = view.findViewById(R.id.recycler_view_table);
                                layoutManager = new LinearLayoutManager(getContext());
                                recyclerViewTable.setLayoutManager(layoutManager);
                                adapter = new GoodsAdapter(response.body().getGoodsList(),getContext(),getActivity());
                                recyclerViewTable.setAdapter(adapter);
                            }

                            @Override
                            public void onError(Response<CodeMsgGoodsList> response) {
                                super.onError(response);
                                Toast.makeText(getContext(), "访问数据库失败！", Toast.LENGTH_SHORT).show();
                            }

                        });
            }
        }).start();

    }

}
