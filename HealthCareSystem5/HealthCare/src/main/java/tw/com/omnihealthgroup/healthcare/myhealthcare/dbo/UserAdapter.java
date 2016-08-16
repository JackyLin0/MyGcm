package tw.com.omnihealthgroup.healthcare.myhealthcare.dbo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class UserAdapter extends DbAdapter {
    private final static String TAG = "UserAdapter";

    public final static String COL_UNIQUELD = "UNIQUELD"; //系統唯一識別碼
    public final static String COL_RFID = "RFID"; //RFID卡號
    public final static String COL_IDNO = "IDNO"; //身分證號碼
    public final static String COL_NATIONALITY = "NATIONALITY"; //國籍，說明：[0=未知(預設)]，[zh-TW=台灣]，[en-US=美國]
    public final static String COL_BLOOD = "BLOOD"; //血型，說明：[0=未知(預設)]，[A]，[B]，[O]，[AB]
    public final static String COL_MARRIED = "MARRIED"; //婚姻狀況，說明：[0=未知(預設)]，[Unmarried=未婚]，[Married=已婚]
    public final static String COL_RHTYPE = "RHTYPE"; //RH血型，說明：[0=未知(預設)]，[Rh+]，[Rh-]

    // UID : 登入帳號
    public final static String COL_UID = "UID";
    // NAME : 用戶姓名     String userName; //姓名
    public final static String COL_NAME = "NAME";
    // PASSWORD: 密碼
    public final static String COL_PASSWORD = "PASSWORD";
    // NICK_NAME : 暱稱     String userEngName; //英文姓名
    public final static String COL_NICK_NAME = "NICK_NAME";
    // GENDER : 性別     String userSex; //性別，說明：[Unknow=未知(預設)]，[Male=男]，[Female=女]
    public final static String COL_GENDER = "GENDER";
    //// BIRTHDAY: 生日     String userBirthDay; //出生年月日，格式:yyyy-MM-dd HH:mm:ss.fff
    public final static String COL_BIRTHDAY = "BIRTHDAY";
    // PHONE :電話     String userHomeTEL; //家裡電話號碼
    public final static String COL_PHONE = "PHONE";
    // AREA :居住縣市
    public final static String COL_AREA = "AREA";
    // MOBILE :行動電話     String userMobile; //手機號碼
    public final static String COL_MOBILE = "MOBILE";
    // AC_HIGH: 飯前血糖
    public final static String COL_ACHIGH = "AC_HIGH";
    // AC_LOW: 飯後血糖
    public final static String COL_ACLOW = "AC_LOW";
    // BHP: 收縮壓
    public final static String COL_BHP = "BHP";
    // BLP: 舒張壓
    public final static String COL_BLP = "BLP";
    // HEIGHT:身高
    public final static String COL_HEIGHT = "HEIGHT";
    // WEIGHT:體重
    public final static String COL_WEIGHT = "WEIGHT";
    // REMEMBER_USER:記憶帳號密碼
    public final static String COL_REMEMBER_USER = "REMEMBER_USER";
    // TYPE:帳號型態
    public final static String COL_TYPE = "TYPE";
    // UNIT:單位名稱
    public final static String COL_UNIT = "UNIT";

    public final static String COL_EMAIL = "EMAIL"; //電子郵件信箱

    public final static String TABLE_USER = "HM_USER";

    public UserAdapter(Context ctx) {
        super(ctx);
    }

    private String[] userDataColumnArray = new String[]{
            COL_UNIQUELD, COL_RFID, COL_IDNO, COL_NATIONALITY,
            COL_BLOOD, COL_MARRIED, COL_RHTYPE, COL_UID,
            COL_NAME, COL_PASSWORD, COL_NICK_NAME, COL_GENDER,
            COL_BIRTHDAY, COL_PHONE, COL_AREA, COL_MOBILE, COL_ACHIGH,
            COL_ACLOW, COL_BHP, COL_BLP, COL_HEIGHT,
            COL_WEIGHT, COL_REMEMBER_USER, COL_TYPE, COL_UNIT, COL_EMAIL
    };

    public synchronized int deleteUser(String uid) {
        SQLiteDatabase db = openDatabase();
        int status = db.delete(TABLE_USER, // 資料表名稱
                "UID=" + uid, // WHERE
                null // WHERE的參數
        );
        db.close();
        return status;
    }

    public synchronized long createtUser(User u) {
        Log.d(TAG, "Create User=" + u.getUid());
        SQLiteDatabase db = openDatabase();
        ContentValues args = new ContentValues();

        args.put(COL_UNIQUELD, u.getUserUniqueId()); //系統唯一識別碼
        args.put(COL_RFID, u.getRfid()); //RFID卡號
        args.put(COL_IDNO, u.getUserIDNO()); //身分證號碼
        args.put(COL_NATIONALITY, u.getUserNationality()); //國籍，說明：[0=未知(預設)]，[zh-TW=台灣]，[en-US=美國]
        args.put(COL_BLOOD, u.getUserBlood()); //血型，說明：[0=未知(預設)]，[A]，[B]，[O]，[AB]
        args.put(COL_MARRIED, u.getUserMarried()); //婚姻狀況，說明：[0=未知(預設)]，[Unmarried=未婚]，[Married=已婚]
        args.put(COL_RHTYPE, u.getUserRhType()); //RH血型，說明：[0=未知(預設)]，[Rh+]，[Rh-]

        args.put(COL_UID, u.getUid());
        args.put(COL_NAME, u.getName()); // NAME : 用戶姓名     String userName; //姓名
        args.put(COL_PASSWORD, u.getPassword());
        args.put(COL_NICK_NAME, u.getNickname()); // NICK_NAME : 暱稱     String userEngName; //英文姓名
        args.put(COL_GENDER, u.getGender()); // GENDER : 性別     String userSex; //性別，說明：[Unknow=未知(預設)]，[Male=男]，[Female=女]
        args.put(COL_BIRTHDAY, u.getBirthday()); // BIRTHDAY: 生日     String userBirthDay; //出生年月日，格式:yyyy-MM-dd HH:mm:ss.fff

        args.put(COL_PHONE, u.getPhone()); // PHONE :電話     String userHomeTEL; //家裡電話號碼
        args.put(COL_AREA, u.getArea()); // AREA :居住縣市
        args.put(COL_MOBILE, u.getMobile()); // MOBILE :行動電話     String userMobile; //手機號碼

        args.put(COL_ACHIGH, u.getAcHigh());
        args.put(COL_ACLOW, u.getAcLow());
        args.put(COL_BHP, u.getBhp());
        args.put(COL_BLP, u.getBlp());
        args.put(COL_HEIGHT, u.getHeight());
        args.put(COL_WEIGHT, u.getWeight());

        args.put(COL_REMEMBER_USER, u.getRememberUser());
        args.put(COL_TYPE, u.getType());
        args.put(COL_UNIT, u.getUnit());
        args.put(COL_EMAIL, u.getEmail()); //電子郵件信箱

        long i = db.insert(TABLE_USER, null, args);
        db.close();
        Log.i(TAG + "info", "insert db result i: " + i);
        return i;
    }

    //update
    public synchronized long updateUserWeightAndHieght(User u) {
        Log.d(TAG, "update User=" + u.getUid());
        SQLiteDatabase db = openDatabase();
        ContentValues args = new ContentValues();
        args.put(COL_UID, u.getUid());
        args.put(COL_HEIGHT, u.getHeight());
        args.put(COL_WEIGHT, u.getWeight());
        long i = db.update(TABLE_USER, args, COL_UID + "=\'" + u.getUid() + "\'", null);
        //Log.i(TAG, "long i:"+i+", COL_UID="+u.getUid());
        db.close();
        return i;
    }

    //update
    public synchronized long updateUserPassword(User u) {
        Log.d(TAG, "updateUserPassword User=" + u.getUid());
        SQLiteDatabase db = openDatabase();
        ContentValues args = new ContentValues();
        args.put(COL_UID, u.getUid());
        args.put(COL_PASSWORD, u.getPassword());
        long i = db.update(TABLE_USER, args, COL_UID + "=\'" + u.getUid() + "\'", null);
        //Log.i(TAG, "long i:"+i+", COL_UID="+u.getUid());
        db.close();
        return i;
    }

    // get user by userId
    public synchronized User getUserByUid(String uid) {
        User u = null;
        SQLiteDatabase db = null;// openDatabase();
        try {
            //Log.i(TAG,"getUserByUid()=" + uid);
            db = openDatabase();
            Cursor uCursor = db.query(true,
                    TABLE_USER,
                    new String[]{COL_UID, COL_NAME},
                    COL_UID + "=\'" + uid + "\'",
                    null,
                    null,
                    null,
                    null, null);
            //Log.i(TAG,"Cursor="+uCursor );
            if (uCursor != null && uCursor.getCount() > 0) {
                uCursor.moveToFirst();
                u = new User();
                u.setUid(uCursor.getString(0));
                u.setName(uCursor.getString(1));
            }
            if (uCursor != null) {
                uCursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "getUserByUid Fail() :" + e.getMessage());
        } finally {
            if (null != db) {
                db.close();
            }
        }
        return u;
    }

    // get user uid
    public synchronized String getUID() {
        //Log.i(TAG,"getUID()");
        String u = null;
        SQLiteDatabase db = null;// openDatabase();
        try {
            db = openDatabase();
            Cursor uCursor = db.query(true,
                    TABLE_USER,
                    new String[]{COL_UID},
                    null,
                    null,
                    null,
                    null,
                    null, null);
            //Log.i(TAG,"Cursor="+uCursor );
            if (uCursor != null && uCursor.getCount() > 0) {
                uCursor.moveToFirst();
                u = uCursor.getString(0);
            }
            if (uCursor != null) {
                uCursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "getUID Fail() :" + e.getMessage());
        } finally {
            if (null != db) {
                db.close();
            }
        }
        return u;
    }

    // get user Unit And Type
    public synchronized User getUIDUnitType() {
        //Log.i(TAG,"getUIDUnitType()");
        User u = null;
        SQLiteDatabase db = null;// openDatabase();
        try {
            db = openDatabase();
            Cursor uCursor = db.query(true,
                    TABLE_USER,
                    new String[]{COL_UID, COL_TYPE, COL_UNIT},
                    null,
                    null,
                    null,
                    null,
                    null, null);
            //Log.i(TAG,"Cursor="+uCursor );
            if (uCursor != null && uCursor.getCount() > 0) {
                uCursor.moveToFirst();
                u = new User();
                u.setUid(uCursor.getString(0));
                u.setType(uCursor.getString(1));
                u.setUnit(uCursor.getString(2));
            }
            if (uCursor != null) {
                uCursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "getUID Fail() :" + e.getMessage());
        } finally {
            if (null != db) {
                db.close();
            }
        }
        return u;
    }

    // get user uid and password
    public synchronized User getUserUIdAndPassword() {
        //Log.i(TAG,"getUID()");
        User u = null;
        SQLiteDatabase db = null;// openDatabase();
        try {
            db = openDatabase();
            Cursor uCursor = db.query(true,
                    TABLE_USER,
                    new String[]{COL_UID, COL_PASSWORD},
                    null,
                    null,
                    null,
                    null,
                    null, null);
            //Log.i(TAG,"Cursor="+uCursor );
            if (uCursor != null && uCursor.getCount() > 0) {
                uCursor.moveToFirst();
                u = new User();
                u.setUid(uCursor.getString(0));
                u.setPassword(uCursor.getString(1));
            }
            if (uCursor != null) {
                uCursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "getUserUIdAndPassword Fail() :" + e.getMessage());
        } finally {
            if (null != db) {
                db.close();
            }
        }
        return u;
    }

    // get remember user
    public synchronized String getRememberUser() {
        //Log.i(TAG,"getRememberUser()");
        String u = null;
        SQLiteDatabase db = null;// openDatabase();
        try {
            db = openDatabase();
            // true為過濾重複值
            Cursor uCursor = db.query(true,
                    TABLE_USER,
                    new String[]{COL_REMEMBER_USER},
                    null,
                    null,
                    null,
                    null,
                    null, null);
            //Log.i(TAG,"Cursor="+uCursor );
            if (uCursor != null && uCursor.getCount() > 0) {
                uCursor.moveToFirst();
                u = uCursor.getString(0);
            }
            if (uCursor != null) {
                uCursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "getUID Fail() :" + e.getMessage());
        } finally {
            if (null != db) {
                db.close();
            }
        }
        return u;
    }

    // get user weight
    public synchronized String getUserWeight() {
        //Log.i(TAG,"getUserWeight()");
        String u = null;
        SQLiteDatabase db = null;// openDatabase();
        try {
            db = openDatabase();
            Cursor uCursor = db.query(true,
                    TABLE_USER,
                    new String[]{COL_WEIGHT},
                    null,
                    null,
                    null,
                    null,
                    null, null);
            //Log.i(TAG,"Cursor="+uCursor );
            if (uCursor != null && uCursor.getCount() > 0) {
                uCursor.moveToFirst();
                u = uCursor.getString(0);
            }
            if (uCursor != null) {
                uCursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "getUID Fail() :" + e.getMessage());
        } finally {
            if (null != db) {
                db.close();
            }
        }
        return u;
    }

    // get all user
    public synchronized ArrayList<User> getAllUser() {
        ArrayList<User> results = new ArrayList<User>();
        SQLiteDatabase db = openDatabase();
        try {
            Cursor uCursor = db.query(true,
                    TABLE_USER,
                    userDataColumnArray,
                    null,
                    null,
                    null,
                    null,
                    null, null);

            int num = uCursor.getCount();
            uCursor.moveToFirst();
            for (int i = 0; i < num; i++) {
                results.add(getUserDataSetting(uCursor));
                uCursor.moveToNext();
            }
            uCursor.close();
        } catch (Exception e) {
            Log.e(TAG, "getAllUser Fail :" + e.getMessage());
        } finally {
            db.close();
        }
        return results;
    }

    public synchronized void delAllUser() {
        SQLiteDatabase db = openDatabase();
        long status = db.delete(TABLE_USER, null, null);
        db.close();
        //Log.i(TAG,"Delete All User=" + status);
    }

    //刪除紀錄，回傳成功刪除筆數
    public synchronized long delUser(String userId) {
        SQLiteDatabase db = openDatabase();
        long status = db.delete(TABLE_USER, "" + userId, null);
        Log.d(TAG, "Delete User=" + userId + ",status=" + status);
        db.close();
        return status;
    }


    /**
     * 存入查詢結果為UserData類別
     *
     * @param cursor 存放查詢結果的cursor
     * @return UserData
     */
    private User getUserDataSetting(Cursor cursor) {
        User b = new User();
        b.setUserUniqueId(cursor.getString(0));
        b.setRfid(cursor.getString(1));
        b.setUserIDNO(cursor.getString(2));
        b.setUserNationality(cursor.getString(3));
        b.setUserBlood(cursor.getString(4));
        b.setUserMarried(cursor.getString(5));
        b.setUserRhType(cursor.getString(6));
        b.setUid(cursor.getString(7));
        b.setName(cursor.getString(8));
        b.setPassword(cursor.getString(9));
        b.setNickname(cursor.getString(10));
        b.setGender(cursor.getString(11));
        b.setBirthday(cursor.getString(12));
        b.setPhone(cursor.getString(13));
        b.setArea(cursor.getString(14));
        b.setMobile(cursor.getString(15));
        b.setAcHigh(cursor.getString(16));
        b.setAcLow(cursor.getString(17));
        b.setBhp(cursor.getString(18));
        b.setBlp(cursor.getString(19));
        b.setHeight(cursor.getString(20));
        b.setWeight(cursor.getString(21));
        b.setRememberUser(cursor.getString(22));
        b.setType(cursor.getString(23));
        b.setUnit(cursor.getString(24));
        b.setEmail(cursor.getString(25));
        return b;
    }
}