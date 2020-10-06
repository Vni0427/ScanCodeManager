package com.example.scancodemanager.Other;


import android.os.Parcel;
import android.os.Parcelable;

public class Goods implements Parcelable {

    //商品一般使用EAN-１３码，管理物品用３９码
    private String goodsName;//商品名称
    private String barcode;//商品码
    private float price;//预估价格
    private String brand;//品牌
    private String supplier;//厂家
    private String standard;//规格
    private Integer number;//数目
    private String tag;//标签
    private String status;//状态
    private String address;//地址
    private String remark;//备注

    public Goods(String goodsName, String barcode,
                 float price, String brand,
                 String supplier, String standard,
                 Integer number, String tag,
                 String status,
                 String address, String remark) {
        this.goodsName = goodsName;
        this.barcode = barcode;
        this.price = price;
        this.brand = brand;
        this.supplier = supplier;
        this.standard = standard;
        this.number = number;
        this.tag = tag;
        this.status = status;
        this.address = address;
        this.remark = remark;
    }

    public Goods() {

    }

    protected Goods(Parcel in) {
        goodsName = in.readString();
        barcode = in.readString();
        price = in.readFloat();
        brand = in.readString();
        supplier = in.readString();
        standard = in.readString();
        if (in.readByte() == 0) {
            number = null;
        } else {
            number = in.readInt();
        }
        tag = in.readString();
        status = in.readString();
        address = in.readString();
        remark = in.readString();
    }

    public static final Creator<Goods> CREATOR = new Creator<Goods>() {
        @Override
        public Goods createFromParcel(Parcel in) {
            return new Goods(in);
        }

        @Override
        public Goods[] newArray(int size) {
            return new Goods[size];
        }
    };

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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
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

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "goodsName='" + goodsName + '\'' +
                ", barcode='" + barcode + '\'' +
                ", price=" + price +
                ", brand='" + brand + '\'' +
                ", supplier='" + supplier + '\'' +
                ", standard='" + standard + '\'' +
                ", number=" + number +
                ", tag='" + tag + '\'' +
                ", status='" + status + '\'' +
                ", address='" + address + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(goodsName);
        dest.writeString(barcode);
        dest.writeFloat(price);
        dest.writeString(brand);
        dest.writeString(supplier);
        dest.writeString(standard);
        if (number == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(number);
        }
        dest.writeString(tag);
        dest.writeString(status);
        dest.writeString(address);
        dest.writeString(remark);
    }
}
