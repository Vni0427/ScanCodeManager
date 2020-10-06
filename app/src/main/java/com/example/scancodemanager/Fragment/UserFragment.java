package com.example.scancodemanager.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scancodemanager.Adapter.BoxAdapter;
import com.example.scancodemanager.MoreActivity;
import com.example.scancodemanager.Other.CodeMsgStringList;
import com.example.scancodemanager.R;
import com.example.scancodemanager.util.JsonCallback;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class UserFragment extends Fragment implements View.OnClickListener{

    private ImageView userIcon;
    private TextView userName;
    private Button orderBoxByAddress;
    private Button orderBoxByTag;
    private View view;
    private RecyclerView recyclerViewBox;
    private LinearLayout llUserMore;
    private BoxAdapter boxAdapter;
    private List<String> boxNameList = new ArrayList<>();

    private final int BOX_SPAN_COUNT = 3;
    private int typeFlag = 1;

    public UserFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user, container, false);

        llUserMore = view.findViewById(R.id.ll_user_more);
        userName = view.findViewById(R.id.tv_user_name);
        userIcon = view.findViewById(R.id.iv_user_icon);
        orderBoxByAddress = view.findViewById(R.id.btn_user_box_address);
        orderBoxByTag = view.findViewById(R.id.btn_user_box_tag);

        userIcon.setImageResource(R.drawable.icon_user);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", MODE_PRIVATE);
        userName.setText(sharedPreferences.getString("Account","userName"));

        orderBoxByAddress.setOnClickListener(this);
        orderBoxByTag.setOnClickListener(this);
        llUserMore.setOnClickListener(this);

        //默认获取地址作为仓库名
        getBoxNameList("address");

        return view;
    }

//    /**
//     * 配置recyclerview
//     * @param view
//     * @param boxList
//     */
//    private void setRecyclerViewBox(View view, List<Box> boxList) {
//        recyclerViewBox = view.findViewById(R.id.recycler_view_box);
//        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),BOX_SPAN_COUNT);
//        recyclerViewBox.setLayoutManager(layoutManager);
//        boxAdapter = new BoxAdapter(boxNameList,getContext(),getActivity(),typeFlag);
//        recyclerViewBox.setAdapter(boxAdapter);
//    }

    /**
     * 设置点击事件，分别获取address，tag作为仓库名
     * @param v
     */
    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_user_box_address:
                typeFlag = 1;
                orderBoxByAddress.setTextColor(R.color.colorMain);
                orderBoxByTag.setTextColor(R.color.black);
                getBoxNameList("address");
                break;
            case R.id.btn_user_box_tag:
                typeFlag = 2;
                orderBoxByAddress.setTextColor(R.color.black);
                orderBoxByTag.setTextColor(R.color.colorMain);
                getBoxNameList("tag");
                break;
            case R.id.ll_user_more:
                Intent intent = new Intent(getActivity(), MoreActivity.class);
                startActivity(intent);
            default:
                break;
        }
    }

    /**
     * 获取仓库名，并将其作为数据源
     * @param string
     */
    private void getBoxNameList(String string) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String orderByAddressUrl = "http://llp.free.idcfengye.com/goods/appquerytype";
                OkGo.<CodeMsgStringList>get(orderByAddressUrl)
                        .tag(this)
                        .params("type",string)
                        .execute(new JsonCallback<CodeMsgStringList>() {
                            @Override
                            public void onSuccess(Response<CodeMsgStringList> response) {
                                super.onSuccess(response);
                                if(response.body().getCode() == 1){
                                    boxNameList = response.body().getStringList();
                                    //boxNameList.set(boxNameList.size()-1,"null");

                                    recyclerViewBox = view.findViewById(R.id.recycler_view_box);
                                    GridLayoutManager layoutManager = new GridLayoutManager(getContext(),BOX_SPAN_COUNT);
                                    recyclerViewBox.setLayoutManager(layoutManager);
                                    boxAdapter = new BoxAdapter(boxNameList,getContext(),getActivity(),typeFlag);
                                    recyclerViewBox.setAdapter(boxAdapter);
                                }else {
                                    Toast.makeText(getContext(), "返回数据失败", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Response<CodeMsgStringList> response) {
                                super.onError(response);
                                Toast.makeText(getContext(), "网络请求失败", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }).start();

    }
}
