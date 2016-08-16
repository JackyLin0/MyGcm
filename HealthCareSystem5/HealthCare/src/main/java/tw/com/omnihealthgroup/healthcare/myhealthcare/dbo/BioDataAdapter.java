package tw.com.omnihealthgroup.healthcare.myhealthcare.dbo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import tw.com.omnihealthgroup.healthcare.WebServiceConnection;


public class BioDataAdapter extends DbAdapter {
    private final static String TAG = "BioDataAdapter";
    // ID : 編號
    public final static String COL_ID = "_ID";
    // USER_ID:使用者代號
    public final static String COL_USER_ID = "USER_ID";
    //設備序號
    public final static String COL_DEVICE_SN = "DEVICE_SN";
    // MAC : MAC稱
    public final static String COL_MAC = "MAC";
    //設備量測時間
    public final static String COL_DEVICE_TIME = "DEVICE_TIME";
    //設備量測紀錄編號
    public final static String COL_DEVICE_ID = "DEVICE_ID";
    //DEVICE_TYPE  量測類型(1:體重、2:血糖、3:血壓)
    public final static String COL_DEVICE_TYPE = "DEVICE_TYPE";
    //讀取時間
    public final static String COL_READ_TIME = "READ_TIME";
    //性別
    public final static String COL_SEX = "SEX";
    //年齡
    public final static String COL_AGE = "AGE";
    //身高紀錄
    public final static String COL_BODY_HEIGHT = "BODY_HEIGHT";
    //體重紀錄
    public final static String COL_BODY_WEIGHT = "BODY_WEIGHT";
    //身體質量指數
    public final static String COL_BMI = "BMI";
    // 飯前血糖
    public final static String COL_AC = "AC";
    //飯後血糖
    public final static String COL_PC = "PC";
    //隨機血糖(不知道是飯前或飯後，無法分類)
    public final static String COL_NM = "NM";
    //收縮壓
    public final static String COL_BHP = "BHP";
    //舒張壓
    public final static String COL_BLP = "BLP";
    //脈搏
    public final static String COL_PULSE = "PULSE";
    //體溫
    public final static String COL_TEMPERATURE = "TEMPERATURE";
    //頭圍
    public final static String COL_HEADCIRCUMFERENCE = "HEADCIRCUMFERENCE";
    //上臂圍
    public final static String COL_ARMCIRCUMFERENCE = "ARMCIRCUMFERENCE";
    //腰圍
    public final static String COL_WAISTLINE = "WAISTLINE";
    //臀圍
    public final static String COL_HIPS = "HIPS";
    // UPLOADED  是否上傳(1.已上傳,0未上傳)
    public final static String COL_UPLOADED = "UPLOADED";
    // INPUT_TYPE  資料來源(Device.儀器資料,Manual.手動輸入資料)
    public final static String COL_INPUT_TYPE = "INPUT_TYPE";

    public final static String TABLE_BIODATA = "HM_BIO_DATA";

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String[] bioDataColumnArray = new String[]{
            COL_ID, COL_USER_ID, COL_DEVICE_SN, COL_MAC, COL_DEVICE_TIME,
            COL_DEVICE_ID, COL_DEVICE_TYPE, COL_READ_TIME, COL_SEX, COL_AGE,
            COL_BODY_HEIGHT, COL_BODY_WEIGHT, COL_BMI, COL_AC, COL_PC, COL_NM,
            COL_BHP, COL_BLP, COL_PULSE, COL_TEMPERATURE, COL_HEADCIRCUMFERENCE,
            COL_ARMCIRCUMFERENCE, COL_WAISTLINE, COL_HIPS, COL_UPLOADED, COL_INPUT_TYPE
    };

    public BioDataAdapter(Context ctx) {
        super(ctx);
    }

    public synchronized int deleteBioData(long _id) {
        SQLiteDatabase db = openDatabase();
        return db.delete(TABLE_BIODATA, "_ID=" + _id, null); // 資料表名稱, WHERE, WHERE的參數
    }

