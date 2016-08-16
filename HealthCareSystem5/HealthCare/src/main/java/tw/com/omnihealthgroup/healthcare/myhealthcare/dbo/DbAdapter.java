package tw.com.omnihealthgroup.healthcare.myhealthcare.dbo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import tw.com.omnihealthgroup.healthcare.R;

public class DbAdapter {
    private static final Object LOCK = new Object();
    private static final String TAG = "DbAdapter";
    private Context mCtx = null;
    // 取得SD卡路徑
    private final String DATABASE_PATH = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/HealthCare";
    private final String NO_SDCARD_DATABASE_PATH = "/data/data/com.omnihealthgroup/databases/";
    private final String DATABASE_FILENAME;

    public DbAdapter(Context ctx) {
        this.mCtx = ctx;
        DATABASE_FILENAME = "personal.db";
    }

    // 取得操作資料庫的對象
    public SQLiteDatabase openDatabase() {
        synchronized (LOCK) {
            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                //Log.i(TAG, "Has SDCard");
                try {
                    boolean b = false;
                    //取得資料庫的完整路徑
                    String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
                    //Log.i(TAG, "databaseFilename : " + databaseFilename);
                    //將資料庫文件從資源文件放到合適地方（資源文件也就是資料庫文件放在項目的res下的raw目錄中）
                    //將資料庫文件複製到SD卡中
                    File dir = new File(DATABASE_PATH);
                    if (!dir.exists()) {
                        //Log.i(TAG,"MakeDir=" + dir.getAbsolutePath());
                        b = dir.mkdir();
                    }
                    //判斷是否存在該文件
                    if (!(new File(databaseFilename)).exists()) {
                        //Log.i(TAG,"Database file=" + databaseFilename);
                        //若不存在則取得資料庫輸入串流對象
                        InputStream is = mCtx.getResources().openRawResource(R.raw.personal);
                        //新建輸出串流
                        FileOutputStream fos = new FileOutputStream(databaseFilename);
                        //將資料輸出
                        byte[] buffer = new byte[8192];
                        int count = 0;
                        while ((count = is.read(buffer)) > 0) {
                            fos.write(buffer, 0, count);
                        }
                        // 關閉資源
                        fos.close();
                        is.close();
                    }
                    //取得SQLDatabase對象
                    return SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
                } catch (Exception e) {
                    Log.e(TAG, "openDatabase() Exception : " + e);
                    System.out.println(e.getMessage());
                }
            } else {
                //Log.i(TAG, "No SDCard");
                try {
                    boolean b = false;
                    //取得資料庫的完整路徑
                    String databaseFilename = NO_SDCARD_DATABASE_PATH + DATABASE_FILENAME;
                    //Log.i(TAG, "databaseFilename : " + databaseFilename);
                    //將資料庫文件從資源文件放到合適地方（資源文件也就是資料庫文件放在項目的res下的raw目錄中）
                    //將資料庫文件複製到手機裡
                    File dir = new File(NO_SDCARD_DATABASE_PATH);
                    if (!dir.exists()) {
                        //Log.i(TAG,"MakeDir=" + dir.getAbsolutePath());
                        b = dir.mkdir();
                    }
                    //判斷是否存在該文件
                    if (!(new File(databaseFilename)).exists()) {
                        //Log.i(TAG,"Database file=" + databaseFilename);
                        //若不存在則取得資料庫輸入串流對象
                        InputStream is = mCtx.getResources().openRawResource(R.raw.personal);
                        //新建輸出串流
                        FileOutputStream fos = new FileOutputStream(databaseFilename);
                        //將資料輸出
                        byte[] buffer = new byte[8192];
                        int count;
                        while ((count = is.read(buffer)) > 0) {
                            fos.write(buffer, 0, count);
                        }
                        //關閉資源
                        fos.close();
                        is.close();
                    }
                    //取得SQLDatabase對象
                    return SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
                } catch (Exception e) {
                    Log.e(TAG, "openDatabase() Exception : " + e);
                    System.out.println(e.getMessage());
                }
            }
        }

        return null;
    }

    public void updateSqlite() {
        synchronized (LOCK) {
            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                //Log.i(TAG, "Has SDCard");
                try {
                    //取得資料庫的完整路徑
                    String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;

                    //Log.i(TAG,"Database file=" + databaseFilename);
                    //若不存在則取得資料庫輸入串流對象
                    InputStream is = mCtx.getResources().openRawResource(R.raw.personal);
                    //新建輸出串流
                    FileOutputStream fos = new FileOutputStream(databaseFilename);
                    //將資料輸出
                    byte[] buffer = new byte[8192];
                    int count = 0;
                    while ((count = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, count);
                    }
                    //關閉資源
                    fos.close();
                    is.close();
                } catch (Exception e) {
                    Log.e(TAG, "openDatabase() Exception : " + e);
                    System.out.println(e.getMessage());
                }
            } else {
                //Log.i(TAG, "No SDCard");
                try {
                    //取得資料庫的完整路徑
                    String databaseFilename = NO_SDCARD_DATABASE_PATH + DATABASE_FILENAME;
                    //Log.i(TAG, "databaseFilename : " + databaseFilename);

                    //Log.i(TAG,"Database file=" + databaseFilename);
                    //若不存在則取得資料庫輸入串流對象
                    InputStream is = mCtx.getResources().openRawResource(R.raw.personal);
                    //新建輸出串流
                    FileOutputStream fos = new FileOutputStream(databaseFilename);
                    //將資料輸出
                    byte[] buffer = new byte[8192];
                    int count = 0;
                    while ((count = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, count);
                    }
                    //關閉資源
                    fos.close();
                    is.close();
                } catch (Exception e) {
                    Log.e(TAG, "update() Exception : " + e);
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
