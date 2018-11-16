package com.b1b.js.erpandroid_nahuo.entity;

import java.util.List;

/**
 Created by 张建宇 on 2018/7/11. */
public class Baoguan {
    private String objname;
    private String objvalue;
    private String objYProvider;
    private String objNProvider;
    private String objPayID;
    private String objPayName;
    private String objBGCompare;
    private String objBGCompareName;
    private String objXDID;
    private String objXDName;
    private String objAmount;
    private List<BaoguanDetail> result;


    public Baoguan() {
    }

    public Baoguan(String objname, String objvalue, String objYProvider, String objNProvider, String objPayID, String
            objPayName, String objBGCompare, String objBGCompareName, String objXDID, String objAmount, List<BaoguanDetail> baoguans) {
        this.objname = objname;
        this.objvalue = objvalue;
        this.objYProvider = objYProvider;
        this.objNProvider = objNProvider;
        this.objPayID = objPayID;
        this.objPayName = objPayName;
        this.objBGCompare = objBGCompare;
        this.objBGCompareName = objBGCompareName;
        this.objXDID = objXDID;
        this.objAmount = objAmount;
        this.result = baoguans;
    }
    public List<BaoguanDetail> getResult() {
        return result;
    }

    public void setResult(List<BaoguanDetail> result) {
        this.result = result;
    }
    public String getObjname() {
        return objname;
    }

    public void setObjname(String objname) {
        this.objname = objname;
    }

    public String getObjvalue() {
        return objvalue;
    }

    public void setObjvalue(String objvalue) {
        this.objvalue = objvalue;
    }

    public String getObjYProvider() {
        return objYProvider;
    }

    public void setObjYProvider(String objYProvider) {
        this.objYProvider = objYProvider;
    }

    public String getObjNProvider() {
        return objNProvider;
    }

    public void setObjNProvider(String objNProvider) {
        this.objNProvider = objNProvider;
    }

    public String getObjPayID() {
        return objPayID;
    }

    public void setObjPayID(String objPayID) {
        this.objPayID = objPayID;
    }

    public String getObjPayName() {
        return objPayName;
    }

    public void setObjPayName(String objPayName) {
        this.objPayName = objPayName;
    }

    public String getObjBGCompare() {
        return objBGCompare;
    }

    public void setObjBGCompare(String objBGCompare) {
        this.objBGCompare = objBGCompare;
    }

    public String getObjBGCompareName() {
        return objBGCompareName;
    }

    public void setObjBGCompareName(String objBGCompareName) {
        this.objBGCompareName = objBGCompareName;
    }

    public String getObjXDID() {
        return objXDID;
    }

    public void setObjXDID(String objXDID) {
        this.objXDID = objXDID;
    }

    public String getObjAmount() {
        return objAmount;
    }

    public void setObjAmount(String objAmount) {
        this.objAmount = objAmount;
    }

    public List<BaoguanDetail> getBaoguans() {
        return result;
    }

    public void setBaoguans(List<BaoguanDetail> baoguans) {
        this.result = baoguans;
    }

    public String getObjXDName() {
        return objXDName;
    }

    public void setObjXDName(String objXDName) {
        this.objXDName = objXDName;
    }
}
