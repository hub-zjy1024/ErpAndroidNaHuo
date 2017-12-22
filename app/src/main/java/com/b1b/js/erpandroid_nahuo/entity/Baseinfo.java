package com.b1b.js.erpandroid_nahuo.entity;

import utils.StringFormatUtils;

/**
 * Created by 张建宇 on 2017/11/20.
 */

public class Baseinfo {
    public String parno;
    public String pihao;
    public String fengzhuang;
    public String factory;
    public String description;
    public String counts;
    public String notes;

    @Override
    public String toString() {
        return
                "型号=" + parno + "\n" +
                        StringFormatUtils.getStringAtLen( "批号=" +pihao) + "   " + StringFormatUtils.getStringAtLen( "数量="+counts) + "\n" +
                        StringFormatUtils.getStringAtLen( "封装=" +fengzhuang) + "   " + StringFormatUtils.getStringAtLen("厂家="+factory) + "\n" +
                        "描述=" + description + "\n" +
                        "备注='" + notes + '\'';
    }
}
