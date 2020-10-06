package com.example.scancodemanager.Other;

public class Box {
    private int boximage;
    private String boxName;

    public Box(int boximage, String boxName) {
        this.boximage = boximage;
        this.boxName = boxName;
    }

    public int getBoximage() {
        return boximage;
    }

    public void setBoximage(int boximage) {
        this.boximage = boximage;
    }

    public String getBoxName() {
        return boxName;
    }

    public void setBoxName(String boxName) {
        this.boxName = boxName;
    }
}
