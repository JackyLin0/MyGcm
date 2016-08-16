package tw.com.omnihealthgroup.healthcare;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Clinic;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Consultation;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Examination;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Meeting;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Operation;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Personal;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.BioData;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.User;
import tw.com.omnihealthgroup.healthcare.myhealthcare.service.BluetoothLeService;
import tw.com.omnihealthgroup.healthcare.util.Rfc2898DeriveBytes;

public class WebServiceConnection {
    private static final String TAG = "WebServiceConnection";

    public static WebServiceConnection WEB_SERVICE_CONNECTION = new WebServiceConnection();
    private static ConnectivityManager connMgr;

    //    =====================================
    //    //嘉義案
    //    public static final String AuthServer = "http://192.168.3.82:81";
    //    //    public static final String AuthServer = "http://th-pp005.24drs.com";
    //    //    public static final String AuthServer = "http://192.168.3.99/ProxyHost/proxy.aspx?url=http://localhost:1907";
    //    public static final String ResourceServer = "http://192.168.3.82";
    //    //    public static final String ResourceServer = "http://th-pp008.24drs.com";
    //    public static final String PushServer = "http://th-pp008.24drs.com";

    //    (目前開發測試之ClientID可使用436a287f-75ab-46dd-a455-8dc82ccc5aaa)
    //    (目前開發測試之secret_key可使用D1C6272B8C2AA4D0D079B3321A8A5C12)

    //    public static final String CLIENT_ID = "436a287f-75ab-46dd-a455-8dc82ccc5aaa";
    //    public static final String CLIENT_SECRET = "D1C6272B8C2AA4D0D079B3321A8A5C12";
    //    =====================================
    //榮新案
    public static final String AuthServer = "http://th-pp005.24drs.com";
    public static final String ResourceServer = " http://th-pp002.24drs.com";
    public static final String PushServer = "http://th-pp008.24drs.com";

    //    client_id=8A4A65DD-6E61-444D-8A8E-9B9DDA931CF8
    //    secret_key=CF2171DAB9A57DFAB08298A22CF90ED0

    public static final String CLIENT_ID = "8A4A65DD-6E61-444D-8A8E-9B9DDA931CF8";
    public static final String CLIENT_SECRET = "CF2171DAB9A57DFAB08298A22CF90ED0";
    //    =====================================

    public static final String AuthRequest = "OAuth/AuthRequest";
    public static final String TokenRequest = "api/OAuthApi/TokenRequest";
    public static final String TokenExRequest = "api/OAuthApi/TokenExRequest";
    public static final String TokenInfo = "api/OAuthApi/TokenInfo";

    //    public static final String CLIENT_ID = "436a287f-75ab-46dd-a455-8dc82ccc5aaa";
    //    public static final String CLIENT_SECRET = "D1C6272B8C2AA4D0D079B3321A8A5C12";
    public static final String REDIRECT_URI = "http://localhost";

    public static final String addMeasureResource = "api/physiology/measure/add";
    public static final String queryMeasureResource = "api/physiology/measure/query";

    public static final String queryUserinfo = "api/user/info";
    public static final String editUserinfo = "api/user/info/edit";

    public static final String queryCaleinfo = "api/cale/info";
    public static final String queryCaletype = "api/cale/type/get";
    public static final String editCaleinfo = "api/cale/info/edit";
    public static final String delCaleinfo = "api/cale/info/del";

    public static final String pushdeviceReg = "DeviceReg";
    public static final String pushdeviceReg_valid = "DeviceReg/Valid";

    public static final String pushmessage = "PushMessage";
    public static final String pushmessage_valid = "PushMessage/Valid";

    public static final String pushdeviceout = "DeviceOut";
    public static final String pushdeviceout_valid = "DeviceOut/Valid";

    public static final int PLATFORM = 1;
    //遠距照護使用
    public static final int DATA_IS_NOT_UPLOAD = 0;
    public static final int DATA_ALREADY_UPLOAD = 1;
    //ActivityResult
    public static final String DELIVER_LOGIN_MESSAGE = "DELIVER_LOGIN_MESSAGE";
    public static final int REQUEST_CODE_SUCCESS = 0;
    //Account Text
    public static final String DELIVER_LOGIN_ACCOUNT = "DELIVER_LOGIN_ACCOUNT";
    //BIODATA
    public static final String BIODATA_DEVICE_TYPE_WEIGHT = "1";
    public static final String BIODATA_DEVICE_TYPE_BLOOD_GLUCOSE = "2";
    public static final String BIODATA_DEVICE_TYPE_BLOOD_PRESSURE = "3";
    //Upload inputType
    public static final String UPLOAD_INPUT_TYPE_DEVICE = "Device";
    public static final String UPLOAD_INPUT_TYPE_MANUAL = "Manual";
    /*Message Code一覽*/
    public static final String MessageCodeSuccess = "A01";//成功。帳號存在或動作執行成功。
    public static final String MessageCodeDataExist = "A11";//成功。資料已存在
    public static final String MessageCodeAccountNotExist = "E01";//帳號不存在。
    public static final String MessageCodePasswordWrong = "E02";//密碼錯誤，身分驗證失敗。
    public static final String MessageCodeAccountFormatWrong = "E03";//帳號格式錯誤
    public static final String MessageCodePasswordFormatWrong = "E04";//密碼格式錯誤
    public static final String MessageCodeAccountExist = "E05";//帳號已存在無法註冊
    public static final String MessageCodeLackData = "E11";//缺少必要資料
    public static final String MessageCodeDataFormatWrong = "E12";//資料格式錯誤
    public static final String MessageCodePhyDataFormatWrong = "E21";//生理資料格式錯誤
    public static final String MessageCodeError = "E99";//其他錯誤

    //檔案下載的WS位置
    private static final String DOWNLOAD_URL = "http://172.16.18.96/Jelly/IterCom_WebXP/IterCom_Web.asmx/";
    //檔案下載的API
    private static final String DATA_DOWNLOADURL = "getDownloadUrl";

    //電子表單的WS位置
    private static final String EFORM_URL = "http://tp3-pp001.24drs.com/throughCrossWEB/IterCom_Web.asmx/Intr_Com_Flow";

    //行事曆、訊息公告、推播設定的WS位置
    private static final String WS_URL = "http://tp3-pp001.24drs.com/ItemComApp/ItemComApp.asmx/";
    //行事曆與訊息公告的API
    private static final String SN_GetDataByPk = "retrieveMyMessageByPk";
    private static final String SN_GetDataByDateAndKind = "retrieveMyMessageByDateAndKind";
    private static final String SN_GetDataByPkAndKind = "retrieveMyMessageByPkAndKind";
    private static final String SN_GetDataLastestKindBMessage = "retrieveLastestKindBMessage";

    //向server註冊推播機碼的API
    private static final String NO_RegMachCode = "reRegMobileKey";
    //向server註銷推播機碼的API
    private static final String NO_UnRegMachCode = "unRegMobileKey";
    //設定推播服務開關的API
    private static final String NO_SetService = "setToggleServicePush";
    //取得推播服務開關設定的API
    private static final String NO_GetService = "getToggleServicePush";


    /**
     * GetAccessToken
     */
    public JSONObject getTokenRequest(String authCode, String CLIENT_ID, String CLIENT_SECRET) {
        //傳送資料
        JSONObject reguestobj = new JSONObject();
        JSONObject responseobj = new JSONObject();
        String POST_URL = AuthServer + "/" + TokenRequest;
        HttpURLConnection urlConnection = null;

        try {
            reguestobj.put("grant_type", "authorization_code");
            reguestobj.put("code", authCode);
            reguestobj.put("client_id", CLIENT_ID);
            reguestobj.put("client_secret", CLIENT_SECRET);
            Log.v(TAG + "reguestobj", reguestobj.toString());

            urlConnection = getUrlConnectionSetting(POST_URL, reguestobj.toString().getBytes("UTF-8"), null, null);
            responseobj = streamingJSONObjectData(urlConnection, reguestobj);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            //結束連線
            httpDisconnection(urlConnection);
        }
        return responseobj;
    }

    /**
     * RefreshToken
     */
    public JSONObject getTokenExRequest(String access_token, String refresh_token) {
        //傳送資料
        JSONObject reguestobj = new JSONObject();
        JSONObject responseobj = new JSONObject();
        String POST_URL = AuthServer + "/" + TokenExRequest;
        HttpURLConnection urlConnection = null;

        try {
            reguestobj.put("token_type", "Bearer");
            reguestobj.put("access_token", access_token);
            reguestobj.put("refresh_token", refresh_token);
            reguestobj.put("expires_in", "3600");

            Log.v(TAG + "reguestobj", reguestobj.toString());

            urlConnection = getUrlConnectionSetting(POST_URL, reguestobj.toString().getBytes("UTF-8"), null, null);
            responseobj = streamingJSONObjectData(urlConnection, reguestobj);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            //結束連線
            httpDisconnection(urlConnection);
        }
        return responseobj;
    }

    /**
     * GetTokenInfo
     */
    public JSONObject getTokenInfo(String access_token) {
        //傳送資料
        JSONObject reguestobj = new JSONObject();
        JSONObject responseobj = new JSONObject();
        String POST_URL = AuthServer + "/" + TokenInfo;
        HttpURLConnection urlConnection = null;

        try {
            reguestobj.put("access_token", access_token);

            Log.v(TAG + "reguestobj", reguestobj.toString());

            urlConnection = getUrlConnectionSetting(POST_URL, null, null, null);
            responseobj = streamingJSONObjectData(urlConnection, reguestobj);

            JSONObject result = responseobj.getJSONObject("result");
            String tok = result.getString("isTokenValid");

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            //結束連線
            httpDisconnection(urlConnection);
        }
        return responseobj;
    }


