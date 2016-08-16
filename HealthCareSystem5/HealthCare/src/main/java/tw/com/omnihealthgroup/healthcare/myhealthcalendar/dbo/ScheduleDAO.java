package tw.com.omnihealthgroup.healthcare.myhealthcalendar.dbo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;

import java.util.LinkedList;
import java.util.List;

import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Clinic;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Consultation;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Examination;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Meeting;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Operation;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Personal;

public class ScheduleDAO {

    /**
     * 表格名稱
     */
    public final static String TABLE_ALL = "AllTable";  // 總覽
    public final static String TABLE_CONSULTATION = "ConsultationTable";  // 會診 - 重要活動
    public final static String TABLE_OPERATE = "OperateTable";  // 開刀 - 量測提醒
    public final static String TABLE_CLINIC = "ClinicTable";  // 門診 - 一般記事
    public final static String TABLE_MEETING = "MeetingTable";  // 會議 - 用藥提醒
    public final static String TABLE_PERSONAL = "PersonalTable";  // 私人行程 - 預約看診
    public final static String TABLE_EXAMINATION = "ExaminationTable"; // 健康檢查
    public final static String TABLE_LAST_MSG_PK = "LastSaveTimeTable";  // 最後更新時間

    /**
     * 編號表格欄位名稱，固定不變
     */
    public static final String KEY_ID = "_id";

    /**
     * 表格共通欄位名稱
     */
    public static final String SUB_KIND_COLUMN = "subkind"; //會診：會診類別 / 開刀：來源+科別 / 門診： / 會議： / 私人行程：類別
    public static final String TITLE_COLUMN = "title";   //會診：開立科別＋床號＋姓名 / 開刀：科別+姓名 / 門診： / 會議： / 私人行程：
    public static final String DATE_COLUMN = "date";    //會診：開立時間 / 開刀：開刀日期 / 門診： / 會議： / 私人行程：起始時間
    public static final String NAME_COLUMN = "name";   //會診：姓名 / 開刀：姓名 / 門診： / 會議： / 私人行程：事件名稱
    public static final String NOTE_COLUMN = "note";   //備註
    public static final String JSON_OBJ_COLUMN = "jsonObj";   //JSONObject
    public static final String MSG_PK_COLUMN = "msgPk";   //MsgPk
    public static final String EVENT_TYPE_PK_COLUMN = "eventTypePk";   //EventTypePk
    public static final String LAST_MSG_PK_COLUMN = "lastMsgPk";   //最後更新時間

    /**
     * 會診表格欄位名稱(重要活動)
     */
    public static final String ADMIN_COLUMN = "admin"; //開立者
    public static final String SEX_COLUMN = "sex";    //性別
    public static final String BED_NUMBER_COLUMN = "bedNo";   //床號
    public static final String DOCTOR_COLUMN = "doctor"; //開立醫師
    public static final String ADMIN_DIVISIOΝ_COLUMN = "adminDivision"; //開立科別
    public static final String CONSULT_DIVISIOΝ_COLUMN = "consultDivision";   //會診科別
    public static final String REPORT_REQUESTED_REGARDING_COLUMN = "rrr";   //REPORT REQUESTED REGARDING
    public static final String BIREF_SUMMARY_COLUMN = "birefSummary";   //BIREF SUMMARY

    /**
     * 開刀表格欄位名稱(量測提醒)
     */
    public static final String FROM_COLUMN = "comeFrom"; //來源
    public static final String OPERATE_TIME_COLUMN = "operateTime"; //預計開刀時間
    public static final String MEDICAL_NUMBER_COLUMN = "medicalNumber"; //病歷號
    public static final String DIVISION_COLUMN = "division"; //科別
    public static final String DIAGNOSIS_COLUMN = "diagnosis"; //主診斷
    public static final String OPERATION_COLUMN = "operation"; //手術名稱

    /**
     * 門診表格欄位名稱(一般記事)
     */

    /**
     * 會議表格欄位名稱(用藥提醒)
     */

    /**
     * 私人行程表格欄位名稱(預約看診)
     */
    public static final String LOCATION_COLUMN = "location"; //行程地點
    public static final String START_DATE_COLUMN = "startdate"; //起始日期
    public static final String END_DATE_COLUMN = "enddate"; //結束日期
    public static final String START_TIME_COLUMN = "starttime"; //起始時間
    public static final String END_TIME_COLUMN = "endtime"; //結束時間
    public static final String ONE_DAY_COLUMN = "oneday"; //是否跨日

    /**
     * 健康檢查 表格欄位名
     */


    /**
     * 建立儲存會診資料的表格(重要活動)
     */
    public static final String CREATE_TABLE_CONSULTATION =
            "CREATE TABLE IF NOT EXISTS " + TABLE_CONSULTATION + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SUB_KIND_COLUMN + " TEXT, " +
                    DATE_COLUMN + " INTEGER, " +
                    TITLE_COLUMN + " TEXT, " +
                    ADMIN_COLUMN + " TEXT, " +
                    NAME_COLUMN + " TEXT, " +
                    SEX_COLUMN + " TEXT, " +
                    BED_NUMBER_COLUMN + " TEXT, " +
                    DOCTOR_COLUMN + " TEXT, " +
                    ADMIN_DIVISIOΝ_COLUMN + " TEXT, " +
                    CONSULT_DIVISIOΝ_COLUMN + " TEXT, " +
                    REPORT_REQUESTED_REGARDING_COLUMN + " TEXT, " +
                    BIREF_SUMMARY_COLUMN + " TEXT, " +
                    NOTE_COLUMN + " TEXT, " +
                    JSON_OBJ_COLUMN + " TEXT, " +
                    MSG_PK_COLUMN + " TEXT, " +
                    EVENT_TYPE_PK_COLUMN + " TEXT, " +

                    //                    SUB_KIND_COLUMN + " TEXT, " +
                    //                    DATE_COLUMN + " INTEGER, " +
                    //                    NAME_COLUMN + " TEXT, " +
                    LOCATION_COLUMN + " TEXT, " +
                    START_DATE_COLUMN + " TEXT, " +
                    END_DATE_COLUMN + " TEXT, " +
                    START_TIME_COLUMN + " TEXT, " +
                    END_TIME_COLUMN + " TEXT, " +
                    //                    NOTE_COLUMN + " TEXT, " +
                    ONE_DAY_COLUMN + " INTEGER)";

