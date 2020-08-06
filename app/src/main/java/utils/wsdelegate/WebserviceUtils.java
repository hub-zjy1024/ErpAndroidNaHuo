package utils.wsdelegate;

import android.util.Log;
import com.b1b.js.erpandroid_nahuo.application.MyApp;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by 张建宇 on 2016/12/20.
 */

public class WebserviceUtils {


    public static final String NAMESPACE = "http://tempuri.org/";
    public static final String ROOT_URL = "http://vpn3.t996.top:7500/";
    //服务名，带后缀名的
    public static final String MartService = "MartService.svc";
    public static final String MartStock = "MartStock.svc";
    public static final String Login = "Login.svc";
    public static final String MyBasicServer = "MyBasicServer.svc";
    public static final String ForeignStockServer = "ForeignStockServer.svc";
    public static final String PMServer = "PMServer.svc";
    public static final String IC360Server = "IC360Server.svc";
    public static final String ChuKuServer = "ChuKuServer.svc";
    public static final String SF_Server = "SF_Server.svc";
    private static final int VERSION_10 = SoapEnvelope.VER10;
    private static final int VERSION_11 = SoapEnvelope.VER11;
    private static final int VERSION_12 = SoapEnvelope.VER12;
    static int MAX_LOG_LENGTH = 1000;
    /**
     * 扫描二维码的返回请求码
     */
    public static final int QR_REQUESTCODE = 100;
    /**
     * 设备No
     */
    public static String DeviceNo = "";
    /**
     * 交互码
     */
    public static String WebServiceCheckWord = "sdr454fgtre6e655t5rt4";
    /**
     * 设备ID
     */
    public static String DeviceID = "ZTE-T U880";

    private int soapVersion = VERSION_11;

    /**
     * 获取Url
     * 不能随意拼接，得自己根据wsdl文档
     *
     * @param serviceName 以svc结尾的service名称
     * @return
     */
    private static String getTransportSEtUrl(String serviceName) {
        //        return ROOT_URL + serviceName + "?singleWsdl";
        return ROOT_URL + serviceName;
    }

    /**
     * 不能随意拼接，得自己根据wsdl文档
     *
     * @param serviceName
     * @param methodName
     * @return
     */
    private static String getSoapAcction(String serviceName, String methodName) {
        Log.d("zjy", "WebserviceUtils1.java->getSoapAcction(): ==" + NAMESPACE + "I" + serviceName
                .substring(0, serviceName
                        .indexOf(".")) + "/" + methodName);
        return NAMESPACE + "I" + serviceName.substring(0, serviceName.indexOf(".")) + "/" + methodName;
    }

    /**
     * 获取SoapObject请求对象
     *
     * @param properties 方法的参数，有序，建议集合使用LinkedHashMap，如果没有，可以传入null
     * @param method     方法的名称
     * @return
     */
    private static SoapObject getRequest(LinkedHashMap<String, Object> properties, String method) {
        SoapObject request = new SoapObject(WebserviceUtils.NAMESPACE, method);
        if (properties != null) {
            // 设定参数
            Set<String> set = properties.keySet();
            for (String string : set) {
                request.addProperty(string, properties.get(string));
            }
        }
        return request;
    }

    /**
     * @param request
     * @param envolopeVesion {@link org.ksoap2.SoapEnvelope}
     * @param serviceName    以svc结尾的service名称
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    private static String getWcfResult(SoapObject request, int envolopeVesion, String
            serviceName) throws
            IOException, XmlPullParserException {
        int timeout = 30 * 1000;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(envolopeVesion);
        //.net开发的ws服务必须设置为true
        envelope.dotNet = true;
        //       envelope.bodyOut = request;
        envelope.setOutputSoapObject(request);
        //创建HttpTransportSE对象
        HttpTransportSE ht = new HttpTransportSE(getTransportSEtUrl(serviceName), timeout);
        //有些不需要传入soapAction，根据wsdl文档
        if (envolopeVesion == VERSION_12) {
            ht.call(null, envelope);
        } else {
            ht.call(getSoapAcction(serviceName, request.getName()), envelope);
        }
        Object sob = envelope.getResponse();
        if (sob == null) {
            MyApp.myLogger.writeBug("Soap response Object null,"+request.toString());
            return "response obj null";
        }
        if (sob instanceof SoapFault) {
            throw new IOException("error requeset", (SoapFault) sob);
        } else if (sob instanceof SoapObject) {
            Log.e("zjy", "WebserviceUtils->getWcfResult(): soapObj=="+request.toString());
            MyApp.myLogger.writeBug("Soap response is SoapObject,"+request.toString());
        } else if (sob instanceof SoapPrimitive) {

        } else {
            MyApp.myLogger.writeBug("Soap response is Unknow,"+request.toString());
        }
        String result = sob.toString();
        String logMsg = result;

        if(result.length()> MAX_LOG_LENGTH){
            logMsg = result.substring(0, MAX_LOG_LENGTH);
        }
        Log.d("zjy", "WebserviceUtils->getWcfResult(): ==" +logMsg);

        return result;
    }

    public static String getWcfResult(LinkedHashMap<String, Object> properties, String method,
                                      String serviceName) throws IOException,
            XmlPullParserException {
        SoapObject request = getRequest(properties, method);
        return getWcfResult(request, VERSION_11, serviceName);
    }
}