    /**
     * 基本資料API
     */
    public JSONObject queryUserinfo(String access_token) {
        //傳送資料
        JSONObject reguestobj = new JSONObject();
        JSONObject responseobj = new JSONObject();
        String POST_URL = ResourceServer + "/" + queryUserinfo;
        HttpURLConnection urlConnection = null;

        try {
            reguestobj.put("userUniqueId", "");
            reguestobj.put("userId", "");
            reguestobj.put("rfid", "");
            Log.v(TAG + "reguestobj", reguestobj.toString());

            urlConnection = getUrlConnectionSetting(POST_URL, reguestobj.toString().getBytes("UTF-8"), access_token, null);
            responseobj = streamingJSONObjectData(urlConnection, reguestobj);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            //結束連線
            httpDisconnection(urlConnection);
        }
        return responseobj;
    }

    /**
     * 基本資料API
     */
    public JSONObject editUserinfo(String access_token, List<User> listUserData) {
        //傳送資料
        JSONObject reguestobj = new JSONObject();
        JSONObject responseobj = new JSONObject();
        String POST_URL = ResourceServer + "/" + editUserinfo;
        HttpURLConnection urlConnection = null;

        try {
            for (int i = 0; i < listUserData.size(); i++) {
                User userdata = listUserData.get(i);
                reguestobj.put("userUniqueId", userdata.getUserUniqueId());
                reguestobj.put("rfid", userdata.getRfid());
                reguestobj.put("userIDNO", userdata.getUserIDNO());
                reguestobj.put("userName", userdata.getName());
                reguestobj.put("userEngName", userdata.getNickname());
                reguestobj.put("userSex", userdata.getGender());
                reguestobj.put("userBirthDay", userdata.getBirthday());
                reguestobj.put("userNationality", userdata.getUserNationality());
                reguestobj.put("userHomeTEL", userdata.getPhone());
                reguestobj.put("userMobile", userdata.getMobile());
                reguestobj.put("userEMail", userdata.getEmail());
                reguestobj.put("userBlood", userdata.getUserBlood());
                reguestobj.put("userRhType", userdata.getUserRhType());
                reguestobj.put("userMarried", userdata.getUserMarried());
            }
            Log.v(TAG + "reguestobj", reguestobj.toString());

            urlConnection = getUrlConnectionSetting(POST_URL, reguestobj.toString().getBytes("UTF-8"), access_token, null);
            responseobj = streamingJSONObjectData(urlConnection, reguestobj);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            //結束連線
            httpDisconnection(urlConnection);
        }
        return responseobj;
    }

    /**
     * queryMeasureResource
     */
    public JSONObject queryMeasureResource(String access_token, String type, String startTime, String endTime) {
        //傳送資料
        JSONObject reguestobj = new JSONObject();
        JSONObject responseobj = new JSONObject();
        String POST_URL = ResourceServer + "/" + queryMeasureResource;
        HttpURLConnection urlConnection = null;

        try {
            reguestobj.put("userUniqueId", "");
            reguestobj.put("userId", "");
            reguestobj.put("rfid", "");
            reguestobj.put("type", type);
            reguestobj.put("startTime", startTime);
            reguestobj.put("endTime", endTime);

            Log.v(TAG + "reguestobj", reguestobj.toString());

            urlConnection = getUrlConnectionSetting(POST_URL, reguestobj.toString().getBytes("UTF-8"), access_token, null);
            responseobj = streamingJSONObjectData(urlConnection, reguestobj);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            //結束連線
            httpDisconnection(urlConnection);
        }
        return responseobj;
    }

    /**
     * addMeasureResource
     */
    public JSONObject addMeasureResource(String access_token, List<BioData> listBioData, boolean isAuto) {
        //傳送資料
        JSONObject reguestobj = new JSONObject();
        JSONObject responseobj = new JSONObject();
        String POST_URL = ResourceServer + "/" + addMeasureResource;
        HttpURLConnection urlConnection = null;
        String uploadTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        try {
            reguestobj.put("userUniqueId", "");
            reguestobj.put("userId", "");
            reguestobj.put("rfid", "");
            reguestobj.put("gatewayId", "");
            reguestobj.put("uploadTime", uploadTime);
            JSONArray dataArray = new JSONArray();
            for (int i = 0; i < listBioData.size(); i++) {
                BioData bioData = listBioData.get(i);
                //                JSONObject dataobj = new JSONObject();
                if (!bioData.getDeviceTime().equals("2000-00-00 00:00:00")) {
                    //                        && !bioData.getDeviceType().equals("1")) { //待其他參數確定
                    //                    Log.v("TTT", String.valueOf(bioData.getBhp()));
                    //                    Log.v("TTT", String.valueOf(bioData.getBlp()));
                    //                    Log.v("TTT", String.valueOf(bioData.getPulse()));
                    //                    Log.v("TTT", String.valueOf(bioData.getDeviceTime()));
                    //                    Log.v("TTT", String.valueOf(bioData.getDeviceType()));
                    //                    Log.v("TTT", String.valueOf(bioData.getDeviceSn()));

                    if (bioData.getDeviceType().equals("3")) {
                        JSONObject dataobj = new JSONObject();
                        dataobj.put("type", "BP");
                        dataobj.put("time", bioData.getDeviceTime());
                        if (bioData.getDeviceId() != null) {
                            dataobj.put("deviceId", bioData.getDeviceMac());
                            //                        } else if (!isAuto) {
                            //                            dataobj.put("deviceId", bioData.getUserId());
                        } else {
                            dataobj.put("deviceId", "");
                        }
                        dataobj.put("mark", "");

                        List<Integer> valuesArray = new ArrayList<Integer>();
                        valuesArray.add(Integer.valueOf(bioData.getBhp()));
                        valuesArray.add(Integer.valueOf(bioData.getBlp()));
                        valuesArray.add(Integer.valueOf(bioData.getPulse()));
                        dataobj.put("values", valuesArray);

                        dataArray.put(dataobj);
                    }

                    if (bioData.getDeviceType().equals("2")) {
                        JSONObject dataobj = new JSONObject();
                        dataobj.put("type", "BS");
                        dataobj.put("time", bioData.getDeviceTime());
                        if (bioData.getDeviceId() != null) {
                            dataobj.put("deviceId", bioData.getDeviceMac());
                            //                        } else if (!isAuto) {
                            //                            dataobj.put("deviceId", bioData.getUserId());
                        } else {
                            dataobj.put("deviceId", "");
                        }

                        List<Integer> valuesArray = new ArrayList<Integer>();

                        if (bioData.getAc() != null) {
                            dataobj.put("mark", "AC");
                            valuesArray.add(Integer.valueOf(bioData.getAc()));
                            dataobj.put("values", valuesArray);
                        }

                        if (bioData.getPc() != null) {
                            dataobj.put("mark", "PC");
                            valuesArray.add(Integer.valueOf(bioData.getPc()));
                            dataobj.put("values", valuesArray);
                        }

                        if (bioData.getNm() != null) {
                            dataobj.put("mark", "NM");
                            valuesArray.add(Integer.valueOf(bioData.getNm()));
                            dataobj.put("values", valuesArray);
                        }

                        dataArray.put(dataobj);
                    }

                    if (bioData.getDeviceType().equals("1")
                            && bioData.getBodyHeight() != null) {
                        JSONObject dataobj = new JSONObject();
                        dataobj.put("type", "HG");
                        dataobj.put("time", bioData.getDeviceTime());
                        if (bioData.getDeviceId() != null) {
                            dataobj.put("deviceId", bioData.getDeviceMac());
                            //                        } else if (!isAuto) {
                            //                            dataobj.put("deviceId", bioData.getUserId());
                        } else {
                            dataobj.put("deviceId", "");
                        }
                        dataobj.put("mark", "");

                        List<Integer> valuesArray = new ArrayList<Integer>();
                        valuesArray.add(Integer.valueOf(bioData.getBodyHeight()));
                        dataobj.put("values", valuesArray);

                        dataArray.put(dataobj);
                    }

                    if (bioData.getDeviceType().equals("1")
                            && bioData.getBodyWeight() != null) {
                        JSONObject dataobj = new JSONObject();
                        dataobj.put("type", "WG");
                        dataobj.put("time", bioData.getDeviceTime());
                        if (bioData.getDeviceId() != null) {
                            dataobj.put("deviceId", bioData.getDeviceMac());
                            //                        } else if (!isAuto) {
                            //                            dataobj.put("deviceId", bioData.getUserId());
                        } else {
                            dataobj.put("deviceId", "");
                        }
                        dataobj.put("mark", "");

                        List<Integer> valuesArray = new ArrayList<Integer>();
                        valuesArray.add(Integer.valueOf(bioData.getBodyWeight()));
                        dataobj.put("values", valuesArray);

                        dataArray.put(dataobj);
                    }

                    if (bioData.getDeviceType().equals("4")) {
                        JSONObject dataobj = new JSONObject();
                        dataobj.put("type", "BT");
                        dataobj.put("time", bioData.getDeviceTime());
                        if (bioData.getDeviceId() != null) {
                            dataobj.put("deviceId", bioData.getDeviceMac());
                            //                        } else if (!isAuto) {
                            //                            dataobj.put("deviceId", bioData.getUserId());
                        } else {
                            dataobj.put("deviceId", "");
                        }
                        dataobj.put("mark", "");

                        List<Integer> valuesArray = new ArrayList<Integer>();
                        valuesArray.add(Integer.valueOf(bioData.getTemperature()));
                        dataobj.put("values", valuesArray);

                        dataArray.put(dataobj);
                    }

                    if (bioData.getDeviceType().equals("5")) {
                        JSONObject dataobj = new JSONObject();
                        dataobj.put("type", "HC");
                        dataobj.put("time", bioData.getDeviceTime());
                        if (bioData.getDeviceId() != null) {
                            dataobj.put("deviceId", bioData.getDeviceMac());
                            //                        } else if (!isAuto) {
                            //                            dataobj.put("deviceId", bioData.getUserId());
                        } else {
                            dataobj.put("deviceId", "");
                        }
                        dataobj.put("mark", "");

                        List<Integer> valuesArray = new ArrayList<Integer>();
                        valuesArray.add(Integer.valueOf(bioData.getHeadcircumference()));
                        dataobj.put("values", valuesArray);

                        dataArray.put(dataobj);
                    }
                }
            }
            reguestobj.put("data", dataArray);
            Log.v(TAG + "reguestobj", reguestobj.toString());
            String reguestobjStr = reguestobj.toString().replaceAll("\\\\/", "-");
            reguestobjStr = reguestobjStr.replaceAll("\\\"\\[", "[");
            reguestobjStr = reguestobjStr.replaceAll("\\]\\\"", "]");

            JSONObject reguestobj2 = new JSONObject(reguestobjStr);
            Log.v(TAG + "reguestobj2", reguestobj2.toString());

            urlConnection = getUrlConnectionSetting(POST_URL, reguestobj2.toString().getBytes("UTF-8"), access_token, null);
            responseobj = streamingJSONObjectData(urlConnection, reguestobj2);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            //結束連線
            httpDisconnection(urlConnection);
        }

        if (isAuto) {
            //  關閉BT3.0掃描與上傳服務
            BluetoothLeService.BLEService.closeBT30Service();
        }
        return responseobj;
    }