    /**
     * 建立儲存開刀資料的表格(量測提醒)
     */
    public static final String CREATE_TABLE_OPERATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_OPERATE + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SUB_KIND_COLUMN + " TEXT, " +
                    DATE_COLUMN + " INTEGER, " +
                    TITLE_COLUMN + " TEXT, " +
                    FROM_COLUMN + " TEXT, " +
                    OPERATE_TIME_COLUMN + " TEXT, " +
                    MEDICAL_NUMBER_COLUMN + " TEXT, " +
                    NAME_COLUMN + " TEXT, " +
                    DIVISION_COLUMN + " TEXT, " +
                    DIAGNOSIS_COLUMN + " TEXT, " +
                    OPERATION_COLUMN + " TEXT, " +
                    NOTE_COLUMN + " TEXT, " +
                    JSON_OBJ_COLUMN + " TEXT, " +
                    MSG_PK_COLUMN + " TEXT, " +
                    EVENT_TYPE_PK_COLUMN + " TEXT, " +

                    //                    SUB_KIND_COLUMN + " TEXT, " +
                    //                    DATE_COLUMN + " INTEGER, " +
                    //                    NAME_COLUMN + " TEXT, " +
                    LOCATION_COLUMN + " TEXT, " +
                    START_DATE_COLUMN + " TEXT, " +
                    END_DATE_COLUMN + " TEXT, " +
                    START_TIME_COLUMN + " TEXT, " +
                    END_TIME_COLUMN + " TEXT, " +
                    //                    NOTE_COLUMN + " TEXT, " +
                    ONE_DAY_COLUMN + " INTEGER)";

    /**
     * 建立儲存門診資料的表格(一般記事)
     */
    public static final String CREATE_TABLE_CLINIC =
            "CREATE TABLE IF NOT EXISTS " + TABLE_CLINIC + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SUB_KIND_COLUMN + " TEXT, " +
                    DATE_COLUMN + " INTEGER, " +
                    TITLE_COLUMN + " TEXT, " +
                    JSON_OBJ_COLUMN + " TEXT, " +
                    MSG_PK_COLUMN + " TEXT, " +
                    EVENT_TYPE_PK_COLUMN + " TEXT, " +

                    //                    SUB_KIND_COLUMN + " TEXT, " +
                    //                    DATE_COLUMN + " INTEGER, " +
                    NAME_COLUMN + " TEXT, " +
                    LOCATION_COLUMN + " TEXT, " +
                    START_DATE_COLUMN + " TEXT, " +
                    END_DATE_COLUMN + " TEXT, " +
                    START_TIME_COLUMN + " TEXT, " +
                    END_TIME_COLUMN + " TEXT, " +
                    NOTE_COLUMN + " TEXT, " +
                    ONE_DAY_COLUMN + " INTEGER)";

    /**
     * 建立儲存會議資料的表格(用藥提醒)
     */
    public static final String CREATE_TABLE_MEETING =
            "CREATE TABLE IF NOT EXISTS " + TABLE_MEETING + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SUB_KIND_COLUMN + " TEXT, " +
                    DATE_COLUMN + " INTEGER, " +
                    TITLE_COLUMN + " TEXT, " +
                    JSON_OBJ_COLUMN + " TEXT, " +
                    MSG_PK_COLUMN + " TEXT, " +
                    EVENT_TYPE_PK_COLUMN + " TEXT, " +

                    //                    SUB_KIND_COLUMN + " TEXT, " +
                    //                    DATE_COLUMN + " INTEGER, " +
                    NAME_COLUMN + " TEXT, " +
                    LOCATION_COLUMN + " TEXT, " +
                    START_DATE_COLUMN + " TEXT, " +
                    END_DATE_COLUMN + " TEXT, " +
                    START_TIME_COLUMN + " TEXT, " +
                    END_TIME_COLUMN + " TEXT, " +
                    NOTE_COLUMN + " TEXT, " +
                    ONE_DAY_COLUMN + " INTEGER)";

    /**
     * 建立儲存私人行程資料的表格(預約看診)
     */
    public static final String CREATE_TABLE_PERSONAL =
            "CREATE TABLE IF NOT EXISTS " + TABLE_PERSONAL + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SUB_KIND_COLUMN + " TEXT, " +
                    DATE_COLUMN + " INTEGER, " +
                    NAME_COLUMN + " TEXT, " +
                    LOCATION_COLUMN + " TEXT, " +
                    START_DATE_COLUMN + " TEXT, " +
                    END_DATE_COLUMN + " TEXT, " +
                    START_TIME_COLUMN + " TEXT, " +
                    END_TIME_COLUMN + " TEXT, " +
                    NOTE_COLUMN + " TEXT, " +
                    ONE_DAY_COLUMN + " INTEGER, " +

                    JSON_OBJ_COLUMN + " TEXT, " +
                    MSG_PK_COLUMN + " TEXT, " +
                    EVENT_TYPE_PK_COLUMN + " TEXT)";


    /**
     * 建立儲存健康檢查的表格
     */
    public static final String CREATE_TABLE_EXAMINATION =
            "CREATE TABLE IF NOT EXISTS " + TABLE_EXAMINATION + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SUB_KIND_COLUMN + " TEXT, " +
                    DATE_COLUMN + " INTEGER, " +
                    NAME_COLUMN + " TEXT, " +
                    LOCATION_COLUMN + " TEXT, " +
                    START_DATE_COLUMN + " TEXT, " +
                    END_DATE_COLUMN + " TEXT, " +
                    START_TIME_COLUMN + " TEXT, " +
                    END_TIME_COLUMN + " TEXT, " +
                    NOTE_COLUMN + " TEXT, " +
                    ONE_DAY_COLUMN + " INTEGER, " +

                    JSON_OBJ_COLUMN + " TEXT, " +
                    MSG_PK_COLUMN + " TEXT, " +
                    EVENT_TYPE_PK_COLUMN + " TEXT)";

    /**
     * 建立儲存最後更新時間的表格
     */
    public static final String CREATE_TABLE_LAST_MSG_PK =
            "CREATE TABLE IF NOT EXISTS " + TABLE_LAST_MSG_PK + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    LAST_MSG_PK_COLUMN + " TEXT)";

