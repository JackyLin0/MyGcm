package tw.com.omnihealthgroup.healthcare.gcm;

import android.content.Context;
import android.provider.Settings;

public class GcmUtil {
    private String ANDROID_ID;
    private static String TOKEN;
    private static String DEVICE_SERAIL;

    /**
     * 取得ANDROID_ID
     *
     * @return String
     */
    public String getAndroidId() {
        return ANDROID_ID;
    }

    /**
     * 設置ANDROID_ID
     *
     * @param androidId ANDROID_ID
     */
    public void setAndroidId(String androidId) {
        ANDROID_ID = androidId;
    }

    /**
     * 向系統取得ANDROID_ID後組合成DeviceSerail並回傳
     *
     * @param context
     */
    public String getDeviceSerail(Context context) {
        String android_id = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
        return context.getPackageName() + android_id;
    }

    /**
     * 向系統取得ANDROID_ID後組合成DeviceSerail並回傳
     */
    public String getDeviceSerail() {
        return DEVICE_SERAIL;
    }

    /**
     * 向系統取得ANDROID_ID後組合成DeviceSerail並回傳
     */
    public void setDeviceSerail(String deviceSerail) {
        GcmUtil.DEVICE_SERAIL = deviceSerail;
    }

    /**
     * 取得給GCM回傳的token
     *
     * @return String
     */
    public String getToken() {
        return GcmUtil.TOKEN;
    }

    /**
     * 儲存GCM回傳的token
     *
     * @param token TOKEN
     */
    public void setToken(String token) {
        GcmUtil.TOKEN = token;
    }
}
