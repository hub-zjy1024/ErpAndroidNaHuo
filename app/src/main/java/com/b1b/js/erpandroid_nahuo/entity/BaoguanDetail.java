package com.b1b.js.erpandroid_nahuo.entity;

/**
 Created by 张建宇 on 2018/7/11. */
public class BaoguanDetail extends Baseinfo {

    private String Area;
    private String pid;
    private String InvoiceCorp;
    private String Cost = "";
    private String CostRMB;
    /**
     默认
     */
    private String Weight = "0";
    private String CustomsCompanyID = "";
    private String RePrice;
    /**
     * 默认空
     */
    private String ProviderID = "";
    private String PayComID;
    private String PayComName;
    /**
     默认空
     */
    private String UseFor = "";
    private String PartTypeID;
    private String PartTypeValue;
    private String CorpID;
    private String DeptID;
    private String EmployeeID;
    private String MakerID;
    private String FormDetailID;

    public BaoguanDetail() {
    }


    public String getArea() {
        return Area;
    }

    public void setArea(String area) {
        Area = area;
    }


    public String getInvoiceCorp() {
        return InvoiceCorp;
    }

    public void setInvoiceCorp(String invoiceCorp) {
        InvoiceCorp = invoiceCorp;
    }

    public String getCost() {
        return Cost;
    }

    public void setCost(String cost) {
        Cost = cost;
    }

    public String getCostRMB() {
        return CostRMB;
    }

    public void setCostRMB(String costRMB) {
        CostRMB = costRMB;
    }

    public String getWeight() {
        return Weight;
    }

    public void setWeight(String weight) {
        Weight = weight;
    }

    public String getCustomsCompanyID() {
        return CustomsCompanyID;
    }

    public void setCustomsCompanyID(String customsCompanyID) {
        CustomsCompanyID = customsCompanyID;
    }

    public String getRePrice() {
        return RePrice;
    }

    public void setRePrice(String rePrice) {
        RePrice = rePrice;
    }

    public String getProviderID() {
        return ProviderID;
    }

    public void setProviderID(String providerID) {
        ProviderID = providerID;
    }

    public String getPayComID() {
        return PayComID;
    }

    public void setPayComID(String payComID) {
        PayComID = payComID;
    }

    public String getPayComName() {
        return PayComName;
    }

    public void setPayComName(String payComName) {
        PayComName = payComName;
    }

    public String getUseFor() {
        return UseFor;
    }

    public void setUseFor(String useFor) {
        UseFor = useFor;
    }

    public String getPartTypeID() {
        return PartTypeID;
    }

    public void setPartTypeID(String partTypeID) {
        PartTypeID = partTypeID;
    }

    public String getPartTypeValue() {
        return PartTypeValue;
    }

    public void setPartTypeValue(String partTypeValue) {
        PartTypeValue = partTypeValue;
    }

    public String getCorpID() {
        return CorpID;
    }

    public void setCorpID(String corpID) {
        CorpID = corpID;
    }

    public String getDeptID() {
        return DeptID;
    }

    public void setDeptID(String deptID) {
        DeptID = deptID;
    }

    public String getEmployeeID() {
        return EmployeeID;
    }

    public void setEmployeeID(String employeeID) {
        EmployeeID = employeeID;
    }

    public String getMakerID() {
        return MakerID;
    }

    public void setMakerID(String makerID) {
        MakerID = makerID;
    }

    public String getFormDetailID() {
        return FormDetailID;
    }

    public void setFormDetailID(String formDetailID) {
        FormDetailID = formDetailID;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}
