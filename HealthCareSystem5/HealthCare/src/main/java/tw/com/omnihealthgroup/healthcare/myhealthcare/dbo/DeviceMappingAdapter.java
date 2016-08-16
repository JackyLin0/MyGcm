package tw.com.omnihealthgroup.healthcare.myhealthcare.dbo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;


public class DeviceMappingAdapter extends DbAdapter {
    private final static String TAG = "DeviceMappingAdapter";
    // DEVICE_ID : 設備編號
    public final static String COL_DEVICE_ID = "DEVICE_ID";
    // DEVICE_SN : 設備序號
    public final static String COL_DEVICE_SN = "DEVICE_SN";
    // DEVICE_DESC: 設備名稱款式
    public final static String COL_DEVICE_DESC = "DEVICE_DESC";
    // MAC : MAC稱
    public final static String COL_MAC = "MAC";

    public final static String TABLE_DEVICEMAPPING = "HM_DEVICE_MAPPING";

    public DeviceMappingAdapter(Context ctx) {
        super(ctx);
    }

    //public String[]
    public int deleteDevice(String _id) {
        SQLiteDatabase db = openDatabase();
        int status = db.delete(TABLE_DEVICEMAPPING, // 資料表名稱
                "DEVICE_ID=" + _id, // WHERE
                null // WHERE的參數
        );
        db.close();
        return status;
    }

    //取得所有設備ID
    public ArrayList<DeviceMapping> getAllDeviceData() {
        ArrayList<DeviceMapping> results = new ArrayList<DeviceMapping>();
        SQLiteDatabase db = openDatabase();
        try {
            //Log.i(TAG,"getAllDeviceId()");
            Cursor cursor = db.query(true,
                    TABLE_DEVICEMAPPING,
                    new String[]{COL_DEVICE_ID, COL_DEVICE_SN, COL_DEVICE_DESC, COL_MAC},
                    null,
                    null,
                    null,
                    null,
                    null, null);
            int num = cursor.getCount();
            Log.i(TAG, "Cursor=" + cursor.getCount());
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
                DeviceMapping device = new DeviceMapping();
                device.setDeviceId(cursor.getString(0));
                device.setDeviceSn(cursor.getString(1));
                device.setDeviceDesc(cursor.getString(2));
                device.setDeviceMac(cursor.getString(3));
                results.add(device);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getDeviceMappingByMac Fail() :" + e.getMessage());
        } finally {
            db.close();
        }
        return results;
    }

    //取得設備ID
    public String getDeviceIdByName(String name) {
        String deviceid = null;
        SQLiteDatabase db = openDatabase();
        try {
            //Log.i(TAG,"getDeviceIdByName()=" + name);
            Cursor uCursor = db.query(true,
                    TABLE_DEVICEMAPPING,
                    new String[]{COL_DEVICE_ID},
                    COL_DEVICE_DESC + "=\'" + name + "\'",
                    null,
                    null,
                    null,
                    null, null);
            //Log.i(TAG,"Cursor="+uCursor.getCount() );
            if (uCursor != null && uCursor.getCount() > 0) {
                uCursor.moveToFirst();
                deviceid = uCursor.getString(0);
            }
            if (uCursor != null) {
                uCursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "getDeviceIdByName Fail() :" + e.getMessage());
        } finally {
            db.close();
        }
        return deviceid;
    }

    //取得設備Sn
    public String getDeviceSnById(String deviceId) {
        String deviceSn = null;
        SQLiteDatabase db = openDatabase();
        try {
            //Log.i(TAG,"getDeviceIdByName()=" + name);
            Cursor uCursor = db.query(true,
                    TABLE_DEVICEMAPPING,
                    new String[]{COL_DEVICE_SN},
                    COL_DEVICE_ID + "=\'" + deviceId + "\'",
                    null,
                    null,
                    null,
                    null, null);
            //Log.i(TAG,"Cursor="+uCursor.getCount() );
            if (uCursor != null && uCursor.getCount() > 0) {
                uCursor.moveToFirst();
                deviceSn = uCursor.getString(0);
            }
            if (uCursor != null) {
                uCursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "getDeviceIdByName Fail() :" + e.getMessage());
        } finally {
            db.close();
        }
        return deviceSn;
    }

