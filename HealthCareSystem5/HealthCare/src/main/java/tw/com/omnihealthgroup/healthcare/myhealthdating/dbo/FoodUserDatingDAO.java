package tw.com.omnihealthgroup.healthcare.myhealthdating.dbo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import tw.com.omnihealthgroup.healthcare.myhealthdating.object.FoodUserDating;

/**
 * Created by Administrator on 2016/8/2.
 */
public class FoodUserDatingDAO {
    private static final String TAG = "FoodUserDatingDAO";

    /**
     * 表格名稱
     */
    public final static String TABLE_NAME = "fooduserdating";  // 總覽

    /**
     * 欄位名稱
     */
    public static final String KEY_ID = "_id";
    public static final String TYPE_COLUMN = "type"; //食品分類
    public static final String NAME_COLUMN = "name"; //食品名稱
    public static final String CONTENT_COLUMN = "content"; //內容物描述
    public static final String COOKTYPE_COLUMN = "cooktype"; //內容物描述
    public static final String UNIT_COLUMN = "unit"; //單位
    public static final String AMOUNT_COLUMN = "amount"; //份量
    public static final String AMOUNTUNIT_COLUMN = "amountunit"; //每單位份量(g/ml)
    public static final String MEALTYPE_COLUMN = "mealtype"; //餐別
    public static final String IMAGE_COLUMN = "image"; //相片
    public static final String MOISTURESUM_COLUMN = "moisturesum"; //餐點總熱量(kcal)
    public static final String PROTEINSUM_COLUMN = "proteinsum"; //餐點總蛋白質(g)
    public static final String FATSUM_COLUMN = "fatsum"; //餐點總脂肪(g)
    public static final String SUGARSUM_COLUMN = "sugarsum"; //餐點總碳水化合物(g)

    public static final String REFIMGSN_COLUMN = "refimgsn"; //每份食物單位參考圖
    public static final String MOISTURE_COLUMN = "moisture"; //每單位熱量(kcal)
    public static final String PROTEIN_COLUMN = "protein"; //每單位蛋白質(g)
    public static final String FAT_COLUMN = "fat"; //每單位脂肪(g)
    public static final String SUGAR_COLUMN = "sugar"; //每單位碳水化合物(g)
    public static final String NOTE_COLUMN = "note"; //備註
    public static final String STATUS_COLUMN = "status"; //狀態
    public static final String CRTIME_COLUMN = "crTime"; //建立時間
    public static final String MDTIME_COLUMN = "mdTime"; //修改時間

    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TYPE_COLUMN + " TEXT, " +
                    NAME_COLUMN + " TEXT, " +
                    CONTENT_COLUMN + " TEXT, " +
                    COOKTYPE_COLUMN + " TEXT, " +
                    UNIT_COLUMN + " TEXT, " +
                    AMOUNT_COLUMN + " TEXT, " +
                    AMOUNTUNIT_COLUMN + " TEXT, " +
                    MEALTYPE_COLUMN + " TEXT, " +
                    IMAGE_COLUMN + " TEXT, " +
                    MOISTURESUM_COLUMN + " TEXT, " +
                    PROTEINSUM_COLUMN + " TEXT, " +
                    FATSUM_COLUMN + " TEXT, " +
                    SUGARSUM_COLUMN + " TEXT, " +
                    REFIMGSN_COLUMN + " TEXT, " +
                    MOISTURE_COLUMN + " TEXT, " +
                    PROTEIN_COLUMN + " TEXT, " +
                    FAT_COLUMN + " TEXT, " +
                    SUGAR_COLUMN + " TEXT, " +
                    NOTE_COLUMN + " TEXT, " +
                    STATUS_COLUMN + " TEXT, " +
                    CRTIME_COLUMN + " TEXT, " +
                    MDTIME_COLUMN + " TEXT)";

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 資料庫物件
     */
    private SQLiteDatabase db;

    /**
     * 建構子，一般的應用都不需要修改
     *
     * @param context 被調用的畫面
     */
    public FoodUserDatingDAO(Context context) {
        db = FoodDefinitionDBHelper.getDatabase(context);
    }

    /**
     * 確認DB是否在開啟狀態
     *
     * @return true：開啟<br/>false：關閉
     */
    public boolean dbIsOpen() {
        return db.isOpen();
    }

