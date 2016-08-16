package tw.com.omnihealthgroup.healthcare.myhealthcalendar.dbo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScheduleDBHelper extends SQLiteOpenHelper {

    public static final String SCHEDULE_DATABASE_NAME = "ScheduleData.db"; // 資料庫名稱
    public static final int SCHEDULE_DATABASE_VERSION = 1;   // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
    // 資料庫物件，固定的欄位變數
    private static SQLiteDatabase scheduleDatabase;

    public ScheduleDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // 需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
    public static SQLiteDatabase getDatabase(Context context) {
        if (scheduleDatabase == null || !scheduleDatabase.isOpen()) {
            System.out.println("SQLiteDatabase is close");
            scheduleDatabase = new ScheduleDBHelper(
                    context,
                    SCHEDULE_DATABASE_NAME,
                    null,
                    SCHEDULE_DATABASE_VERSION
            ).getWritableDatabase();
        }
        return scheduleDatabase;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 建立需要的表格
        db.execSQL(ScheduleDAO.CREATE_TABLE_CONSULTATION);
        db.execSQL(ScheduleDAO.CREATE_TABLE_OPERATE);
        db.execSQL(ScheduleDAO.CREATE_TABLE_CLINIC);
        db.execSQL(ScheduleDAO.CREATE_TABLE_MEETING);
        db.execSQL(ScheduleDAO.CREATE_TABLE_PERSONAL);
        db.execSQL(ScheduleDAO.CREATE_TABLE_EXAMINATION);
        db.execSQL(ScheduleDAO.CREATE_TABLE_LAST_MSG_PK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // 刪除原有的表格
        db.execSQL("DROP TABLE IF EXISTS " + ScheduleDAO.TABLE_CONSULTATION);
        db.execSQL("DROP TABLE IF EXISTS " + ScheduleDAO.TABLE_OPERATE);
        db.execSQL("DROP TABLE IF EXISTS " + ScheduleDAO.TABLE_CLINIC);
        db.execSQL("DROP TABLE IF EXISTS " + ScheduleDAO.TABLE_MEETING);
        db.execSQL("DROP TABLE IF EXISTS " + ScheduleDAO.TABLE_PERSONAL);
        db.execSQL("DROP TABLE IF EXISTS " + ScheduleDAO.TABLE_EXAMINATION);
        db.execSQL("DROP TABLE IF EXISTS " + ScheduleDAO.TABLE_LAST_MSG_PK);
        // 呼叫onCreate建立新版的表格
        onCreate(db);
    }
}