    public static final String[] TABLE_ARRAY = new String[]{
            TABLE_ALL, TABLE_CLINIC, TABLE_CONSULTATION,
            TABLE_OPERATE, TABLE_MEETING, TABLE_PERSONAL,
            TABLE_EXAMINATION
    };

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
    public ScheduleDAO(Context context) {
        db = ScheduleDBHelper.getDatabase(context);
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
     * 關閉資料庫
     */
    public void closeDB() {
        db.close();
    }

    /**
     * 新增參數指定的物件
     *
     * @param tableName   要新增資料的表格名稱
     * @param scheduleObj 傳入的資料物件
     */
    public void insert(String tableName, Object scheduleObj) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        switch (tableName) {
            case TABLE_CONSULTATION:
                Consultation consultation = (Consultation) scheduleObj; // 會診 - 重要活動
                cv.put(SUB_KIND_COLUMN, consultation.getConsultationSubkind());
                cv.put(DATE_COLUMN, consultation.getConsultationDate());
                cv.put(
                        TITLE_COLUMN,
                        consultation.getConsultationAdminDivision() +
                                consultation.getConsultationBedNo() +
                                consultation.getConsultationName()
                );
                cv.put(ADMIN_COLUMN, consultation.getConsultationAdmin());
                cv.put(NAME_COLUMN, consultation.getConsultationName());
                cv.put(SEX_COLUMN, consultation.getConsultationSex());
                cv.put(BED_NUMBER_COLUMN, consultation.getConsultationBedNo());
                cv.put(DOCTOR_COLUMN, consultation.getConsultationDoctor());
                cv.put(ADMIN_DIVISIOΝ_COLUMN, consultation.getConsultationAdminDivision());
                cv.put(CONSULT_DIVISIOΝ_COLUMN, consultation.getConsultationDivision());
                cv.put(REPORT_REQUESTED_REGARDING_COLUMN, consultation.getConsultationRRR());
                cv.put(BIREF_SUMMARY_COLUMN, consultation.getConsultationBirefSummary());
                cv.put(NOTE_COLUMN, consultation.getConsultationNote());
                cv.put(JSON_OBJ_COLUMN, consultation.getConsultationJsonObj());
                cv.put(MSG_PK_COLUMN, consultation.getConsultationMsgPk());
                cv.put(EVENT_TYPE_PK_COLUMN, consultation.getConsultationEventTypePk());

                //                cv.put(SUB_KIND_COLUMN, consultation.getConsultationSubkind());
                //                cv.put(DATE_COLUMN, consultation.getConsultationDate());
                //                cv.put(NAME_COLUMN, consultation.getConsultationName());
                cv.put(LOCATION_COLUMN, consultation.getConsultationLocation());
                cv.put(START_DATE_COLUMN, consultation.getConsultationStartDate());
                cv.put(END_DATE_COLUMN, consultation.getConsultationEndDate());
                cv.put(START_TIME_COLUMN, consultation.getConsultationStartTime());
                cv.put(END_TIME_COLUMN, consultation.getConsultationEndTime());
                //                cv.put(NOTE_COLUMN, consultation.getConsultationNote());
                cv.put(ONE_DAY_COLUMN, consultation.getConsultationOneDay());
                break;

            case TABLE_OPERATE:
                Operation operation = (Operation) scheduleObj; // 開刀 - 量測提醒
                cv.put(SUB_KIND_COLUMN, operation.getOperateSubkind());
                cv.put(DATE_COLUMN, operation.getOperateDate());
                cv.put(TITLE_COLUMN, operation.getOperateTitle());
                cv.put(FROM_COLUMN, operation.getOperateFrom());
                cv.put(OPERATE_TIME_COLUMN, operation.getOperateTime());
                cv.put(MEDICAL_NUMBER_COLUMN, operation.getOperateMedicalNumber());
                cv.put(NAME_COLUMN, operation.getOperateName());
                cv.put(DIVISION_COLUMN, operation.getOperateDivision());
                cv.put(DIAGNOSIS_COLUMN, operation.getOperateDiagnosis());
                cv.put(OPERATION_COLUMN, operation.getOperation());
                cv.put(NOTE_COLUMN, operation.getOperateNote());
                cv.put(JSON_OBJ_COLUMN, operation.getOperateJsonObj());
                cv.put(MSG_PK_COLUMN, operation.getOperateMsgPk());
                cv.put(EVENT_TYPE_PK_COLUMN, operation.getOperateEventTypePk());

                //                cv.put(SUB_KIND_COLUMN, operation.getOperateSubkind());
                //                cv.put(DATE_COLUMN, operation.getOperateDate());
                //                cv.put(NAME_COLUMN, operation.getOperateName());
                cv.put(LOCATION_COLUMN, operation.getOperateLocation());
                cv.put(START_DATE_COLUMN, operation.getOperateStartDate());
                cv.put(END_DATE_COLUMN, operation.getOperateEndDate());
                cv.put(START_TIME_COLUMN, operation.getOperateStartTime());
                cv.put(END_TIME_COLUMN, operation.getOperateEndTime());
                //                cv.put(NOTE_COLUMN, operation.getOperateNote());
                cv.put(ONE_DAY_COLUMN, operation.getOperateOneDay());
                break;

            case TABLE_CLINIC:
                Clinic clinic = (Clinic) scheduleObj; // 門診 - 一般記事
                cv.put(SUB_KIND_COLUMN, clinic.getClinicSubkind());
                cv.put(DATE_COLUMN, clinic.getClinicDate());
                cv.put(TITLE_COLUMN, clinic.getClinicTitle());
                cv.put(JSON_OBJ_COLUMN, clinic.getClinicJsonObj());
                cv.put(MSG_PK_COLUMN, clinic.getClinicMsgPk());
                cv.put(EVENT_TYPE_PK_COLUMN, clinic.getClinicEventTypePk());

                //                cv.put(SUB_KIND_COLUMN, clinic.getClinicSubkind());
                //                cv.put(DATE_COLUMN, clinic.getClinicDate());
                cv.put(NAME_COLUMN, clinic.getClinicName());
                cv.put(LOCATION_COLUMN, clinic.getClinicLocation());
                cv.put(START_DATE_COLUMN, clinic.getClinicStartDate());
                cv.put(END_DATE_COLUMN, clinic.getClinicEndDate());
                cv.put(START_TIME_COLUMN, clinic.getClinicStartTime());
                cv.put(END_TIME_COLUMN, clinic.getClinicEndTime());
                cv.put(NOTE_COLUMN, clinic.getClinicNote());
                cv.put(ONE_DAY_COLUMN, clinic.getClinicOneDay());
                break;

            case TABLE_MEETING: // 會議 - 用藥提醒
                Meeting meeting = (Meeting) scheduleObj;
                cv.put(SUB_KIND_COLUMN, meeting.getMeetingSubkind());
                cv.put(DATE_COLUMN, meeting.getMeetingDate());
                cv.put(TITLE_COLUMN, meeting.getMeetingTitle());
                cv.put(JSON_OBJ_COLUMN, meeting.getMeetingJsonObj());
                cv.put(MSG_PK_COLUMN, meeting.getMeetingMsgPk());
                cv.put(EVENT_TYPE_PK_COLUMN, meeting.getMeetingEventTypePk());

                //                cv.put(SUB_KIND_COLUMN, meeting.getMeetingSubkind());
                //                cv.put(DATE_COLUMN, meeting.getMeetingDate());
                cv.put(NAME_COLUMN, meeting.getMeetingName());
                cv.put(LOCATION_COLUMN, meeting.getMeetingLocation());
                cv.put(START_DATE_COLUMN, meeting.getMeetingStartDate());
                cv.put(END_DATE_COLUMN, meeting.getMeetingEndDate());
                cv.put(START_TIME_COLUMN, meeting.getMeetingStartTime());
                cv.put(END_TIME_COLUMN, meeting.getMeetingEndTime());
                cv.put(NOTE_COLUMN, meeting.getMeetingNote());
                cv.put(ONE_DAY_COLUMN, meeting.getMeetingOneDay());
                break;

            case TABLE_PERSONAL: // 私人行程 - 預約看診
                Personal personal = (Personal) scheduleObj;
                cv.put(SUB_KIND_COLUMN, personal.getPersonalSubkind());
                cv.put(DATE_COLUMN, personal.getPersonalDate());
                cv.put(NAME_COLUMN, personal.getPersonalName());
                cv.put(LOCATION_COLUMN, personal.getPersonalLocation());
                cv.put(START_DATE_COLUMN, personal.getPersonalStartDate());
                cv.put(END_DATE_COLUMN, personal.getPersonalEndDate());
                cv.put(START_TIME_COLUMN, personal.getPersonalStartTime());
                cv.put(END_TIME_COLUMN, personal.getPersonalEndTime());
                cv.put(NOTE_COLUMN, personal.getPersonalNote());
                cv.put(ONE_DAY_COLUMN, personal.getPersonalOneDay());

                cv.put(JSON_OBJ_COLUMN, personal.getPersonalJsonObj());
                cv.put(MSG_PK_COLUMN, personal.getPersonalMsgPk());
                cv.put(EVENT_TYPE_PK_COLUMN, personal.getPersonalEventTypePk());
                break;

            case TABLE_EXAMINATION: // 健康檢查
                Examination examination = (Examination) scheduleObj;
                cv.put(SUB_KIND_COLUMN, examination.getExaminationSubkind());
                cv.put(DATE_COLUMN, examination.getExaminationDate());
                cv.put(NAME_COLUMN, examination.getExaminationName());
                cv.put(LOCATION_COLUMN, examination.getExaminationLocation());
                cv.put(START_DATE_COLUMN, examination.getExaminationStartDate());
                cv.put(END_DATE_COLUMN, examination.getExaminationEndDate());
                cv.put(START_TIME_COLUMN, examination.getExaminationStartTime());
                cv.put(END_TIME_COLUMN, examination.getExaminationEndTime());
                cv.put(NOTE_COLUMN, examination.getExaminationNote());
                cv.put(ONE_DAY_COLUMN, examination.getExaminationOneDay());

                cv.put(JSON_OBJ_COLUMN, examination.getExaminationJsonObj());
                cv.put(MSG_PK_COLUMN, examination.getExaminationMsgPk());
                cv.put(EVENT_TYPE_PK_COLUMN, examination.getExaminationEventTypePk());
                break;

            case TABLE_LAST_MSG_PK:
                cv.put(LAST_MSG_PK_COLUMN, scheduleObj.toString());
                break;

        }
        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        db.insert(tableName, null, cv);
    }

