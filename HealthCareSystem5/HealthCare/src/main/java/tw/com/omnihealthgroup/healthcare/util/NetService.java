package tw.com.omnihealthgroup.healthcare.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.BioData;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.User;
import tw.com.omnihealthgroup.healthcare.myhealthcare.service.BluetoothLeService;


public class NetService {
    private final String TAG = "NetService";

    private int TimeOut = 30000;

    public NetService() {

    }

    /*
     * 註冊
     */
    public String CallRegisterUser(User user) {
        Log.i(TAG, "NetService CallRegisterUser()");
        SoapObject request = new SoapObject(SystemProperty.WSDL_TARGET_NAMESPACE, SystemProperty.OPERATION_APP_REGISTER);
        //帳號
        request.addProperty("ID", user.getUid());
        //密碼
        request.addProperty("Pwd", user.getPassword());

        String sendJsonData = "{"
                + "\"eMail\":" + "\"" + user.getEmail() + "\","
                + "\"Sex\":" + "\"" + user.getGender() + "\","
                + "\"Tel\":" + "\"" + user.getPhone() + "\","
                + "\"AreaCode\":" + "\"" + user.getArea() + "\","
                + "\"Birth\":" + "\"" + user.getBirthday() + "\""
                + "}";
        request.addProperty("RegisterData", sendJsonData);

        Log.i("info", "sendJsonData:" + sendJsonData);
        Log.i("info", "request:" + request);
        Log.i(TAG, "addProperty Finish");

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        //SOAP_ACTION
        String SOAP_ACTION = SystemProperty.WSDL_TARGET_NAMESPACE + SystemProperty.OPERATION_APP_REGISTER;
        FakeX509TrustManager.allowSSL();
        HttpTransportSE httpTransport = new HttpTransportSE(SystemProperty.URL_MEMBER, TimeOut);
        Object response;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = envelope.getResponse();
        } catch (Exception e) {
            response = e.toString();
            Log.i(TAG, "exception response : " + e.getMessage());
        }
        Log.i(TAG, "response : " + response.toString());
        return response.toString();
    }

    /*
     * 登入
     */
    public String CallValidateUser(User user) {
        Log.i(TAG, "NetService CallValidateUser()");
        SoapObject request = new SoapObject(SystemProperty.WSDL_TARGET_NAMESPACE, SystemProperty.OPERATION_NAME_VALIDATE_USER);
        //帳號
        request.addProperty("ID", user.getUid());
        //密碼
        request.addProperty("Pwd", user.getPassword());

        Log.i(TAG, "addProperty Finish : " + request.toString());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        //SOAP_ACTION
        String SOAP_ACTION = SystemProperty.WSDL_TARGET_NAMESPACE + SystemProperty.OPERATION_NAME_VALIDATE_USER;
        FakeX509TrustManager.allowSSL();
        HttpTransportSE httpTransport = new HttpTransportSE(SystemProperty.URL_MEMBER, TimeOut);
        Log.i("info", "SOAP_ACTION:" + SOAP_ACTION);
        Object response;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = envelope.getResponse();
            return response.toString();
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e);
            Log.e(TAG, "Exception msg: " + e.getMessage());
            response = e.toString();
        }
        System.gc();
        Log.i(TAG, "response : " + response.toString());
        return response.toString();
    }

    /**
     * 12.4*29
     *
     * @return 縣市list json
     * @author james
     */
    public String GetAreaList() {
        Log.i(TAG, "NetService Call GetAreaList()");
        SoapObject request = new SoapObject(SystemProperty.WSDL_TARGET_NAMESPACE, SystemProperty.OPERATION_NAME_REGISTER_GETAREALIST);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        //SOAP_ACTION
        String SOAP_ACTION = SystemProperty.WSDL_TARGET_NAMESPACE + SystemProperty.OPERATION_NAME_REGISTER_GETAREALIST;
        HttpTransportSE httpTransport = new HttpTransportSE(SystemProperty.URL_MEMBER, TimeOut);
        Object response;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = envelope.getResponse();
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e);
            response = "";
        }
        Log.i(TAG, "response : " + response.toString());
        return response.toString();
    }


    /**
     * @return webservice 目前提示設定內容與未量測通知天數
     * @author james
     */
    public String GetNotifyPreferences(String loginID, String pwd) {
        Log.i(TAG, "NetService CallValidateUser()");
        SoapObject request = new SoapObject(SystemProperty.WSDL_TARGET_NAMESPACE, SystemProperty.GET_NOTIFY_PREFER);
        //帳號
        request.addProperty("ID", loginID);
        request.addProperty("Pwd", pwd);

        Log.i("info", "addProperty Finish : " + request.toString());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        //SOAP_ACTION
        String SOAP_ACTION = SystemProperty.WSDL_TARGET_NAMESPACE + SystemProperty.GET_NOTIFY_PREFER;
        HttpTransportSE httpTransport = new HttpTransportSE(SystemProperty.URL_MEMBER, TimeOut);
        Log.i("info", "SOAP_ACTION:" + SOAP_ACTION);
        Object response;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = envelope.getResponse();
            return response.toString();
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e);
            Log.e(TAG, "Exception msg: " + e.getMessage());
            response = e.toString();
        }
        System.gc();
        Log.i(TAG, "response : " + response.toString());
        return response.toString();
    }


    /**
     * @return webservice 目前提示設定內容與未量測通知天數
     * @author james
     */
    public String SetNotifyPreferences(String userID, String pwd, int platform, String key, Boolean unusal, Boolean notMeasure) {
        Log.i(TAG, "NetService CallValidateUser()");
        SoapObject request = new SoapObject(SystemProperty.WSDL_TARGET_NAMESPACE, SystemProperty.SET_NOTIFY_PREFER);
        //帳號
        request.addProperty("ID", userID);
        request.addProperty("Pwd", pwd);
        String noty = "{" + "\"UserID\":" + "\"" + userID + "\"" + ","
                + "\"Platform\":" + "\"" + platform + "\"" + ","
                + "\"Key\":" + "\"" + key + "\"" + ","
                + "\"UnusualNotify\":" + "\"" + unusal + "\"" + ","
                + "\"NotMeasureNotify\":" + "\"" + notMeasure + "\""
                + "}";
        request.addProperty("NotifyPreferences", noty);
        // 平台
        //        request.addProperty("Platform", platform);
        // registered id
        //        request.addProperty("Key", key);
        // 量測異常通知
        //        request.addProperty("UnusualNotify", unusal);
        // 未量測通知
        //        request.addProperty("NotMeasureNotify", notMeasure);

        Log.i("info", "addProperty Finish : " + request.toString());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        //SOAP_ACTION
        String SOAP_ACTION = SystemProperty.WSDL_TARGET_NAMESPACE + SystemProperty.SET_NOTIFY_PREFER;
        HttpTransportSE httpTransport = new HttpTransportSE(SystemProperty.URL_MEMBER, TimeOut);
        Log.i("info", "SOAP_ACTION:" + SOAP_ACTION);
        Object response;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = envelope.getResponse();
            return response.toString();
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e);
            Log.e(TAG, "Exception msg: " + e.getMessage());
            response = e.toString();
        }
        System.gc();
        Log.i(TAG, "response : " + response.toString());
        return response.toString();
    }

    /**
     * @return 縣市list json
     * @author james
     */
    public String GetMarksList() {
        Log.i(TAG, "NetService Call GetAreaList()");
        SoapObject request = new SoapObject(SystemProperty.WSDL_TARGET_NAMESPACE, SystemProperty.LOCATIONI_GETGPSDATA);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        //SOAP_ACTION
        String SOAP_ACTION = SystemProperty.WSDL_TARGET_NAMESPACE + SystemProperty.LOCATIONI_GETGPSDATA;
        HttpTransportSE httpTransport = new HttpTransportSE(SystemProperty.URL_MEMBER, TimeOut);
        Object response;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = envelope.getResponse();
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e);
            response = "";
        }
        Log.i(TAG, "response : " + response.toString());
        return response.toString();
    }

    /*
     * 修改密碼
     */
    public String CallChangePassword(User user, String newPassword) {
        Log.i(TAG, "NetService CallChangePassword()");
        SoapObject request = new SoapObject(SystemProperty.WSDL_TARGET_NAMESPACE, SystemProperty.OPERATION_NAME_CHANGE_PASSWORD);
        //帳號
        request.addProperty("ID", user.getUid());
        //密碼
        request.addProperty("OldPwd", user.getPassword());
        //新密碼
        request.addProperty("NewPwd", newPassword);
        Log.i(TAG, "addProperty Finish");

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        //SOAP_ACTION
        String SOAP_ACTION = SystemProperty.WSDL_TARGET_NAMESPACE + SystemProperty.OPERATION_NAME_CHANGE_PASSWORD;
        HttpTransportSE httpTransport = new HttpTransportSE(SystemProperty.URL_MEMBER, TimeOut);
        Object response;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = envelope.getResponse();
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e);
            response = e.toString();
        }
        Log.i(TAG, "response : " + response.toString());
        return response.toString();
    }

    /*
     * 忘記密碼
     */
    public String CallResetPassword(String resetPasswordID) {
        Log.i(TAG, "NetService CallResetPassword()");
        SoapObject request = new SoapObject(SystemProperty.WSDL_TARGET_NAMESPACE, SystemProperty.OPERATION_NAME_RESET_PASSWORD);
        //帳號
        request.addProperty("ID", resetPasswordID);
        Log.i(TAG, "addProperty Finish");

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        //SOAP_ACTION
        String SOAP_ACTION = SystemProperty.WSDL_TARGET_NAMESPACE + SystemProperty.OPERATION_NAME_RESET_PASSWORD;
        HttpTransportSE httpTransport = new HttpTransportSE(SystemProperty.URL_MEMBER, TimeOut);
        Object response;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
            response = envelope.getResponse();
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e);
            response = e.toString();
        }
        Log.i(TAG, "response : " + response.toString());
        return response.toString();
    }

    /**
     * 生理數值上傳
     */
    public String CallUploadVitalSign(User user, List<BioData> listBioData, boolean isAuto) {
        //Log.i(TAG, "NetService CallUploadVitalSign(), listioData size : " + listioData.size());
        SoapObject request = new SoapObject(SystemProperty.WSDL_TARGET_NAMESPACE, SystemProperty.OPERATION_NAME_UPLOAD_VITAL_SIGN);
        //帳號
        request.addProperty("ID", user.getUid());
        //密碼
        request.addProperty("Pwd", user.getPassword());

        //生理資料
        JSONObject obj = new JSONObject();
        try {
            if (listBioData.get(0).getDeviceId() != null) {
                obj.put("DeviceID", listBioData.get(0).getDeviceId());
            } else {
                obj.put("DeviceID", "");
            }
            obj.put("GatewayID", "");
            obj.put("MemberID", user.getUid());
            JSONArray array = new JSONArray();
            for (int i = 0; i < listBioData.size(); i++) {
                BioData bioData = listBioData.get(i);
                JSONObject objBioData = new JSONObject();
                objBioData.put("InputType", bioData.getInputType());
                if (bioData.getDeviceType().equals("3")) {
                    //血壓
                    objBioData.put("Type", "BP");
                    objBioData.put("MTime", bioData.getDeviceTime());
                    objBioData.put("Mark", "");
                    List<Integer> intArray = new ArrayList<Integer>();
                    intArray.add(Integer.valueOf(bioData.getBhp()));
                    intArray.add(Integer.valueOf(bioData.getBlp()));
                    intArray.add(Integer.valueOf(bioData.getPulse()));
                    objBioData.put("Values", intArray);
                } else if (bioData.getDeviceType().equals("2")) {
                    //血糖
                    objBioData.put("Type", "BG");
                    objBioData.put("MTime", bioData.getDeviceTime());
                    List<Integer> intArray = new ArrayList<Integer>();
                    if (bioData.getAc() != null) {
                        objBioData.put("Mark", "AC");
                        intArray.add(Integer.valueOf(bioData.getAc()));
                        objBioData.put("Values", intArray);
                    } else if (bioData.getPc() != null) {
                        objBioData.put("Mark", "PC");
                        intArray.add(Integer.valueOf(bioData.getPc()));
                        objBioData.put("Values", intArray);
                    } else if (bioData.getNm() != null) {
                        objBioData.put("Mark", "NM");
                        intArray.add(Integer.valueOf(bioData.getNm()));
                        objBioData.put("Values", intArray);
                    }
                }
                array.put(objBioData);
            }
            obj.put("VitalSign", array);
        } catch (Exception e) {
            Log.e(TAG, "Exception Error : " + e);
        }

        String jsonTest = "{\"DeviceID\":\"A123\",\"GatewayID\":\"G123\",\"MemberID\":\"wendell.chuang@ebti.com.tw\",\"VitalSign\":[{\"Type\":\"BP\",\"MTime\":\"2012-10-09 12:01:27\",\"InputType\":\"Manual\",\"Mark\":\"NM\",\"Values\":[121,76,72]}]}";
        Log.i(TAG, jsonTest);
        String data = obj.toString().replaceAll("\\\\/", "-");
        data = data.replaceAll("\\\"\\[", "[");
        data = data.replaceAll("\\]\\\"", "]");
        Log.i(TAG, data);
        request.addProperty("VSData", data);
        Log.i(TAG, "addProperty Finish, request : " + request.toString());

        //SOAP_ACTION
        Log.i(TAG, "before create SOAP_ACTION");
        String SOAP_ACTION = SystemProperty.WSDL_TARGET_NAMESPACE + SystemProperty.OPERATION_NAME_UPLOAD_VITAL_SIGN;
        HttpTransportSE httpTransport = new HttpTransportSE(SystemProperty.URL_VITAL_SIGN, TimeOut);
        Object response;
        try {
            Log.i(TAG, "before create envelope");
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            Log.i(TAG, "envelope.bodyOut : " + envelope.bodyOut);
            Log.i(TAG, "before call soap");
            httpTransport.call(SOAP_ACTION, envelope);

            Log.i(TAG, "before get response");
            response = envelope.getResponse();
            Log.i(TAG, "CallUploadVitalSign response : " + response);
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e);
            response = e.toString();
        }
        Log.i(TAG, "response : " + response.toString());
        if (isAuto) {
            //  關閉BT3.0掃描與上傳服務
            BluetoothLeService.BLEService.closeBT30Service();
        }
        return response.toString();
    }

    /**
     * 取得生理資料
     */
    public String CallGetVitalSign(User user, String startDate, String endDate) {
        Log.i(TAG, "NetService CallGetVitalSign()");
        SoapObject request = new SoapObject(SystemProperty.WSDL_TARGET_NAMESPACE, SystemProperty.OPERATION_NAME_GET_VITAL_SIGN);
        //帳號
        request.addProperty("ID", user.getUid());
        request.addProperty("Pwd", user.getPassword());
        //request.addProperty("Type", "All");
        request.addProperty("StartDate", startDate);
        request.addProperty("EndDate", endDate);
        Log.i(TAG, "addProperty Finish, request : " + request.toString());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        Log.i(TAG, "envelope : " + envelope);
        //SOAP_ACTION
        String SOAP_ACTION = SystemProperty.WSDL_TARGET_NAMESPACE + SystemProperty.OPERATION_NAME_GET_VITAL_SIGN;
        HttpTransportSE httpTransport = new HttpTransportSE(SystemProperty.URL_VITAL_SIGN, TimeOut);
        Object response = null;
        System.gc();
        for (int i = 0; i < 5; i++) {
            try {
                httpTransport.call(SOAP_ACTION, envelope);
                response = envelope.getResponse();
                return response.toString();
            } catch (Exception e) {
                Log.e(TAG, "Exception : " + e);
                response = e.toString();
            }
        }
        System.gc();
        return response.toString();
    }
}