    /**
     * queryCaleinfo
     */
    public JSONObject queryCaleinfo(String access_token, String startTime, String endTime) {
        //傳送資料
        JSONObject reguestobj = new JSONObject();
        JSONObject responseobj = new JSONObject();
        String POST_URL = ResourceServer + "/" + queryCaleinfo;
        HttpURLConnection urlConnection = null;

        try {
            reguestobj.put("userUniqueId", "");//系統唯一識別碼
            reguestobj.put("userId", "");//身分證號碼
            reguestobj.put("rfid", "");//RFID卡號
            reguestobj.put("start", startTime);//資料啟始日期
            reguestobj.put("end", endTime);//資料結束日期
            reguestobj.put("userType", "1");//類型，1：會員 2：員工

            Log.v(TAG + "reguestobj", reguestobj.toString());

            urlConnection = getUrlConnectionSetting(POST_URL, reguestobj.toString().getBytes("UTF-8"), access_token, null);
            responseobj = streamingJSONObjectData(urlConnection, reguestobj);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            //結束連線
            httpDisconnection(urlConnection);
        }
        return responseobj;
    }

    /**
     * queryCaletype
     */
    public JSONObject queryCaletype(String access_token) {
        //傳送資料
        JSONObject reguestobj = new JSONObject();
        JSONObject responseobj = new JSONObject();
        String POST_URL = ResourceServer + "/" + queryCaleinfo;
        HttpURLConnection urlConnection = null;

        try {
            reguestobj.put("userUniqueId", "");//系統唯一識別碼
            reguestobj.put("userId", "");//身分證號碼
            reguestobj.put("rfid", "");//RFID卡號
            reguestobj.put("userType", "1");//類型，1：會員 2：員工

            Log.v(TAG + "reguestobj", reguestobj.toString());

            urlConnection = getUrlConnectionSetting(POST_URL, reguestobj.toString().getBytes("UTF-8"), access_token, null);
            responseobj = streamingJSONObjectData(urlConnection, reguestobj);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            //結束連線
            httpDisconnection(urlConnection);
        }
        return responseobj;
    }

    /**
     * editCaleinfo
     */
    public JSONObject editCaleinfo(String access_token, Object object, String eventType) {
        //傳送資料
        JSONObject reguestobj = new JSONObject();
        JSONObject responseobj = new JSONObject();
        String POST_URL = ResourceServer + "/" + editCaleinfo;
        HttpURLConnection urlConnection = null;

        try {
            reguestobj.put("userUniqueId", "");//系統唯一識別碼
            reguestobj.put("userId", "");//身分證號碼
            reguestobj.put("rfid", "");//RFID卡號
            reguestobj.put("userType", "1");//類型，1：會員 2：員工

            switch (eventType) {
                case "23":
                    Clinic clinicObj = (Clinic) object;

                    if (clinicObj.getClinicMsgPk() != null
                            && !clinicObj.getClinicMsgPk().equals("1")) {
                        reguestobj.put("calendarEventPK", clinicObj.getClinicMsgPk());//修改時填入活動識別碼。
                    } else {
                        reguestobj.put("calendarEventPK", "");//修改時填入活動識別碼。
                    }
                    reguestobj.put("eventType", eventType);//活動類型 識別碼

                    String StartDate_clinicObj = clinicObj.getClinicStartDate().replace(" / ", "-");
                    StartDate_clinicObj = StartDate_clinicObj.replace(" AM", "");
                    StartDate_clinicObj = StartDate_clinicObj.replace(" PM", "");

                    String endDate_clinicObj = clinicObj.getClinicEndDate().replace(" / ", "-");
                    endDate_clinicObj = endDate_clinicObj.replace(" AM", "");
                    endDate_clinicObj = endDate_clinicObj.replace(" PM", "");

                    reguestobj.put("eventStartDate", StartDate_clinicObj);//開始時間
                    reguestobj.put("eventEndDate", endDate_clinicObj);//結束時間

                    reguestobj.put("eventTitle", clinicObj.getClinicName());//標題
                    reguestobj.put("eventMemo", clinicObj.getClinicNote());//備註說明

                    break;
                case "24":
                    Consultation consultationObj = (Consultation) object;

                    if (consultationObj.getConsultationMsgPk() != null
                            && !consultationObj.getConsultationMsgPk().equals("1")) {
                        reguestobj.put("calendarEventPK", consultationObj.getConsultationMsgPk());//修改時填入活動識別碼。
                    } else {
                        reguestobj.put("calendarEventPK", "");//修改時填入活動識別碼。
                    }
                    reguestobj.put("eventType", eventType);//活動類型 識別碼

                    String StartDate_consultationObj = consultationObj.getConsultationStartDate().replace(" / ", "-");
                    StartDate_consultationObj = StartDate_consultationObj.replace(" AM", "");
                    StartDate_consultationObj = StartDate_consultationObj.replace(" PM", "");

                    String endDate_consultationObj = consultationObj.getConsultationEndDate().replace(" / ", "-");
                    endDate_consultationObj = endDate_consultationObj.replace(" AM", "");
                    endDate_consultationObj = endDate_consultationObj.replace(" PM", "");

                    reguestobj.put("eventStartDate", StartDate_consultationObj);//開始時間
                    reguestobj.put("eventEndDate", endDate_consultationObj);//結束時間

                    reguestobj.put("eventTitle", consultationObj.getConsultationName());//標題
                    reguestobj.put("eventMemo", consultationObj.getConsultationNote());//備註說明

                    break;
                case "25":
                    Operation operationObj = (Operation) object;

                    if (operationObj.getOperateMsgPk() != null
                            && !operationObj.getOperateMsgPk().equals("1")) {
                        reguestobj.put("calendarEventPK", operationObj.getOperateMsgPk());//修改時填入活動識別碼。
                    } else {
                        reguestobj.put("calendarEventPK", "");//修改時填入活動識別碼。
                    }
                    reguestobj.put("eventType", eventType);//活動類型 識別碼

                    String StartDate_operationObj = operationObj.getOperateStartDate().replace(" / ", "-");
                    StartDate_operationObj = StartDate_operationObj.replace(" AM", "");
                    StartDate_operationObj = StartDate_operationObj.replace(" PM", "");

                    String endDate_operationObj = operationObj.getOperateEndDate().replace(" / ", "-");
                    endDate_operationObj = endDate_operationObj.replace(" AM", "");
                    endDate_operationObj = endDate_operationObj.replace(" PM", "");

                    reguestobj.put("eventStartDate", StartDate_operationObj);//開始時間
                    reguestobj.put("eventEndDate", endDate_operationObj);//結束時間

                    reguestobj.put("eventTitle", operationObj.getOperateName());//標題
                    reguestobj.put("eventMemo", operationObj.getOperateNote());//備註說明

                    break;
                case "26":
                    Meeting meetingObj = (Meeting) object;

                    if (meetingObj.getMeetingMsgPk() != null
                            && !meetingObj.getMeetingMsgPk().equals("1")) {
                        reguestobj.put("calendarEventPK", meetingObj.getMeetingMsgPk());//修改時填入活動識別碼。
                    } else {
                        reguestobj.put("calendarEventPK", "");//修改時填入活動識別碼。
                    }
                    reguestobj.put("eventType", eventType);//活動類型 識別碼

                    String StartDate_meetingObj = meetingObj.getMeetingStartDate().replace(" / ", "-");
                    StartDate_meetingObj = StartDate_meetingObj.replace(" AM", "");
                    StartDate_meetingObj = StartDate_meetingObj.replace(" PM", "");

                    String endDate_meetingObj = meetingObj.getMeetingEndDate().replace(" / ", "-");
                    endDate_meetingObj = endDate_meetingObj.replace(" AM", "");
                    endDate_meetingObj = endDate_meetingObj.replace(" PM", "");

                    reguestobj.put("eventStartDate", StartDate_meetingObj);//開始時間
                    reguestobj.put("eventEndDate", endDate_meetingObj);//結束時間

                    reguestobj.put("eventTitle", meetingObj.getMeetingName());//標題
                    reguestobj.put("eventMemo", meetingObj.getMeetingNote());//備註說明

                    break;
                case "27":
                    Personal personalObj = (Personal) object;

                    if (personalObj.getPersonalMsgPk() != null
                            && !personalObj.getPersonalMsgPk().equals("1")) {
                        reguestobj.put("calendarEventPK", personalObj.getPersonalMsgPk());//修改時填入活動識別碼。
                    } else {
                        reguestobj.put("calendarEventPK", "");//修改時填入活動識別碼。
                    }
                    reguestobj.put("eventType", eventType);//活動類型 識別碼

                    String StartDate_personalObj = personalObj.getPersonalStartDate().replace(" / ", "-");
                    StartDate_personalObj = StartDate_personalObj.replace(" AM", "");
                    StartDate_personalObj = StartDate_personalObj.replace(" PM", "");

                    String endDate_personalObj = personalObj.getPersonalEndDate().replace(" / ", "-");
                    endDate_personalObj = endDate_personalObj.replace(" AM", "");
                    endDate_personalObj = endDate_personalObj.replace(" PM", "");

                    reguestobj.put("eventStartDate", StartDate_personalObj);//開始時間
                    reguestobj.put("eventEndDate", endDate_personalObj);//結束時間

                    reguestobj.put("eventTitle", personalObj.getPersonalName());//標題
                    reguestobj.put("eventMemo", personalObj.getPersonalNote());//備註說明

                    break;
                case "28":
                    Examination examinationObj = (Examination) object;

                    if (examinationObj.getExaminationMsgPk() != null
                            && !examinationObj.getExaminationMsgPk().equals("1")) {
                        reguestobj.put("calendarEventPK", examinationObj.getExaminationMsgPk());//修改時填入活動識別碼。
                    } else {
                        reguestobj.put("calendarEventPK", "");//修改時填入活動識別碼。
                    }
                    reguestobj.put("eventType", eventType);//活動類型 識別碼

                    String StartDate_examinationObj = examinationObj.getExaminationStartDate().replace(" / ", "-");
                    StartDate_examinationObj = StartDate_examinationObj.replace(" AM", "");
                    StartDate_examinationObj = StartDate_examinationObj.replace(" PM", "");

                    String endDate_examinationObj = examinationObj.getExaminationEndDate().replace(" / ", "-");
                    endDate_examinationObj = endDate_examinationObj.replace(" AM", "");
                    endDate_examinationObj = endDate_examinationObj.replace(" PM", "");

                    reguestobj.put("eventStartDate", StartDate_examinationObj);//開始時間
                    reguestobj.put("eventEndDate", endDate_examinationObj);//結束時間

                    reguestobj.put("eventTitle", examinationObj.getExaminationName());//標題
                    reguestobj.put("eventMemo", examinationObj.getExaminationNote());//備註說明

                    break;
            }

            Log.v(TAG + "reguestobj", reguestobj.toString());

            urlConnection = getUrlConnectionSetting(POST_URL, reguestobj.toString().getBytes("UTF-8"), access_token, null);
            responseobj = streamingJSONObjectData(urlConnection, reguestobj);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            //結束連線
            httpDisconnection(urlConnection);
        }
        return responseobj;
    }

