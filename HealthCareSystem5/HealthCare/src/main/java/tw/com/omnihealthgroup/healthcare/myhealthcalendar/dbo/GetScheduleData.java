package tw.com.omnihealthgroup.healthcare.myhealthcalendar.dbo;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Clinic;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Consultation;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Examination;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Meeting;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Operation;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Personal;

public class GetScheduleData {
    private static final String TAG = "GetScheduleData";
    //    public static GetScheduleData GET_SCHEDULE_DATA = new GetScheduleData();
    private LinkedList<JSONObject> sortedJsonObjList;
    private ScheduleDAO scheduleDAO;

    public GetScheduleData(Context context, LinkedList<JSONObject> sortedJsonObjList) {
        scheduleDAO = new ScheduleDAO(context);
        this.sortedJsonObjList = sortedJsonObjList;
    }

    /**
     * 取得門診排程資料 - 一般記事 - PK23
     *
     * @return null
     */
    //    public ArrayList<String> getOutPatientSchedule() {
    //        LinkedList<String> outPatientList = new LinkedList<>();
    //        LinkedList<JSONObject> opJsonObjList = new LinkedList<>();
    //        return null;
    //    }
    public void getClinicSchedule() {
        Calendar calendar = Calendar.getInstance();
        try {
            for (int i = 0; i < sortedJsonObjList.size(); i++) {
                if (sortedJsonObjList.get(i).getString("eventType").equals("23")) {
                    String objTime = sortedJsonObjList.get(i).getString("eventStartDate");
                    objTime = objTime.replace("-", "");
                    calendar.set(
                            Integer.valueOf(objTime.substring(0, 4)),
                            (Integer.valueOf(objTime.substring(4, 6)) - 1),
                            Integer.valueOf(objTime.substring(6, 8)),
                            0, 0, 0
                    );

                    String StartDate = null, EndDate = null;

                    StartDate = sortedJsonObjList.get(i).getString("eventStartDate");
                    StartDate = StartDate.replace("-", "");
                    StartDate = StartDate.substring(0, 8);
                    Log.v(TAG + "StartDate", StartDate);

                    EndDate = sortedJsonObjList.get(i).getString("eventEndDate");
                    EndDate = EndDate.replace("-", "");
                    EndDate = EndDate.substring(0, 8);
                    Log.v(TAG + "EndDate", EndDate);

                    String day = DateCompare(StartDate, EndDate);
                    Log.v(TAG + "day", day);

                    boolean isNeaData = scheduleDAO.checkDuplicateMsgPk(ScheduleDAO.TABLE_CLINIC, sortedJsonObjList.get(i).getString("calendarEventPK"));
                    if (isNeaData) {
                        if (Integer.parseInt(day) == 0) {
                            scheduleDAO.insert(ScheduleDAO.TABLE_CLINIC, insertClinicData(sortedJsonObjList.get(i), StartDate, 1));
                        } else {
                            for (int j = 0; j <= Integer.parseInt(day); j++) {
                                scheduleDAO.insert(ScheduleDAO.TABLE_CLINIC, insertClinicData(sortedJsonObjList.get(i), howManyDays(StartDate, j), 0));
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取得會診排程資料 // 會診 - 重要活動 - PK24
     */
    public void getConsultationSchedule() {
        Calendar calendar = Calendar.getInstance();
        try {
            //  建立BLOB JsonObjList
            for (int i = 0; i < sortedJsonObjList.size(); i++) {
                //                if (sortedJsonObjList.get(i).getString("KindCode").equals("C")) {
                //                    JSONObject ssJsonObj = new JSONObject(sortedJsonObjList.get(i).getString("BLOB"));
                //                    ssJsonObj.put(ScheduleString.MSG_PK, sortedJsonObjList.get(i).getString(ScheduleString.MSG_PK));
                //                    String objTime = ssJsonObj.getString(ScheduleString.ConsultationSchedule.CONSULTATION_TIME);
                //                    objTime = objTime.replace("-", "");
                //                    calendar.set(
                //                            Integer.valueOf(objTime.substring(0, 4)),
                //                            (Integer.valueOf(objTime.substring(4, 6)) - 1),
                //                            Integer.valueOf(objTime.substring(6)),
                //                            0, 0, 0
                //                    );
                //                    ssJsonObj.put(ScheduleString.ConsultationSchedule.CONSULTATION_TIME, objTime);
                //                    boolean isNeaData = scheduleDAO.checkDuplicateMsgPk(ScheduleDAO.TABLE_CONSULTATION, ssJsonObj.getString(ScheduleString.MSG_PK));
                //                    if (isNeaData) {
                //                        scheduleDAO.insert(ScheduleDAO.TABLE_CONSULTATION, setToObject("C", ssJsonObj));
                //                    }
                //                }
                if (sortedJsonObjList.get(i).getString("eventType").equals("24")) {
                    //                    JSONObject ssJsonObj = new JSONObject(sortedJsonObjList.get(i).getString("BLOB"));
                    //                    ssJsonObj.put(ScheduleString.MSG_PK, sortedJsonObjList.get(i).getString(ScheduleString.MSG_PK));
                    String objTime = sortedJsonObjList.get(i).getString("eventStartDate");
                    objTime = objTime.replace("-", "");
                    calendar.set(
                            Integer.valueOf(objTime.substring(0, 4)),
                            (Integer.valueOf(objTime.substring(4, 6)) - 1),
                            Integer.valueOf(objTime.substring(6, 8)),
                            0, 0, 0
                    );

                    String StartDate = null, EndDate = null;

                    StartDate = sortedJsonObjList.get(i).getString("eventStartDate");
                    StartDate = StartDate.replace("-", "");
                    StartDate = StartDate.substring(0, 8);
                    Log.v(TAG + "StartDate", StartDate);

                    EndDate = sortedJsonObjList.get(i).getString("eventEndDate");
                    EndDate = EndDate.replace("-", "");
                    EndDate = EndDate.substring(0, 8);
                    Log.v(TAG + "EndDate", EndDate);

                    String day = DateCompare(StartDate, EndDate);
                    Log.v(TAG + "day", day);

                    //                    ssJsonObj.put(ScheduleString.ConsultationSchedule.CONSULTATION_TIME, objTime);
                    boolean isNeaData = scheduleDAO.checkDuplicateMsgPk(ScheduleDAO.TABLE_CONSULTATION, sortedJsonObjList.get(i).getString("calendarEventPK"));


                    if (isNeaData) {
                        if (Integer.parseInt(day) == 0) {
                            //                    consultation = insertConsultationData(consultation, jsonObj, StartDate, 1);
                            //                            return insertConsultationData(jsonObj, StartDate, 1);
                            scheduleDAO.insert(ScheduleDAO.TABLE_CONSULTATION, insertConsultationData(sortedJsonObjList.get(i), StartDate, 1));
                        } else {
                            for (int j = 0; j <= Integer.parseInt(day); j++) {
                                //                        consultation = insertConsultationData(consultation, jsonObj, howManyDays(StartDate, i), 0);
                                //                                return insertConsultationData(jsonObj, howManyDays(StartDate, i), 0);
                                scheduleDAO.insert(ScheduleDAO.TABLE_CONSULTATION, insertConsultationData(sortedJsonObjList.get(i), howManyDays(StartDate, j), 0));
                            }
                        }
                        //                        scheduleDAO.insert(ScheduleDAO.TABLE_CONSULTATION, setToObject("C", sortedJsonObjList.get(i)));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取得手術排程資料 - 量測提醒 - PK25
     */
    public void getOperationSchedule() {
        Calendar calendar = Calendar.getInstance();
        try {
            //  建立BLOB JsonObjList
            //            for (int i = 0; i < sortedJsonObjList.size(); i++) {
            //                if (sortedJsonObjList.get(i).getString("KindCode").equals("O")) {
            //                    JSONObject ssJsonObj = new JSONObject(sortedJsonObjList.get(i).getString("BLOB"));
            //                    ssJsonObj.put(ScheduleString.MSG_PK, sortedJsonObjList.get(i).getString(ScheduleString.MSG_PK));
            //                    String objTime = ssJsonObj.getString(ScheduleString.OperateSchedule.OPERATE_DATE);
            //                    objTime = String.valueOf(Integer.valueOf(objTime.substring(0, 3)) + 1911) + objTime.substring(3);
            //                    calendar.set(
            //                            Integer.valueOf(objTime.substring(0, 4)),
            //                            (Integer.valueOf(objTime.substring(4, 6)) - 1),
            //                            Integer.valueOf(objTime.substring(6)),
            //                            0, 0, 0
            //                    );
            //                    ssJsonObj.put(ScheduleString.OperateSchedule.OPERATE_DATE, objTime);
            //                    //檢查本機端是否已經有相同資料
            //                    boolean isNeaData = scheduleDAO.checkDuplicateMsgPk(ScheduleDAO.TABLE_OPERATE, ssJsonObj.getString(ScheduleString.MSG_PK));
            //                    if (isNeaData) {
            //                        scheduleDAO.insert(ScheduleDAO.TABLE_OPERATE, setToObject("O", ssJsonObj));
            //                    }
            //                }
            //            }
            for (int i = 0; i < sortedJsonObjList.size(); i++) {
                if (sortedJsonObjList.get(i).getString("eventType").equals("25")) {
                    String objTime = sortedJsonObjList.get(i).getString("eventStartDate");
                    objTime = objTime.replace("-", "");
                    calendar.set(
                            Integer.valueOf(objTime.substring(0, 4)),
                            (Integer.valueOf(objTime.substring(4, 6)) - 1),
                            Integer.valueOf(objTime.substring(6, 8)),
                            0, 0, 0
                    );

                    String StartDate = null, EndDate = null;

                    StartDate = sortedJsonObjList.get(i).getString("eventStartDate");
                    StartDate = StartDate.replace("-", "");
                    StartDate = StartDate.substring(0, 8);
                    Log.v(TAG + "StartDate", StartDate);

                    EndDate = sortedJsonObjList.get(i).getString("eventEndDate");
                    EndDate = EndDate.replace("-", "");
                    EndDate = EndDate.substring(0, 8);
                    Log.v(TAG + "EndDate", EndDate);

                    String day = DateCompare(StartDate, EndDate);
                    Log.v(TAG + "day", day);

                    boolean isNeaData = scheduleDAO.checkDuplicateMsgPk(ScheduleDAO.TABLE_OPERATE, sortedJsonObjList.get(i).getString("calendarEventPK"));
                    if (isNeaData) {
                        if (Integer.parseInt(day) == 0) {
                            scheduleDAO.insert(ScheduleDAO.TABLE_OPERATE, insertOperationData(sortedJsonObjList.get(i), StartDate, 1));
                        } else {
                            for (int j = 0; j <= Integer.parseInt(day); j++) {
                                scheduleDAO.insert(ScheduleDAO.TABLE_OPERATE, insertOperationData(sortedJsonObjList.get(i), howManyDays(StartDate, j), 0));
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取得會議排程資料 - 用藥提醒 - PK26
     *
     * @return null
     */
    //    public ArrayList<String> getMeetingSchedule() {
    //        LinkedList<String> meetingList = new LinkedList<>();
    //        LinkedList<JSONObject> msJsonObjList = new LinkedList<>();
    //        return null;
    //    }
    public void getMeetingSchedule() {
        Calendar calendar = Calendar.getInstance();
        try {
            for (int i = 0; i < sortedJsonObjList.size(); i++) {
                if (sortedJsonObjList.get(i).getString("eventType").equals("26")) {
                    String objTime = sortedJsonObjList.get(i).getString("eventStartDate");
                    objTime = objTime.replace("-", "");
                    calendar.set(
                            Integer.valueOf(objTime.substring(0, 4)),
                            (Integer.valueOf(objTime.substring(4, 6)) - 1),
                            Integer.valueOf(objTime.substring(6, 8)),
                            0, 0, 0
                    );

                    String StartDate = null, EndDate = null;

                    StartDate = sortedJsonObjList.get(i).getString("eventStartDate");
                    StartDate = StartDate.replace("-", "");
                    StartDate = StartDate.substring(0, 8);
                    Log.v(TAG + "StartDate", StartDate);

                    EndDate = sortedJsonObjList.get(i).getString("eventEndDate");
                    EndDate = EndDate.replace("-", "");
                    EndDate = EndDate.substring(0, 8);
                    Log.v(TAG + "EndDate", EndDate);

                    String day = DateCompare(StartDate, EndDate);
                    Log.v(TAG + "day", day);

                    boolean isNeaData = scheduleDAO.checkDuplicateMsgPk(ScheduleDAO.TABLE_MEETING, sortedJsonObjList.get(i).getString("calendarEventPK"));
                    if (isNeaData) {
                        if (Integer.parseInt(day) == 0) {
                            scheduleDAO.insert(ScheduleDAO.TABLE_MEETING, insertMeetingData(sortedJsonObjList.get(i), StartDate, 1));
                        } else {
                            for (int j = 0; j <= Integer.parseInt(day); j++) {
                                scheduleDAO.insert(ScheduleDAO.TABLE_MEETING, insertMeetingData(sortedJsonObjList.get(i), howManyDays(StartDate, j), 0));
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 預約看診 - PK27
     *
     * @return null
     */
    public void getPersonalSchedule() {
        Calendar calendar = Calendar.getInstance();
        try {
            for (int i = 0; i < sortedJsonObjList.size(); i++) {
                if (sortedJsonObjList.get(i).getString("eventType").equals("27")) {
                    String objTime = sortedJsonObjList.get(i).getString("eventStartDate");
                    objTime = objTime.replace("-", "");
                    calendar.set(
                            Integer.valueOf(objTime.substring(0, 4)),
                            (Integer.valueOf(objTime.substring(4, 6)) - 1),
                            Integer.valueOf(objTime.substring(6, 8)),
                            0, 0, 0
                    );

                    String StartDate = null, EndDate = null;

                    StartDate = sortedJsonObjList.get(i).getString("eventStartDate");
                    StartDate = StartDate.replace("-", "");
                    StartDate = StartDate.substring(0, 8);
                    Log.v(TAG + "StartDate", StartDate);

                    EndDate = sortedJsonObjList.get(i).getString("eventEndDate");
                    EndDate = EndDate.replace("-", "");
                    EndDate = EndDate.substring(0, 8);
                    Log.v(TAG + "EndDate", EndDate);

                    String day = DateCompare(StartDate, EndDate);
                    Log.v(TAG + "day", day);

                    boolean isNeaData = scheduleDAO.checkDuplicateMsgPk(ScheduleDAO.TABLE_PERSONAL, sortedJsonObjList.get(i).getString("calendarEventPK"));
                    if (isNeaData) {
                        if (Integer.parseInt(day) == 0) {
                            scheduleDAO.insert(ScheduleDAO.TABLE_PERSONAL, insertPersonalData(sortedJsonObjList.get(i), StartDate, 1));
                        } else {
                            for (int j = 0; j <= Integer.parseInt(day); j++) {
                                scheduleDAO.insert(ScheduleDAO.TABLE_PERSONAL, insertPersonalData(sortedJsonObjList.get(i), howManyDays(StartDate, j), 0));
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 健康檢查 - PK28
     *
     * @return null
     */
    public void getExaminationSchedule() {
        Calendar calendar = Calendar.getInstance();
        try {
            for (int i = 0; i < sortedJsonObjList.size(); i++) {
                if (sortedJsonObjList.get(i).getString("eventType").equals("28")) {
                    String objTime = sortedJsonObjList.get(i).getString("eventStartDate");
                    objTime = objTime.replace("-", "");
                    calendar.set(
                            Integer.valueOf(objTime.substring(0, 4)),
                            (Integer.valueOf(objTime.substring(4, 6)) - 1),
                            Integer.valueOf(objTime.substring(6, 8)),
                            0, 0, 0
                    );

                    String StartDate = null, EndDate = null;

                    StartDate = sortedJsonObjList.get(i).getString("eventStartDate");
                    StartDate = StartDate.replace("-", "");
                    StartDate = StartDate.substring(0, 8);
                    Log.v(TAG + "StartDate", StartDate);

                    EndDate = sortedJsonObjList.get(i).getString("eventEndDate");
                    EndDate = EndDate.replace("-", "");
                    EndDate = EndDate.substring(0, 8);
                    Log.v(TAG + "EndDate", EndDate);

                    String day = DateCompare(StartDate, EndDate);
                    Log.v(TAG + "day", day);

                    boolean isNeaData = scheduleDAO.checkDuplicateMsgPk(ScheduleDAO.TABLE_EXAMINATION, sortedJsonObjList.get(i).getString("calendarEventPK"));
                    if (isNeaData) {
                        if (Integer.parseInt(day) == 0) {
                            scheduleDAO.insert(ScheduleDAO.TABLE_EXAMINATION, insertExaminationData(sortedJsonObjList.get(i), StartDate, 1));
                        } else {
                            for (int j = 0; j <= Integer.parseInt(day); j++) {
                                scheduleDAO.insert(ScheduleDAO.TABLE_EXAMINATION, insertExaminationData(sortedJsonObjList.get(i), howManyDays(StartDate, j), 0));
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //    /**
    //     * @param kindCode 事件類別
    //     * @param jsonObj  已解析過的JSON OBJECT
    //     * @return Object
    //     * @throws JSONException
    //     */
    //    private Object setToObject(String kindCode, JSONObject jsonObj) throws JSONException {
    //        switch (kindCode) {
    //            case "C":
    //                Consultation consultation = new Consultation();
    //                Log.v("jsonObj", jsonObj.toString());
    //                //會診類別
    //                consultation.setConsultationSubkind(jsonObj.getString(ScheduleString.ConsultationSchedule.CONSULTATION_CATEGORY));
    //                //開立時間
    //                consultation.setConsultationDate(jsonObj.getInt(ScheduleString.ConsultationSchedule.CONSULTATION_TIME));
    //                //開立科別＋床號＋姓名
    //                consultation.setConsultationTitle(
    //                        jsonObj.getString(ScheduleString.ConsultationSchedule.CONSULTATION_DIVISION) +
    //                                jsonObj.getString(ScheduleString.ConsultationSchedule.CONSULTATION_BED_NO) +
    //                                jsonObj.getString(ScheduleString.ConsultationSchedule.CONSULTATION_NAME)
    //                );
    //                //開立者
    //                consultation.setConsultationAdmin(jsonObj.getString(ScheduleString.ConsultationSchedule.CONSULTATION_ADMIN));
    //                //姓名
    //                consultation.setConsultationName(jsonObj.getString(ScheduleString.ConsultationSchedule.CONSULTATION_NAME));
    //                //性別
    //                consultation.setConsultationSex(jsonObj.getString(ScheduleString.ConsultationSchedule.CONSULTATION_SEX));
    //                //床號
    //                consultation.setConsultationBedNo(jsonObj.getString(ScheduleString.ConsultationSchedule.CONSULTATION_BED_NO));
    //                //開立醫師
    //                consultation.setConsultationDoctor(jsonObj.getString(ScheduleString.ConsultationSchedule.CONSULTATION_DOCTOR));
    //                //開立科別
    //                consultation.setConsultationAdminDivision(jsonObj.getString(ScheduleString.ConsultationSchedule.CONSULTATION_ADMIN_DIVISION));
    //                //會診科別
    //                consultation.setConsultationDivision(jsonObj.getString(ScheduleString.ConsultationSchedule.CONSULTATION_DIVISION));
    //                //會診REPORT REQUESTED REGARDING
    //                consultation.setConsultationRRR(jsonObj.getString(ScheduleString.ConsultationSchedule.CONSULTATION_RRR));
    //                //會診BIREF SUMMARY
    //                consultation.setConsultationBirefSummary(jsonObj.getString(ScheduleString.ConsultationSchedule.CONSULTATION_BS));
    //                //備註
    //                for (int i = 0; i < jsonObj.length(); i++) {
    //                    if (jsonObj.keys().next().equals(ScheduleString.ConsultationSchedule.CONSULTATION_NOTE)) {
    //                        consultation.setConsultationNote(jsonObj.getString(ScheduleString.ConsultationSchedule.CONSULTATION_NOTE));
    //                    }
    //                }
    //
    //
    //                if (Integer.parseInt(day) == 0) {
    //                    consultation = insertConsultationData(consultation, jsonObj, StartDate, 1);
    //                    return insertConsultationData(jsonObj, StartDate, 1);
    //                } else {
    //                    for (int i = 0; i <= Integer.parseInt(day); i++) {
    //                        consultation = insertConsultationData(consultation, jsonObj, howManyDays(StartDate, i), 0);
    //                        return insertConsultationData(jsonObj, howManyDays(StartDate, i), 0);
    //                    }
    //
    //                }
    //                return consultation;
    //
    //            case "O":
    //                Operation operation = new Operation();
    //                //來源＋科別
    //                operation.setOperateSubkind(
    //                        jsonObj.getString(ScheduleString.OperateSchedule.OPERATE_FROM) +
    //                                jsonObj.getString(ScheduleString.OperateSchedule.OPERATE_DIVISION)
    //                );
    //                //開刀日期
    //                operation.setOperateDate(jsonObj.getInt(ScheduleString.OperateSchedule.OPERATE_DATE));
    //                //科別+姓名
    //                operation.setOperateTitle(
    //                        jsonObj.getString(ScheduleString.OperateSchedule.OPERATE_DIVISION) +
    //                                jsonObj.getString(ScheduleString.OperateSchedule.OPERATE_NAME)
    //                );
    //                //來源
    //                operation.setOperateFrom(jsonObj.getString(ScheduleString.OperateSchedule.OPERATE_FROM));
    //                //預計開刀時間
    //                operation.setOperateTime(jsonObj.getString(ScheduleString.OperateSchedule.OPERATE_SCHEDULE_DATE));
    //                //病歷號
    //                operation.setOperateMedicalNumber(jsonObj.getString(ScheduleString.OperateSchedule.OPERATE_MEDICAL_NUMBER));
    //                //姓名
    //                operation.setOperateName(jsonObj.getString(ScheduleString.OperateSchedule.OPERATE_NAME));
    //                //科別
    //                operation.setOperateDivision(jsonObj.getString(ScheduleString.OperateSchedule.OPERATE_DIVISION));
    //                //主診斷
    //                operation.setOperateDiagnosis(jsonObj.getString(ScheduleString.OperateSchedule.OPERATE_DIAGNOSIS));
    //                //手術名稱
    //                operation.setOperation(jsonObj.getString(ScheduleString.OperateSchedule.OPERATE_OPERATION));
    //                //備註
    //                for (int i = 0; i < jsonObj.length(); i++) {
    //                    if (jsonObj.keys().next().equals(ScheduleString.OperateSchedule.OPERATE_NOTE)) {
    //                        operation.setOperateNote(jsonObj.getString(ScheduleString.OperateSchedule.OPERATE_NOTE));
    //                    }
    //                }
    //                //STORE JSON OBJECT
    //                operation.setOperateJsonObj(jsonObj.toString());
    //                //MsgPk
    //                operation.setOperateMsgPk(jsonObj.getString(ScheduleString.MSG_PK));
    //                return operation;
    //
    //            default:
    //                return null;
    //        }
    //    }

    private Clinic insertClinicData(JSONObject jsonObj, String StartDate, int oneDay) {
        Clinic clinic = new Clinic();
        try {
            String eventStartDate, eventStartTime = null;
            eventStartDate = jsonObj.getString("eventStartDate");
            eventStartDate = eventStartDate.replace("-", " / ");
            eventStartDate = eventStartDate.substring(0, 20);
            if (Integer.parseInt(eventStartDate.substring(15, 17)) >= 12) {
                eventStartDate = eventStartDate + " " + "PM";
                eventStartTime = "下午" + " " + eventStartDate.substring(15, 20);
            } else {
                eventStartDate = eventStartDate + " " + "AM";
                eventStartTime = "上午" + " " + eventStartDate.substring(15, 20);
            }
            Log.v(TAG + "eventStartDate", eventStartDate);

            String eventEndDate, eventEndTime = null;
            eventEndDate = jsonObj.getString("eventEndDate");
            eventEndDate = eventEndDate.replace("-", " / ");
            eventEndDate = eventEndDate.substring(0, 20);
            if (Integer.parseInt(eventEndDate.substring(15, 17)) >= 12) {
                eventEndDate = eventEndDate + " " + "PM";
                eventEndTime = "下午" + " " + eventStartDate.substring(15, 20);
            } else {
                eventEndDate = eventEndDate + " " + "AM";
                eventEndTime = "上午" + " " + eventStartDate.substring(15, 20);
            }
            Log.v(TAG + "eventEndDate", eventEndDate);

            clinic.setClinicDate(Integer.parseInt(StartDate));
            clinic.setClinicName(jsonObj.getString("eventTitle"));
            //            clinic.setClinicLocation(jsonObj.getString("eventTitle"));
            clinic.setClinicLocation("");
            if (jsonObj.getString("eventMemo") != null
                    && jsonObj.getString("eventMemo") != "null") {
                clinic.setClinicNote(jsonObj.getString("eventMemo"));
            } else {
                clinic.setClinicNote("");
            }

            clinic.setClinicStartDate(eventStartDate);
            clinic.setClinicEndDate(eventEndDate);
            clinic.setClinicStartTime(eventStartTime);
            clinic.setClinicEndTime(eventEndTime);
            clinic.setClinicOneDay(oneDay); //是否跨日

            //STORE JSON OBJECT
            clinic.setClinicJsonObj(jsonObj.toString());
            //MsgPk
            clinic.setClinicMsgPk(jsonObj.getString("calendarEventPK"));
            clinic.setClinicEventTypePk(jsonObj.getString("eventType"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return clinic;
    }

    private Consultation insertConsultationData(JSONObject jsonObj, String StartDate, int oneDay) {
        Consultation consultation = new Consultation();
        try {
            String eventStartDate, eventStartTime = null;
            eventStartDate = jsonObj.getString("eventStartDate");
            eventStartDate = eventStartDate.replace("-", " / ");
            eventStartDate = eventStartDate.substring(0, 20);
            if (Integer.parseInt(eventStartDate.substring(15, 17)) >= 12) {
                eventStartDate = eventStartDate + " " + "PM";
                eventStartTime = "下午" + " " + eventStartDate.substring(15, 20);
            } else {
                eventStartDate = eventStartDate + " " + "AM";
                eventStartTime = "上午" + " " + eventStartDate.substring(15, 20);
            }
            Log.v(TAG + "eventStartDate", eventStartDate);

            String eventEndDate, eventEndTime = null;
            eventEndDate = jsonObj.getString("eventEndDate");
            eventEndDate = eventEndDate.replace("-", " / ");
            eventEndDate = eventEndDate.substring(0, 20);
            if (Integer.parseInt(eventEndDate.substring(15, 17)) >= 12) {
                eventEndDate = eventEndDate + " " + "PM";
                eventEndTime = "下午" + " " + eventStartDate.substring(15, 20);
            } else {
                eventEndDate = eventEndDate + " " + "AM";
                eventEndTime = "上午" + " " + eventStartDate.substring(15, 20);
            }
            Log.v(TAG + "eventEndDate", eventEndDate);

            consultation.setConsultationDate(Integer.parseInt(StartDate));
            consultation.setConsultationName(jsonObj.getString("eventTitle"));
            //            consultation.setConsultationLocation(jsonObj.getString("eventTitle"));
            consultation.setConsultationLocation("");
            if (jsonObj.getString("eventMemo") != null
                    && jsonObj.getString("eventMemo") != "null") {
                consultation.setConsultationNote(jsonObj.getString("eventMemo"));
            } else {
                consultation.setConsultationNote("");
            }

            consultation.setConsultationStartDate(eventStartDate);
            consultation.setConsultationEndDate(eventEndDate);
            consultation.setConsultationStartTime(eventStartTime);
            consultation.setConsultationEndTime(eventEndTime);
            consultation.setConsultationOneDay(oneDay); //是否跨日

            //STORE JSON OBJECT
            consultation.setConsultationJsonObj(jsonObj.toString());
            //MsgPk
            consultation.setConsultationMsgPk(jsonObj.getString("calendarEventPK"));
            consultation.setConsultationEventTypePk(jsonObj.getString("eventType"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return consultation;
    }

    private Operation insertOperationData(JSONObject jsonObj, String StartDate, int oneDay) {
        Operation operation = new Operation();
        try {
            String eventStartDate, eventStartTime = null;
            eventStartDate = jsonObj.getString("eventStartDate");
            eventStartDate = eventStartDate.replace("-", " / ");
            eventStartDate = eventStartDate.substring(0, 20);
            if (Integer.parseInt(eventStartDate.substring(15, 17)) >= 12) {
                eventStartDate = eventStartDate + " " + "PM";
                eventStartTime = "下午" + " " + eventStartDate.substring(15, 20);
            } else {
                eventStartDate = eventStartDate + " " + "AM";
                eventStartTime = "上午" + " " + eventStartDate.substring(15, 20);
            }
            Log.v(TAG + "eventStartDate", eventStartDate);

            String eventEndDate, eventEndTime = null;
            eventEndDate = jsonObj.getString("eventEndDate");
            eventEndDate = eventEndDate.replace("-", " / ");
            eventEndDate = eventEndDate.substring(0, 20);
            if (Integer.parseInt(eventEndDate.substring(15, 17)) >= 12) {
                eventEndDate = eventEndDate + " " + "PM";
                eventEndTime = "下午" + " " + eventStartDate.substring(15, 20);
            } else {
                eventEndDate = eventEndDate + " " + "AM";
                eventEndTime = "上午" + " " + eventStartDate.substring(15, 20);
            }
            Log.v(TAG + "eventEndDate", eventEndDate);

            operation.setOperateDate(Integer.parseInt(StartDate));
            operation.setOperateName(jsonObj.getString("eventTitle"));
            //            operation.setOperateLocation(jsonObj.getString("eventTitle"));
            operation.setOperateLocation("");
            if (jsonObj.getString("eventMemo") != null
                    && jsonObj.getString("eventMemo") != "null") {
                operation.setOperateNote(jsonObj.getString("eventMemo"));
            } else {
                operation.setOperateNote("");
            }

            operation.setOperateStartDate(eventStartDate);
            operation.setOperateEndDate(eventEndDate);
            operation.setOperateStartTime(eventStartTime);
            operation.setOperateEndTime(eventEndTime);
            operation.setOperateOneDay(oneDay); //是否跨日

            //STORE JSON OBJECT
            operation.setOperateJsonObj(jsonObj.toString());
            //MsgPk
            operation.setOperateMsgPk(jsonObj.getString("calendarEventPK"));
            operation.setOperateEventTypePk(jsonObj.getString("eventType"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return operation;
    }

    private Meeting insertMeetingData(JSONObject jsonObj, String StartDate, int oneDay) {
        Meeting meeting = new Meeting();
        try {
            String eventStartDate, eventStartTime = null;
            eventStartDate = jsonObj.getString("eventStartDate");
            eventStartDate = eventStartDate.replace("-", " / ");
            eventStartDate = eventStartDate.substring(0, 20);
            if (Integer.parseInt(eventStartDate.substring(15, 17)) >= 12) {
                eventStartDate = eventStartDate + " " + "PM";
                eventStartTime = "下午" + " " + eventStartDate.substring(15, 20);
            } else {
                eventStartDate = eventStartDate + " " + "AM";
                eventStartTime = "上午" + " " + eventStartDate.substring(15, 20);
            }
            Log.v(TAG + "eventStartDate", eventStartDate);

            String eventEndDate, eventEndTime = null;
            eventEndDate = jsonObj.getString("eventEndDate");
            eventEndDate = eventEndDate.replace("-", " / ");
            eventEndDate = eventEndDate.substring(0, 20);
            if (Integer.parseInt(eventEndDate.substring(15, 17)) >= 12) {
                eventEndDate = eventEndDate + " " + "PM";
                eventEndTime = "下午" + " " + eventStartDate.substring(15, 20);
            } else {
                eventEndDate = eventEndDate + " " + "AM";
                eventEndTime = "上午" + " " + eventStartDate.substring(15, 20);
            }
            Log.v(TAG + "eventEndDate", eventEndDate);

            meeting.setMeetingDate(Integer.parseInt(StartDate));
            meeting.setMeetingName(jsonObj.getString("eventTitle"));
            //            meeting.setMeetingLocation(jsonObj.getString("eventTitle"));
            meeting.setMeetingLocation("");
            if (jsonObj.getString("eventMemo") != null
                    && jsonObj.getString("eventMemo") != "null") {
                meeting.setMeetingNote(jsonObj.getString("eventMemo"));
            } else {
                meeting.setMeetingNote("");
            }

            meeting.setMeetingStartDate(eventStartDate);
            meeting.setMeetingEndDate(eventEndDate);
            meeting.setMeetingStartTime(eventStartTime);
            meeting.setMeetingEndTime(eventEndTime);
            meeting.setMeetingOneDay(oneDay); //是否跨日

            //STORE JSON OBJECT
            meeting.setMeetingJsonObj(jsonObj.toString());
            //MsgPk
            meeting.setMeetingMsgPk(jsonObj.getString("calendarEventPK"));
            meeting.setMeetingEventTypePk(jsonObj.getString("eventType"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return meeting;
    }

    private Personal insertPersonalData(JSONObject jsonObj, String StartDate, int oneDay) {
        Personal personal = new Personal();
        try {
            String eventStartDate, eventStartTime = null;
            eventStartDate = jsonObj.getString("eventStartDate");
            eventStartDate = eventStartDate.replace("-", " / ");
            eventStartDate = eventStartDate.substring(0, 20);
            if (Integer.parseInt(eventStartDate.substring(15, 17)) >= 12) {
                eventStartDate = eventStartDate + " " + "PM";
                eventStartTime = "下午" + " " + eventStartDate.substring(15, 20);
            } else {
                eventStartDate = eventStartDate + " " + "AM";
                eventStartTime = "上午" + " " + eventStartDate.substring(15, 20);
            }
            Log.v(TAG + "eventStartDate", eventStartDate);

            String eventEndDate, eventEndTime = null;
            eventEndDate = jsonObj.getString("eventEndDate");
            eventEndDate = eventEndDate.replace("-", " / ");
            eventEndDate = eventEndDate.substring(0, 20);
            if (Integer.parseInt(eventEndDate.substring(15, 17)) >= 12) {
                eventEndDate = eventEndDate + " " + "PM";
                eventEndTime = "下午" + " " + eventStartDate.substring(15, 20);
            } else {
                eventEndDate = eventEndDate + " " + "AM";
                eventEndTime = "上午" + " " + eventStartDate.substring(15, 20);
            }
            Log.v(TAG + "eventEndDate", eventEndDate);

            personal.setPersonalDate(Integer.parseInt(StartDate));
            personal.setPersonalName(jsonObj.getString("eventTitle"));
            //            personal.setPersonalLocation(jsonObj.getString("eventTitle"));
            personal.setPersonalLocation("");
            if (jsonObj.getString("eventMemo") != null
                    && jsonObj.getString("eventMemo") != "null") {
                personal.setPersonalNote(jsonObj.getString("eventMemo"));
            } else {
                personal.setPersonalNote("");
            }

            personal.setPersonalStartDate(eventStartDate);
            personal.setPersonalEndDate(eventEndDate);
            personal.setPersonalStartTime(eventStartTime);
            personal.setPersonalEndTime(eventEndTime);
            personal.setPersonalOneDay(oneDay); //是否跨日

            //STORE JSON OBJECT
            personal.setPersonalJsonObj(jsonObj.toString());
            //MsgPk
            personal.setPersonalMsgPk(jsonObj.getString("calendarEventPK"));
            personal.setPersonalEventTypePk(jsonObj.getString("eventType"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return personal;
    }

    private Examination insertExaminationData(JSONObject jsonObj, String StartDate, int oneDay) {
        Examination examination = new Examination();
        try {
            String eventStartDate, eventStartTime = null;
            eventStartDate = jsonObj.getString("eventStartDate");
            eventStartDate = eventStartDate.replace("-", " / ");
            eventStartDate = eventStartDate.substring(0, 20);
            if (Integer.parseInt(eventStartDate.substring(15, 17)) >= 12) {
                eventStartDate = eventStartDate + " " + "PM";
                eventStartTime = "下午" + " " + eventStartDate.substring(15, 20);
            } else {
                eventStartDate = eventStartDate + " " + "AM";
                eventStartTime = "上午" + " " + eventStartDate.substring(15, 20);
            }
            Log.v(TAG + "eventStartDate", eventStartDate);

            String eventEndDate, eventEndTime = null;
            eventEndDate = jsonObj.getString("eventEndDate");
            eventEndDate = eventEndDate.replace("-", " / ");
            eventEndDate = eventEndDate.substring(0, 20);
            if (Integer.parseInt(eventEndDate.substring(15, 17)) >= 12) {
                eventEndDate = eventEndDate + " " + "PM";
                eventEndTime = "下午" + " " + eventStartDate.substring(15, 20);
            } else {
                eventEndDate = eventEndDate + " " + "AM";
                eventEndTime = "上午" + " " + eventStartDate.substring(15, 20);
            }
            Log.v(TAG + "eventEndDate", eventEndDate);

            examination.setExaminationDate(Integer.parseInt(StartDate));
            examination.setExaminationName(jsonObj.getString("eventTitle"));
            //            examination.setExaminationLocation(jsonObj.getString("eventTitle"));
            examination.setExaminationLocation("");
            if (jsonObj.getString("eventMemo") != null
                    && jsonObj.getString("eventMemo") != "null") {
                examination.setExaminationNote(jsonObj.getString("eventMemo"));
            } else {
                examination.setExaminationNote("");
            }

            examination.setExaminationStartDate(eventStartDate);
            examination.setExaminationEndDate(eventEndDate);
            examination.setExaminationStartTime(eventStartTime);
            examination.setExaminationEndTime(eventEndTime);
            examination.setExaminationOneDay(oneDay); //是否跨日

            //STORE JSON OBJECT
            examination.setExaminationJsonObj(jsonObj.toString());
            //MsgPk
            examination.setExaminationMsgPk(jsonObj.getString("calendarEventPK"));
            examination.setExaminationEventTypePk(jsonObj.getString("eventType"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return examination;
    }


    public String DateCompare(String s1, String s2) {
        String re = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date d1 = sdf.parse(s1);
            Date d2 = sdf.parse(s2);

            re = String.valueOf(Math.abs(((d2.getTime() - d1.getTime()) / (24 * 60 * 60 * 1000))));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }

    public String howManyDays(String StartDate, int day) {
        String timeStr = null;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        try {
            Date date = formatter.parse(StartDate);
            Log.v(TAG + "date", date.toString());
            date.setDate(date.getDate() + day);
            timeStr = formatter.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v(TAG + "timeStr", timeStr);
        return timeStr;
    }
}