package com.example.scancodemanager.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scancodemanager.DisplayActivity;
import com.example.scancodemanager.Other.CodeMsg;
import com.example.scancodemanager.Other.Goods;
import com.example.scancodemanager.R;
import com.example.scancodemanager.util.JsonCallback;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.List;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.ViewHolder>{

    private List<Goods> goodsList;
    private Context context;
    private Activity activity;

    public GoodsAdapter(List<Goods> goodsList, Context context,Activity activity) {
        this.goodsList = goodsList;
        this.context = context;
        this.activity = activity;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView goodsCode;
        TextView gooodsName;
        TextView goodsNumber;
        View goodsView;

        public ViewHolder( View itemView) {
            super(itemView);
            goodsCode = itemView.findViewById(R.id.tv_goods_code);
            gooodsName = itemView.findViewById(R.id.tv_goods_name);
            goodsNumber = itemView.findViewById(R.id.tv_goods_number);
            goodsView = itemView;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.goods_item,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Goods goods = goodsList.get(position);
        viewHolder.gooodsName.setText(goods.getGoodsName());
        viewHolder.goodsCode.setText(goods.getBarcode());
        viewHolder.goodsNumber.setText(goods.getNumber() +"");
        viewHolder.goodsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DisplayActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("goods", goodsList.get(position));
                intent.putExtras(bundle);
                intent.putExtra("page", 3);
                context.startActivity(intent);
            }
        });
        viewHolder.goodsView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle("删除商品信息");
                dialog.setMessage("是否删除商品信息？");
                dialog.setCancelable(true);
                dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteGoods(goodsList.get(position));
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();
                return false;
            }

            private void deleteGoods(Goods goods) {
                String barcode = goods.getBarcode();
                Goods deleteGoods = new Goods();
                deleteGoods.setBarcode(barcode);
                String deleteUrl = "http://llp.free.idcfengye.com/goods/appdelete" ;
                Gson gson = new Gson();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkGo.<CodeMsg>post(deleteUrl)
                                .upJson(gson.toJson(deleteGoods))
                                .execute(new JsonCallback<CodeMsg>() {
                                    @Override
                                    public void onSuccess(Response<CodeMsg> response) {
                                        super.onSuccess(response);
                                        if(response.body().getCode() == 1){
                                            Toast.makeText(context, "删除成功！", Toast.LENGTH_SHORT).show();
                                        }else {
                                            Toast.makeText(context, "删除失败！", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    @Override
                                    public void onError(Response<CodeMsg> response) {
                                        super.onError(response);
                                        Toast.makeText(context, "网络请求失败！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }).start();

            }
        });

    }

    @Override
    public int getItemCount() {
        return goodsList.size();
    }


}