    /**
     * delCaleinfo
     */
    public JSONObject delCaleinfo(String access_token, String calendarEventPK) {
        //傳送資料
        JSONObject reguestobj = new JSONObject();
        JSONObject responseobj = new JSONObject();
        String POST_URL = ResourceServer + "/" + delCaleinfo;
        HttpURLConnection urlConnection = null;

        try {
            reguestobj.put("userUniqueId", "");//系統唯一識別碼
            reguestobj.put("userId", "");//身分證號碼
            reguestobj.put("rfid", "");//RFID卡號
            reguestobj.put("calendarEventPK", calendarEventPK);//活動唯一識別碼

            Log.v(TAG + "reguestobj", reguestobj.toString());

            urlConnection = getUrlConnectionSetting(POST_URL, reguestobj.toString().getBytes("UTF-8"), access_token, null);
            responseobj = streamingJSONObjectData(urlConnection, reguestobj);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            //結束連線
            httpDisconnection(urlConnection);
        }
        return responseobj;
    }

    /**
     * pushdeviceReg
     */
    public JSONObject pushdeviceReg(String DeviceToken, String UserPK, String DeviceSerail, String ModelNumber) {
        //傳送資料
        //        JSONObject reguestobj = new JSONObject();
        StringBuilder reguestobj = new StringBuilder();
        JSONObject responseobj = new JSONObject();
        String POST_URL = PushServer + "/" + pushdeviceReg;
        HttpURLConnection urlConnection = null;

        try {
            reguestobj.append("SystemCode");
            reguestobj.append("=" + "Test01");
            reguestobj.append("&" + "UserPK");
            reguestobj.append("=" + UserPK);
            reguestobj.append("&" + "OSType");
            reguestobj.append("=" + "2");//1（IOS）， 2（Android）
            reguestobj.append("&" + "DeviceToken");
            reguestobj.append("=" + DeviceToken);
            reguestobj.append("&" + "DeviceCode");
            reguestobj.append("=" + DeviceSerail);
            reguestobj.append("&" + "ModelNumber");
            reguestobj.append("=" + ModelNumber);

            Log.v(TAG + "reguestobj", reguestobj.toString());

            urlConnection = getUrlConnectionSetting(POST_URL, reguestobj.toString().getBytes("UTF-8"), null, reguestobj);
            responseobj = streamingJSONObjectData(urlConnection, reguestobj);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            //結束連線
            httpDisconnection(urlConnection);
        }
        return responseobj;
    }

    /**
     * pushdeviceReg_valid
     */
    public String pushdeviceReg_valid(String pushId, String SHA_encrypt) {
        //傳送資料
        //        JSONObject reguestobj = new JSONObject();
        StringBuilder reguestobj = new StringBuilder();
        String responseobj = null;
        String POST_URL = PushServer + "/" + pushdeviceReg_valid;
        HttpURLConnection urlConnection = null;

        try {
            reguestobj.append("SystemCode");
            reguestobj.append("=" + "Test01");
            reguestobj.append("&" + "u");
            reguestobj.append("=" + URLEncoder.encode(pushId));
            reguestobj.append("&" + "v");
            reguestobj.append("=" + SHA_encrypt);

            Log.v(TAG + "reguestobj", reguestobj.toString());

            urlConnection = getUrlConnectionSetting(POST_URL, reguestobj.toString().getBytes("UTF-8"), null, reguestobj);
            responseobj = streamingStringBuilderData(urlConnection, reguestobj);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            //結束連線
            httpDisconnection(urlConnection);
        }
        return responseobj;
    }

    /**
     * pushmessage
     */
    public JSONObject pushmessage(JSONObject obj, String UserPKs) {
        //傳送資料
        //        JSONObject reguestobj = new JSONObject();
        StringBuilder reguestobj = new StringBuilder();
        JSONObject responseobj = new JSONObject();
        String POST_URL = PushServer + "/" + pushmessage;
        HttpURLConnection urlConnection = null;

        try {
            reguestobj.append("SystemCode");
            reguestobj.append("=" + "Test01");
            reguestobj.append("&" + "UserPKs");
            reguestobj.append("=" + UserPKs);
            reguestobj.append("&" + "JsonMessage");
            reguestobj.append("=" + obj.toString());

            Log.v(TAG + "reguestobj", reguestobj.toString());

            urlConnection = getUrlConnectionSetting(POST_URL, reguestobj.toString().getBytes("UTF-8"), null, reguestobj);
            responseobj = streamingJSONObjectData(urlConnection, reguestobj);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            //結束連線
            httpDisconnection(urlConnection);
        }
        return responseobj;
    }

    /**
     * pushmessage_valid
     */
    public String pushmessage_valid(String pushId, String SHA_encrypt) {
        //傳送資料
        //        JSONObject reguestobj = new JSONObject();
        StringBuilder reguestobj = new StringBuilder();
        String responseobj = null;
        String POST_URL = PushServer + "/" + pushmessage_valid;
        HttpURLConnection urlConnection = null;

        try {
            reguestobj.append("SystemCode");
            reguestobj.append("=" + "Test01");
            reguestobj.append("&" + "u");
            reguestobj.append("=" + URLEncoder.encode(pushId));
            reguestobj.append("&" + "v");
            reguestobj.append("=" + SHA_encrypt);

            Log.v(TAG + "reguestobj", reguestobj.toString());

            urlConnection = getUrlConnectionSetting(POST_URL, reguestobj.toString().getBytes("UTF-8"), null, reguestobj);
            responseobj = streamingStringBuilderData(urlConnection, reguestobj);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            //結束連線
            httpDisconnection(urlConnection);
        }
        return responseobj;
    }

    /**
     * pushdeviceout
     */
    public JSONObject pushdeviceout(String DeviceToken, String UserPK) {
        //傳送資料
        //        JSONObject reguestobj = new JSONObject();
        StringBuilder reguestobj = new StringBuilder();
        JSONObject responseobj = new JSONObject();
        String POST_URL = PushServer + "/" + pushdeviceout;
        HttpURLConnection urlConnection = null;

        try {
            reguestobj.append("SystemCode");
            reguestobj.append("=" + "Test01");
            reguestobj.append("&" + "UserPK");
            reguestobj.append("=" + UserPK);
            reguestobj.append("&" + "OSType");
            reguestobj.append("=" + "2");//1（IOS）， 2（Android）
            reguestobj.append("&" + "DeviceToken");
            reguestobj.append("=" + DeviceToken);

            Log.v(TAG + "reguestobj", reguestobj.toString());

            urlConnection = getUrlConnectionSetting(POST_URL, reguestobj.toString().getBytes("UTF-8"), null, reguestobj);
            responseobj = streamingJSONObjectData(urlConnection, reguestobj);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            //結束連線
            httpDisconnection(urlConnection);
        }
        return responseobj;
    }

