package com.b1b.js.erpandroid_nahuo.entity;

/**
 Created by 张建宇 on 2018/7/6. */
public class CaigouBaoguanInfo extends Baseinfo {
    private String id;

    private String name;

    public CaigouBaoguanInfo(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
