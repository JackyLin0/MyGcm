package tw.com.omnihealthgroup.healthcare.myhealthdating.dbo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

import tw.com.omnihealthgroup.healthcare.myhealthdating.object.FoodDefinition;

/**
 * Created by Administrator on 2016/8/2.
 */
public class FoodDefinitionDAO {
    private static final String TAG = "FoodDefinitionDAO";

    /**
     * 表格名稱
     */
    public final static String TABLE_NAME = "fooddefinition";  // 總覽

    /**
     * 欄位名稱
     */
    public static final String KEY_ID = "_id";
    public static final String TYPE_COLUMN = "type"; //食品分類
    public static final String NAME_COLUMN = "name"; //食品名稱
    public static final String CONTENT_COLUMN = "content"; //內容物描述
    public static final String UNIT_COLUMN = "unit"; //單位
    public static final String AMOUNT_COLUMN = "amount"; //每份食物單位
    public static final String AMOUNTUNIT_COLUMN = "amountunit"; //每份食物單位(g/ml)
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
                    UNIT_COLUMN + " TEXT, " +
                    AMOUNT_COLUMN + " TEXT, " +
                    AMOUNTUNIT_COLUMN + " TEXT, " +
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
    public FoodDefinitionDAO(Context context) {
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
     *
     * @param TABLE_NAME     要新增資料的表格名稱
     * @param foodDefinition 傳入的資料物件
     */
    public synchronized void insert(FoodDefinition foodDefinition) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(TYPE_COLUMN, foodDefinition.getType());
        cv.put(NAME_COLUMN, foodDefinition.getName());
        cv.put(CONTENT_COLUMN, foodDefinition.getContent());
        cv.put(UNIT_COLUMN, foodDefinition.getUnit());
        cv.put(AMOUNT_COLUMN, foodDefinition.getAmount());
        cv.put(AMOUNTUNIT_COLUMN, foodDefinition.getAmountunit());
        cv.put(REFIMGSN_COLUMN, foodDefinition.getRefimgsn());
        cv.put(MOISTURE_COLUMN, foodDefinition.getMoisture());
        cv.put(PROTEIN_COLUMN, foodDefinition.getProtein());
        cv.put(FAT_COLUMN, foodDefinition.getFat());
        cv.put(SUGAR_COLUMN, foodDefinition.getSugar());
        cv.put(NOTE_COLUMN, foodDefinition.getNote());
        cv.put(STATUS_COLUMN, foodDefinition.getStatus());
        cv.put(CRTIME_COLUMN, foodDefinition.getCrTime());
        cv.put(MDTIME_COLUMN, foodDefinition.getMdTime());

        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);
        foodDefinition.set_id(id);
    }

    /**
     * 修改參數指定的物件
     *
     * @param TABLE_NAME     要新增資料的表格名稱
     * @param foodDefinition 傳入的資料物件
     */
    public synchronized boolean update(FoodDefinition foodDefinition) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();
        String where = null;
        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(TYPE_COLUMN, foodDefinition.getType());
        cv.put(NAME_COLUMN, foodDefinition.getName());
        cv.put(CONTENT_COLUMN, foodDefinition.getContent());
        cv.put(UNIT_COLUMN, foodDefinition.getUnit());
        cv.put(AMOUNT_COLUMN, foodDefinition.getAmount());
        cv.put(AMOUNTUNIT_COLUMN, foodDefinition.getAmountunit());
        cv.put(REFIMGSN_COLUMN, foodDefinition.getRefimgsn());
        cv.put(MOISTURE_COLUMN, foodDefinition.getMoisture());
        cv.put(PROTEIN_COLUMN, foodDefinition.getProtein());
        cv.put(FAT_COLUMN, foodDefinition.getFat());
        cv.put(SUGAR_COLUMN, foodDefinition.getSugar());
        cv.put(NOTE_COLUMN, foodDefinition.getNote());
        cv.put(STATUS_COLUMN, foodDefinition.getStatus());
        cv.put(CRTIME_COLUMN, foodDefinition.getCrTime());
        cv.put(MDTIME_COLUMN, foodDefinition.getMdTime());

        // 設定條件為Title，格式為「欄位名稱=開立科別＋床號＋姓名」
        where = KEY_ID + "=" + foodDefinition.get_id();

        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    public synchronized boolean deleteAll() {
        return db.delete(TABLE_NAME, null, null) > 0;
    }


    /**
     * 讀取所有記事資料
     *
     * @param tableNumber 要讀取資料的類型代號
     * @return List Object
     */
    public List<FoodDefinition> getfood_type() {
        List<FoodDefinition> resultList = new LinkedList<>();
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
     *
     * @param tableNumber 要讀取資料的類型代號
     * @return List Object
     */
    public List<FoodDefinition> getfood_type_data(String type) {
        List<FoodDefinition> resultList = new LinkedList<>();
        String where = null;
        db.beginTransaction();
        if (type != null) {
            where = TYPE_COLUMN + "=" + "'" + type + "'";
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


    public FoodDefinition getRecord(Cursor cursor) {
        FoodDefinition foodDefinition = new FoodDefinition();
        foodDefinition.set_id(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        foodDefinition.setType(cursor.getString(cursor.getColumnIndex(TYPE_COLUMN)));
        foodDefinition.setName(cursor.getString(cursor.getColumnIndex(NAME_COLUMN)));
        foodDefinition.setContent(cursor.getString(cursor.getColumnIndex(CONTENT_COLUMN)));
        foodDefinition.setUnit(cursor.getString(cursor.getColumnIndex(UNIT_COLUMN)));
        foodDefinition.setAmount(cursor.getString(cursor.getColumnIndex(AMOUNT_COLUMN)));
        foodDefinition.setAmountunit(cursor.getString(cursor.getColumnIndex(AMOUNTUNIT_COLUMN)));
        foodDefinition.setRefimgsn(cursor.getString(cursor.getColumnIndex(REFIMGSN_COLUMN)));
        foodDefinition.setMoisture(cursor.getString(cursor.getColumnIndex(MOISTURE_COLUMN)));
        foodDefinition.setProtein(cursor.getString(cursor.getColumnIndex(PROTEIN_COLUMN)));
        foodDefinition.setFat(cursor.getString(cursor.getColumnIndex(FAT_COLUMN)));
        foodDefinition.setSugar(cursor.getString(cursor.getColumnIndex(SUGAR_COLUMN)));
        foodDefinition.setNote(cursor.getString(cursor.getColumnIndex(NOTE_COLUMN)));
        foodDefinition.setStatus(cursor.getString(cursor.getColumnIndex(STATUS_COLUMN)));
        foodDefinition.setCrTime(cursor.getString(cursor.getColumnIndex(CRTIME_COLUMN)));
        foodDefinition.setMdTime(cursor.getString(cursor.getColumnIndex(MDTIME_COLUMN)));

        return foodDefinition;
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