    //取得設備Mac
    public String getMacByName(String name) {
        String deviceMac = null;
        SQLiteDatabase db = openDatabase();
        try {
            //Log.i(TAG,"getMacByName()=" + name);
            Cursor uCursor = db.query(true,
                    TABLE_DEVICEMAPPING,
                    new String[]{COL_MAC},
                    COL_DEVICE_DESC + "=\'" + name + "\'",
                    null,
                    null,
                    null,
                    null, null);
            //Log.i(TAG,"Cursor="+uCursor.getCount() );
            if (uCursor != null && uCursor.getCount() > 0) {
                uCursor.moveToFirst();
                deviceMac = uCursor.getString(0);
            }
            if (uCursor != null) {
                uCursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "getMacByName Fail() :" + e.getMessage());
        } finally {
            db.close();
        }
        //Log.i(TAG, "deviceMac:"+deviceMac);
        return deviceMac;
    }

    //以Id名稱取得設備Mac
    public String getMacById(String Id) {
        String deviceMac = null;
        SQLiteDatabase db = openDatabase();
        try {
            //Log.i(TAG,"getMacById()=" + Id);
            Cursor uCursor = db.query(true,
                    TABLE_DEVICEMAPPING,
                    new String[]{COL_MAC},
                    COL_DEVICE_ID + "=\'" + Id + "\'",
                    null,
                    null,
                    null,
                    null, null);
            //Log.i(TAG,"Cursor="+uCursor.getCount() );
            if (uCursor != null && uCursor.getCount() > 0) {
                uCursor.moveToFirst();
                deviceMac = uCursor.getString(0);
            }
            if (uCursor != null) {
                uCursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "getMacById Fail() :" + e.getMessage());
        } finally {
            db.close();
        }
        //Log.i(TAG, "deviceMac:"+deviceMac);
        return deviceMac;
    }

    // 建立Device 對應表
    public long createDeviceMapping(DeviceMapping device) {
        Log.d(TAG, "Create DeviceMapping=" + device.getDeviceId());
        SQLiteDatabase db = openDatabase();
        ContentValues args = new ContentValues();
        args.put(COL_DEVICE_ID, device.getDeviceId());
        args.put(COL_DEVICE_SN, device.getDeviceSn());
        args.put(COL_DEVICE_DESC, device.getDeviceDesc());
        args.put(COL_MAC, device.getDeviceMac());
        long i = db.insert(TABLE_DEVICEMAPPING, null, args);
        db.close();
        return i;
    }

    //update mac by deciceId
    public long updateMacByDeviceId(String deviceId, String Mac) {
        SQLiteDatabase db = openDatabase();
        ContentValues args = new ContentValues();
        args.put(COL_MAC, Mac);
        long status = db.update(TABLE_DEVICEMAPPING, args, COL_DEVICE_ID + "=\'" + deviceId + "\'", null);
        db.close();
        return status;
    }

    // get device by mac
    public DeviceMapping getDeviceMappingByMac(String mac) {
        DeviceMapping device = null;
        SQLiteDatabase db = openDatabase();
        try {
            //Log.i(TAG,"getDeviceMappingByMac()=" + mac);
            Cursor uCursor = db.query(true,
                    TABLE_DEVICEMAPPING,
                    new String[]{COL_DEVICE_ID, COL_DEVICE_SN, COL_DEVICE_DESC, COL_MAC},
                    COL_MAC + "=\'" + mac + "\'",
                    null,
                    null,
                    null,
                    null, null);
            //Log.i(TAG,"Cursor="+uCursor.getCount() );
            if (uCursor != null && uCursor.getCount() > 0) {
                uCursor.moveToFirst();
                device = new DeviceMapping();
                device.setDeviceId(uCursor.getString(0));
                device.setDeviceSn(uCursor.getString(1));
                device.setDeviceDesc(uCursor.getString(2));
                device.setDeviceMac(uCursor.getString(3));
            }
            if (uCursor != null) {
                uCursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "getDeviceMappingByMac Fail() :" + e.getMessage());
        } finally {
            db.close();
        }
        return device;
    }

    //刪除所有設備資料
    public void delAllDevice() {
        SQLiteDatabase db = openDatabase();
        long status = db.delete(TABLE_DEVICEMAPPING, null, null);
        db.close();
        //Log.i(TAG,"Delete All Device=" + status);
    }

    //取得所有Device Mapping
    public String[] getAllDevice() {
        Log.d(TAG, "getAllDevice()");
        SQLiteDatabase db = openDatabase();
        ArrayList<String> results = new ArrayList<String>();
        try {
            Cursor cursor = db.query(TABLE_DEVICEMAPPING,
                    new String[]{COL_DEVICE_DESC},
                    null, null, null, null, null);
            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
                String s = cursor.getString(0);
                Log.d(TAG, "Data=" + s);
                results.add(s);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getAllDevice Fail :" + e.getMessage());
        } finally {
            db.close();
        }
        return results.toArray(new String[results.size()]);
    }
}