    /**
     * pushdeviceout_valid
     */
    public String pushdeviceout_valid(String pushId, String SHA_encrypt) {
        //傳送資料
        //        JSONObject reguestobj = new JSONObject();
        StringBuilder reguestobj = new StringBuilder();
        String responseobj = null;
        String POST_URL = PushServer + "/" + pushdeviceout_valid;
        HttpURLConnection urlConnection = null;

        try {
            reguestobj.append("SystemCode");
            reguestobj.append("=" + "Test01");
            reguestobj.append("&" + "u");
            reguestobj.append("=" + URLEncoder.encode(pushId));
            reguestobj.append("&" + "v");
            reguestobj.append("=" + SHA_encrypt);

            Log.v(TAG + "reguestobj", reguestobj.toString());

            urlConnection = getUrlConnectionSetting(POST_URL, reguestobj.toString().getBytes("UTF-8"), null, reguestobj);
            responseobj = streamingStringBuilderData(urlConnection, reguestobj);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            //結束連線
            httpDisconnection(urlConnection);
        }
        return responseobj;
    }

    /**
     * 設定UrlConnection
     *
     * @param postUrl 要連線的WS位置
     * @return HttpURLConnection
     * @throws IOException
     */
    private HttpURLConnection getUrlConnectionSetting(String postUrl, byte[] content_length, String access_token, StringBuilder stringBuilder) throws IOException {
        HttpURLConnection urlConnection;
        URL url = new URL(postUrl);
        urlConnection = (HttpURLConnection) url.openConnection();
        Log.i(TAG, "開始連線");

        urlConnection.setDoOutput(true);    //設為可上傳
        urlConnection.setDoInput(true);    //設為可下載
        urlConnection.setUseCaches(false);  //使用Post方式不能使用暫存
        urlConnection.setRequestMethod("POST"); //訊息回傳方式
        urlConnection.setConnectTimeout(5 * 1000); //记得设置连接超时,如果网络不好,Android系统在超过默认时间会收回资源中断操作.
        if (stringBuilder != null) {
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");    //訊息回傳格式
        } else {
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");    //訊息回傳格式
        }
        if (content_length != null) {
            urlConnection.setRequestProperty("Content-Length", String.valueOf(content_length.length));
            Log.v(TAG + "Content-Length", Integer.toString(content_length.length));
        }
        if (access_token != null) {
            urlConnection.setRequestProperty("Authorization", "Bearer" + " " + access_token);
        }
        urlConnection.setChunkedStreamingMode(0);   //回傳長度未知

        return urlConnection;
    }

