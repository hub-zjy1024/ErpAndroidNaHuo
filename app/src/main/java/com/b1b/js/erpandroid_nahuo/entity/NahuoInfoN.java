package com.b1b.js.erpandroid_nahuo.entity;

/**
 * Created by 张建宇 on 2017/10/17.
 */

public class NahuoInfoN {

    //        "表":[{
    //        "PID":"811734", "委托人":"管理员", "指定人":"管理员", "ProviderID":"138386", "供应商":
    //        "测试供应商", "地址":"北京市海淀区彩和坊路1+1大厦503室", "电话":"62105300", "联系人":"何华荣", "型号":
    //        "TESTSCCG20170302001", "数量":"10", "单价":"10.0000", "批号":"+", "客户开票":"3",
    // "开票公司":
    //        "51", "开票公司信息":"普通发票", "备注":"测试", "NH_CheckInfo":"何华荣测试数据信息"
    //    }]}
    private String pid;
    private String weituiBy;
    private String id;
    private String providerID;
    private String providerName;
    private String address;
    private String phone;
    private String person;

    public NahuoInfoN(String pid, String weituiBy, String id, String providerID, String
            providerName, String address, String phone, String person, String partNO,
                      String counts, String danjia, String pihao, String kaiPiaoType,
                      String kaipiaoCompany, String kaiPiaoCompanyInfo, String note,
                      String detailInfo) {
        this.pid = pid;
        this.weituiBy = weituiBy;
        this.id = id;
        this.providerID = providerID;
        this.providerName = providerName;
        this.address = address;
        this.phone = phone;
        this.person = person;
        this.partNO = partNO;
        this.counts = counts;
        this.danjia = danjia;
        this.pihao = pihao;
        this.kaiPiaoType = kaiPiaoType;
        this.kaipiaoCompany = kaipiaoCompany;
        this.kaiPiaoCompanyInfo = kaiPiaoCompanyInfo;
        this.note = note;
        this.detailInfo = detailInfo;
    }

    private String partNO;
    private String counts;
    private String danjia;
    private String pihao;
    private String kaiPiaoType;
    private String kaipiaoCompany;
    private String kaiPiaoCompanyInfo;
    private String note;
    private String detailInfo;

    private boolean isChecked = false;
    public NahuoInfoN() {
    }

    public NahuoInfoN(String pid, String weituiBy, String id, String providerID, String
            providerName, String address, String phone, String partNO, String counts,
                      String danjia, String pihao, String kaiPiaoType, String
                             kaipiaoCompany, String kaiPiaoCompanyInfo, String note,
                      String detailInfo) {
        this.pid = pid;
        this.weituiBy = weituiBy;
        this.id = id;
        this.providerID = providerID;
        this.providerName = providerName;
        this.address = address;
        this.phone = phone;
        this.partNO = partNO;
        this.counts = counts;
        this.danjia = danjia;
        this.pihao = pihao;
        this.kaiPiaoType = kaiPiaoType;
        this.kaipiaoCompany = kaipiaoCompany;
        this.kaiPiaoCompanyInfo = kaiPiaoCompanyInfo;
        this.note = note;
        this.detailInfo = detailInfo;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getWeituiBy() {
        return weituiBy;
    }

    public void setWeituiBy(String weituiBy) {
        this.weituiBy = weituiBy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProviderID() {
        return providerID;
    }

    public void setProviderID(String providerID) {
        this.providerID = providerID;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPartNO() {
        return partNO;
    }

    public void setPartNO(String partNO) {
        this.partNO = partNO;
    }

    public String getCounts() {
        return counts;
    }

    public void setCounts(String counts) {
        this.counts = counts;
    }

    public String getDanjia() {
        return danjia;
    }

    public void setDanjia(String danjia) {
        this.danjia = danjia;
    }

    public String getPihao() {
        return pihao;
    }

    public void setPihao(String pihao) {
        this.pihao = pihao;
    }

    public String getKaiPiaoType() {
        return kaiPiaoType;
    }

    public void setKaiPiaoType(String kaiPiaoType) {
        this.kaiPiaoType = kaiPiaoType;
    }

    public String getKaipiaoCompany() {
        return kaipiaoCompany;
    }

    public void setKaipiaoCompany(String kaipiaoCompany) {
        this.kaipiaoCompany = kaipiaoCompany;
    }

    public String getKaiPiaoCompanyInfo() {
        return kaiPiaoCompanyInfo;
    }

    public void setKaiPiaoCompanyInfo(String kaiPiaoCompanyInfo) {
        this.kaiPiaoCompanyInfo = kaiPiaoCompanyInfo;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDetailInfo() {
        return detailInfo;
    }

    public void setDetailInfo(String detailInfo) {
        this.detailInfo = detailInfo;
    }

    @Override
    public String toString() {
        return
                "单据号='" + pid + '\'' +"\n" +
                        "委托人='" + weituiBy + '\'' + "\n" +
                        "指定人='" + id + '\'' +"\n" +
                        "供应商ID='" + providerID + '\'' +"\n" +
                        "供应商名称='" + providerName + '\'' +"\n" +
                        "拿货地址='" + address + '\'' +"\n" +
                        "联系电话='" + phone + '\'' +"\n" +
                        "联系人='" + person + '\'' +"\n" +
                        "型号='" + partNO + '\'' +"\n" +
                        "数量='" + counts + '\'' +"\n" +
                        "单价='" + danjia + '\'' +"\n" +
                        "批号='" + pihao + '\'' +"\n" +
                        "开票类型='" + kaiPiaoType + '\'' +"\n" +
                        "开票公司='" + kaipiaoCompany + '\'' +"\n" +
                        "开票公司信息='" + kaiPiaoCompanyInfo + '\'' +"\n" +
                        "备注='" + note + '\'' +"\n" +
                        "明细='" + detailInfo + '\'';
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