    public synchronized void deleteBioData_device_type(long device_type) {
        SQLiteDatabase db = openDatabase();
        try {
            //        long status = db.delete(TABLE_BIODATA, "DEVICE_TYPE=" + device_type, null); // 資料表名稱, WHERE, WHERE的參數
            long status = db.delete(TABLE_BIODATA, "DEVICE_TYPE=" + device_type + " AND UPLOADED=1", null); // 資料表名稱, WHERE, WHERE的參數
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    //Create Data
    public synchronized void createBioData(BioData bio) {
        //Log.i(TAG,"Create BioData()");
        SQLiteDatabase db = openDatabase();
        ContentValues args = new ContentValues();
        args.put(COL_ID, bio.get_id());
        args.put(COL_USER_ID, bio.getUserId());
        args.put(COL_DEVICE_SN, bio.getDeviceSn());
        args.put(COL_MAC, bio.getDeviceMac());
        args.put(COL_DEVICE_TIME, bio.getDeviceTime());
        args.put(COL_DEVICE_ID, bio.getDeviceId());
        args.put(COL_DEVICE_TYPE, bio.getDeviceType());
        args.put(COL_READ_TIME, dateFormat.format(System.currentTimeMillis()));
        args.put(COL_SEX, bio.getSex());
        args.put(COL_AGE, bio.getAge());
        args.put(COL_BODY_HEIGHT, bio.getBodyHeight());
        args.put(COL_BODY_WEIGHT, bio.getBodyWeight());
        args.put(COL_BMI, bio.getBmi());
        args.put(COL_AC, bio.getAc());
        args.put(COL_PC, bio.getPc());
        args.put(COL_NM, bio.getNm());
        args.put(COL_BHP, bio.getBhp());
        args.put(COL_BLP, bio.getBlp());
        args.put(COL_PULSE, bio.getPulse());
        args.put(COL_TEMPERATURE, bio.getTemperature());
        args.put(COL_HEADCIRCUMFERENCE, bio.getHeadcircumference());
        args.put(COL_ARMCIRCUMFERENCE, bio.getArmcircumference());
        args.put(COL_WAISTLINE, bio.getWaistline());
        args.put(COL_HIPS, bio.getHips());
        args.put(COL_UPLOADED, bio.getUploaded());
        args.put(COL_INPUT_TYPE, bio.getInputType());
        long i = db.insert(TABLE_BIODATA, null, args);
        db.close();
        //Log.i(TAG, "long i:"+i);
    }

    //Create  blood pressure record
    public synchronized long createBloodPressure(BioData bio) {
        //Log.i(TAG,"Create Bloodpressure()" + "");
        SQLiteDatabase db = openDatabase();
        ContentValues args = new ContentValues();
        args.put(COL_ID, bio.get_id());
        args.put(COL_USER_ID, bio.getUserId());
        args.put(COL_DEVICE_SN, bio.getDeviceSn());
        args.put(COL_MAC, bio.getDeviceMac());
        args.put(COL_DEVICE_TIME, bio.getDeviceTime());
        args.put(COL_DEVICE_ID, bio.getDeviceId());
        args.put(COL_DEVICE_TYPE, tw.com.omnihealthgroup.healthcare.WebServiceConnection.BIODATA_DEVICE_TYPE_BLOOD_PRESSURE);
        args.put(COL_READ_TIME, dateFormat.format(System.currentTimeMillis()));

        args.put(COL_BHP, bio.getBhp());
        args.put(COL_BLP, bio.getBlp());
        args.put(COL_PULSE, bio.getPulse());

        args.put(COL_INPUT_TYPE, bio.getInputType());
        args.put(COL_UPLOADED, bio.getUploaded());
        long i = db.insert(TABLE_BIODATA, null, args);
        db.close();
        return i;
    }

    //Create glucose record
    public synchronized long createGlucose(BioData bioData) {
        //Log.i(TAG,"Create glucose()");
        SQLiteDatabase db = openDatabase();
        ContentValues args = new ContentValues();
        args.put(COL_ID, bioData.get_id());
        args.put(COL_USER_ID, bioData.getUserId());
        args.put(COL_DEVICE_SN, bioData.getDeviceSn());
        args.put(COL_MAC, bioData.getDeviceMac());
        args.put(COL_DEVICE_TIME, bioData.getDeviceTime());
        args.put(COL_DEVICE_ID, bioData.getDeviceId());
        args.put(COL_DEVICE_TYPE, tw.com.omnihealthgroup.healthcare.WebServiceConnection.BIODATA_DEVICE_TYPE_BLOOD_GLUCOSE);
        args.put(COL_READ_TIME, dateFormat.format(System.currentTimeMillis()));

        args.put(COL_AC, bioData.getAc());
        args.put(COL_PC, bioData.getPc());
        args.put(COL_NM, bioData.getNm());

        args.put(COL_INPUT_TYPE, bioData.getInputType());
        args.put(COL_UPLOADED, bioData.getUploaded());
        long i = db.insert(TABLE_BIODATA, null, args);
        db.close();
        return i;
    }

    //Create glucose record
    public synchronized long createBody(BioData bioData) {
        //Log.i(TAG,"Create glucose()");
        SQLiteDatabase db = openDatabase();
        ContentValues args = new ContentValues();
        args.put(COL_ID, bioData.get_id());
        args.put(COL_USER_ID, bioData.getUserId());
        args.put(COL_DEVICE_SN, bioData.getDeviceSn());
        args.put(COL_MAC, bioData.getDeviceMac());
        args.put(COL_DEVICE_TIME, bioData.getDeviceTime());
        args.put(COL_DEVICE_ID, bioData.getDeviceId());
        args.put(COL_DEVICE_TYPE, bioData.getDeviceType());
        args.put(COL_READ_TIME, dateFormat.format(System.currentTimeMillis()));

        args.put(COL_BODY_HEIGHT, bioData.getBodyHeight());
        args.put(COL_BODY_WEIGHT, bioData.getBodyWeight());
        args.put(COL_BMI, bioData.getBmi());

        args.put(COL_TEMPERATURE, bioData.getTemperature());
        args.put(COL_HEADCIRCUMFERENCE, bioData.getHeadcircumference());
        args.put(COL_ARMCIRCUMFERENCE, bioData.getArmcircumference());
        args.put(COL_WAISTLINE, bioData.getWaistline());
        args.put(COL_HIPS, bioData.getHips());

        args.put(COL_INPUT_TYPE, bioData.getInputType());
        args.put(COL_UPLOADED, tw.com.omnihealthgroup.healthcare.WebServiceConnection.DATA_IS_NOT_UPLOAD);
        long i = db.insert(TABLE_BIODATA, null, args);
        db.close();
        return i;
    }

    public synchronized long createBodyHeight(BioData bioData) {
        //Log.i(TAG,"Create glucose()");
        SQLiteDatabase db = openDatabase();
        ContentValues args = new ContentValues();
        args.put(COL_ID, bioData.get_id());
        args.put(COL_USER_ID, bioData.getUserId());
        args.put(COL_DEVICE_SN, bioData.getDeviceSn());
        args.put(COL_MAC, bioData.getDeviceMac());
        args.put(COL_DEVICE_TIME, bioData.getDeviceTime());
        args.put(COL_DEVICE_ID, bioData.getDeviceId());
        args.put(COL_DEVICE_TYPE, bioData.getDeviceType());
        args.put(COL_READ_TIME, dateFormat.format(System.currentTimeMillis()));

        args.put(COL_BODY_HEIGHT, bioData.getBodyHeight());
        args.put(COL_BODY_WEIGHT, bioData.getBodyWeight());
        args.put(COL_BMI, bioData.getBmi());

        args.put(COL_INPUT_TYPE, bioData.getInputType());
        args.put(COL_UPLOADED, bioData.getUploaded());
        long i = db.insert(TABLE_BIODATA, null, args);
        db.close();
        return i;
    }

    public synchronized long createBodyTemperature(BioData bioData) {
        //Log.i(TAG,"Create glucose()");
        SQLiteDatabase db = openDatabase();
        ContentValues args = new ContentValues();
        args.put(COL_ID, bioData.get_id());
        args.put(COL_USER_ID, bioData.getUserId());
        args.put(COL_DEVICE_SN, bioData.getDeviceSn());
        args.put(COL_MAC, bioData.getDeviceMac());
        args.put(COL_DEVICE_TIME, bioData.getDeviceTime());
        args.put(COL_DEVICE_ID, bioData.getDeviceId());
        args.put(COL_DEVICE_TYPE, bioData.getDeviceType());
        args.put(COL_READ_TIME, dateFormat.format(System.currentTimeMillis()));

        args.put(COL_TEMPERATURE, bioData.getTemperature());

        args.put(COL_INPUT_TYPE, bioData.getInputType());
        args.put(COL_UPLOADED, bioData.getUploaded());
        long i = db.insert(TABLE_BIODATA, null, args);
        db.close();
        return i;
    }

    public synchronized long createBodyHeadCircumference(BioData bioData) {
        //Log.i(TAG,"Create glucose()");
        SQLiteDatabase db = openDatabase();
        ContentValues args = new ContentValues();
        args.put(COL_ID, bioData.get_id());
        args.put(COL_USER_ID, bioData.getUserId());
        args.put(COL_DEVICE_SN, bioData.getDeviceSn());
        args.put(COL_MAC, bioData.getDeviceMac());
        args.put(COL_DEVICE_TIME, bioData.getDeviceTime());
        args.put(COL_DEVICE_ID, bioData.getDeviceId());
        args.put(COL_DEVICE_TYPE, bioData.getDeviceType());
        args.put(COL_READ_TIME, dateFormat.format(System.currentTimeMillis()));

        args.put(COL_HEADCIRCUMFERENCE, bioData.getHeadcircumference());

        args.put(COL_INPUT_TYPE, bioData.getInputType());
        args.put(COL_UPLOADED, bioData.getUploaded());
        long i = db.insert(TABLE_BIODATA, null, args);
        db.close();
        return i;
    }


    //Create glucose record
    public synchronized long createBodyArmCircumference(BioData bioData) {
        //Log.i(TAG,"Create glucose()");
        SQLiteDatabase db = openDatabase();
        ContentValues args = new ContentValues();
        args.put(COL_ID, bioData.get_id());
        args.put(COL_USER_ID, bioData.getUserId());
        args.put(COL_DEVICE_SN, bioData.getDeviceSn());
        args.put(COL_MAC, bioData.getDeviceMac());
        args.put(COL_DEVICE_TIME, bioData.getDeviceTime());
        args.put(COL_DEVICE_ID, bioData.getDeviceId());
        args.put(COL_DEVICE_TYPE, bioData.getDeviceType());
        args.put(COL_READ_TIME, dateFormat.format(System.currentTimeMillis()));

        args.put(COL_ARMCIRCUMFERENCE, bioData.getArmcircumference());

        args.put(COL_INPUT_TYPE, bioData.getInputType());
        args.put(COL_UPLOADED, bioData.getUploaded());
        long i = db.insert(TABLE_BIODATA, null, args);
        db.close();
        return i;
    }

    public synchronized long createBodyWaistline(BioData bioData) {
        //Log.i(TAG,"Create glucose()");
        SQLiteDatabase db = openDatabase();
        ContentValues args = new ContentValues();
        args.put(COL_ID, bioData.get_id());
        args.put(COL_USER_ID, bioData.getUserId());
        args.put(COL_DEVICE_SN, bioData.getDeviceSn());
        args.put(COL_MAC, bioData.getDeviceMac());
        args.put(COL_DEVICE_TIME, bioData.getDeviceTime());
        args.put(COL_DEVICE_ID, bioData.getDeviceId());
        args.put(COL_DEVICE_TYPE, bioData.getDeviceType());
        args.put(COL_READ_TIME, dateFormat.format(System.currentTimeMillis()));

        args.put(COL_WAISTLINE, bioData.getWaistline());

        args.put(COL_INPUT_TYPE, bioData.getInputType());
        args.put(COL_UPLOADED, bioData.getUploaded());
        long i = db.insert(TABLE_BIODATA, null, args);
        db.close();
        return i;
    }

    public synchronized long createBodyHips(BioData bioData) {
        //Log.i(TAG,"Create glucose()");
        SQLiteDatabase db = openDatabase();
        ContentValues args = new ContentValues();
        args.put(COL_ID, bioData.get_id());
        args.put(COL_USER_ID, bioData.getUserId());
        args.put(COL_DEVICE_SN, bioData.getDeviceSn());
        args.put(COL_MAC, bioData.getDeviceMac());
        args.put(COL_DEVICE_TIME, bioData.getDeviceTime());
        args.put(COL_DEVICE_ID, bioData.getDeviceId());
        args.put(COL_DEVICE_TYPE, bioData.getDeviceType());
        args.put(COL_READ_TIME, dateFormat.format(System.currentTimeMillis()));

        args.put(COL_HIPS, bioData.getHips());

        args.put(COL_INPUT_TYPE, bioData.getInputType());
        args.put(COL_UPLOADED, bioData.getUploaded());
        long i = db.insert(TABLE_BIODATA, null, args);
        db.close();
        return i;
    }

    //Create height & weight
    public synchronized long createWeight(BioData bioData) {
        //Log.i(TAG,"Create glucose()" + "");
        SQLiteDatabase db = openDatabase();
        ContentValues args = new ContentValues();
        args.put(COL_ID, bioData.get_id());
        args.put(COL_USER_ID, bioData.getUserId());
        args.put(COL_DEVICE_SN, bioData.getDeviceSn());
        args.put(COL_MAC, bioData.getDeviceMac());
        args.put(COL_DEVICE_TIME, bioData.getDeviceTime());
        args.put(COL_DEVICE_TYPE, tw.com.omnihealthgroup.healthcare.WebServiceConnection.BIODATA_DEVICE_TYPE_WEIGHT);
        args.put(COL_READ_TIME, dateFormat.format(System.currentTimeMillis()));
        args.put(COL_BODY_HEIGHT, bioData.getBodyHeight());
        args.put(COL_BODY_WEIGHT, bioData.getBodyWeight());
        args.put(COL_INPUT_TYPE, bioData.getInputType());
        args.put(COL_UPLOADED, 0);
        long i = db.insert(TABLE_BIODATA, null, args);
        db.close();
        return i;
    }

    //刪除所有資料
    public synchronized void delAll() {
        SQLiteDatabase db = openDatabase();
        long status = db.delete(TABLE_BIODATA, null, null);
        db.close();
        //Log.i(TAG,"Delete All TABLE_BIODATA=" + status);
    }

    //刪除超過90天的資料
    @SuppressLint("SimpleDateFormat")
    public synchronized void delBefore90DaysData() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -90);
        Log.i(TAG + "info", "Calendar.DAY_OF_YEAR:" + Calendar.DAY_OF_YEAR);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String deviceTime = sdf.format(c.getTime());
        Log.i(TAG + "info", "deviceTime:" + deviceTime);
        SQLiteDatabase db = openDatabase();
        long status = db.delete(TABLE_BIODATA, COL_DEVICE_TIME + " < '" + deviceTime + "'", null);
        db.close();
        //Log.i(TAG,"Delete delBefore90DaysData TABLE_BIODATA=" + status);
    }