    /**
     * 串流WS回傳的JSONObject資料
     *
     * @param getUrlConnection UrlConnection物件
     * @param getReguestobj    送去WS的參數
     * @return JSONObject
     * @throws IOException
     * @throws JSONException
     */
    private JSONObject streamingJSONObjectData(HttpURLConnection getUrlConnection, JSONObject getReguestobj) throws IOException, JSONException {
        JSONObject getJsonObject = new JSONObject();

        // 獲得一個輸出串流，向伺服器寫入數據，默認情況下，系統不允許向伺服器輸出內容
        OutputStream outputStream = getUrlConnection.getOutputStream();    //開啟輸出串流
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));   //開啟寫入緩衝
        bufferedWriter.write(String.valueOf(getReguestobj));
        Log.v(TAG + "getReguestobj", getReguestobj.toString());
        bufferedWriter.flush();
        outputStream.close();   //關閉輸出串流
        bufferedWriter.close(); //關閉寫入緩衝

        int httpResult = getUrlConnection.getResponseCode();   // 呼叫此方法就不必再使用urlConnection.connect()方法
        switch (httpResult) {
            case HttpURLConnection.HTTP_OK: //200
                InputStream inputStream = getUrlConnection.getInputStream();   //開啟寫入串流
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));    //開啟讀取緩衝
                String getBufferedReader;
                StringBuilder stringBuilder = new StringBuilder();
                while ((getBufferedReader = bufferedReader.readLine()) != null) {
                    stringBuilder.append(getBufferedReader);
                }

                inputStream.close();    //關閉輸入串流
                bufferedReader.close(); //關閉讀取緩衝
                getJsonObject = new JSONObject(stringBuilder.toString());
                Log.d(TAG, String.valueOf(getJsonObject));
                break;

            case HttpURLConnection.HTTP_NO_CONTENT: //404
                Log.w(TAG, "HTTP RESULT:" + httpResult + "\t" + "NO_CONTENT");
                break;

            case HttpURLConnection.HTTP_CLIENT_TIMEOUT: //408
                Log.w(TAG, "HTTP RESULT:" + httpResult + "\t" + "CLIENT_TIMEOUT");
                break;

            case HttpURLConnection.HTTP_PRECON_FAILED: //412
                Log.w(TAG, "HTTP RESULT:" + httpResult + "\t" + "REQ_TOO_LONG");
                break;

            case HttpURLConnection.HTTP_REQ_TOO_LONG: //414
                Log.w(TAG, "HTTP RESULT:" + httpResult + "\t" + "REQ_TOO_LONG");
                break;

            case HttpURLConnection.HTTP_INTERNAL_ERROR: //500
                Log.w(TAG, "HTTP RESULT:" + httpResult + "\t" + "INTERNAL_ERROR");
                break;

            case HttpURLConnection.HTTP_GATEWAY_TIMEOUT: //504
                Log.w(TAG, "HTTP RESULT:" + httpResult + "\t" + "GATEWAY_TIMEOUT");
                break;
        }
        return getJsonObject;
    }


    /**
     * 串流WS回傳的JSONObject資料
     *
     * @param getUrlConnection UrlConnection物件
     * @param getReguestobj    送去WS的參數
     * @return JSONObject
     * @throws IOException
     * @throws JSONException
     */
    private JSONObject streamingJSONObjectData(HttpURLConnection getUrlConnection, StringBuilder getReguestobj) throws IOException, JSONException {
        JSONObject getJsonObject = new JSONObject();

        // 獲得一個輸出串流，向伺服器寫入數據，默認情況下，系統不允許向伺服器輸出內容
        OutputStream outputStream = getUrlConnection.getOutputStream();    //開啟輸出串流
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));   //開啟寫入緩衝
        bufferedWriter.write(String.valueOf(getReguestobj));
        Log.v(TAG + "getReguestobj", getReguestobj.toString());
        bufferedWriter.flush();
        outputStream.close();   //關閉輸出串流
        bufferedWriter.close(); //關閉寫入緩衝

        int httpResult = getUrlConnection.getResponseCode();   // 呼叫此方法就不必再使用urlConnection.connect()方法
        switch (httpResult) {
            case HttpURLConnection.HTTP_OK: //200
                InputStream inputStream = getUrlConnection.getInputStream();   //開啟寫入串流
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));    //開啟讀取緩衝
                String getBufferedReader;
                StringBuilder stringBuilder = new StringBuilder();
                while ((getBufferedReader = bufferedReader.readLine()) != null) {
                    stringBuilder.append(getBufferedReader);
                }

                inputStream.close();    //關閉輸入串流
                bufferedReader.close(); //關閉讀取緩衝
                getJsonObject = new JSONObject(stringBuilder.toString());
                Log.d(TAG, String.valueOf(getJsonObject));
                break;

            case HttpURLConnection.HTTP_NO_CONTENT: //404
                Log.w(TAG, "HTTP RESULT:" + httpResult + "\t" + "NO_CONTENT");
                break;

            case HttpURLConnection.HTTP_CLIENT_TIMEOUT: //408
                Log.w(TAG, "HTTP RESULT:" + httpResult + "\t" + "CLIENT_TIMEOUT");
                break;

            case HttpURLConnection.HTTP_PRECON_FAILED: //412
                Log.w(TAG, "HTTP RESULT:" + httpResult + "\t" + "REQ_TOO_LONG");
                break;

            case HttpURLConnection.HTTP_REQ_TOO_LONG: //414
                Log.w(TAG, "HTTP RESULT:" + httpResult + "\t" + "REQ_TOO_LONG");
                break;

            case HttpURLConnection.HTTP_INTERNAL_ERROR: //500
                Log.w(TAG, "HTTP RESULT:" + httpResult + "\t" + "INTERNAL_ERROR");
                break;

            case HttpURLConnection.HTTP_GATEWAY_TIMEOUT: //504
                Log.w(TAG, "HTTP RESULT:" + httpResult + "\t" + "GATEWAY_TIMEOUT");
                break;
        }
        return getJsonObject;
    }


    /**
     * 串流WS回傳的JSONObject資料
     *
     * @param getUrlConnection UrlConnection物件
     * @param getReguestobj    送去WS的參數
     * @return JSONObject
     * @throws IOException
     * @throws JSONException
     */
    private String streamingStringBuilderData(HttpURLConnection getUrlConnection, StringBuilder getReguestobj) throws IOException, JSONException {
        //        JSONObject getJsonObject = new JSONObject();
        String getJsonObject = null;
        // 獲得一個輸出串流，向伺服器寫入數據，默認情況下，系統不允許向伺服器輸出內容
        OutputStream outputStream = getUrlConnection.getOutputStream();    //開啟輸出串流
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));   //開啟寫入緩衝
        bufferedWriter.write(String.valueOf(getReguestobj));
        Log.v(TAG + "getReguestobj", getReguestobj.toString());
        bufferedWriter.flush();
        outputStream.close();   //關閉輸出串流
        bufferedWriter.close(); //關閉寫入緩衝

        int httpResult = getUrlConnection.getResponseCode();   // 呼叫此方法就不必再使用urlConnection.connect()方法
        switch (httpResult) {
            case HttpURLConnection.HTTP_OK: //200
                InputStream inputStream = getUrlConnection.getInputStream();   //開啟寫入串流
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));    //開啟讀取緩衝
                String getBufferedReader;
                StringBuilder stringBuilder = new StringBuilder();
                while ((getBufferedReader = bufferedReader.readLine()) != null) {
                    stringBuilder.append(getBufferedReader);
                }

                inputStream.close();    //關閉輸入串流
                bufferedReader.close(); //關閉讀取緩衝
                getJsonObject = stringBuilder.toString();
                Log.d(TAG, String.valueOf(getJsonObject));
                break;

            case HttpURLConnection.HTTP_NO_CONTENT: //404
                Log.w(TAG, "HTTP RESULT:" + httpResult + "\t" + "NO_CONTENT");
                break;

            case HttpURLConnection.HTTP_CLIENT_TIMEOUT: //408
                Log.w(TAG, "HTTP RESULT:" + httpResult + "\t" + "CLIENT_TIMEOUT");
                break;

            case HttpURLConnection.HTTP_PRECON_FAILED: //412
                Log.w(TAG, "HTTP RESULT:" + httpResult + "\t" + "REQ_TOO_LONG");
                break;

            case HttpURLConnection.HTTP_REQ_TOO_LONG: //414
                Log.w(TAG, "HTTP RESULT:" + httpResult + "\t" + "REQ_TOO_LONG");
                break;

            case HttpURLConnection.HTTP_INTERNAL_ERROR: //500
                Log.w(TAG, "HTTP RESULT:" + httpResult + "\t" + "INTERNAL_ERROR");
                break;

            case HttpURLConnection.HTTP_GATEWAY_TIMEOUT: //504
                Log.w(TAG, "HTTP RESULT:" + httpResult + "\t" + "GATEWAY_TIMEOUT");
                break;
        }
        return getJsonObject;
    }

    /**
     * 結束HTTP連線
     */
    private void httpDisconnection(HttpURLConnection urlConnection) {
        if (urlConnection != null) {
            urlConnection.disconnect();
            Log.i(TAG, "結束連線");
        }
    }

    /**
     * 設定瀏覽屬性
     */
    public void setBrowserProperty(WebView myBrowser) {
        WebSettings browserSetting = myBrowser.getSettings();
        // 支援放大縮小
        browserSetting.setSupportZoom(true);
        // 顯示放踏大縮小工具
        browserSetting.setBuiltInZoomControls(true);
        // 顯示放大縮小視點
        browserSetting.setUseWideViewPort(true);
        browserSetting.setLoadWithOverviewMode(true);
        // 支援javascript
        browserSetting.setJavaScriptEnabled(true);
        browserSetting.setSupportMultipleWindows(true);
        browserSetting.setJavaScriptCanOpenWindowsAutomatically(true);
    }

    /**
     * 設定WebView參數
     */
    public void setWebviewProperty(final WebView myBrowser, final Context context) {
        myBrowser.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("file")) {
                    return false; // file:/// 交由 WebView 處理
                }

                if (!url.startsWith("http")) {
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    } catch (Throwable e) {
                        Log.v(TAG + "Ian", url, e);
                    }

                    return true; // true = consumed url and webview will not handle url
                }
                return false;
            }

            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                event.getAction();
                return super.shouldOverrideKeyEvent(view, event);
            }

            //            @Override
            //            public void onPageFinished(WebView view, String url) {
            //                super.onPageFinished(view, url);
            //                pb.dismiss();
            //            }
        });

        myBrowser.setWebChromeClient(new WebChromeClient() {
            //            public void onProgressChanged(WebView view, int progress) {
            //                try {
            //                    //pd.setProgress(0);
            //                    //activity.setTitle("loading...");
            //                    getActivity().setProgress(progress * 100);
            //                    pd.setTitle(getString(R.string.webview_loading_title));
            //                    pd.setMessage(getString(R.string.webview_loading_body));
            //                    pd.show();
            //                    Log.i("info", "progress:" + progress);
            //                    //pd.incrementProgressBy(progress);
            //                    if (progress == 100 /*&& pd.isShowing()*/) {
            //                        getActivity().setTitle(R.string.app_name);
            //                        pd.dismiss();
            //                    }
            //                } catch (Exception e) {
            //                    e.printStackTrace();
            //                }
            //            }

            // 遇到JS的window.open或target_blank時呼叫此方法
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog,
                                          boolean isUserGesture, Message resultMsg) {
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(myBrowser);
                resultMsg.sendToTarget();
                return true;
            }
        });

        myBrowser.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                // TODO Auto-generated method stub
                Log.v(TAG + "info", url);
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });
    }

    public String DateCompare(String s1, String s2) {
        String re = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1 = sdf.parse(s1);
            Date d2 = sdf.parse(s2);

            if (Math.abs(((d1.getTime() - d2.getTime()) / (60 * 1000))) <= 30) {
                Log.v(TAG + "math < 1800", String.valueOf(Math.abs(((d1.getTime() - d2.getTime()) / (60 * 1000)))));
                re = "true";
            } else if (Math.abs(((d1.getTime() - d2.getTime()) / (60 * 1000))) > 30
                    && Math.abs(((d1.getTime() - d2.getTime()) / (60 * 1000))) <= 55) {
                Log.v(TAG + "math < 3600", String.valueOf(Math.abs(((d1.getTime() - d2.getTime()) / (60 * 1000)))));
                re = "exToke";
            } else {
                Log.v(TAG + "math > 3600", String.valueOf(Math.abs(((d1.getTime() - d2.getTime()) / (60 * 1000)))));
                re = "false";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    /**
     * 確認網路狀態
     *
     * @return true：開啟，false：關閉
     */
    //    public static boolean isMobileNetworkAvailable(Context con) {
    //        if (null == connMgr) {
    //            connMgr = (ConnectivityManager) con.getSystemService(con.CONNECTIVITY_SERVICE);
    //        }
    //        NetworkInfo wifiInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    //        NetworkInfo mobileInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    //        if (wifiInfo != null && wifiInfo.isAvailable()) {
    //            return true;
    //        } else if (mobileInfo != null && mobileInfo.isAvailable()) {
    //            return true;
    //        } else {
    //            return false;
    //        }
    //    }
    public boolean isOnline(Context context) {
        boolean hasNetwork = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null) {
            hasNetwork = cm.getActiveNetworkInfo().isConnectedOrConnecting();
        }
        return hasNetwork;

    }

    /**
     * 用來判斷服務是否執行
     *
     * @param context   get Application Context
     * @param className 判斷服務的名稱：package name + class name
     * @return true 有執行, false 沒有執行
     */
    public static boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;
        Log.i(TAG, " className : " + className);
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            //Log.i(TAG, "Service Name : " + serviceList.get(i).service.getClassName());
            if (serviceList.get(i).service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }
        Log.i(TAG, "service is running?==" + isRunning);
        return isRunning;
    }

    /**
     * 通用的訊息顯示AlertDialog
     *
     * @param title   標題
     * @param message 內文訊息
     * @return 實作MessageDialog
     */
    public AlertDialog getMessageDialog(String title, String message, Context context) {
        //產生一個Builder物件
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //設定Dialog的標題
        // builder.setIcon(R.drawable.alert_icon);
        builder.setTitle(title);
        //設定Dialog的內容
        builder.setMessage(message);
        //設定Positive按鈕資料
        builder.setPositiveButton(context.getString(R.string.msg_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //按下按鈕時顯示快顯
            }
        });
        //利用Builder物件建立AlertDialog
        return builder.create();
    }

    /**
     * 欄位未填寫的警告視窗
     *
     * @param alertString 警告訊息
     */
    public void editTextAlertDialog(Context context, String alertString) {
        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(context);
        dialogBuilder.setMessage(alertString);
        android.support.v7.app.AlertDialog alertDialog = dialogBuilder.show();
        TextView alertMessageText = (TextView) alertDialog.findViewById(android.R.id.message);
        alertMessageText.setGravity(Gravity.CENTER);
        alertDialog.show();
    }

    public void initToast(Context context, String message) {
        LinearLayout ll = new LinearLayout(context);
        ll.setBackgroundColor(Color.BLACK);
        TextView tv = new TextView(context);
        tv.setTextSize(18);
        tv.setPadding(10, 5, 10, 5);
        tv.setTextColor(Color.WHITE);
        tv.setText(message);

        ll.addView(tv);

        Toast toastStart = new Toast(context);
        toastStart.setGravity(Gravity.CENTER, 0, 0);
        toastStart.setDuration(Toast.LENGTH_LONG);
        toastStart.setView(ll);
        toastStart.show();
    }

    /**
     * 設置折線圖的樣式
     *
     * @param chart
     */
    public void setLineChart(LineChart chart) {

        //        chart.setBackgroundColor(getResources().getColor(R.color.ECECEC));
        chart.setDescription("");//设置图表描述信息
        //        chart.setDescriptionColor(Color.RED);
        //        chart.setDescriptionPosition(150f,150f);
        //        chart.setDescriptionTextSize(16f);
        //        chart.setNoDataTextDescription("Data 为空！");
        //        chart.setOnChartGestureListener(this);
        chart.setScaleEnabled(true); // 是否可以缩放
        chart.setTouchEnabled(true);  // 设置是否可以触摸
        //        chart.setScaleXEnabled(true);
        //        chart.setScaleYEnabled(true);
        //        chart.setPinchZoom(true);
        chart.setDragEnabled(true); // 是否可以拖拽
        chart.setDrawGridBackground(false); //设置是否显示表格
        //        chart.setGridBackgroundColor(Color.BLUE);
        //        chart.setDrawBorders(true);
        //        chart.setBorderColor(Color.YELLOW);
        //        chart.setBorderWidth(4f);
        //        chart.setDoubleTapToZoomEnabled(false); // 设置双击不进行缩放
        chart.setAutoScaleMinMaxEnabled(false);
        //        chart.setViewPortOffsets(0f, 0f, 0f, 0f);

        // 设置X轴
        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // 设置X轴的位置
        //        xAxis.setTypeface(mTf); // 设置字体
        xAxis.setDrawAxisLine(true); // X轴坐标轴线
        xAxis.setDrawGridLines(false);// X轴格線
        xAxis.setDrawLabels(true); // X坐标轴数组Label
        xAxis.setAxisLineWidth(2f);
        xAxis.setTextSize(15f);
        //        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setSpaceBetweenLabels(1);
        xAxis.setYOffset(10f);

        // 设置x轴的LimitLine，index是从0开始的
        //                LimitLine xLimitLine = new LimitLine(4f,"xL 测试");
        //                xLimitLine.setLineColor(Color.GREEN);
        //                xAxis.addLimitLine(xLimitLine);
        //                xAxis.setDrawLimitLinesBehindData(true);

        // 获得左侧侧坐标轴
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setDrawAxisLine(true); // 左侧坐标轴线
        leftAxis.setDrawGridLines(false);// y轴格線
        leftAxis.setDrawLabels(true); // 左侧坐标轴数组Label
        leftAxis.setTextSize(15f);
        leftAxis.setAxisLineWidth(2f);
        //        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        //        leftAxis.setXOffset(30f);
        //        leftAxis.setXOffset(-10f);

        //
        //        // 设置左侧的LimitLine
        //        LimitLine ll1 = new LimitLine(140f, "飯前血糖過高");
        //        ll1.setLineWidth(2f);
        //        ll1.setLineColor(0xFFE8D7B5);
        //        ll1.enableDashedLine(10f, 10f, 0f);
        //        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        //        ll1.setTextSize(10f);
        //
        //        LimitLine ll2 = new LimitLine(70f, "飯前血糖過低");
        //        ll2.setLineWidth(2f);
        //        ll2.setLineColor(0xFFE8D7B5);
        //        ll2.enableDashedLine(10f, 10f, 0f);
        //        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        //        ll2.setTextSize(10f);
        //
        //        LimitLine ll3 = new LimitLine(200f, "飯後血糖過高");
        //        ll3.setLineWidth(2f);
        //        ll3.setLineColor(0xFFE2B8B1);
        //        ll3.enableDashedLine(10f, 10f, 0f);
        //        ll3.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        //        ll3.setTextSize(10f);
        //
        //        LimitLine ll4 = new LimitLine(90f, "飯後血糖過低");
        //        ll4.setLineWidth(2f);
        //        ll4.setLineColor(0xFFE2B8B1);
        //        ll4.enableDashedLine(10f, 10f, 0f);
        //        ll4.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        //        ll4.setTextSize(10f);
        //
        //        leftAxis.addLimitLine(ll1);
        //        leftAxis.addLimitLine(ll2);
        //        leftAxis.addLimitLine(ll3);
        //        leftAxis.addLimitLine(ll4);

        //设置右侧坐标轴
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        //        rightAxis.setDrawAxisLine(false); // 右侧坐标轴线
        //        rightAxis.setDrawLabels(false); // 右侧坐标轴数组Label
        //        rightAxis.setTypeface(mTf);
        //        rightAxis.setLabelCount(5);
        //        rightAxis.setDrawGridLines(false);


        // 图例
        Legend legend = chart.getLegend();
        //        legend.setEnabled(false);
        //        legend.setTextColor(Color.GREEN);
        legend.setTextSize(13f);
        //        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        //        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);
        //        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);
        //        legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        //        legend.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
        //        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        legend.setFormSize(18f);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setForm(Legend.LegendForm.LINE);
    }

    /**
     * creat txt to record old regID
     * <p/>
     * //	 * @param filek
     *
     * @return regIDstr
     */
    public static void writeToFile(File file, String regId) {
        System.out.println("write regID = " + regId);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(regId.getBytes());
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (Exception e) {

            }
        }
    }

    public static String readFromFile(File fin) {
        StringBuilder data = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(fin), "utf-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data.toString();
    }

    //推播設定中向SERVER註冊GCM用的固定參數
    public static final String APP_ID = "ShowME15";

    public static LinkedList<JSONObject> GET_JSON_OBJ_LIST = new LinkedList<>();
    public static String FILE_NAME = null;
    private String lastMsgPk;
    public static int JSON_ARRAY_SIZE;
    private ArrayList<String> eFormNumList = new ArrayList<>();
    private ArrayList<String> eFormDateList = new ArrayList<>();
    private ArrayList<String> eFormEventList = new ArrayList<>();
    private int downloadDataSize;

    /**
     * 取得行事曆和訊息公告的資料
     *
     * @param sn       功能類別
     * @param userId   MIS帳號
     * @param kindCode 撈取資料的KindCode
     * @param fromDate 撈取資料的起始時間
     * @param toDate   撈取資料的結束時間
     * @throws IOException, JSONException
     */
    public void getScheduleAndNewsData(String sn, String userId, String kindCode, String fromDate, String toDate) throws IOException, JSONException {
        HttpURLConnection urlConnection = null;
        String POST_URL = WS_URL + SN_GetDataByDateAndKind;
        JSONObject jsonObject = new JSONObject();

        try {
            //  URL連線設定
            urlConnection = getUrlConnectionSetting(POST_URL, null, null, null);

            // post請求的參數
            jsonObject.put("MyId", userId);
            jsonObject.put("MsgKind", kindCode);
            jsonObject.put("fromDate", fromDate);
            jsonObject.put("toDate", toDate);

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "資料讀取失敗");
        } finally {
            httpDisconnection(urlConnection);
            //撈取會診排程或新藥通知
            resloveJSONObject(streamingJSONObjectData(urlConnection, jsonObject));
            if (sn.equals("Schedule")) {
                switch (kindCode) {
                    case "C":
                        //撈取手術排程
                        getScheduleAndNewsData(sn, userId, "O", fromDate, toDate);
                        break;
                    //撈取門診排程
                    //撈取會議排程
                }
                //                resloveJSONObject(streamingJSONObjectData(urlConnection, jsonObject));
                //            } else if (sn.equals("News")) {
                //                switch (kindCode) {
                //                    case "M":
                //                        //撈取體系推播
                //                        getScheduleAndNewsData(sn, userId, "S", fromDate, toDate);
                //                        break;
                //                    case "S":
                //                        //撈取健保法規
                //                        getScheduleAndNewsData(sn, userId, "H", fromDate, toDate);
                //                        break;
                //                    case "H":
                //                        //撈取行政公告
                //                        getScheduleAndNewsData(sn, userId, "D", fromDate, toDate);
                //                        break;
                //                    case "D":
                //                        //撈取秘書叮嚀
                //                        getScheduleAndNewsData(sn, userId, "E", fromDate, toDate);
                //                        break;
                //                    case "E":
                //                        //撈取營運焦點
                //                        getOperationFocusData();
                //                        break;
                //                }
                //                resloveJSONObject(streamingJSONObjectData(urlConnection, jsonObject));
            }
        }
    }

    public void getOperationFocusData() throws IOException, JSONException {
        HttpURLConnection urlConnection = null;
        String POST_URL = WS_URL + SN_GetDataLastestKindBMessage;
        JSONObject jsonObject = new JSONObject();
        try {
            //  URL連線設定
            urlConnection = getUrlConnectionSetting(POST_URL, null, null, null);

            // post請求的參數
            jsonObject.put("MyId", "");
            jsonObject.put("AreaList", "");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "資料讀取失敗");
        } finally {
            httpDisconnection(urlConnection);
            resloveJSONObjectToFile(streamingJSONObjectData(urlConnection, jsonObject));
        }
    }

    /**
     * 取得電子表單資料
     *
     * @param userId MIS帳號
     */
    public void getEformListData(String userId) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = getUrlConnectionSetting(EFORM_URL, null, null, null);
            // post請求的參數
            JSONObject reguestobj = new JSONObject();
            reguestobj.put("ACT_NO", userId);

            JSONObject getJsonObj = streamingJSONObjectData(urlConnection, reguestobj);
            System.out.println(TAG + getJsonObj);

            // 取得JSON物件陣列
            JSONArray jsonArray = getJsonObj.getJSONArray("d");
            JSON_ARRAY_SIZE = jsonArray.length();

            // 取得JSON物件列表，並分類放入List內
            for (int i = 0; i < JSON_ARRAY_SIZE; i++) {
                JSONObject responseJsonObj = (JSONObject) jsonArray.get(i);

                eFormNumList.add(responseJsonObj.getString("apply_no"));    //申請單號
                eFormDateList.add(responseJsonObj.getString("signdate"));   //申請日期
                eFormEventList.add(responseJsonObj.getString("flow_desc")); //表單名稱
            }
            System.out.println("Lancer: " + jsonArray.length());

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            //結束連線
            httpDisconnection(urlConnection);
        }
    }

    /**
     * 向server註冊推播機碼
     *
     * @param appId        傳送給後台的固定參數
     * @param userId       MIS帳號
     * @param machCode     推播所使用的機碼
     * @param deviceSerail 手機的唯一識別碼
     */
    public void getRegisterMachCode(String appId, String userId, String machCode, String deviceSerail) {
        //傳送資料
        JSONObject reguestobj = new JSONObject();
        String POST_URL = WS_URL + "/" + NO_RegMachCode;
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = getUrlConnectionSetting(POST_URL, null, null, null);
            reguestobj.put("AppId", base64Str(appId));
            reguestobj.put("OsType", "android");
            reguestobj.put("Official", true);
            reguestobj.put("UserId", base64Str(userId));
            reguestobj.put("MachCode", machCode);
            reguestobj.put("serial", deviceSerail);

            Log.d("RegisterReguestObj", String.valueOf(reguestobj));

            JSONObject getjson = streamingJSONObjectData(urlConnection, reguestobj);

            if (getjson.getJSONObject("d").getBoolean("Result")) {
                Log.i(TAG + "getRegisterMachCode", "Register Success");
            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            //結束連線
            httpDisconnection(urlConnection);
        }
    }

    /**
     * 向server註銷推播機碼
     *
     * @param machCode     推播所使用的機碼
     * @param deviceSerail 手機的唯一識別碼
     */
    public void getUnRegisterMachCode(String machCode, String deviceSerail) {
        //傳送資料
        JSONObject reguestobj = new JSONObject();
        String POST_URL = WS_URL + "/" + NO_UnRegMachCode;
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = getUrlConnectionSetting(POST_URL, null, null, null);

            reguestobj.put("machCode", machCode);
            reguestobj.put("inSerial", deviceSerail);

            //            Log.d("UnRegisterReguestObj", String.valueOf(reguestobj));

            JSONObject getjson = streamingJSONObjectData(urlConnection, reguestobj);

            if (getjson.getJSONObject("d").getBoolean("Result")) {
                Log.i(TAG + "getUnRegisterMachCode", "UnRegister Success");
            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            //結束連線
            httpDisconnection(urlConnection);
        }
    }

    /**
     * 取得推播服務的開關設定
     *
     * @param AppId  傳送給後台的固定參數
     * @param UserId MIS帳號
     * @return JSONArray
     */
    public JSONArray getNotifyService(String AppId, String UserId) {
        //傳送資料
        JSONObject reguestobj = new JSONObject();
        String POST_URL = WS_URL + "/" + NO_GetService;
        HttpURLConnection urlConnection = null;
        String[] inService = {"B", "C", "D", "E", "H", "M", "O", "S"};
        JSONArray result = new JSONArray();

        try {
            for (int i = 0; i < inService.length; i++) {
                urlConnection = getUrlConnectionSetting(POST_URL, null, null, null);

                reguestobj.put("iAppId", base64Str(AppId));
                reguestobj.put("iUserId", base64Str(UserId));
                reguestobj.put("inService", inService[i]);

                Log.v(TAG + "reguestobj", String.valueOf(reguestobj));

                JSONObject getjson = streamingJSONObjectData(urlConnection, reguestobj).getJSONObject("d");
                String resultjson = getjson.getString("Message");
                Log.v(TAG + "GGGjos", resultjson);

                result.put(resultjson);
                Log.v(TAG + "result", String.valueOf(result));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //結束連線
            httpDisconnection(urlConnection);
        }
        return result;
    }

    /**
     * 設置推播服務的開關設定
     *
     * @param inAppId   傳送給後台的固定參數
     * @param inUserId  MIS帳號
     * @param inService 要設置的推播服務類別
     * @param enable    開 / 關
     * @return String
     */
    public String setNotifyService(String inAppId, String inUserId, String inService, boolean enable) {
        //傳送資料
        JSONObject reguestobj = new JSONObject();
        String POST_URL = WS_URL + "/" + NO_SetService;
        String code = null;
        HttpURLConnection urlConnection = null;
        String result = null;

        switch (inService) {
            case "營運焦點":
                code = "B";
                break;
            case "會診排程":
                code = "C";
                break;
            case "行政公告":
                code = "D";
                break;
            case "秘書叮嚀":
                code = "E";
                break;
            case "健保法規":
                code = "H";
                break;
            case "新藥通知":
                code = "M";
                break;
            case "開刀排程":
                code = "O";
                break;
            case "體系推播":
                code = "S";
                break;
        }

        try {
            urlConnection = getUrlConnectionSetting(POST_URL, null, null, null);

            reguestobj.put("inAppId", base64Str(inAppId));
            reguestobj.put("inUserId", base64Str(inUserId));
            reguestobj.put("inService", code);
            reguestobj.put("enable", enable);

            Log.v(TAG + "setNotifyServiceObj", String.valueOf(reguestobj));

            JSONObject getjson = streamingJSONObjectData(urlConnection, reguestobj).getJSONObject("d");
            String resultjson = getjson.getString("Message");
            Log.v(TAG + "GGGjos", resultjson);
            result = resultjson;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //結束連線
            httpDisconnection(urlConnection);
        }
        return result;
    }


    /**
     * 解析JSONobject
     *
     * @throws JSONException
     */
    private void resloveJSONObject(JSONObject getJsonObject) throws JSONException, IOException {
        // 取得JSON物件陣列
        if (getJsonObject != null) {
            Log.v(TAG + "getJsonObject", getJsonObject.toString());
            JSONArray jsonArray = getJsonObject.getJSONArray("d");
            setDownloadDataSize(jsonArray.length());
            // 取得JSON物件列表，並分類放入List內
            for (int i = 0; i < jsonArray.length(); i++) {
                GET_JSON_OBJ_LIST.add(jsonArray.getJSONObject(i));
            }
            Log.v(TAG + "GET_JSON_OBJ_LIST", GET_JSON_OBJ_LIST.toString());
            Log.i(TAG, String.valueOf(GET_JSON_OBJ_LIST.size()));
        }
    }

    /**
     * 解析營運焦點的JSONobject，並存成html檔
     *
     * @throws JSONException
     */
    private void resloveJSONObjectToFile(JSONObject getJsonObject) throws JSONException, IOException {
        // 取得JSON物件陣列
        if (getJsonObject != null) {
            JSONObject htmlJsonObject = new JSONObject();
            htmlJsonObject.put("Html", getJsonObject.getString("d"));
            htmlJsonObject.put("KindCode", "B");
            GET_JSON_OBJ_LIST.add(htmlJsonObject);
        }
    }

    /**
     * 取得最後一筆下載資料的MsgPk
     *
     * @return lastMsgPk    最後一筆資料的MsgPk
     */
    public String getLastMsgPk() {
        return lastMsgPk;
    }

    /**
     * 設置最後一筆下載資料的MsgPk
     */
    private void setLastMsgPk(String lastMsgPk) {
        this.lastMsgPk = lastMsgPk;
    }

    /**
     * 取得解析後的JSONObject List
     *
     * @return LinkedList
     */
    public LinkedList<JSONObject> getJsonObjList() {
        return GET_JSON_OBJ_LIST;
    }

    /**
     * 取得申請單號的資料
     *
     * @param position 傳入的listview項目編號
     * @return 第 position 筆資料
     */
    public String getEformNum(int position) {
        return eFormNumList.get(position);
    }

    /**
     * 取得申請日期的資料
     *
     * @param position 傳入的listview項目編號
     * @return 第 position 筆資料
     */
    public String getEformDate(int position) {
        return eFormDateList.get(position);
    }

    /**
     * 取得表單名稱的資料
     *
     * @param position 傳入的listview項目編號
     * @return 第 position 筆資料
     */
    public String getEformEvent(int position) {
        return eFormEventList.get(position);
    }

    /**
     * 取得未簽核電子表單的數量
     *
     * @return 未簽核電子表單的數量
     */
    public int getJsonArraySize() {
        return JSON_ARRAY_SIZE;
    }

    /**
     * 取得下載資料的筆數
     *
     * @return 下載資料的筆數
     */
    public int getDownloadDataSize() {
        return downloadDataSize;
    }

    /**
     * 設定下載資料的筆數
     */
    public void setDownloadDataSize(int downloadDataSize) {
        this.downloadDataSize = downloadDataSize;
    }

    /**
     * Base64加密
     *
     * @param str Base64加密後的字串
     * @return String
     */
    public static String base64Str(String str) {
        byte[] result = str.getBytes();// 加密
        try {
            str = new String(Base64.encode(result, 0, result.length,
                    Base64.DEFAULT), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return str.trim();
    }

    /**
     * ASE265解密 - RFC2829金鑰向量編成
     */
    public static String decrypt(String data, byte[] salt, byte[] Password) {
        Rfc2898DeriveBytes keyGenerator = null;
        String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";

        try {
            keyGenerator = new Rfc2898DeriveBytes(Password, salt, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        byte[] bKey = keyGenerator.getBytes(32);
        byte[] bIv = keyGenerator.getBytes(16);
        byte[] datadata = null;

        Log.v(TAG + "bKey", bKey.toString());
        Log.v(TAG + "bIv", bIv.toString());

        try {
            SecretKey secretKey = new SecretKeySpec(bKey, "AES");
            AlgorithmParameterSpec param = new IvParameterSpec(bIv);

            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, param);

            datadata = cipher.doFinal(Base64.decode(data, Base64.DEFAULT));
            return new String(datadata, "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //SHA1 加密实例
    public static String getSHA(String val) throws NoSuchAlgorithmException {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] result = sha1.digest(val.getBytes("UTF-8"));//加密
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < result.length; i++) {
                sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}