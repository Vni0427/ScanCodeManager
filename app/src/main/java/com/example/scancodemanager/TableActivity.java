package com.example.scancodemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.scancodemanager.Adapter.GoodsAdapter;
import com.example.scancodemanager.Other.CodeMsgGoodsList;
import com.example.scancodemanager.Other.Goods;
import com.example.scancodemanager.util.JsonCallback;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

public class TableActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GoodsAdapter adapter;
    private LinearLayoutManager layoutManager;
    private List<Goods> goodsList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        //获得box名
        Intent intent = getIntent();
        int typeFlag = intent.getIntExtra("type",1);
        String name = intent.getStringExtra("name");
        Goods goods = new Goods();
        if(typeFlag == 1){
            goods.setAddress(name);
        }else {
            goods.setTag(name);
        }

        //根据box名请求相应的gonds
        new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                String select = "http://llp.free.idcfengye.com/goods/appselectbyall";
                OkGo.<CodeMsgGoodsList>post(select)
                        .tag(this)
                        .upJson(gson.toJson(goods))
                        .execute(new JsonCallback<CodeMsgGoodsList>() {
                            @Override
                            public void onSuccess(Response<CodeMsgGoodsList> response) {
                                super.onSuccess(response);
                                if(response.body().getCode() ==1){
                                    goodsList = response.body().getGoodsList();
                                    setRecyclerViewTable(goodsList);
                                }else {
                                    Toast.makeText(TableActivity.this, "列表获取失败！", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Response<CodeMsgGoodsList> response) {
                                super.onError(response);
                                Toast.makeText(TableActivity.this, "网络请求失败！", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }).start();

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    /**
     * 配置好recyclerView
     * @param goodsList
     */
    private void setRecyclerViewTable( List<Goods> goodsList) {
        recyclerView = findViewById(R.id.recycler_view_table_activity);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new GoodsAdapter(goodsList,this,this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;

            default:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