    /**
     * 關閉資料庫，一般的應用都不需要修改
     */
    public void closeDB() {
        db.close();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 新增參數指定的物件
     */
    public synchronized void insert(FoodUserDating foodUserDating) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(TYPE_COLUMN, foodUserDating.getType());
        cv.put(NAME_COLUMN, foodUserDating.getName());
        cv.put(CONTENT_COLUMN, foodUserDating.getContent());
        cv.put(COOKTYPE_COLUMN, foodUserDating.getCooktype());
        cv.put(UNIT_COLUMN, foodUserDating.getUnit());
        cv.put(AMOUNT_COLUMN, foodUserDating.getAmount());
        cv.put(AMOUNTUNIT_COLUMN, foodUserDating.getAmountunit());
        cv.put(MEALTYPE_COLUMN, foodUserDating.getMealtype());
        cv.put(IMAGE_COLUMN, foodUserDating.getImage());
        cv.put(MOISTURESUM_COLUMN, foodUserDating.getMoisturesum());
        cv.put(PROTEINSUM_COLUMN, foodUserDating.getProteinsum());
        cv.put(FATSUM_COLUMN, foodUserDating.getFatsum());
        cv.put(SUGARSUM_COLUMN, foodUserDating.getSugarsum());

        cv.put(REFIMGSN_COLUMN, foodUserDating.getRefimgsn());
        cv.put(MOISTURE_COLUMN, foodUserDating.getMoisture());
        cv.put(PROTEIN_COLUMN, foodUserDating.getProtein());
        cv.put(FAT_COLUMN, foodUserDating.getFat());
        cv.put(SUGAR_COLUMN, foodUserDating.getSugar());
        cv.put(NOTE_COLUMN, foodUserDating.getNote());
        cv.put(STATUS_COLUMN, foodUserDating.getStatus());
        cv.put(CRTIME_COLUMN, foodUserDating.getCrTime());
        cv.put(MDTIME_COLUMN, foodUserDating.getMdTime());

        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);
        foodUserDating.set_id(id);
    }

    /**
     * 修改參數指定的物件
     */
    public synchronized boolean update(FoodUserDating foodUserDating) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();
        String where = null;
        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(TYPE_COLUMN, foodUserDating.getType());
        cv.put(NAME_COLUMN, foodUserDating.getName());
        cv.put(CONTENT_COLUMN, foodUserDating.getContent());
        cv.put(COOKTYPE_COLUMN, foodUserDating.getCooktype());
        cv.put(UNIT_COLUMN, foodUserDating.getUnit());
        cv.put(AMOUNT_COLUMN, foodUserDating.getAmount());
        cv.put(AMOUNTUNIT_COLUMN, foodUserDating.getAmountunit());
        cv.put(MEALTYPE_COLUMN, foodUserDating.getMealtype());
        cv.put(IMAGE_COLUMN, foodUserDating.getImage());
        cv.put(MOISTURESUM_COLUMN, foodUserDating.getMoisturesum());
        cv.put(PROTEINSUM_COLUMN, foodUserDating.getProteinsum());
        cv.put(FATSUM_COLUMN, foodUserDating.getFatsum());
        cv.put(SUGARSUM_COLUMN, foodUserDating.getSugarsum());

        cv.put(REFIMGSN_COLUMN, foodUserDating.getRefimgsn());
        cv.put(MOISTURE_COLUMN, foodUserDating.getMoisture());
        cv.put(PROTEIN_COLUMN, foodUserDating.getProtein());
        cv.put(FAT_COLUMN, foodUserDating.getFat());
        cv.put(SUGAR_COLUMN, foodUserDating.getSugar());
        cv.put(NOTE_COLUMN, foodUserDating.getNote());
        cv.put(STATUS_COLUMN, foodUserDating.getStatus());
        cv.put(CRTIME_COLUMN, foodUserDating.getCrTime());
        cv.put(MDTIME_COLUMN, foodUserDating.getMdTime());

        // 設定條件為Title，格式為「欄位名稱=開立科別＋床號＋姓名」
        where = KEY_ID + "=" + foodUserDating.get_id();

        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    public synchronized boolean deleteAll() {
        return db.delete(TABLE_NAME, null, null) > 0;
    }

    public synchronized boolean delete_ID(FoodUserDating foodUserDating) {
        String where = null;
        where = KEY_ID + "=" + foodUserDating.get_id();
        return db.delete(TABLE_NAME, where, null) > 0;
    }


    /**
     * 讀取所有記事資料
     */
    public List<FoodUserDating> getfood_type() {
        List<FoodUserDating> resultList = new LinkedList<>();
        db.beginTransaction();
        String where = null;
        Cursor cursor = db.query(
                //                    TABLE_ARRAY[tableNumber], null, DATE_COLUMN + " LIKE?", new String[]{todayTime + "%"}, null, null, null, null);
                true, TABLE_NAME, null, where, null, TYPE_COLUMN, null, KEY_ID + " ASC", null);

        // 如果有查詢結果
        while (cursor.moveToNext()) {
            // 讀取包裝一筆資料的物件
            resultList.add(getRecord(cursor));
        }
        //關閉Cursor物件
        cursor.close();
        db.endTransaction();

        return resultList;
    }

    /**
     * 讀取所有記事資料
     */
    public List<FoodUserDating> getfood_type_data(String day, String type) {
        List<FoodUserDating> resultList = new LinkedList<>();
        String where = null;
        Log.v(TAG, day);
        Log.v(TAG, type);

        db.beginTransaction();
        if (day != null) {
            //            where = CRTIME_COLUMN + " like" + "'" + day + "'" + " and " + MEALTYPE_COLUMN + "=" + "'" + type + "'";
            where = MEALTYPE_COLUMN + " = " + "'" + type + "'"
                    + " and " + CRTIME_COLUMN + " like " + "'" + "%" + day + "%" + "'";
        }
        Cursor cursor = db.query(
                //                    TABLE_ARRAY[tableNumber], null, DATE_COLUMN + " LIKE?", new String[]{todayTime + "%"}, null, null, null, null);
                true, TABLE_NAME, null, where, null, null, null, KEY_ID + " ASC", null);

        // 如果有查詢結果
        while (cursor.moveToNext()) {
            // 讀取包裝一筆資料的物件
            resultList.add(getRecord(cursor));
        }
        //關閉Cursor物件
        cursor.close();
        db.endTransaction();

        return resultList;
    }

    public FoodUserDating getRecord(Cursor cursor) {
        FoodUserDating foodUserDating = new FoodUserDating();
        foodUserDating.set_id(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        foodUserDating.setType(cursor.getString(cursor.getColumnIndex(TYPE_COLUMN)));
        foodUserDating.setName(cursor.getString(cursor.getColumnIndex(NAME_COLUMN)));
        foodUserDating.setContent(cursor.getString(cursor.getColumnIndex(CONTENT_COLUMN)));
        foodUserDating.setCooktype(cursor.getString(cursor.getColumnIndex(COOKTYPE_COLUMN)));
        foodUserDating.setUnit(cursor.getString(cursor.getColumnIndex(UNIT_COLUMN)));
        foodUserDating.setAmount(cursor.getString(cursor.getColumnIndex(AMOUNT_COLUMN)));
        foodUserDating.setAmountunit(cursor.getString(cursor.getColumnIndex(AMOUNTUNIT_COLUMN)));
        foodUserDating.setMealtype(cursor.getString(cursor.getColumnIndex(MEALTYPE_COLUMN)));
        foodUserDating.setImage(cursor.getString(cursor.getColumnIndex(IMAGE_COLUMN)));
        foodUserDating.setMoisturesum(cursor.getString(cursor.getColumnIndex(MOISTURESUM_COLUMN)));
        foodUserDating.setProteinsum(cursor.getString(cursor.getColumnIndex(PROTEINSUM_COLUMN)));
        foodUserDating.setFatsum(cursor.getString(cursor.getColumnIndex(FATSUM_COLUMN)));
        foodUserDating.setSugarsum(cursor.getString(cursor.getColumnIndex(SUGARSUM_COLUMN)));

        foodUserDating.setRefimgsn(cursor.getString(cursor.getColumnIndex(REFIMGSN_COLUMN)));
        foodUserDating.setMoisture(cursor.getString(cursor.getColumnIndex(MOISTURE_COLUMN)));
        foodUserDating.setProtein(cursor.getString(cursor.getColumnIndex(PROTEIN_COLUMN)));
        foodUserDating.setFat(cursor.getString(cursor.getColumnIndex(FAT_COLUMN)));
        foodUserDating.setSugar(cursor.getString(cursor.getColumnIndex(SUGAR_COLUMN)));
        foodUserDating.setNote(cursor.getString(cursor.getColumnIndex(NOTE_COLUMN)));
        foodUserDating.setStatus(cursor.getString(cursor.getColumnIndex(STATUS_COLUMN)));
        foodUserDating.setCrTime(cursor.getString(cursor.getColumnIndex(CRTIME_COLUMN)));
        foodUserDating.setMdTime(cursor.getString(cursor.getColumnIndex(MDTIME_COLUMN)));

        return foodUserDating;
    }

    // 取得資料數量
    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        cursor.close();
        return result;
    }


}