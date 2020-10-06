package com.example.scancodemanager.Other;

public class Data {

    //商品一般使用EAN-１３码，管理物品用３９码
    private String goodsName;//商品名称
    private String barcode;//商品码
    private String price;//预估价格
    private String brand;//品牌
    private String supplier;//厂家
    private String standard;//规格

    public Data(String goodsName, String barcode,
                String brand, String supplier,
                String standard, String price) {
        this.goodsName = goodsName;
        this.barcode = barcode;
        this.brand = brand;
        this.supplier = supplier;
        this.standard = standard;
        this.price = price;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
