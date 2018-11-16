package utils.printer;

/**
 * Created by 张建宇 on 2017/11/21.
 */

public class StringFormatUtils {
    private static int DEF_LENGTH = 15;
    public static String getStringAtLen(String target, int len, boolean needDot) {
        String str = "";
        int tlenght = target.length();
        if (tlenght> len) {
            if (needDot) {
                str = target.substring(0, len - 3) + "...";
            } else {
                str = target.substring(0, len) ;
            }

        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(target);
            for (int i = 0; i < len - tlenght; i++) {
                builder.append(" ");
            }
            str = builder.toString();
        }
//        {"表":[{"PID":"837776","采购地":"北京市场","制单日期":"2017/11/21 11:16:27",
//                "公司":"ALLIC分公司","部门":"KINGSTAR北京","业务员":"范红燕","单据状态":"" +
//                "等待验货","收款":"现款现货","客户开票":"普通发票","供应商开票":"普通发票","" +
//                "供应商":"北京捷创特科技发展有限公司","采购员":"武常新","询价员":"",
//                "型号":"MPXHZ6250AC6T1","DeptID":"1029"}] }
        return str;
    }
    public static String getStringAtLen(String target) {

        return getStringAtLen(target, DEF_LENGTH, true);
    }
}
