package com.b1b.js.erpandroid_nahuo.utils;

import android.support.annotation.NonNull;

import com.b1b.js.erpandroid_nahuo.entity.Baoguan;
import com.b1b.js.erpandroid_nahuo.entity.BaoguanDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 Created by 张建宇 on 2018/7/11. */
public class JsonParser {
    public static String parseBaoguanToJson(Baoguan baoguan) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("objname", baoguan.getObjname());
        object.put("objvalue", baoguan.getObjvalue());
        object.put("objYProvider", baoguan.getObjYProvider());
        object.put("objNProvider", baoguan.getObjNProvider());
        object.put("objPayID", baoguan.getObjPayID());
        object.put("objPayName", baoguan.getObjPayName());
        object.put("objBGCompare", baoguan.getObjBGCompare());
        object.put("objBGCompareName", baoguan.getObjBGCompareName());
        object.put("objXDID", baoguan.getObjXDID());
        object.put("objXDName", baoguan.getObjXDName());
        object.put("objAmount", baoguan.getObjAmount());
        List<BaoguanDetail> result = baoguan.getResult();
        JSONArray jsonArray = getFormatJarray(result);
        object.put("result", jsonArray);
        return object.toString();
    }

    @NonNull
    private static JSONArray getJsonArray2(List<BaoguanDetail> result) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        JSONObject innerObje = new JSONObject();
        for (int i = 0; i < result.size(); i++) {
            JSONObject object1 = new JSONObject();
            BaoguanDetail detail = result.get(i);
            object1.put("PartNo", detail.parno);
            object1.put("MFC", detail.factory);
            object1.put("BatchNo", detail.fengzhuang);
            object1.put("Pack", detail.pihao);
            object1.put("Quantity", detail.counts);
            object1.put("Description", detail.description);
            object1.put("Area", detail.getArea());
            object1.put("InvoiceCorp", detail.getInvoiceCorp());
            object1.put("Cost", detail.getCost());
            object1.put("CostRMB", detail.getCostRMB());
            object1.put("Weight", detail.getWeight());
            object1.put("CustomsCompanyID", detail.getCustomsCompanyID());
            object1.put("UseFor", detail.getUseFor());
            object1.put("RePrice", detail.getRePrice());
            object1.put("ProviderID", detail.getProviderID());
            object1.put("PayComID", detail.getPayComID());
            object1.put("PayComName", detail.getPayComName());
            object1.put("PartTypeID", detail.getPartTypeID());
            object1.put("PartTypeValue", detail.getPartTypeValue());
            object1.put("CorpID", detail.getCorpID());
            object1.put("DeptID", detail.getDeptID());
            object1.put("MakerID", detail.getMakerID());
            object1.put("EmployeeID", detail.getEmployeeID());
            object1.put("FormDetailID", detail.getFormDetailID());
            innerObje.put("data" + (i + 1), object1);
            jsonArray.put(innerObje);
        }
        return jsonArray;
    }

    public static JSONArray getFormatJarray(List<BaoguanDetail> result) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < result.size(); i++) {
            JSONObject object1 = new JSONObject();
            BaoguanDetail detail = result.get(i);
            object1.put("PartNo", detail.parno);
            object1.put("MFC", detail.factory);
            object1.put("BatchNo", detail.fengzhuang);
            object1.put("Pack", detail.pihao);
            object1.put("Quantity", detail.counts);
            object1.put("Description", detail.description);
            object1.put("Area", detail.getArea());
            object1.put("InvoiceCorp", detail.getInvoiceCorp());
            object1.put("Cost", detail.getCost());
            object1.put("CostRMB", detail.getCostRMB());
            object1.put("Weight", detail.getWeight());
            object1.put("CustomsCompanyID", detail.getCustomsCompanyID());
            object1.put("UseFor", detail.getUseFor());
            object1.put("RePrice", detail.getRePrice());
            object1.put("ProviderID", detail.getProviderID());
            object1.put("PayComID", detail.getPayComID());
            object1.put("PayComName", detail.getPayComName());
            object1.put("PartTypeID", detail.getPartTypeID());
            object1.put("PartTypeValue", detail.getPartTypeValue());
            object1.put("CorpID", detail.getCorpID());
            object1.put("DeptID", detail.getDeptID());
            object1.put("MakerID", detail.getMakerID());
            object1.put("EmployeeID", detail.getEmployeeID());
            object1.put("FormDetailID", detail.getFormDetailID());
            jsonArray.put(object1);
        }
        return jsonArray;
    }
}