    //取得體重資訊
    public synchronized ArrayList<BioData> getWieght() {
        ArrayList<BioData> results = new ArrayList<BioData>();
        SQLiteDatabase db = openDatabase();
        try {
            Cursor cursor = db.query(
                    TABLE_BIODATA,
                    bioDataColumnArray,
                    COL_DEVICE_TYPE + " = '" + tw.com.omnihealthgroup.healthcare.WebServiceConnection.BIODATA_DEVICE_TYPE_WEIGHT + "'",
                    null,
                    null,
                    null,
                    COL_DEVICE_TIME + " DESC"
            );

            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
                results.add(getBioDataSetting(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getWieght() Fail :" + e.getMessage());
        } finally {
            db.close();
        }
        return results;
    }

    //取得身理資訊
    public synchronized ArrayList<BioData> getBody() {
        ArrayList<BioData> results = new ArrayList<BioData>();
        SQLiteDatabase db = openDatabase();
        try {
            Cursor cursor = db.query(
                    TABLE_BIODATA,
                    bioDataColumnArray,
                    COL_DEVICE_TYPE + " = '" + WebServiceConnection.BIODATA_DEVICE_TYPE_WEIGHT + "'",
                    null,
                    null,
                    null,
                    //                    COL_DEVICE_TIME + " DESC"  //日期大至小
                    COL_DEVICE_TIME + " ASC" //日期小至大
            );

            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
                results.add(getBioDataSetting(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getBody() Fail :" + e.getMessage());
        } finally {
            db.close();
        }
        return results;
    }

    //取得身理資訊(身高體重BMI)
    public synchronized ArrayList<BioData> getBody_withTime(String beginDate, String endDate) {
        ArrayList<BioData> results = new ArrayList<BioData>();
        SQLiteDatabase db = openDatabase();
        try {
            Cursor cursor = db.query(
                    TABLE_BIODATA,
                    bioDataColumnArray,
                    COL_DEVICE_TYPE + " = '" + WebServiceConnection.BIODATA_DEVICE_TYPE_WEIGHT
                            + "' and " + COL_DEVICE_TIME + " >= '" + beginDate
                            + "' and +" + COL_DEVICE_TIME + " <= '" + endDate + "' ",
                    null,
                    null,
                    null,
                    //                    COL_DEVICE_TIME + " DESC"  //日期大至小
                    COL_DEVICE_TIME + " ASC" //日期小至大
            );

            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
                results.add(getBioDataSetting(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getBody_withTime() Fail :" + e.getMessage());
        } finally {
            db.close();
        }
        return results;
    }

    //取得身理資訊(體溫)
    public synchronized ArrayList<BioData> getBodyTemperature_withTime(String beginDate, String endDate) {
        ArrayList<BioData> results = new ArrayList<BioData>();
        SQLiteDatabase db = openDatabase();
        try {
            Cursor cursor = db.query(
                    TABLE_BIODATA,
                    bioDataColumnArray,
                    COL_DEVICE_TYPE + " = '" + "4"
                            + "' and " + COL_DEVICE_TIME + " >= '" + beginDate
                            + "' and +" + COL_DEVICE_TIME + " <= '" + endDate + "' ",
                    null,
                    null,
                    null,
                    //                    COL_DEVICE_TIME + " DESC"  //日期大至小
                    COL_DEVICE_TIME + " ASC" //日期小至大
            );

            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
                results.add(getBioDataSetting(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getBodyTemperature_withTime() Fail :" + e.getMessage());
        } finally {
            db.close();
        }
        return results;
    }

    //取得身理資訊(頭圍)
    public synchronized ArrayList<BioData> getBodyHeadCircumference_withTime(String beginDate, String endDate) {
        ArrayList<BioData> results = new ArrayList<BioData>();
        SQLiteDatabase db = openDatabase();
        try {
            Cursor cursor = db.query(
                    TABLE_BIODATA,
                    bioDataColumnArray,
                    COL_DEVICE_TYPE + " = '" + "5"
                            + "' and " + COL_DEVICE_TIME + " >= '" + beginDate
                            + "' and +" + COL_DEVICE_TIME + " <= '" + endDate + "' ",
                    null,
                    null,
                    null,
                    //                    COL_DEVICE_TIME + " DESC"  //日期大至小
                    COL_DEVICE_TIME + " ASC" //日期小至大
            );

            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
                results.add(getBioDataSetting(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getBodyHeadCircumference_withTime() Fail :" + e.getMessage());
        } finally {
            db.close();
        }
        return results;
    }

    //取得身理資訊(上臂圍)
    public synchronized ArrayList<BioData> getBodyArmCircumference_withTime(String beginDate, String endDate) {
        ArrayList<BioData> results = new ArrayList<BioData>();
        SQLiteDatabase db = openDatabase();
        try {
            Cursor cursor = db.query(
                    TABLE_BIODATA,
                    bioDataColumnArray,
                    COL_DEVICE_TYPE + " = '" + "6"
                            + "' and " + COL_DEVICE_TIME + " >= '" + beginDate
                            + "' and +" + COL_DEVICE_TIME + " <= '" + endDate + "' ",
                    null,
                    null,
                    null,
                    //                    COL_DEVICE_TIME + " DESC"  //日期大至小
                    COL_DEVICE_TIME + " ASC" //日期小至大
            );

            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
                results.add(getBioDataSetting(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getBodyArmCircumference_withTime() Fail :" + e.getMessage());
        } finally {
            db.close();
        }
        return results;
    }

    //取得身理資訊(腰圍)
    public synchronized ArrayList<BioData> getBodyWaistline_withTime(String beginDate, String endDate) {
        ArrayList<BioData> results = new ArrayList<BioData>();
        SQLiteDatabase db = openDatabase();
        try {
            Cursor cursor = db.query(
                    TABLE_BIODATA,
                    bioDataColumnArray,
                    COL_DEVICE_TYPE + " = '" + "7"
                            + "' and " + COL_DEVICE_TIME + " >= '" + beginDate
                            + "' and +" + COL_DEVICE_TIME + " <= '" + endDate + "' ",
                    null,
                    null,
                    null,
                    //                    COL_DEVICE_TIME + " DESC"  //日期大至小
                    COL_DEVICE_TIME + " ASC" //日期小至大
            );

            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
                results.add(getBioDataSetting(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getBodyWaistline_withTime() Fail :" + e.getMessage());
        } finally {
            db.close();
        }
        return results;
    }

    //取得身理資訊(臀圍)
    public synchronized ArrayList<BioData> getBodyHips_withTime(String beginDate, String endDate) {
        ArrayList<BioData> results = new ArrayList<BioData>();
        SQLiteDatabase db = openDatabase();
        try {
            Cursor cursor = db.query(
                    TABLE_BIODATA,
                    bioDataColumnArray,
                    COL_DEVICE_TYPE + " = '" + "8"
                            + "' and " + COL_DEVICE_TIME + " >= '" + beginDate
                            + "' and +" + COL_DEVICE_TIME + " <= '" + endDate + "' ",
                    null,
                    null,
                    null,
                    //                    COL_DEVICE_TIME + " DESC"  //日期大至小
                    COL_DEVICE_TIME + " ASC" //日期小至大
            );

            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
                results.add(getBioDataSetting(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getBodyHips_withTime() Fail :" + e.getMessage());
        } finally {
            db.close();
        }
        return results;
    }

    //取得血糖資訊
    public synchronized ArrayList<BioData> getBloodGlucose() {
        ArrayList<BioData> results = new ArrayList<BioData>();
        SQLiteDatabase db = openDatabase();
        try {
            Cursor cursor = db.query(
                    TABLE_BIODATA,
                    bioDataColumnArray,
                    COL_DEVICE_TYPE + " = '" + tw.com.omnihealthgroup.healthcare.WebServiceConnection.BIODATA_DEVICE_TYPE_BLOOD_GLUCOSE + "'",
                    null,
                    null,
                    null,
                    //                    COL_DEVICE_TIME + " DESC"  //日期大至小
                    COL_DEVICE_TIME + " ASC" //日期小至大
            );

            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
                results.add(getBioDataSetting(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getBloodGlucose() Fail :" + e.getMessage());
        } finally {
            db.close();
        }
        return results;
    }

    //取得血糖資訊
    public synchronized ArrayList<BioData> getBloodGlucose_withTime(String beginDate, String endDate) {
        ArrayList<BioData> results = new ArrayList<BioData>();
        SQLiteDatabase db = openDatabase();
        try {
            Cursor cursor = db.query(
                    TABLE_BIODATA,
                    bioDataColumnArray,
                    COL_DEVICE_TYPE + " = '" + tw.com.omnihealthgroup.healthcare.WebServiceConnection.BIODATA_DEVICE_TYPE_BLOOD_GLUCOSE
                            + "' and " + COL_DEVICE_TIME + " >= '" + beginDate
                            + "' and +" + COL_DEVICE_TIME + " <= '" + endDate + "' ",
                    null,
                    null,
                    null,
                    //                    COL_DEVICE_TIME + " DESC"  //日期大至小
                    COL_DEVICE_TIME + " ASC" //日期小至大
            );

            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
                results.add(getBioDataSetting(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getBloodGlucose_withTime() Fail :" + e.getMessage());
        } finally {
            db.close();
        }
        return results;
    }

    //取得血糖資訊
    public synchronized ArrayList<BioData> getUserBloodGlucose(String userId) {
        ArrayList<BioData> results = new ArrayList<BioData>();
        SQLiteDatabase db = openDatabase();
        try {
            Cursor cursor = db.query(
                    TABLE_BIODATA,
                    bioDataColumnArray,
                    COL_DEVICE_TYPE + " = '" + tw.com.omnihealthgroup.healthcare.WebServiceConnection.BIODATA_DEVICE_TYPE_BLOOD_GLUCOSE
                            + "' and " + COL_USER_ID + " = '" + userId + "' ",
                    null,
                    null,
                    null,
                    COL_DEVICE_TIME + " DESC"
            );

            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
                results.add(getBioDataSetting(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getBloodGlucose() Fail :" + e.getMessage());
        } finally {
            db.close();
        }
        return results;
    }

    //取得時間區間內血糖資訊
    public synchronized ArrayList<BioData> getUserBloodGlucose(String userId, String beginDate, String endDate) {
        ArrayList<BioData> results = new ArrayList<BioData>();
        SQLiteDatabase db = openDatabase();
        try {
            Cursor cursor = db.query(
                    TABLE_BIODATA,
                    bioDataColumnArray,
                    COL_DEVICE_TYPE + " = '" + tw.com.omnihealthgroup.healthcare.WebServiceConnection.BIODATA_DEVICE_TYPE_BLOOD_GLUCOSE
                            + "' and " + COL_USER_ID + " = '" + userId
                            + "' and " + COL_DEVICE_TIME + " >= '" + beginDate
                            + "' and " + COL_DEVICE_TIME + " <= '" + endDate + "' ",
                    null,
                    null,
                    null,
                    COL_DEVICE_TIME + " DESC"
            );

            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
                results.add(getBioDataSetting(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getBloodGlucose() Fail :" + e.getMessage());
        } finally {
            db.close();
        }
        return results;
    }

    //取得血壓資訊
    public synchronized ArrayList<BioData> getBloodPressure() {
        ArrayList<BioData> results = new ArrayList<BioData>();
        SQLiteDatabase db = openDatabase();
        try {
            Cursor cursor = db.query(
                    TABLE_BIODATA,
                    bioDataColumnArray,
                    COL_DEVICE_TYPE + " = '" + tw.com.omnihealthgroup.healthcare.WebServiceConnection.BIODATA_DEVICE_TYPE_BLOOD_PRESSURE + "'",
                    null,
                    null,
                    null,
                    //                    COL_DEVICE_TIME + " DESC"  //日期大至小
                    COL_DEVICE_TIME + " ASC" //日期小至大
            );
            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
                results.add(getBioDataSetting(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getBloodPressure() Fail :" + e.getMessage());
        } finally {
            db.close();
        }
        return results;
    }

    //取得血壓資訊
    public synchronized ArrayList<BioData> getBloodPressure_withTime(String beginDate, String endDate) {
        ArrayList<BioData> results = new ArrayList<BioData>();
        SQLiteDatabase db = openDatabase();
        try {
            Cursor cursor = db.query(
                    TABLE_BIODATA,
                    bioDataColumnArray,
                    COL_DEVICE_TYPE + " = '" + tw.com.omnihealthgroup.healthcare.WebServiceConnection.BIODATA_DEVICE_TYPE_BLOOD_PRESSURE
                            + "' and " + COL_DEVICE_TIME + " >= '" + beginDate
                            + "' and +" + COL_DEVICE_TIME + " <= '" + endDate + "' ",
                    null,
                    null,
                    null,
                    //                    COL_DEVICE_TIME + " DESC"  //日期大至小
                    COL_DEVICE_TIME + " ASC" //日期小至大
            );
            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
                results.add(getBioDataSetting(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getBloodPressure_withTime() Fail :" + e.getMessage());
        } finally {
            db.close();
        }
        return results;
    }

    //取得血壓資訊
    public synchronized ArrayList<BioData> getUserBloodPressure(String userId) {
        ArrayList<BioData> results = new ArrayList<BioData>();
        SQLiteDatabase db = openDatabase();
        try {
            Cursor cursor = db.query(
                    TABLE_BIODATA,
                    bioDataColumnArray,
                    COL_DEVICE_TYPE + " = '" + tw.com.omnihealthgroup.healthcare.WebServiceConnection.BIODATA_DEVICE_TYPE_BLOOD_PRESSURE
                            + "' and " + COL_USER_ID + " = '" + userId + "'",
                    null,
                    null,
                    null,
                    COL_DEVICE_TIME + " DESC"
            );
            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
                results.add(getBioDataSetting(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getBloodPressure() Fail :" + e.getMessage());
        } finally {
            db.close();
        }
        return results;
    }

    //取得時間區間內血壓資訊
    public synchronized ArrayList<BioData> getUserBloodPressure(String userId, String beginDate, String endDate) {
        ArrayList<BioData> results = new ArrayList<BioData>();
        SQLiteDatabase db = openDatabase();
        try {
            Cursor cursor = db.query(
                    TABLE_BIODATA,
                    bioDataColumnArray,
                    COL_DEVICE_TYPE + " = '" + tw.com.omnihealthgroup.healthcare.WebServiceConnection.BIODATA_DEVICE_TYPE_BLOOD_PRESSURE
                            + "' and " + COL_USER_ID + " = '" + userId
                            + "' and " + COL_DEVICE_TIME + " >= '" + beginDate
                            + "' and +" + COL_DEVICE_TIME + " <= '" + endDate + "' ",
                    null,
                    null,
                    null,
                    COL_DEVICE_TIME + " DESC"
            );
            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
                results.add(getBioDataSetting(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getBloodPressure() Fail :" + e.getMessage());
        } finally {
            db.close();
        }
        return results;
    }

    //取得所有生理資訊
    public synchronized ArrayList<BioData> getAll() {
        ArrayList<BioData> results = new ArrayList<BioData>();
        SQLiteDatabase db = openDatabase();
        try {
            Cursor cursor = db.query(
                    TABLE_BIODATA,
                    bioDataColumnArray,
                    null,
                    null,
                    null,
                    null,
                    COL_DEVICE_TIME + " DESC"
            );
            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
                results.add(getBioDataSetting(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getAll() Fail :" + e.getMessage());
        } finally {
            db.close();
        }
        return results;
    }
    //2012.10.19 by Neo

    /**
     * 查詢Uploaded = 0 的生理資訊（未上傳）
     */
    public synchronized ArrayList<BioData> getUploaded() {
        ArrayList<BioData> results = new ArrayList<BioData>();
        SQLiteDatabase db = openDatabase();
        try {
            Cursor cursor = db.query(
                    TABLE_BIODATA,
                    bioDataColumnArray,
                    COL_UPLOADED + "='" + tw.com.omnihealthgroup.healthcare.WebServiceConnection.DATA_IS_NOT_UPLOAD + "'",
                    null,
                    null,
                    null,
                    COL_DEVICE_TIME + " DESC"
            );
            int num = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < num; i++) {
                results.add(getBioDataSetting(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getAll() Fail :" + e.getMessage());
        } finally {
            db.close();
        }
        return results;
    }
    //2012.10.22 by Neo

    /**
     * 更新Uploaded為 1(已上傳)
     */
    public synchronized void updataUploaded(ArrayList<BioData> listBioData) {
        SQLiteDatabase db = openDatabase();
        ContentValues args = new ContentValues();
        try {
            for (BioData bioData : listBioData) {
                args.put(COL_UPLOADED, tw.com.omnihealthgroup.healthcare.WebServiceConnection.DATA_ALREADY_UPLOAD);
                db.update(TABLE_BIODATA, args, COL_DEVICE_TIME + "=\'" + bioData.getDeviceTime() + "\'", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    /**
     * 存入查詢結果為BioData類別
     *
     * @param cursor 存放查詢結果的cursor
     * @return BioData
     */
    private BioData getBioDataSetting(Cursor cursor) {
        BioData b = new BioData();
        b.set_id(cursor.getString(0));
        b.setUserId(cursor.getString(1));
        b.setDeviceSn(cursor.getString(2));
        b.setDeviceMac(cursor.getString(3));
        b.setDeviceTime(cursor.getString(4));
        b.setDeviceId(cursor.getString(5));
        b.setDeviceType(cursor.getString(6));
        b.setReadTime(cursor.getString(7));
        b.setSex(cursor.getString(8));
        b.setAge(cursor.getString(9));
        b.setBodyHeight(cursor.getString(10));
        b.setBodyWeight(cursor.getString(11));
        b.setBmi(cursor.getString(12));
        b.setAc(cursor.getString(13));
        b.setPc(cursor.getString(14));
        b.setNm(cursor.getString(15));
        b.setBhp(cursor.getString(16));
        b.setBlp(cursor.getString(17));
        b.setPulse(cursor.getString(18));
        b.setTemperature(cursor.getString(19));
        b.setHeadcircumference(cursor.getString(20));
        b.setArmcircumference(cursor.getString(21));
        b.setWaistline(cursor.getString(22));
        b.setHips(cursor.getString(23));
        b.setUploaded(cursor.getInt(24));
        b.setInputType(cursor.getString(25));
        Log.d(TAG, "BioData=" + b.toString());
        return b;
    }
}