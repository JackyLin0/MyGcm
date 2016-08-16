package tw.com.omnihealthgroup.healthcare.myhealthcalendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.util.ShowMEProgressDiaLog;
import tw.com.omnihealthgroup.healthcare.WebServiceConnection;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.adapter.EventTypeSpinnerAdapter;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.dbo.ScheduleDAO;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Clinic;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Consultation;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Examination;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Meeting;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Operation;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Personal;

public class AddNewEventActivity extends AppCompatActivity {
    private static final String TAG = "AddNewEventActivity";
    private Button newEventCancel, newEventCommit;
    private EditText newEventName, newEventLocation, newEventNote;
    private TextView startDate, endDate;
    private ImageView delevent;
    private AlertDialog alertDialog = null;

    private WebServiceConnection webServiceConnection;
    private SharedPreferences prf;

    private StringBuilder startClockTime = new StringBuilder();
    private StringBuilder endClockTime = new StringBuilder();
    private StringBuilder timeRecordString = new StringBuilder();
    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();
    private Spinner selectClassify_event;
    private String eventType = null;
    private String eventPK = null;
    private String del_PKMark = null;
    private boolean flag_edit = false, flag_Clinic = true, flag_Consultation = true, flag_Operation = true, flag_Meeting = true, flag_Personal = true, flag_Examination = true;