    /**
     * 修改參數指定的物件
     *
     * @param tableName   要更新資料的表格名稱
     * @param scheduleObj 傳入的資料物件
     * @return boolean
     */
    public boolean update(String tableName, Object scheduleObj) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();
        String where = null;
        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        switch (tableName) {
            case TABLE_CONSULTATION:
                Consultation consultation = (Consultation) scheduleObj;
                cv.put(SUB_KIND_COLUMN, consultation.getConsultationSubkind());
                cv.put(DATE_COLUMN, consultation.getConsultationDate());
                cv.put(
                        TITLE_COLUMN,
                        consultation.getConsultationAdminDivision() +
                                consultation.getConsultationBedNo() +
                                consultation.getConsultationName()
                );
                cv.put(ADMIN_COLUMN, consultation.getConsultationAdmin());
                cv.put(NAME_COLUMN, consultation.getConsultationName());
                cv.put(SEX_COLUMN, consultation.getConsultationSex());
                cv.put(BED_NUMBER_COLUMN, consultation.getConsultationBedNo());
                cv.put(DOCTOR_COLUMN, consultation.getConsultationDoctor());
                cv.put(ADMIN_DIVISIOΝ_COLUMN, consultation.getConsultationAdminDivision());
                cv.put(CONSULT_DIVISIOΝ_COLUMN, consultation.getConsultationDivision());
                cv.put(REPORT_REQUESTED_REGARDING_COLUMN, consultation.getConsultationRRR());
                cv.put(BIREF_SUMMARY_COLUMN, consultation.getConsultationBirefSummary());
                cv.put(NOTE_COLUMN, consultation.getConsultationNote());
                cv.put(JSON_OBJ_COLUMN, consultation.getConsultationJsonObj());
                cv.put(MSG_PK_COLUMN, consultation.getConsultationMsgPk());
                cv.put(EVENT_TYPE_PK_COLUMN, consultation.getConsultationEventTypePk());

                // 設定條件為Title，格式為「欄位名稱=開立科別＋床號＋姓名」
                where = MSG_PK_COLUMN + "=" + consultation.getConsultationMsgPk();


                //                cv.put(SUB_KIND_COLUMN, consultation.getConsultationSubkind());
                //                cv.put(DATE_COLUMN, consultation.getConsultationDate());
                //                cv.put(NAME_COLUMN, consultation.getConsultationName());
                cv.put(LOCATION_COLUMN, consultation.getConsultationLocation());
                cv.put(START_DATE_COLUMN, consultation.getConsultationStartDate());
                cv.put(END_DATE_COLUMN, consultation.getConsultationEndDate());
                cv.put(START_TIME_COLUMN, consultation.getConsultationStartTime());
                cv.put(END_TIME_COLUMN, consultation.getConsultationEndTime());
                //                cv.put(NOTE_COLUMN, consultation.getConsultationNote());
                cv.put(ONE_DAY_COLUMN, consultation.getConsultationOneDay());
                break;

            case TABLE_OPERATE:
                Operation operation = (Operation) scheduleObj;
                cv.put(SUB_KIND_COLUMN, operation.getOperateSubkind());
                cv.put(DATE_COLUMN, operation.getOperateDate());
                cv.put(TITLE_COLUMN, operation.getOperateTitle());
                cv.put(FROM_COLUMN, operation.getOperateFrom());
                cv.put(OPERATE_TIME_COLUMN, operation.getOperateTime());
                cv.put(MEDICAL_NUMBER_COLUMN, operation.getOperateMedicalNumber());
                cv.put(NAME_COLUMN, operation.getOperateName());
                cv.put(DIVISION_COLUMN, operation.getOperateDivision());
                cv.put(DIAGNOSIS_COLUMN, operation.getOperateDiagnosis());
                cv.put(OPERATION_COLUMN, operation.getOperation());
                cv.put(NOTE_COLUMN, operation.getOperateNote());
                cv.put(JSON_OBJ_COLUMN, operation.getOperateJsonObj());
                cv.put(MSG_PK_COLUMN, operation.getOperateMsgPk());
                cv.put(EVENT_TYPE_PK_COLUMN, operation.getOperateEventTypePk());

                //                cv.put(SUB_KIND_COLUMN, operation.getOperateSubkind());
                //                cv.put(DATE_COLUMN, operation.getOperateDate());
                //                cv.put(NAME_COLUMN, operation.getOperateName());
                cv.put(LOCATION_COLUMN, operation.getOperateLocation());
                cv.put(START_DATE_COLUMN, operation.getOperateStartDate());
                cv.put(END_DATE_COLUMN, operation.getOperateEndDate());
                cv.put(START_TIME_COLUMN, operation.getOperateStartTime());
                cv.put(END_TIME_COLUMN, operation.getOperateEndTime());
                //                cv.put(NOTE_COLUMN, operation.getOperateNote());
                cv.put(ONE_DAY_COLUMN, operation.getOperateOneDay());
                break;

            case TABLE_CLINIC:
                Clinic clinic = (Clinic) scheduleObj;
                where = MSG_PK_COLUMN + "=" + clinic.getClinicMsgPk();

                cv.put(SUB_KIND_COLUMN, clinic.getClinicSubkind());
                cv.put(DATE_COLUMN, clinic.getClinicDate());
                cv.put(TITLE_COLUMN, clinic.getClinicTitle());
                cv.put(JSON_OBJ_COLUMN, clinic.getClinicJsonObj());
                cv.put(MSG_PK_COLUMN, clinic.getClinicMsgPk());
                cv.put(EVENT_TYPE_PK_COLUMN, clinic.getClinicEventTypePk());

                //                cv.put(SUB_KIND_COLUMN, clinic.getClinicSubkind());
                //                cv.put(DATE_COLUMN, clinic.getClinicDate());
                cv.put(NAME_COLUMN, clinic.getClinicName());
                cv.put(LOCATION_COLUMN, clinic.getClinicLocation());
                cv.put(START_DATE_COLUMN, clinic.getClinicStartDate());
                cv.put(END_DATE_COLUMN, clinic.getClinicEndDate());
                cv.put(START_TIME_COLUMN, clinic.getClinicStartTime());
                cv.put(END_TIME_COLUMN, clinic.getClinicEndTime());
                cv.put(NOTE_COLUMN, clinic.getClinicNote());
                cv.put(ONE_DAY_COLUMN, clinic.getClinicOneDay());
                break;

            case TABLE_MEETING:
                Meeting meeting = (Meeting) scheduleObj;
                cv.put(SUB_KIND_COLUMN, meeting.getMeetingSubkind());
                cv.put(DATE_COLUMN, meeting.getMeetingDate());
                cv.put(TITLE_COLUMN, meeting.getMeetingTitle());
                cv.put(JSON_OBJ_COLUMN, meeting.getMeetingJsonObj());
                cv.put(MSG_PK_COLUMN, meeting.getMeetingMsgPk());
                cv.put(EVENT_TYPE_PK_COLUMN, meeting.getMeetingEventTypePk());

                //                cv.put(SUB_KIND_COLUMN, meeting.getMeetingSubkind());
                //                cv.put(DATE_COLUMN, meeting.getMeetingDate());
                cv.put(NAME_COLUMN, meeting.getMeetingName());
                cv.put(LOCATION_COLUMN, meeting.getMeetingLocation());
                cv.put(START_DATE_COLUMN, meeting.getMeetingStartDate());
                cv.put(END_DATE_COLUMN, meeting.getMeetingEndDate());
                cv.put(START_TIME_COLUMN, meeting.getMeetingStartTime());
                cv.put(END_TIME_COLUMN, meeting.getMeetingEndTime());
                cv.put(NOTE_COLUMN, meeting.getMeetingNote());
                cv.put(ONE_DAY_COLUMN, meeting.getMeetingOneDay());
                break;

            case TABLE_PERSONAL:
                Personal personal = (Personal) scheduleObj;
                cv.put(SUB_KIND_COLUMN, personal.getPersonalSubkind());
                cv.put(DATE_COLUMN, personal.getPersonalDate());
                cv.put(NAME_COLUMN, personal.getPersonalName());
                cv.put(LOCATION_COLUMN, personal.getPersonalLocation());
                cv.put(START_DATE_COLUMN, personal.getPersonalStartDate());
                cv.put(END_DATE_COLUMN, personal.getPersonalEndDate());
                cv.put(START_TIME_COLUMN, personal.getPersonalStartTime());
                cv.put(END_TIME_COLUMN, personal.getPersonalEndTime());
                cv.put(NOTE_COLUMN, personal.getPersonalNote());
                cv.put(ONE_DAY_COLUMN, personal.getPersonalOneDay());

                cv.put(JSON_OBJ_COLUMN, personal.getPersonalJsonObj());
                cv.put(MSG_PK_COLUMN, personal.getPersonalMsgPk());
                cv.put(EVENT_TYPE_PK_COLUMN, personal.getPersonalEventTypePk());
                break;

            case TABLE_EXAMINATION:
                Examination examination = (Examination) scheduleObj;
                cv.put(SUB_KIND_COLUMN, examination.getExaminationSubkind());
                cv.put(DATE_COLUMN, examination.getExaminationDate());
                cv.put(NAME_COLUMN, examination.getExaminationName());
                cv.put(LOCATION_COLUMN, examination.getExaminationLocation());
                cv.put(START_DATE_COLUMN, examination.getExaminationStartDate());
                cv.put(END_DATE_COLUMN, examination.getExaminationEndDate());
                cv.put(START_TIME_COLUMN, examination.getExaminationStartTime());
                cv.put(END_TIME_COLUMN, examination.getExaminationEndTime());
                cv.put(NOTE_COLUMN, examination.getExaminationNote());
                cv.put(ONE_DAY_COLUMN, examination.getExaminationOneDay());

                cv.put(JSON_OBJ_COLUMN, examination.getExaminationJsonObj());
                cv.put(MSG_PK_COLUMN, examination.getExaminationMsgPk());
                cv.put(EVENT_TYPE_PK_COLUMN, examination.getExaminationEventTypePk());
                break;
        }
        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(tableName, cv, where, null) > 0;
    }


    public boolean update_msgPK(String tableName, Object scheduleObj, String msg_PK) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();
        String where = null;
        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        switch (tableName) {
            case TABLE_CLINIC:
                Clinic clinic = (Clinic) scheduleObj;
                where = MSG_PK_COLUMN + "=" + msg_PK;
                cv.put(MSG_PK_COLUMN, clinic.getClinicMsgPk());
                break;

            case TABLE_CONSULTATION:
                Consultation consultation = (Consultation) scheduleObj;
                where = MSG_PK_COLUMN + "=" + msg_PK;
                cv.put(MSG_PK_COLUMN, consultation.getConsultationMsgPk());
                break;

            case TABLE_OPERATE:
                Operation operation = (Operation) scheduleObj;
                where = MSG_PK_COLUMN + "=" + msg_PK;
                cv.put(MSG_PK_COLUMN, operation.getOperateMsgPk());
                break;

            case TABLE_MEETING:
                Meeting meeting = (Meeting) scheduleObj;
                where = MSG_PK_COLUMN + "=" + msg_PK;
                cv.put(MSG_PK_COLUMN, meeting.getMeetingMsgPk());
                break;

            case TABLE_PERSONAL:
                Personal personal = (Personal) scheduleObj;
                where = MSG_PK_COLUMN + "=" + msg_PK;
                cv.put(MSG_PK_COLUMN, personal.getPersonalMsgPk());
                break;

            case TABLE_EXAMINATION:
                Examination examination = (Examination) scheduleObj;
                where = MSG_PK_COLUMN + "=" + msg_PK;
                cv.put(MSG_PK_COLUMN, examination.getExaminationMsgPk());
                break;
        }
        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(tableName, cv, where, null) > 0;
    }

    /**
     * 刪除參數指定員編的資料
     *
     * @param tableName   要刪除資料的表格名稱
     * @param scheduleObj 傳入的資料物件
     * @return boolean
     */
    public boolean delete(String tableName, Object scheduleObj) throws JSONException {
        // 設定條件為Title，格式為「欄位名稱=開立科別＋床號＋姓名」
        String where = null;
        switch (tableName) {
            case TABLE_CONSULTATION:
                Consultation consultation = (Consultation) scheduleObj;
                where = MSG_PK_COLUMN + "=" + consultation.getConsultationMsgPk();
                break;
            case TABLE_OPERATE:
                break;
            case TABLE_CLINIC:
                break;
            case TABLE_MEETING:
                break;
            case TABLE_PERSONAL:
                break;
            case TABLE_EXAMINATION:
                break;
        }
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(tableName, where, null) > 0;
    }


    /**
     * 刪除參數指定員編的資料
     *
     * @param tableName   要刪除資料的表格名稱
     * @param scheduleObj 傳入的資料物件
     * @return boolean
     */
    public boolean delete_MsgPk(String tableName, Object scheduleObj) throws JSONException {
        // 設定條件為Title，格式為「欄位名稱=開立科別＋床號＋姓名」
        String where = null;
        switch (tableName) {
            case TABLE_CLINIC:
                Clinic clinic = (Clinic) scheduleObj;
                where = MSG_PK_COLUMN + "=" + clinic.getClinicMsgPk();
                break;
            case TABLE_CONSULTATION:
                Consultation consultation = (Consultation) scheduleObj;
                where = MSG_PK_COLUMN + "=" + consultation.getConsultationMsgPk();
                break;
            case TABLE_OPERATE:
                Operation operation = (Operation) scheduleObj;
                where = MSG_PK_COLUMN + "=" + operation.getOperateMsgPk();
                break;
            case TABLE_MEETING:
                Meeting meeting = (Meeting) scheduleObj;
                where = MSG_PK_COLUMN + "=" + meeting.getMeetingMsgPk();
                break;
            case TABLE_PERSONAL:
                Personal personal = (Personal) scheduleObj;
                where = MSG_PK_COLUMN + "=" + personal.getPersonalMsgPk();
                break;
            case TABLE_EXAMINATION:
                Examination examination = (Examination) scheduleObj;
                where = MSG_PK_COLUMN + "=" + examination.getExaminationMsgPk();
                break;
        }
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(tableName, where, null) > 0;
    }

    public boolean delete_table(String tableName, Object scheduleObj) throws JSONException {
        // 設定條件為Title，格式為「欄位名稱=開立科別＋床號＋姓名」
        String where = null;
        //        switch (tableName) {
        //            case TABLE_CONSULTATION:
        //                Consultation consultation = (Consultation) scheduleObj;
        //                where = MSG_PK_COLUMN + "=" + consultation.getConsultationMsgPk();
        //                break;
        //            case TABLE_OPERATE:
        //                break;
        //            case TABLE_CLINIC:
        //                break;
        //            case TABLE_MEETING:
        //                break;
        //            case TABLE_PERSONAL:
        //                break;
        //            case TABLE_EXAMINATION:
        //                break;
        //        }
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(tableName, where, null) > 0;
    }

    /**
     * 刪除參數所有的資料
     *
     * @param tableName 要刪除資料的表格名稱
     */
    public void deleteAll(String tableName, LinkedList<Object> objectList) {
        System.out.println("EmployeeDAO：" + objectList.size());
        // 設定條件為Title，格式為「欄位名稱=開立科別＋床號＋姓名」
        String where = MSG_PK_COLUMN + "= ?";
        for (int i = 0; i < objectList.size(); i++) {
            // 刪除指定編號資料
            //            db.delete(tableName, where, new String[]{objectList.get(i).});
        }
    }

    /**
     * 刪除重複MsgPk的資料
     *
     * @param tableName 要刪除資料的表格名稱
     */
    public boolean checkDuplicateMsgPk(String tableName, String msgPk) {
        boolean isNewData = true;
        String where = MSG_PK_COLUMN + "= " + msgPk;
        db.beginTransaction();
        Cursor cursor = db.query(
                tableName, null, where, null, null, null, null, null);
        while (cursor.moveToNext()) {
            isNewData = false;
        }
        //關閉Cursor物件
        cursor.close();
        db.endTransaction();
        return isNewData;
    }

    /**
     * 讀取所有記事資料
     *
     * @param tableName 要讀取資料的表格名稱
     * @return List<Employee> result
     */
    public List<Object> getAll(String tableName) {
        List<Object> resultList = new LinkedList<>();
        db.beginTransaction();
        Cursor cursor = db.query(
                tableName, null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            resultList.add(getRecord(tableName, cursor));
        }
        //關閉Cursor物件
        cursor.close();
        db.endTransaction();
        return resultList;
    }

    /**
     * 取得指定員編的資料物件
     *
     * @param tableName   要讀取資料的表格名稱
     * @param scheduleObj 指定的資料物件
     * @return Object
     */
    public Object get(String tableName, Object scheduleObj) {
        String where = null;
        Object resultObj = scheduleObj;
        // 準備回傳結果用的物件
        switch (tableName) {
            case TABLE_CONSULTATION:
                Consultation consultationObj = (Consultation) resultObj;
                // 使用MsgPk為查詢條件
                where = MSG_PK_COLUMN + "=" + consultationObj.getConsultationMsgPk();
                break;
            case TABLE_OPERATE:
                Operation operationObj = (Operation) resultObj;
                // 使用MsgPk為查詢條件
                where = MSG_PK_COLUMN + "=" + operationObj.getOperateMsgPk();
                break;
            case TABLE_CLINIC:
                Clinic clinicObj = (Clinic) resultObj;
                // 使用MsgPk為查詢條件
                where = MSG_PK_COLUMN + "=" + clinicObj.getClinicMsgPk();
                break;
            case TABLE_MEETING:
                Meeting meetingObj = (Meeting) resultObj;
                // 使用MsgPk為查詢條件
                where = MSG_PK_COLUMN + "=" + meetingObj.getMeetingMsgPk();
                break;
            case TABLE_PERSONAL:
                Personal personalObj = (Personal) resultObj;
                // 使用MsgPk為查詢條件
                where = MSG_PK_COLUMN + "=" + personalObj.getPersonalMsgPk();
                break;
            case TABLE_EXAMINATION:
                Examination examinationObj = (Examination) resultObj;
                // 使用MsgPk為查詢條件
                where = MSG_PK_COLUMN + "=" + examinationObj.getExaminationMsgPk();
                break;
        }
        // 執行查詢
        db.beginTransaction();
        Cursor result = db.query(
                tableName, null, where, null, null, null, null, null);

        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            resultObj = getRecord(tableName, result);
        }
        // 關閉Cursor物件
        result.close();
        db.endTransaction();
        // 回傳結果
        return resultObj;
    }

    public String searchNotifyDate(String kindCode, String msgPk) {
        String resultDate = null;
        String tableName;
        switch (kindCode) {
            case "C":
                tableName = TABLE_CONSULTATION;
                break;
            case "O":
                tableName = TABLE_OPERATE;
                break;
            default:
                tableName = null;
        }
        // 使用MsgPk為查詢條件
        String where = MSG_PK_COLUMN + "=" + msgPk;
        // 執行查詢
        db.beginTransaction();
        Cursor cursorResult = db.query(
                tableName, null, where, null, null, null, null, null);

        // 如果有查詢結果
        if (cursorResult.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            resultDate = getRecordByMsgPk(cursorResult);
        }
        // 關閉Cursor物件
        cursorResult.close();
        db.endTransaction();
        return resultDate;
    }

    /**
     * 確認當日是否有事件
     *
     * @param tableNumber 要讀取資料的類型代號
     * @param startTime   開始搜尋的日期
     * @return boolean
     */
    public boolean hasDateEvent(int tableNumber, String startTime) {

        Cursor result;
        if (tableNumber > 0) {
            db.beginTransaction();
            result = db.query(TABLE_ARRAY[tableNumber], null, DATE_COLUMN + "=?", new String[]{startTime}, null, null, null);
            if (result.moveToNext()) {
                result.close();
                db.endTransaction();
                return true;
            }
            result.close();
            db.endTransaction();
        }
        return false;
    }

    /**
     * 讀取所有記事資料
     *
     * @param tableNumber 要讀取資料的類型代號
     * @return List Object
     */
    public List<Object> getDayEvent(int tableNumber, String todayTime) {
        List<Object> resultList = new LinkedList<>();
        if (tableNumber > 0) {
            db.beginTransaction();
            Cursor cursor = db.query(
                    TABLE_ARRAY[tableNumber], null, DATE_COLUMN + "=?", new String[]{todayTime}, null, null, null, null);
            while (cursor.moveToNext()) {
                resultList.add(getRecord(TABLE_ARRAY[tableNumber], cursor));
            }
            //關閉Cursor物件
            cursor.close();
            db.endTransaction();
        }
        return resultList;
    }

    /**
     * 讀取所有記事資料
     *
     * @param tableNumber 要讀取資料的類型代號
     * @return List Object
     */
    public List<Object> getDayEvent_month(int tableNumber, String todayTime) {
        List<Object> resultList = new LinkedList<>();
        if (tableNumber > 0) {
            db.beginTransaction();
            Cursor cursor = db.query(
                    //                    TABLE_ARRAY[tableNumber], null, DATE_COLUMN + " LIKE?", new String[]{todayTime + "%"}, null, null, null, null);
                    true, TABLE_ARRAY[tableNumber], null, DATE_COLUMN + " LIKE?", new String[]{todayTime + "%"}, MSG_PK_COLUMN, null, null, null);

            while (cursor.moveToNext()) {
                resultList.add(getRecord(TABLE_ARRAY[tableNumber], cursor));
            }
            //關閉Cursor物件
            cursor.close();
            db.endTransaction();
        }
        return resultList;
    }

    /**
     * 把Cursor目前的資料包裝為物件
     *
     * @param tableName 要讀取資料的表格名稱
     * @param cursor    讀取的資料
     * @return Object
     */
    private Object getRecord(String tableName, Cursor cursor) {
        // 準備回傳結果用的物件
        switch (tableName) {
            case TABLE_CONSULTATION:
                Consultation consultationObj = new Consultation();
                consultationObj.setConsultationSubkind(cursor.getString(1));
                consultationObj.setConsultationDate(cursor.getInt(2));
                consultationObj.setConsultationTitle(cursor.getString(3));
                consultationObj.setConsultationAdmin(cursor.getString(4));
                consultationObj.setConsultationName(cursor.getString(5));
                consultationObj.setConsultationSex(cursor.getString(6));
                consultationObj.setConsultationBedNo(cursor.getString(7));
                consultationObj.setConsultationDoctor(cursor.getString(8));
                consultationObj.setConsultationAdminDivision(cursor.getString(9));
                consultationObj.setConsultationDivision(cursor.getString(10));
                consultationObj.setConsultationRRR(cursor.getString(11));
                consultationObj.setConsultationBirefSummary(cursor.getString(12));
                consultationObj.setConsultationNote(cursor.getString(13));
                consultationObj.setConsultationJsonObj(cursor.getString(14));
                consultationObj.setConsultationMsgPk(cursor.getString(15));
                consultationObj.setConsultationEventTypePk(cursor.getString(16));

                consultationObj.setConsultationLocation(cursor.getString(17));
                consultationObj.setConsultationStartDate(cursor.getString(18));
                consultationObj.setConsultationEndDate(cursor.getString(19));
                consultationObj.setConsultationStartTime(cursor.getString(20));
                consultationObj.setConsultationEndTime(cursor.getString(21));
                consultationObj.setConsultationOneDay(cursor.getInt(22));

                return consultationObj;

            case TABLE_OPERATE:
                Operation operationObj = new Operation();
                operationObj.setOperateSubkind(cursor.getString(1));
                operationObj.setOperateDate(cursor.getInt(2));
                operationObj.setOperateTitle(cursor.getString(3));
                operationObj.setOperateFrom(cursor.getString(4));
                operationObj.setOperateTime(cursor.getString(5));
                operationObj.setOperateMedicalNumber(cursor.getString(6));
                operationObj.setOperateName(cursor.getString(7));
                operationObj.setOperateDivision(cursor.getString(8));
                operationObj.setOperateDiagnosis(cursor.getString(9));
                operationObj.setOperation(cursor.getString(10));
                operationObj.setOperateNote(cursor.getString(11));
                operationObj.setOperateJsonObj(cursor.getString(12));
                operationObj.setOperateMsgPk(cursor.getString(13));
                operationObj.setOperateEventTypePk(cursor.getString(14));

                operationObj.setOperateLocation(cursor.getString(15));
                operationObj.setOperateStartDate(cursor.getString(16));
                operationObj.setOperateEndDate(cursor.getString(17));
                operationObj.setOperateStartTime(cursor.getString(18));
                operationObj.setOperateEndTime(cursor.getString(19));
                operationObj.setOperateOneDay(cursor.getInt(20));

                return operationObj;

            case TABLE_CLINIC:
                Clinic clinicObj = new Clinic();
                clinicObj.setClinicSubkind(cursor.getString(1));
                clinicObj.setClinicDate(cursor.getInt(2));
                clinicObj.setClinicTitle(cursor.getString(3));
                clinicObj.setClinicJsonObj(cursor.getString(4));
                clinicObj.setClinicMsgPk(cursor.getString(5));
                clinicObj.setClinicEventTypePk(cursor.getString(6));

                clinicObj.setClinicName(cursor.getString(7));
                clinicObj.setClinicLocation(cursor.getString(8));
                clinicObj.setClinicStartDate(cursor.getString(9));
                clinicObj.setClinicEndDate(cursor.getString(10));
                clinicObj.setClinicStartTime(cursor.getString(11));
                clinicObj.setClinicEndTime(cursor.getString(12));
                clinicObj.setClinicNote(cursor.getString(13));
                clinicObj.setClinicOneDay(cursor.getInt(14));

                return clinicObj;

            case TABLE_MEETING:
                Meeting meetingObj = new Meeting();
                meetingObj.setMeetingSubkind(cursor.getString(1));
                meetingObj.setMeetingDate(cursor.getInt(2));
                meetingObj.setMeetingTitle(cursor.getString(3));
                meetingObj.setMeetingJsonObj(cursor.getString(4));
                meetingObj.setMeetingMsgPk(cursor.getString(5));
                meetingObj.setMeetingEventTypePk(cursor.getString(6));

                meetingObj.setMeetingName(cursor.getString(7));
                meetingObj.setMeetingLocation(cursor.getString(8));
                meetingObj.setMeetingStartDate(cursor.getString(9));
                meetingObj.setMeetingEndDate(cursor.getString(10));
                meetingObj.setMeetingStartTime(cursor.getString(11));
                meetingObj.setMeetingEndTime(cursor.getString(12));
                meetingObj.setMeetingNote(cursor.getString(13));
                meetingObj.setMeetingOneDay(cursor.getInt(14));

                return meetingObj;

            case TABLE_PERSONAL:
                Personal personalObj = new Personal();
                personalObj.setPersonalSubkind(cursor.getString(1));
                personalObj.setPersonalDate(cursor.getInt(2));
                personalObj.setPersonalName(cursor.getString(3));
                personalObj.setPersonalLocation(cursor.getString(4));
                personalObj.setPersonalStartDate(cursor.getString(5));
                personalObj.setPersonalEndDate(cursor.getString(6));
                personalObj.setPersonalStartTime(cursor.getString(7));
                personalObj.setPersonalEndTime(cursor.getString(8));
                personalObj.setPersonalNote(cursor.getString(9));
                personalObj.setPersonalOneDay(cursor.getInt(10));

                personalObj.setPersonalJsonObj(cursor.getString(11));
                personalObj.setPersonalMsgPk(cursor.getString(12));
                personalObj.setPersonalEventTypePk(cursor.getString(13));

                return personalObj;

            case TABLE_EXAMINATION:
                Examination examinationObj = new Examination();
                examinationObj.setExaminationSubkind(cursor.getString(1));
                examinationObj.setExaminationDate(cursor.getInt(2));
                examinationObj.setExaminationName(cursor.getString(3));
                examinationObj.setExaminationLocation(cursor.getString(4));
                examinationObj.setExaminationStartDate(cursor.getString(5));
                examinationObj.setExaminationEndDate(cursor.getString(6));
                examinationObj.setExaminationStartTime(cursor.getString(7));
                examinationObj.setExaminationEndTime(cursor.getString(8));
                examinationObj.setExaminationNote(cursor.getString(9));
                examinationObj.setExaminationOneDay(cursor.getInt(10));

                examinationObj.setExaminationJsonObj(cursor.getString(11));
                examinationObj.setExaminationMsgPk(cursor.getString(12));
                examinationObj.setExaminationEventTypePk(cursor.getString(13));

                return examinationObj;

            case TABLE_LAST_MSG_PK:
                return cursor.getString(1);
            case DATE_COLUMN:
                return cursor.getInt(2);
            default:
                System.out.println("NOTHING");
                return null;
        }
    }

    /**
     * 透過推播通知傳來的MagPk取得對應的事件日期
     *
     * @param cursor 搜尋結果
     * @return String
     */
    private String getRecordByMsgPk(Cursor cursor) {
        return String.valueOf(cursor.getInt(2));
    }
}
