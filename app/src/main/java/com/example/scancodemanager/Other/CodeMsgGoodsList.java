package com.example.scancodemanager.Other;

import java.util.List;

public class CodeMsgGoodsList {
    private int code;
    private String msg;
    private List<Goods> goodslist;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<Goods> getGoodsList() {
        return goodslist;
    }

    public void setGoodsList(List<Goods> goodsList) {
        this.goodslist = goodsList;
    }
}