    private Object object = null;
    private Clinic clinicObj = null;
    private Consultation consultationObj = null;
    private Operation operationObj = null;
    private Meeting meetingObj = null;
    private Personal personalObj = null;
    private Examination examinationObj = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_add_new);
        Log.v(TAG, "onCreate");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //editview 不會自動跳出鍵盤
        prf = getApplicationContext().getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
        webServiceConnection = new WebServiceConnection();

        initView(this.getIntent().getExtras());
        initHandler(this.getIntent().getExtras());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");

        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        //        super.onBackPressed();
        Log.v(TAG, "onBackPressed");

        CancelAlertDialog();
    }

    /**
     * 初始化UI元件
     */
    private void initView(Bundle arguments) {
        newEventCancel = (Button) findViewById(R.id.new_event_btn_cancel);
        newEventCancel.setTextColor(getResources().getColor(R.color.white_color));
        newEventCommit = (Button) findViewById(R.id.new_event_btn_commit);
        newEventCommit.setTextColor(getResources().getColor(R.color.white_color));

        newEventName = (EditText) findViewById(R.id.add_new_event_name);
        newEventLocation = (EditText) findViewById(R.id.add_new_event_locate);
        newEventNote = (EditText) findViewById(R.id.add_new_event_note);

        startDate = (TextView) findViewById(R.id.time_from_ymd);
        endDate = (TextView) findViewById(R.id.time_to_ymd);

        delevent = (ImageView) findViewById(R.id.del_btn);
        selectClassify_event = (Spinner) findViewById(R.id.spinner_classify_event);

        // 初始化選擇事件類型的Adapter
        EventTypeSpinnerAdapter selectClassifyAdapter = new EventTypeSpinnerAdapter(AddNewEventActivity.this);
        selectClassify_event.setAdapter(selectClassifyAdapter);

        if (arguments.getSerializable("clinicObj") != null) {
            flag_edit = true;

            clinicObj = (Clinic) arguments.getSerializable("clinicObj");
            newEventName.setText(clinicObj.getClinicName());
            //            newEventLocation.setText(clinicObj.getClinicLocation());
            newEventNote.setText(clinicObj.getClinicNote());
            startDate.setText(clinicObj.getClinicStartDate());
            endDate.setText(clinicObj.getClinicEndDate());

            startCalendar.set(Integer.parseInt(clinicObj.getClinicStartDate().substring(0, 4))
                    , Integer.parseInt(clinicObj.getClinicStartDate().substring(7, 9)) - 1
                    , Integer.parseInt(clinicObj.getClinicStartDate().substring(12, 14)));
            endCalendar.set(Integer.parseInt(clinicObj.getClinicEndDate().substring(0, 4))
                    , Integer.parseInt(clinicObj.getClinicEndDate().substring(7, 9)) - 1
                    , Integer.parseInt(clinicObj.getClinicEndDate().substring(12, 14)));

            selectClassify_event.setSelection(0, true);
            eventType = "0";
            eventPK = clinicObj.getClinicMsgPk();
            del_PKMark = ScheduleDAO.TABLE_CLINIC;
            object = clinicObj;
        } else if (arguments.getSerializable("consultationObj") != null) {
            flag_edit = true;

            consultationObj = (Consultation) arguments.getSerializable("consultationObj");
            newEventName.setText(consultationObj.getConsultationName());
            //            newEventLocation.setText(consultationObj.getConsultationLocation());
            newEventNote.setText(consultationObj.getConsultationNote());
            startDate.setText(consultationObj.getConsultationStartDate());
            endDate.setText(consultationObj.getConsultationEndDate());

            startCalendar.set(Integer.parseInt(consultationObj.getConsultationStartDate().substring(0, 4))
                    , Integer.parseInt(consultationObj.getConsultationStartDate().substring(7, 9)) - 1
                    , Integer.parseInt(consultationObj.getConsultationStartDate().substring(12, 14)));
            endCalendar.set(Integer.parseInt(consultationObj.getConsultationEndDate().substring(0, 4))
                    , Integer.parseInt(consultationObj.getConsultationEndDate().substring(7, 9)) - 1
                    , Integer.parseInt(consultationObj.getConsultationEndDate().substring(12, 14)));

            selectClassify_event.setSelection(1, true);
            eventType = "1";
            eventPK = consultationObj.getConsultationMsgPk();
            del_PKMark = ScheduleDAO.TABLE_CONSULTATION;
            object = consultationObj;
        } else if (arguments.getSerializable("operationObj") != null) {
            flag_edit = true;

            operationObj = (Operation) arguments.getSerializable("operationObj");
            newEventName.setText(operationObj.getOperateName());
            //            newEventLocation.setText(operationObj.getOperateLocation());
            newEventNote.setText(operationObj.getOperateNote());
            startDate.setText(operationObj.getOperateStartDate());
            endDate.setText(operationObj.getOperateEndDate());

            startCalendar.set(Integer.parseInt(operationObj.getOperateStartDate().substring(0, 4))
                    , Integer.parseInt(operationObj.getOperateStartDate().substring(7, 9)) - 1
                    , Integer.parseInt(operationObj.getOperateStartDate().substring(12, 14)));
            endCalendar.set(Integer.parseInt(operationObj.getOperateEndDate().substring(0, 4))
                    , Integer.parseInt(operationObj.getOperateEndDate().substring(7, 9)) - 1
                    , Integer.parseInt(operationObj.getOperateEndDate().substring(12, 14)));

            selectClassify_event.setSelection(2, true);
            eventType = "2";
            eventPK = operationObj.getOperateMsgPk();
            del_PKMark = ScheduleDAO.TABLE_OPERATE;
            object = operationObj;
        } else if (arguments.getSerializable("meetingObj") != null) {
            flag_edit = true;

            meetingObj = (Meeting) arguments.getSerializable("meetingObj");
            newEventName.setText(meetingObj.getMeetingName());
            //            newEventLocation.setText(meetingObj.getMeetingLocation());
            newEventNote.setText(meetingObj.getMeetingNote());
            startDate.setText(meetingObj.getMeetingStartDate());
            endDate.setText(meetingObj.getMeetingEndDate());

            startCalendar.set(Integer.parseInt(meetingObj.getMeetingStartDate().substring(0, 4))
                    , Integer.parseInt(meetingObj.getMeetingStartDate().substring(7, 9)) - 1
                    , Integer.parseInt(meetingObj.getMeetingStartDate().substring(12, 14)));
            endCalendar.set(Integer.parseInt(meetingObj.getMeetingEndDate().substring(0, 4))
                    , Integer.parseInt(meetingObj.getMeetingEndDate().substring(7, 9)) - 1
                    , Integer.parseInt(meetingObj.getMeetingEndDate().substring(12, 14)));

            selectClassify_event.setSelection(3, true);
            eventType = "3";
            eventPK = meetingObj.getMeetingMsgPk();
            del_PKMark = ScheduleDAO.TABLE_MEETING;
            object = meetingObj;
        } else if (arguments.getSerializable("personalObj") != null) {
            flag_edit = true;

            personalObj = (Personal) arguments.getSerializable("personalObj");
            newEventName.setText(personalObj.getPersonalName());
            //            newEventLocation.setText(personalObj.getPersonalLocation());
            newEventNote.setText(personalObj.getPersonalNote());
            startDate.setText(personalObj.getPersonalStartDate());
            endDate.setText(personalObj.getPersonalEndDate());

            startCalendar.set(Integer.parseInt(personalObj.getPersonalStartDate().substring(0, 4))
                    , Integer.parseInt(personalObj.getPersonalStartDate().substring(7, 9)) - 1
                    , Integer.parseInt(personalObj.getPersonalStartDate().substring(12, 14)));
            endCalendar.set(Integer.parseInt(personalObj.getPersonalEndDate().substring(0, 4))
                    , Integer.parseInt(personalObj.getPersonalEndDate().substring(7, 9)) - 1
                    , Integer.parseInt(personalObj.getPersonalEndDate().substring(12, 14)));

            selectClassify_event.setSelection(4, true);
            eventType = "4";
            eventPK = personalObj.getPersonalMsgPk();
            del_PKMark = ScheduleDAO.TABLE_PERSONAL;
            object = personalObj;
        } else if (arguments.getSerializable("examinationObj") != null) {
            flag_edit = true;

            examinationObj = (Examination) arguments.getSerializable("examinationObj");
            newEventName.setText(examinationObj.getExaminationName());
            //            newEventLocation.setText(examinationObj.getExaminationLocation());
            newEventNote.setText(examinationObj.getExaminationNote());
            startDate.setText(examinationObj.getExaminationStartDate());
            endDate.setText(examinationObj.getExaminationEndDate());

            startCalendar.set(Integer.parseInt(examinationObj.getExaminationStartDate().substring(0, 4))
                    , Integer.parseInt(examinationObj.getExaminationStartDate().substring(7, 9)) - 1
                    , Integer.parseInt(examinationObj.getExaminationStartDate().substring(12, 14)));
            endCalendar.set(Integer.parseInt(examinationObj.getExaminationEndDate().substring(0, 4))
                    , Integer.parseInt(examinationObj.getExaminationEndDate().substring(7, 9)) - 1
                    , Integer.parseInt(examinationObj.getExaminationEndDate().substring(12, 14)));

            selectClassify_event.setSelection(5, true);
            eventType = "5";
            eventPK = examinationObj.getExaminationMsgPk();
            del_PKMark = ScheduleDAO.TABLE_EXAMINATION;
            object = examinationObj;
        } else {
            flag_edit = false;

            Calendar hintCalendar = Calendar.getInstance();
            hintCalendar.set(
                    arguments.getInt("StartedYear"),
                    arguments.getInt("StartedMonth"),
                    arguments.getInt("StartedDate")
            );


            //            startDate.setHint(getDateHint(hintCalendar));
            startDate.setText(getDateHint(hintCalendar));
            startCalendar.set(hintCalendar.get(Calendar.YEAR), hintCalendar.get(Calendar.MONTH), hintCalendar.get(Calendar.DAY_OF_MONTH));

            hintCalendar.add(Calendar.DATE, 1);
            //            endDate.setHint(getDateHint(hintCalendar));
            endDate.setText(getDateHint(hintCalendar));
            endCalendar.set(hintCalendar.get(Calendar.YEAR), hintCalendar.get(Calendar.MONTH), hintCalendar.get(Calendar.DAY_OF_MONTH));
        }
        setTimeRecord(startDate, startDate.getText().toString().substring(15));
        setTimeRecord(endDate, endDate.getText().toString().substring(15));

        if (flag_edit == true) {
            delevent.setVisibility(View.VISIBLE);

            delevent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new android.support.v7.app.AlertDialog.Builder(AddNewEventActivity.this)
                            .setTitle(getString(R.string.message_title))
                            .setMessage(getString(R.string.user_del_event))
                            .setPositiveButton(getString(R.string.msg_confirm), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    final ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(AddNewEventActivity.this
                                            , getString(R.string.webview_loading_title)
                                            , getString(R.string.msg_tokenget), false, true);
                                    pb.show();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                while (true) {
                                                    Log.i(TAG, "manual upload data");
                                                    JSONObject reUpdateResponse = webServiceConnection.delCaleinfo(prf.getString("access_token", ""), eventPK);
                                                    //                    if (reUpdateResponse != null && reUpdateResponse.equals("{\"Message\" : \"A01\"}")) {
                                                    if (reUpdateResponse != null && reUpdateResponse.getString("message").toString().equals("Success.")) {
                                                        // 更新sql lite資料庫
                                                        ScheduleDAO scheduleDAO = new ScheduleDAO(AddNewEventActivity.this);
                                                        scheduleDAO.delete_MsgPk(del_PKMark, object);
                                                        Looper.prepare();
                                                        Toast.makeText(AddNewEventActivity.this, getString(R.string.msg_net_reUpdate), Toast.LENGTH_LONG).show();

                                                        //                                                            flag = "del";
                                                        //                                                            Fragment fragment = new MyHealthCalendarFragment();
                                                        //
                                                        //                                                            getChildFragmentManager().beginTransaction()
                                                        //                                                                    .replace(R.id.dialog_main, fragment)
                                                        //                                                                            //                                .addToBackStack(null)
                                                        //                                                                    .commit();
                                                        //
                                                        //                                                            //                                            dismiss();
                                                        //
                                                        //                                                            onDestroyView();
                                                        pb.dismiss();
                                                        finish();
                                                        Looper.loop();
                                                        break;
                                                    } else {
                                                        Looper.prepare();
                                                        Toast.makeText(AddNewEventActivity.this, getString(R.string.msg_net_faild), Toast.LENGTH_LONG).show();
                                                        Looper.loop();
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                }
                            })
                            .setNegativeButton(getString(R.string.msg_cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //                                        dialog.dismiss();
                                    return;
                                }
                            }).show();
                }
            });
        }


        // 事件類型選擇的監聽
        selectClassify_event.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        eventType = "0";
                        newEventName.setHint(getResources().getString(R.string.new_Clinic_name_hint));
                        break;
                    case 1:
                        eventType = "1";
                        newEventName.setHint(getResources().getString(R.string.new_Consultation_name_hint));
                        break;
                    case 2:
                        eventType = "2";
                        newEventName.setHint(getResources().getString(R.string.new_Operation_name_hint));
                        break;
                    case 3:
                        eventType = "3";
                        newEventName.setHint(getResources().getString(R.string.new_Meeting_name_hint));
                        break;
                    case 4:
                        eventType = "4";
                        newEventName.setHint(getResources().getString(R.string.new_Personal_name_hint));
                        break;
                    case 5:
                        eventType = "5";
                        newEventName.setHint(getResources().getString(R.string.new_Examination_name_hint));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(AddNewEventActivity.this, getResources().getString(R.string.new_event_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 初始化完成按鈕與取消按鈕的監聽器
     */
    private void initHandler(final Bundle arguments) {
        //取消按鈕
        newEventCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelAlertDialog();
            }
        });

        //完成按鈕
        newEventCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newEventName.getText().toString().equals("")) {
                    webServiceConnection.editTextAlertDialog(AddNewEventActivity.this, getResources().getString(R.string.new_event_alert_name));
                    //                } else if (newEventLocation.getText().toString().equals("")) {
                    //                    editTextAlertDialog(getResources().getString(R.string.new_event_alert_location));
                } else if (startDate.getText().toString().equals("")) {
                    webServiceConnection.editTextAlertDialog(AddNewEventActivity.this, getResources().getString(R.string.new_event_alert_start_time));
                } else if (endDate.getText().toString().equals("")) {
                    webServiceConnection.editTextAlertDialog(AddNewEventActivity.this, getResources().getString(R.string.new_event_alert_end_time));
                } else if (endCalendar.getTimeInMillis() < startCalendar.getTimeInMillis()) {
                    webServiceConnection.editTextAlertDialog(AddNewEventActivity.this, getResources().getString(R.string.new_event_wrong_date));
                } else {
                    StringBuilder StartDayBuilder = new StringBuilder();
                    Log.v("startCalendar", startCalendar.toString());
                    StartDayBuilder.append(startCalendar.get(Calendar.YEAR));
                    if ((startCalendar.get(Calendar.MONTH) + 1) < 10) {
                        StartDayBuilder.append("0");
                    }
                    StartDayBuilder.append(startCalendar.get(Calendar.MONTH) + 1);
                    if ((startCalendar.get(Calendar.DATE)) < 10) {
                        StartDayBuilder.append("0");
                    }
                    StartDayBuilder.append(startCalendar.get(Calendar.DATE));
                    Log.v("StartDayBuilder", StartDayBuilder.toString());

                    StringBuilder endDayBuilder = new StringBuilder();
                    Log.v("endCalendar", endCalendar.toString());
                    endDayBuilder.append(endCalendar.get(Calendar.YEAR));
                    if ((endCalendar.get(Calendar.MONTH) + 1) < 10) {
                        endDayBuilder.append("0");
                    }
                    endDayBuilder.append(endCalendar.get(Calendar.MONTH) + 1);
                    if ((endCalendar.get(Calendar.DATE)) < 10) {
                        endDayBuilder.append("0");
                    }
                    endDayBuilder.append(endCalendar.get(Calendar.DATE));
                    Log.v("endDayBuilder", endDayBuilder.toString());

                    //                    int days = endCalendar.get(Calendar.DATE) - startCalendar.get(Calendar.DATE);
                    //                    Log.v("days", String.valueOf(days));

                    String days = DateCompare(endDayBuilder.toString(), StartDayBuilder.toString());
                    Log.v("days", days);

                    switch (eventType) {
                        case "0":
                            if (Integer.parseInt(days) == 0) {
                                insertClinicData(getTimeRecordString(), 1);
                            } else {
                                flag_Clinic = true;
                                for (int i = 0; i <= Integer.parseInt(days); i++) {
                                    if (alertDialog != null) {
                                        alertDialog.dismiss();
                                    }
                                    insertClinicData(Integer.parseInt(howManyDays(StartDayBuilder.toString(), i)), 0);
                                }
                            }
                            break;
                        case "1":
                            if (Integer.parseInt(days) == 0) {
                                insertConsultationData(getTimeRecordString(), 1);
                            } else {
                                flag_Consultation = true;
                                for (int i = 0; i <= Integer.parseInt(days); i++) {
                                    insertConsultationData(Integer.parseInt(howManyDays(StartDayBuilder.toString(), i)), 0);
                                }
                            }
                            break;
                        case "2":
                            if (Integer.parseInt(days) == 0) {
                                insertOperationData(getTimeRecordString(), 1);
                            } else {
                                flag_Operation = true;
                                for (int i = 0; i <= Integer.parseInt(days); i++) {
                                    if (alertDialog != null) {
                                        alertDialog.dismiss();
                                    }
                                    insertOperationData(Integer.parseInt(howManyDays(StartDayBuilder.toString(), i)), 0);
                                }
                            }
                            break;
                        case "3":
                            if (Integer.parseInt(days) == 0) {
                                insertMeetingData(getTimeRecordString(), 1);
                            } else {
                                flag_Meeting = true;
                                for (int i = 0; i <= Integer.parseInt(days); i++) {
                                    if (alertDialog != null) {
                                        alertDialog.dismiss();
                                    }
                                    insertMeetingData(Integer.parseInt(howManyDays(StartDayBuilder.toString(), i)), 0);
                                }
                            }
                            break;
                        case "4":
                            if (Integer.parseInt(days) == 0) {
                                insertPersonalData(getTimeRecordString(), 1);
                            } else {
                                flag_Personal = true;
                                for (int i = 0; i <= Integer.parseInt(days); i++) {
                                    if (alertDialog != null) {
                                        alertDialog.dismiss();
                                    }
                                    insertPersonalData(Integer.parseInt(howManyDays(StartDayBuilder.toString(), i)), 0);
                                }
                            }
                            break;
                        case "5":
                            if (Integer.parseInt(days) == 0) {
                                insertExaminationData(getTimeRecordString(), 1);
                            } else {
                                flag_Examination = true;
                                for (int i = 0; i <= Integer.parseInt(days); i++) {
                                    if (alertDialog != null) {
                                        alertDialog.dismiss();
                                    }
                                    insertExaminationData(Integer.parseInt(howManyDays(StartDayBuilder.toString(), i)), 0);
                                }
                            }
                            break;
                    }
                }
            }
        });

        //起始時間按鈕
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(startDate, arguments);
            }
        });

        //結束時間按鈕
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker(endDate, arguments);
            }
        });
    }

    /**
     * 計算並回傳開始與結束日期的間距
     *
     * @return int
     */
    //    private int howManyDays() {
    //        StringBuilder dayStringBuilder = new StringBuilder();
    //        dayStringBuilder.delete(0, dayStringBuilder.length());
    //        if (startCalendar.get(Calendar.DATE) < 10) {
    //            dayStringBuilder.append("0");
    //        }
    //        dayStringBuilder.append(startCalendar.get(Calendar.DATE));
    //        timeRecordString.replace(6, timeRecordString.length(), dayStringBuilder.toString());
    //        startCalendar.add(Calendar.DATE, 1);
    //        Log.v("timeRecordString", timeRecordString.toString());
    //        return Integer.valueOf(timeRecordString.toString());
    //    }
    private String howManyDays(String StartDate, int day) {
        String timeStr = null;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        try {
            Date date = formatter.parse(StartDate);
            Log.v("date", date.toString());
            date.setDate(date.getDate() + day);
            timeStr = formatter.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("timeStr", timeStr);
        return timeStr;
    }

    /**
     * 寫入 一般記事 的資料到資料庫
     *
     * @param timeRecord 時間的紀錄
     * @param oneDay     判斷是否跨日，用1和0做判斷
     */
    private void insertClinicData(int timeRecord, int oneDay) {
        final Clinic clinic = new Clinic();
        clinic.setClinicName(newEventName.getText().toString()); //行程名稱
        if (newEventLocation.getText().toString().trim() != "") {
            clinic.setClinicLocation(newEventLocation.getText().toString()); //行程地點
        } else {
            clinic.setClinicLocation(""); //行程地點
        }
        clinic.setClinicDate(timeRecord); //時間紀錄
        clinic.setClinicStartDate(startDate.getText().toString()); //起始日期
        clinic.setClinicEndDate(endDate.getText().toString()); //結束日期
        clinic.setClinicStartTime(startClockTime.toString()); //起始時間
        clinic.setClinicEndTime(endClockTime.toString());  //結束時間
        clinic.setClinicNote(newEventNote.getText().toString()); //備註
        clinic.setClinicEventTypePk("23");
        clinic.setClinicOneDay(oneDay); //是否跨日

        final ScheduleDAO scheduleDAO = new ScheduleDAO(AddNewEventActivity.this);
        try {
            if (flag_edit == true) {
                clinic.setClinicMsgPk(eventPK);

                if (flag_Clinic == true) {
                    switch (del_PKMark) {
                        case ScheduleDAO.TABLE_CLINIC:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_CLINIC, clinicObj);
                            break;
                        case ScheduleDAO.TABLE_CONSULTATION:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_CONSULTATION, consultationObj);
                            break;
                        case ScheduleDAO.TABLE_OPERATE:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_OPERATE, operationObj);
                            break;
                        case ScheduleDAO.TABLE_MEETING:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_MEETING, meetingObj);
                            break;
                        case ScheduleDAO.TABLE_PERSONAL:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_PERSONAL, personalObj);
                            break;
                        case ScheduleDAO.TABLE_EXAMINATION:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_EXAMINATION, examinationObj);
                            break;
                    }
                    flag_Clinic = false;
                }
            } else {
                clinic.setClinicMsgPk("1");
            }
            scheduleDAO.insert(ScheduleDAO.TABLE_CLINIC, clinic);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddNewEventActivity.this);
        //        new AlertDialog.Builder(AddNewEventActivity.this)
        dialogBuilder.setTitle(getString(R.string.message_title))
                .setMessage(getString(R.string.user_save_event))
                .setPositiveButton(getString(R.string.msg_confirm),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                final ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(AddNewEventActivity.this
                                        , getString(R.string.webview_loading_title)
                                        , getString(R.string.msg_tokenget), false, true);
                                pb.show();

                                //        final NetService netService = new NetService();
                                //        final ArrayList<BioData> listBioData = bioDataAdapter.getUploaded();
                                final Object object = scheduleDAO.get(ScheduleDAO.TABLE_CLINIC, clinic);

                                Log.v(TAG, clinic.getClinicMsgPk());
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            while (true) {
                                                Log.i(TAG, "manual upload data");
                                                //                    String reUpdateResponse = netService.CallUploadVitalSign(user, listBioData, false);
                                                JSONObject reUpdateResponse = webServiceConnection.editCaleinfo(prf.getString("access_token", ""), object, clinic.getClinicEventTypePk());
                                                //                    if (reUpdateResponse != null && reUpdateResponse.equals("{\"Message\" : \"A01\"}")) {
                                                if (reUpdateResponse != null && reUpdateResponse.getString("message").toString().equals("Success.")) {
                                                    // 更新sql lite資料庫

                                                    Clinic clinicdata = (Clinic) object;
                                                    String msgPK = clinicdata.getClinicMsgPk();

                                                    clinicdata.setClinicMsgPk(reUpdateResponse.getJSONObject("result").getString("calendarEventPK"));
                                                    scheduleDAO.update_msgPK(ScheduleDAO.TABLE_CLINIC, clinicdata, msgPK);

                                                    //  bioDataAdapter.updataUploaded(listBioData);
                                                    //  Log.i(TAG, "資料重新上傳成功");
                                                    Looper.prepare();
                                                    Toast.makeText(AddNewEventActivity.this, getString(R.string.msg_net_reUpdate), Toast.LENGTH_LONG).show();
                                                    Looper.loop();
                                                    break;
                                                } else {
                                                    Looper.prepare();
                                                    Toast.makeText(AddNewEventActivity.this, getString(R.string.msg_net_faild), Toast.LENGTH_LONG).show();
                                                    Looper.loop();
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();

                                try {
                                    Thread.sleep(500);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    //返回行事曆檢視畫面
                                    pb.dismiss();
                                    finish();
                                }
                            }
                            //                        }).show();
                        });
        alertDialog = dialogBuilder.show();
    }

    /**
     * 寫入 重要活動 的資料到資料庫
     *
     * @param timeRecord 時間的紀錄
     * @param oneDay     判斷是否跨日，用1和0做判斷
     */
    private void insertConsultationData(int timeRecord, int oneDay) {
        final Consultation consultation = new Consultation();
        consultation.setConsultationName(newEventName.getText().toString()); //行程名稱
        if (newEventLocation.getText().toString().trim() != "") {
            consultation.setConsultationLocation(newEventLocation.getText().toString()); //行程地點
        } else {
            consultation.setConsultationLocation(""); //行程地點
        }
        consultation.setConsultationDate(timeRecord); //時間紀錄
        consultation.setConsultationStartDate(startDate.getText().toString()); //起始日期
        consultation.setConsultationEndDate(endDate.getText().toString()); //結束日期
        consultation.setConsultationStartTime(startClockTime.toString()); //起始時間
        consultation.setConsultationEndTime(endClockTime.toString());  //結束時間
        consultation.setConsultationNote(newEventNote.getText().toString()); //備註
        consultation.setConsultationEventTypePk("24");
        consultation.setConsultationOneDay(oneDay); //是否跨日

        final ScheduleDAO scheduleDAO = new ScheduleDAO(AddNewEventActivity.this);
        try {
            if (flag_edit == true) {
                consultation.setConsultationMsgPk(eventPK);

                if (flag_Consultation == true) {
                    switch (del_PKMark) {
                        case ScheduleDAO.TABLE_CLINIC:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_CLINIC, clinicObj);
                            break;
                        case ScheduleDAO.TABLE_CONSULTATION:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_CONSULTATION, consultationObj);
                            break;
                        case ScheduleDAO.TABLE_OPERATE:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_OPERATE, operationObj);
                            break;
                        case ScheduleDAO.TABLE_MEETING:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_MEETING, meetingObj);
                            break;
                        case ScheduleDAO.TABLE_PERSONAL:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_PERSONAL, personalObj);
                            break;
                        case ScheduleDAO.TABLE_EXAMINATION:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_EXAMINATION, examinationObj);
                            break;
                    }
                    flag_Consultation = false;
                }
            } else {
                consultation.setConsultationMsgPk("1");
            }
            scheduleDAO.insert(ScheduleDAO.TABLE_CONSULTATION, consultation);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddNewEventActivity.this);
        dialogBuilder.setTitle(getString(R.string.message_title))
                .setMessage(getString(R.string.user_save_event))
                .setPositiveButton(getString(R.string.msg_confirm),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                final ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(AddNewEventActivity.this
                                        , getString(R.string.webview_loading_title)
                                        , getString(R.string.msg_tokenget), false, true);
                                pb.show();
                                //        final NetService netService = new NetService();
                                //        final ArrayList<BioData> listBioData = bioDataAdapter.getUploaded();
                                final Object object = scheduleDAO.get(ScheduleDAO.TABLE_CONSULTATION, consultation);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            while (true) {
                                                Log.i(TAG, "manual upload data");
                                                //                    String reUpdateResponse = netService.CallUploadVitalSign(user, listBioData, false);
                                                JSONObject reUpdateResponse = webServiceConnection.editCaleinfo(prf.getString("access_token", ""), object, consultation.getConsultationEventTypePk());
                                                //                    if (reUpdateResponse != null && reUpdateResponse.equals("{\"Message\" : \"A01\"}")) {
                                                if (reUpdateResponse != null && reUpdateResponse.getString("message").toString().equals("Success.")) {
                                                    // 更新sql lite資料庫

                                                    Consultation consultationdata = (Consultation) object;
                                                    String msgPK = consultationdata.getConsultationMsgPk();

                                                    consultationdata.setConsultationMsgPk(reUpdateResponse.getJSONObject("result").getString("calendarEventPK"));
                                                    scheduleDAO.update_msgPK(ScheduleDAO.TABLE_CONSULTATION, consultationdata, msgPK);

                                                    //  bioDataAdapter.updataUploaded(listBioData);
                                                    //  Log.i(TAG, "資料重新上傳成功");
                                                    Looper.prepare();
                                                    Toast.makeText(AddNewEventActivity.this, getString(R.string.msg_net_reUpdate), Toast.LENGTH_LONG).show();
                                                    Looper.loop();
                                                    break;
                                                } else {
                                                    Looper.prepare();
                                                    Toast.makeText(AddNewEventActivity.this, getString(R.string.msg_net_faild), Toast.LENGTH_LONG).show();
                                                    Looper.loop();
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();

                                try {
                                    Thread.sleep(500);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    //返回行事曆檢視畫面
                                    pb.dismiss();
                                    finish();
                                }
                            }
                        });
        alertDialog = dialogBuilder.show();
    }

    /**
     * 寫入 量測提醒 的資料到資料庫
     *
     * @param timeRecord 時間的紀錄
     * @param oneDay     判斷是否跨日，用1和0做判斷
     */
    private void insertOperationData(int timeRecord, int oneDay) {
        final Operation operation = new Operation();
        operation.setOperateName(newEventName.getText().toString()); //行程名稱
        if (newEventLocation.getText().toString().trim() != "") {
            operation.setOperateLocation(newEventLocation.getText().toString()); //行程地點
        } else {
            operation.setOperateLocation(""); //行程地點
        }
        operation.setOperateDate(timeRecord); //時間紀錄
        operation.setOperateStartDate(startDate.getText().toString()); //起始日期
        operation.setOperateEndDate(endDate.getText().toString()); //結束日期
        operation.setOperateStartTime(startClockTime.toString()); //起始時間
        operation.setOperateEndTime(endClockTime.toString());  //結束時間
        operation.setOperateNote(newEventNote.getText().toString()); //備註
        operation.setOperateEventTypePk("25");
        operation.setOperateOneDay(oneDay); //是否跨日

        final ScheduleDAO scheduleDAO = new ScheduleDAO(AddNewEventActivity.this);
        try {
            if (flag_edit == true) {
                operation.setOperateMsgPk(eventPK);

                if (flag_Operation == true) {
                    switch (del_PKMark) {
                        case ScheduleDAO.TABLE_CLINIC:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_CLINIC, clinicObj);
                            break;
                        case ScheduleDAO.TABLE_CONSULTATION:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_CONSULTATION, consultationObj);
                            break;
                        case ScheduleDAO.TABLE_OPERATE:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_OPERATE, operationObj);
                            break;
                        case ScheduleDAO.TABLE_MEETING:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_MEETING, meetingObj);
                            break;
                        case ScheduleDAO.TABLE_PERSONAL:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_PERSONAL, personalObj);
                            break;
                        case ScheduleDAO.TABLE_EXAMINATION:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_EXAMINATION, examinationObj);
                            break;
                    }
                    flag_Operation = false;
                }
            } else {
                operation.setOperateMsgPk("1");
            }
            scheduleDAO.insert(ScheduleDAO.TABLE_OPERATE, operation);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddNewEventActivity.this);
        dialogBuilder.setTitle(getString(R.string.message_title))
                .setMessage(getString(R.string.user_save_event))
                .setPositiveButton(getString(R.string.msg_confirm),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                final ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(AddNewEventActivity.this
                                        , getString(R.string.webview_loading_title)
                                        , getString(R.string.msg_tokenget), false, true);
                                pb.show();
                                //        final NetService netService = new NetService();
                                //        final ArrayList<BioData> listBioData = bioDataAdapter.getUploaded();
                                final Object object = scheduleDAO.get(ScheduleDAO.TABLE_OPERATE, operation);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            while (true) {
                                                Log.i(TAG, "manual upload data");
                                                //                    String reUpdateResponse = netService.CallUploadVitalSign(user, listBioData, false);
                                                JSONObject reUpdateResponse = webServiceConnection.editCaleinfo(prf.getString("access_token", ""), object, operation.getOperateEventTypePk());
                                                //                    if (reUpdateResponse != null && reUpdateResponse.equals("{\"Message\" : \"A01\"}")) {
                                                if (reUpdateResponse != null && reUpdateResponse.getString("message").toString().equals("Success.")) {
                                                    // 更新sql lite資料庫

                                                    Operation objectdata = (Operation) object;
                                                    String msgPK = objectdata.getOperateMsgPk();

                                                    objectdata.setOperateMsgPk(reUpdateResponse.getJSONObject("result").getString("calendarEventPK"));
                                                    scheduleDAO.update_msgPK(ScheduleDAO.TABLE_OPERATE, objectdata, msgPK);

                                                    //  bioDataAdapter.updataUploaded(listBioData);
                                                    //  Log.i(TAG, "資料重新上傳成功");
                                                    Looper.prepare();
                                                    Toast.makeText(AddNewEventActivity.this, getString(R.string.msg_net_reUpdate), Toast.LENGTH_LONG).show();
                                                    Looper.loop();
                                                    break;
                                                } else {
                                                    Looper.prepare();
                                                    Toast.makeText(AddNewEventActivity.this, getString(R.string.msg_net_faild), Toast.LENGTH_LONG).show();
                                                    Looper.loop();
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();

                                try {
                                    Thread.sleep(500);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    //返回行事曆檢視畫面
                                    pb.dismiss();
                                    finish();
                                }
                            }
                        });
        alertDialog = dialogBuilder.show();
    }

    /**
     * 寫入 用藥提醒 的資料到資料庫
     *
     * @param timeRecord 時間的紀錄
     * @param oneDay     判斷是否跨日，用1和0做判斷
     */
    private void insertMeetingData(int timeRecord, int oneDay) {
        final Meeting meeting = new Meeting();
        meeting.setMeetingName(newEventName.getText().toString()); //行程名稱
        if (newEventLocation.getText().toString().trim() != "") {
            meeting.setMeetingLocation(newEventLocation.getText().toString()); //行程地點
        } else {
            meeting.setMeetingLocation(""); //行程地點
        }
        meeting.setMeetingDate(timeRecord); //時間紀錄
        meeting.setMeetingStartDate(startDate.getText().toString()); //起始日期
        meeting.setMeetingEndDate(endDate.getText().toString()); //結束日期
        meeting.setMeetingStartTime(startClockTime.toString()); //起始時間
        meeting.setMeetingEndTime(endClockTime.toString());  //結束時間
        meeting.setMeetingNote(newEventNote.getText().toString()); //備註
        meeting.setMeetingEventTypePk("26");
        meeting.setMeetingOneDay(oneDay); //是否跨日

        final ScheduleDAO scheduleDAO = new ScheduleDAO(AddNewEventActivity.this);
        try {
            if (flag_edit == true) {
                meeting.setMeetingMsgPk(eventPK);

                if (flag_Meeting == true) {
                    switch (del_PKMark) {
                        case ScheduleDAO.TABLE_CLINIC:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_CLINIC, clinicObj);
                            break;
                        case ScheduleDAO.TABLE_CONSULTATION:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_CONSULTATION, consultationObj);
                            break;
                        case ScheduleDAO.TABLE_OPERATE:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_OPERATE, operationObj);
                            break;
                        case ScheduleDAO.TABLE_MEETING:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_MEETING, meetingObj);
                            break;
                        case ScheduleDAO.TABLE_PERSONAL:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_PERSONAL, personalObj);
                            break;
                        case ScheduleDAO.TABLE_EXAMINATION:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_EXAMINATION, examinationObj);
                            break;
                    }
                    flag_Meeting = false;
                }
            } else {
                meeting.setMeetingMsgPk("1");
            }
            scheduleDAO.insert(ScheduleDAO.TABLE_MEETING, meeting);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddNewEventActivity.this);
        dialogBuilder.setTitle(getString(R.string.message_title))
                .setMessage(getString(R.string.user_save_event))
                .setPositiveButton(getString(R.string.msg_confirm),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                final ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(AddNewEventActivity.this
                                        , getString(R.string.webview_loading_title)
                                        , getString(R.string.msg_tokenget), false, true);
                                pb.show();
                                //        final NetService netService = new NetService();
                                //        final ArrayList<BioData> listBioData = bioDataAdapter.getUploaded();
                                final Object object = scheduleDAO.get(ScheduleDAO.TABLE_MEETING, meeting);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            while (true) {
                                                Log.i(TAG, "manual upload data");
                                                //                    String reUpdateResponse = netService.CallUploadVitalSign(user, listBioData, false);
                                                JSONObject reUpdateResponse = webServiceConnection.editCaleinfo(prf.getString("access_token", ""), object, meeting.getMeetingEventTypePk());
                                                //                    if (reUpdateResponse != null && reUpdateResponse.equals("{\"Message\" : \"A01\"}")) {
                                                if (reUpdateResponse != null && reUpdateResponse.getString("message").toString().equals("Success.")) {
                                                    // 更新sql lite資料庫

                                                    Meeting meetingdata = (Meeting) object;
                                                    String msgPK = meetingdata.getMeetingMsgPk();

                                                    meetingdata.setMeetingMsgPk(reUpdateResponse.getJSONObject("result").getString("calendarEventPK"));
                                                    scheduleDAO.update_msgPK(ScheduleDAO.TABLE_MEETING, meetingdata, msgPK);

                                                    //  bioDataAdapter.updataUploaded(listBioData);
                                                    //  Log.i(TAG, "資料重新上傳成功");
                                                    Looper.prepare();
                                                    Toast.makeText(AddNewEventActivity.this, getString(R.string.msg_net_reUpdate), Toast.LENGTH_LONG).show();
                                                    Looper.loop();
                                                    break;
                                                } else {
                                                    Looper.prepare();
                                                    Toast.makeText(AddNewEventActivity.this, getString(R.string.msg_net_faild), Toast.LENGTH_LONG).show();
                                                    Looper.loop();
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();

                                try {
                                    Thread.sleep(500);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    //返回行事曆檢視畫面
                                    pb.dismiss();
                                    finish();
                                }
                            }
                        });
        alertDialog = dialogBuilder.show();
    }

    /**
     * 寫入私人行程(預約看診)的資料到資料庫
     *
     * @param timeRecord 時間的紀錄
     * @param oneDay     判斷是否跨日，用1和0做判斷
     */
    private void insertPersonalData(int timeRecord, int oneDay) {
        final Personal personal = new Personal();
        personal.setPersonalName(newEventName.getText().toString()); //行程名稱
        if (newEventLocation.getText().toString().trim() != "") {
            personal.setPersonalLocation(newEventLocation.getText().toString()); //行程地點
        } else {
            personal.setPersonalLocation(""); //行程地點
        }
        personal.setPersonalDate(timeRecord); //時間紀錄
        personal.setPersonalStartDate(startDate.getText().toString()); //起始日期
        personal.setPersonalEndDate(endDate.getText().toString()); //結束日期
        personal.setPersonalStartTime(startClockTime.toString()); //起始時間
        personal.setPersonalEndTime(endClockTime.toString());  //結束時間
        personal.setPersonalNote(newEventNote.getText().toString()); //備註
        personal.setPersonalEventTypePk("27");
        personal.setPersonalOneDay(oneDay); //是否跨日

        final ScheduleDAO scheduleDAO = new ScheduleDAO(AddNewEventActivity.this);
        try {
            if (flag_edit == true) {
                personal.setPersonalMsgPk(eventPK);

                if (flag_Personal == true) {
                    switch (del_PKMark) {
                        case ScheduleDAO.TABLE_CLINIC:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_CLINIC, clinicObj);
                            break;
                        case ScheduleDAO.TABLE_CONSULTATION:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_CONSULTATION, consultationObj);
                            break;
                        case ScheduleDAO.TABLE_OPERATE:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_OPERATE, operationObj);
                            break;
                        case ScheduleDAO.TABLE_MEETING:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_MEETING, meetingObj);
                            break;
                        case ScheduleDAO.TABLE_PERSONAL:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_PERSONAL, personalObj);
                            break;
                        case ScheduleDAO.TABLE_EXAMINATION:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_EXAMINATION, examinationObj);
                            break;
                    }
                    flag_Personal = false;
                }
            } else {
                personal.setPersonalMsgPk("1");
            }
            scheduleDAO.insert(ScheduleDAO.TABLE_PERSONAL, personal);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddNewEventActivity.this);
        dialogBuilder.setTitle(getString(R.string.message_title))
                .setMessage(getString(R.string.user_save_event))
                .setPositiveButton(getString(R.string.msg_confirm),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                final ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(AddNewEventActivity.this
                                        , getString(R.string.webview_loading_title)
                                        , getString(R.string.msg_tokenget), false, true);
                                pb.show();
                                //        final NetService netService = new NetService();
                                //        final ArrayList<BioData> listBioData = bioDataAdapter.getUploaded();
                                final Object object = scheduleDAO.get(ScheduleDAO.TABLE_PERSONAL, personal);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            while (true) {
                                                Log.i(TAG, "manual upload data");
                                                //                    String reUpdateResponse = netService.CallUploadVitalSign(user, listBioData, false);
                                                JSONObject reUpdateResponse = webServiceConnection.editCaleinfo(prf.getString("access_token", ""), object, personal.getPersonalEventTypePk());
                                                //                    if (reUpdateResponse != null && reUpdateResponse.equals("{\"Message\" : \"A01\"}")) {
                                                if (reUpdateResponse != null && reUpdateResponse.getString("message").toString().equals("Success.")) {
                                                    // 更新sql lite資料庫

                                                    Personal personaldata = (Personal) object;
                                                    String msgPK = personaldata.getPersonalMsgPk();

                                                    personaldata.setPersonalMsgPk(reUpdateResponse.getJSONObject("result").getString("calendarEventPK"));
                                                    scheduleDAO.update_msgPK(ScheduleDAO.TABLE_PERSONAL, personaldata, msgPK);

                                                    //  bioDataAdapter.updataUploaded(listBioData);
                                                    //  Log.i(TAG, "資料重新上傳成功");
                                                    Looper.prepare();
                                                    Toast.makeText(AddNewEventActivity.this, getString(R.string.msg_net_reUpdate), Toast.LENGTH_LONG).show();
                                                    Looper.loop();
                                                    break;
                                                } else {
                                                    Looper.prepare();
                                                    Toast.makeText(AddNewEventActivity.this, getString(R.string.msg_net_faild), Toast.LENGTH_LONG).show();
                                                    Looper.loop();
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();

                                try {
                                    Thread.sleep(500);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    //返回行事曆檢視畫面
                                    pb.dismiss();
                                    finish();
                                }
                            }
                        });
        alertDialog = dialogBuilder.show();
    }

    /**
     * 寫入 健康檢查 的資料到資料庫
     *
     * @param timeRecord 時間的紀錄
     * @param oneDay     判斷是否跨日，用1和0做判斷
     */
    private void insertExaminationData(int timeRecord, int oneDay) {
        final Examination examination = new Examination();
        examination.setExaminationName(newEventName.getText().toString()); //行程名稱
        if (newEventLocation.getText().toString().trim() != "") {
            examination.setExaminationLocation(newEventLocation.getText().toString()); //行程地點
        } else {
            examination.setExaminationLocation(""); //行程地點
        }
        examination.setExaminationDate(timeRecord); //時間紀錄
        examination.setExaminationStartDate(startDate.getText().toString()); //起始日期
        examination.setExaminationEndDate(endDate.getText().toString()); //結束日期
        examination.setExaminationStartTime(startClockTime.toString()); //起始時間
        examination.setExaminationEndTime(endClockTime.toString());  //結束時間
        examination.setExaminationNote(newEventNote.getText().toString()); //備註
        examination.setExaminationEventTypePk("28");
        examination.setExaminationOneDay(oneDay); //是否跨日

        final ScheduleDAO scheduleDAO = new ScheduleDAO(AddNewEventActivity.this);
        try {
            if (flag_edit == true) {
                examination.setExaminationMsgPk(eventPK);

                if (flag_Examination == true) {
                    switch (del_PKMark) {
                        case ScheduleDAO.TABLE_CLINIC:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_CLINIC, clinicObj);
                            break;
                        case ScheduleDAO.TABLE_CONSULTATION:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_CONSULTATION, consultationObj);
                            break;
                        case ScheduleDAO.TABLE_OPERATE:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_OPERATE, operationObj);
                            break;
                        case ScheduleDAO.TABLE_MEETING:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_MEETING, meetingObj);
                            break;
                        case ScheduleDAO.TABLE_PERSONAL:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_PERSONAL, personalObj);
                            break;
                        case ScheduleDAO.TABLE_EXAMINATION:
                            scheduleDAO.delete_MsgPk(ScheduleDAO.TABLE_EXAMINATION, examinationObj);
                            break;
                    }
                    flag_Examination = false;
                }
            } else {
                examination.setExaminationMsgPk("1");
            }
            scheduleDAO.insert(ScheduleDAO.TABLE_EXAMINATION, examination);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddNewEventActivity.this);
        dialogBuilder.setTitle(getString(R.string.message_title))
                .setMessage(getString(R.string.user_save_event))
                .setPositiveButton(getString(R.string.msg_confirm),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                final ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(AddNewEventActivity.this
                                        , getString(R.string.webview_loading_title)
                                        , getString(R.string.msg_tokenget), false, true);
                                pb.show();
                                //        final NetService netService = new NetService();
                                //        final ArrayList<BioData> listBioData = bioDataAdapter.getUploaded();
                                final Object object = scheduleDAO.get(ScheduleDAO.TABLE_EXAMINATION, examination);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            while (true) {
                                                Log.i(TAG, "manual upload data");
                                                //                    String reUpdateResponse = netService.CallUploadVitalSign(user, listBioData, false);
                                                JSONObject reUpdateResponse = webServiceConnection.editCaleinfo(prf.getString("access_token", ""), object, examination.getExaminationEventTypePk());
                                                //                    if (reUpdateResponse != null && reUpdateResponse.equals("{\"Message\" : \"A01\"}")) {
                                                if (reUpdateResponse != null && reUpdateResponse.getString("message").toString().equals("Success.")) {
                                                    // 更新sql lite資料庫

                                                    Examination examinationdata = (Examination) object;
                                                    String msgPK = examinationdata.getExaminationMsgPk();

                                                    examinationdata.setExaminationMsgPk(reUpdateResponse.getJSONObject("result").getString("calendarEventPK"));
                                                    scheduleDAO.update_msgPK(ScheduleDAO.TABLE_EXAMINATION, examinationdata, msgPK);

                                                    //  bioDataAdapter.updataUploaded(listBioData);
                                                    //  Log.i(TAG, "資料重新上傳成功");
                                                    Looper.prepare();
                                                    Toast.makeText(AddNewEventActivity.this, getString(R.string.msg_net_reUpdate), Toast.LENGTH_LONG).show();
                                                    Looper.loop();
                                                    break;
                                                } else {
                                                    Looper.prepare();
                                                    Toast.makeText(AddNewEventActivity.this, getString(R.string.msg_net_faild), Toast.LENGTH_LONG).show();
                                                    Looper.loop();
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();

                                try {
                                    Thread.sleep(500);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    //返回行事曆檢視畫面
                                    pb.dismiss();
                                    finish();
                                }
                            }
                        });
        alertDialog = dialogBuilder.show();
    }

    /**
     * 串接新增私人行程的日期提示字串
     *
     * @param hintCalendar 紀錄當日時間用的Calendar物件
     * @return String
     */
    private String getDateHint(Calendar hintCalendar) {
        StringBuilder hintBuilder = new StringBuilder();
        hintBuilder.append(hintCalendar.get(Calendar.YEAR));
        hintBuilder.append(" / ");
        if ((hintCalendar.get(Calendar.MONTH) + 1) < 10) {
            hintBuilder.append("0");
        }
        hintBuilder.append(hintCalendar.get(Calendar.MONTH) + 1);
        hintBuilder.append(" / ");
        if ((hintCalendar.get(Calendar.DATE)) < 10) {
            hintBuilder.append("0");
        }
        hintBuilder.append(hintCalendar.get(Calendar.DATE));
        hintBuilder.append(" ");
        if ((hintCalendar.get(Calendar.HOUR_OF_DAY)) < 10) {
            hintBuilder.append("0");
        }
        hintBuilder.append(hintCalendar.get(Calendar.HOUR_OF_DAY));
        hintBuilder.append(":");
        if ((hintCalendar.get(Calendar.MINUTE)) < 10) {
            hintBuilder.append("0");
        }
        hintBuilder.append(hintCalendar.get(Calendar.MINUTE));
        if (hintCalendar.get(Calendar.AM_PM) == 0) {
            hintBuilder.append(" AM");
        } else {
            hintBuilder.append(" PM");
        }

        return hintBuilder.toString();
    }

    /**
     * 設置私人行程日期
     *
     * @param dateText 私人行程日期
     */
    private void datePicker(final TextView dateText, Bundle arguments) {
        Calendar hintCalendar = Calendar.getInstance();
        hintCalendar.set(
                arguments.getInt("StartedYear"),
                arguments.getInt("StartedMonth"),
                arguments.getInt("StartedDate")
        );
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            boolean mFired = false;

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if (mFired == true) {
                    Log.v("datePickerDialog", "Double fire occured. Silently-ish returning");
                    return;
                } else {
                    //first time mFired
                    mFired = true;

                    StringBuilder dateStringBuilder = new StringBuilder();
                    dateStringBuilder.append(year);
                    dateStringBuilder.append(" / ");
                    //月份小於10補0
                    if ((monthOfYear + 1) < 10) {
                        dateStringBuilder.append("0");
                    }
                    dateStringBuilder.append((monthOfYear + 1));
                    dateStringBuilder.append(" / ");
                    //日期小於10補0
                    if (dayOfMonth < 10) {
                        dateStringBuilder.append("0");
                    }
                    dateStringBuilder.append(dayOfMonth);
                    dateStringBuilder.append(" ");
                    timePicker(dateText, dateStringBuilder);
                    //紀錄年月日
                    if (dateText.equals(startDate)) {
                        startCalendar.set(year, monthOfYear, dayOfMonth);
                    } else {
                        endCalendar.set(year, monthOfYear, dayOfMonth);
                    }
                }
            }
        }, hintCalendar.get(Calendar.YEAR), hintCalendar.get(Calendar.MONTH), hintCalendar.get(Calendar.DATE));
        datePickerDialog.setCanceledOnTouchOutside(false);
        datePickerDialog.show();
    }

    /**
     * 設置私人行程時間
     *
     * @param dateText          私人行程時間
     * @param timeStringBuilder 私人行程時間字串
     */
    private void timePicker(final TextView dateText, final StringBuilder timeStringBuilder) {
        Calendar calendar = Calendar.getInstance();
        final int second = calendar.get(Calendar.SECOND);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            boolean mFired = false;

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (mFired == true) {
                    Log.v("timePickerDialog", "Double fire occured. Silently-ish returning");
                    return;
                } else {
                    //first time mFired
                    mFired = true;
                    //                int militaryTimeHour = hourOfDay;
                    //                if (hourOfDay > 12) {   //12小時制換算
                    //                    militaryTimeHour = hourOfDay - 12;
                    //                }
                    //                if (militaryTimeHour < 10) {   //小時 < 10點，補0
                    //                    timeStringBuilder.append("0");
                    //                }
                    if (hourOfDay < 10) {   //小時 < 10點，補0
                        timeStringBuilder.append("0");
                    }
                    timeStringBuilder.append(hourOfDay);
                    timeStringBuilder.append(":");
                    if (minute < 10) {   //分鐘 < 10分，補0
                        timeStringBuilder.append("0");
                    }
                    timeStringBuilder.append(minute);
                    //                    timeStringBuilder.append(":");
                    //                    if (second < 10) {   //秒數 < 10分，補0
                    //                        timeStringBuilder.append("0");
                    //                    }
                    //                    timeStringBuilder.append(second);
                    if (hourOfDay >= 12) {   //判斷上下午
                        timeStringBuilder.append(" PM");
                    } else {
                        timeStringBuilder.append(" AM");
                    }
                    //紀錄時、分、時制
                    if (dateText.equals(startDate)) {
                        startCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        startCalendar.set(Calendar.MINUTE, minute);
                        startCalendar.set(Calendar.SECOND, second);
                    } else {
                        endCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        endCalendar.set(Calendar.MINUTE, minute);
                        endCalendar.set(Calendar.SECOND, second);
                    }
                    dateText.setText(timeStringBuilder.toString());
                    Log.v("timeStringBuilder", timeStringBuilder.toString());
                    setTimeRecord(dateText, timeStringBuilder.substring(15));
                }
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(getApplication()));
        timePickerDialog.setCanceledOnTouchOutside(false);
        timePickerDialog.show();
    }

    /**
     * 取得時間紀錄
     *
     * @return int
     */
    private int getTimeRecordString() {
        return Integer.valueOf(timeRecordString.toString());
    }

    /**
     * 設置時間紀錄
     *
     * @param dateText 時間紀錄
     */
    private void setTimeRecord(TextView dateText, String clockTime) {
        String timeRecord = dateText.getText().toString();
        timeRecord = timeRecord.replace(" ", "");
        String timeRecordYear = timeRecord.substring(0, 4);
        String timeRecordMonth = timeRecord.substring(5, 7);
        String timeRecordDate = timeRecord.substring(8, 10);
        if (dateText.equals(startDate)) {
            startClockTime.delete(0, startClockTime.length());
            //紀錄起始詳細時間
            Log.v("clockTime", clockTime);
            Log.v("clockTime_s", (clockTime.substring(clockTime.length() - 2, clockTime.length())));
            if (clockTime.substring(clockTime.length() - 2, clockTime.length()).equals("AM")) {
                startClockTime.append("上午 ");
            } else {
                startClockTime.append("下午 ");
            }
            startClockTime.append(clockTime.substring(0, 5));
            System.out.println("SHOW CLOCK / " + startClockTime);

            //初始化
            if (timeRecordString.length() > 0) {
                timeRecordString.delete(0, timeRecordString.length());
            }
            timeRecordString.append(timeRecordYear);
            timeRecordString.append(timeRecordMonth);
            timeRecordString.append(String.valueOf(timeRecordDate));
            System.out.println("TIMEEEEE" + timeRecordString);

        } else {
            //紀錄結束詳細時間
            endClockTime.delete(0, endClockTime.length());
            if (clockTime.substring(clockTime.length() - 2, clockTime.length()).equals("AM")) {
                endClockTime.append("上午 ");
            } else {
                endClockTime.append("下午 ");
            }
            endClockTime.append(clockTime.substring(0, 5));
        }
    }

    private String DateCompare(String s1, String s2) {
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

    //    /**
    //     * 欄位未填寫的警告視窗
    //     *
    //     * @param alertString 警告訊息
    //     */
    //    private void editTextAlertDialog(String alertString) {
    //        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddNewEventActivity.this);
    //        dialogBuilder
    //                .setMessage(alertString);
    //        AlertDialog alertDialog = dialogBuilder.show();
    //        TextView alertMessageText = (TextView) alertDialog.findViewById(android.R.id.message);
    //        alertMessageText.setGravity(Gravity.CENTER);
    //        alertDialog.show();
    //    }

    private void CancelAlertDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddNewEventActivity.this);
        dialogBuilder
                .setTitle(getResources().getString(R.string.new_event_cancel_title))
                .setMessage(getResources().getString(R.string.new_event_cancel_message))
                .setPositiveButton(
                        getResources().getString(R.string.msg_confirm),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //取消新增私人行程回到行事曆檢視畫面
                                finish();
                            }
                        })
                .setNeutralButton(
                        getResources().getString(R.string.msg_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //關閉提醒視窗回到新增私人行程
                            }
                        });
        AlertDialog alertDialog = dialogBuilder.show();
        TextView alertMessageText = (TextView) alertDialog.findViewById(android.R.id.message);
        alertMessageText.setGravity(Gravity.CENTER);
        alertDialog.show();
    }

}
