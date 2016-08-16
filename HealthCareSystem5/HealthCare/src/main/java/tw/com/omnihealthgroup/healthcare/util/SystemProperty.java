package tw.com.omnihealthgroup.healthcare.util;

import android.util.Log;


public class SystemProperty {
    private static SystemProperty instance = null;

    private SystemProperty() {
    }

    public static SystemProperty getInstance() {
        if (instance == null) {
            synchronized (SystemProperty.class) {
                if (instance == null) {
                    instance = new SystemProperty();
                }
            }
        }
        return instance;
    }

    public static String URL_HOST() {
        Log.i("SystemProperty", "URL_HOST()=" + URL_HOST_EXTERNAL);
        return URL_HOST_EXTERNAL;
    }

    public static boolean isNonVerifyTest() {
        return NON_VERIFY_TEST;
    }

    private static final boolean NON_VERIFY_TEST = false;
    public static String URL_HOST_EXTERNAL = "http://service.telecare.com.tw/MessageHubWS"; // 2015/12/10 版
//    public static String URL_HOST_EXTERNAL = "http://doh.telecare.com.tw/MessageHubWS";
//    public static String URL_HOST_EXTERNAL = "https://139.223.23.100:443/MessageHubWS";
//    public static String URL_HOST_EXTERNAL = "https://60.250.13.204/MessageHubWS";
//    public static String URL_HOST_EXTERNAL = "http://172.31.6.92:443/MessageHubWS";


    public static String URL_MEMBER = URL_HOST() + "/Member.asmx";
    public static String URL_VITAL_SIGN = URL_HOST() + "/VitalSign.asmx";
    public static String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
    public static String OPERATION_NAME_REGISTER_USER = "RegisterUser";
    public static String OPERATION_NAME_VALIDATE_USER = "ValidateUser";
    public static String OPERATION_NAME_CHANGE_PASSWORD = "ChangePassword";
    public static String OPERATION_NAME_RESET_PASSWORD = "ResetPassword";
    public static String OPERATION_NAME_UPLOAD_VITAL_SIGN = "UploadVitalSign";
    public static String OPERATION_NAME_GET_VITAL_SIGN = "GetVitalSign";
    // @author james
    public static String OPERATION_NAME_REGISTER_GETAREALIST = "GetAreaList";
    public static String LOCATIONI_GETGPSDATA = "GetGPSData";
    public static String GET_NOTIFY_PREFER = "GetNotifyPreferences";
    public static String SET_NOTIFY_PREFER = "SetNotifyPreferences";
    public static String OPERATION_NAME_REGISTER_USERDETAILS = "RegisterUserWithDetails";
    public static String OPERATION_APP_REGISTER = "APPRegisterUser";

    public  int MIN_DISTANCE = 20;
}
