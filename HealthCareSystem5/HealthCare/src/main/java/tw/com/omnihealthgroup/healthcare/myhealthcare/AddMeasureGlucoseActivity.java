package tw.com.omnihealthgroup.healthcare.myhealthcare;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.util.ShowMEProgressDiaLog;
import tw.com.omnihealthgroup.healthcare.WebServiceConnection;
import tw.com.omnihealthgroup.healthcare.gcm.GcmUtil;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.BioData;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.BioDataAdapter;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.User;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.UserAdapter;
import tw.com.omnihealthgroup.healthcare.myhealthcare.service.GetBlueToothDeviceDataService;
import tw.com.omnihealthgroup.healthcare.myhealthcare.service.MainService;

public class AddMeasureGlucoseActivity extends AppCompatActivity {
    private static final String TAG = "AddMeasureGlucoseActivity";

    private Button newEventCancel, newEventCommit;
    private static EditText editTextRecordDateTime;
    private WebServiceConnection webServiceConnection;
    private SharedPreferences prf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measureglucose_add_new);
        Log.v(TAG, "onCreate");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //editview 不會自動跳出鍵盤
        prf = getApplicationContext().getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
        webServiceConnection = new WebServiceConnection();

        loadPageView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");

        boolean isStartMainService = webServiceConnection.isServiceRunning(AddMeasureGlucoseActivity.this, MainService.class.getName());
        if (isStartMainService) {
            //  關閉BT3.0的Service
            Intent btServiceIntent = new Intent(AddMeasureGlucoseActivity.this, GetBlueToothDeviceDataService.class);
            AddMeasureGlucoseActivity.this.stopService(btServiceIntent);
            //  關閉解析BT3.0的Service
            Intent MainServiceIntent = new Intent(AddMeasureGlucoseActivity.this, MainService.class);
            AddMeasureGlucoseActivity.this.stopService(MainServiceIntent);
        }
    }

    @Override
    public void onBackPressed() {
        //        super.onBackPressed();
        Log.v(TAG, "onBackPressed");

        CancelAlertDialog();
    }

    /**
     * 初始化完成按鈕與取消按鈕的監聽器
     */
    private void loadPageView() {
        newEventCancel = (Button) findViewById(R.id.new_event_btn_cancel);
        newEventCancel.setTextColor(getResources().getColor(R.color.white_color));
        newEventCommit = (Button) findViewById(R.id.new_event_btn_commit);
        newEventCommit.setTextColor(getResources().getColor(R.color.white_color));

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
                saveKeyInData();
            }
        });

        //輸入量測日期
        editTextRecordDateTime = (EditText) findViewById(R.id.editTextRecordDateTime);
        //先帶入現在時間
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        editTextRecordDateTime.setText(sdf.format(new Date()));
        //        editTextRecordDateTime.setInputType(InputType.TYPE_NULL); // 關閉軟鍵盤

        editTextRecordDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 設定初始日期
                showTruitonTimePickerDialog(v);
                showTruitonDatePickerDialog(v);
            }
        });
    }

    public void showTruitonDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTruitonTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user(顯示在EditView中Month要加一)
            editTextRecordDateTime.setText(year + "-" + (month + 1) + "-" + day);
        }
    }

    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Calendar c = Calendar.getInstance();

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute
                    , DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            int second = c.get(Calendar.SECOND);
            int millisecond = c.get(Calendar.MILLISECOND);

            // Do something with the time chosen by the user
            editTextRecordDateTime.setText(editTextRecordDateTime.getText() + " " + hourOfDay + ":" + minute + ":" + second + "." + millisecond);
            try {
                Log.v(TAG + "editTextRecordDateTime", editTextRecordDateTime.getText().toString());
                Date date = formatter.parse(editTextRecordDateTime.getText().toString());
                String timeStr = formatter.format(date);
                editTextRecordDateTime.setText(timeStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveKeyInData() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //血糖類型
        RadioButton rbLimosis = (RadioButton) findViewById(R.id.limosisradiobtn);
        RadioButton rbAfterMeals = (RadioButton) findViewById(R.id.aftermealradiobtn);
        RadioButton rbUsually = (RadioButton) findViewById(R.id.usuallyradiobtn);

        //血糖資料
        EditText etBloodGlucoseValue = (EditText) findViewById(R.id.editTextBloodGlucoseValue);
        EditText editTextRecordDateTime = (EditText) findViewById(R.id.editTextRecordDateTime);
        String recorderDateTime = editTextRecordDateTime.getText().toString();

        BioData bioData = new BioData();
        bioData.setDeviceTime(recorderDateTime);
        bioData.setDeviceType(webServiceConnection.BIODATA_DEVICE_TYPE_BLOOD_GLUCOSE);
        bioData.setInputType(webServiceConnection.UPLOAD_INPUT_TYPE_MANUAL);

        Date recordDate;
        try {
            recordDate = sdf.parse(recorderDateTime);
        } catch (ParseException e) {
            Log.e(TAG, "recorderDateTime ParseException : " + e);
            e.printStackTrace();
            webServiceConnection.editTextAlertDialog(AddMeasureGlucoseActivity.this, getString(R.string.user_alert_message5));
            return;
        }
        if (etBloodGlucoseValue.getText().toString().trim().equals("")) {
            webServiceConnection.editTextAlertDialog(AddMeasureGlucoseActivity.this, getString(R.string.user_enter_blood_glucose));
            return;
        } else if (Integer.parseInt(etBloodGlucoseValue.getText().toString().trim()) > 350 || Integer.parseInt(etBloodGlucoseValue.getText().toString().trim()) < 30) {
            webServiceConnection.editTextAlertDialog(AddMeasureGlucoseActivity.this, getString(R.string.user_enter_blood_glucose2));
            return;
        } else if (editTextRecordDateTime.getText().toString().trim().equals("")) {
            webServiceConnection.editTextAlertDialog(AddMeasureGlucoseActivity.this, getString(R.string.user_enter_blood_glucose_time));
            return;
        } else if (recordDate.after(new Date())) {
            webServiceConnection.editTextAlertDialog(AddMeasureGlucoseActivity.this, getString(R.string.user_alert_message6));
            editTextRecordDateTime.setText(sdf.format(new Date()));
            return;
        } else if (rbLimosis.isChecked()) {
            bioData.setAc(etBloodGlucoseValue.getText().toString());
        } else if (rbAfterMeals.isChecked()) {
            bioData.setPc(etBloodGlucoseValue.getText().toString());
        } else if (rbUsually.isChecked()) {
            //Toast.makeText(getApplicationContext(), "rbUsually", Toast.LENGTH_LONG).show();
            bioData.setNm(etBloodGlucoseValue.getText().toString());
        } else {
            webServiceConnection.editTextAlertDialog(AddMeasureGlucoseActivity.this, getString(R.string.user_enter_blood_glucose_type));
            return;
        }

        //UserData
        //        UserAdapter userAdapter = new UserAdapter(AddMeasureGlucoseActivity.this);
        //        final User user = userAdapter.getUserUIdAndPassword();
        UserAdapter userAdapter = new UserAdapter(AddMeasureGlucoseActivity.this);
        List<User> userList = userAdapter.getAllUser();
        String UserName = null;
        for (User user : userList) {
            Log.v(TAG + "userName", user.getName().toString());
            UserName = user.getName();
        }

        final BioDataAdapter bioDataAdapter = new BioDataAdapter(AddMeasureGlucoseActivity.this);
        //        bioData.set_id(recorderDateTime + user.getUid());
        //        bioData.setUserId(user.getUid());
        GcmUtil gcmUtil = new GcmUtil();
        //        bioData.set_id(recorderDateTime + gcmUtil.getDeviceSerail(AddMeasureGlucoseActivity.this));
        bioData.setUserId(UserName);
        //        bioData.setInputType(webServiceConnection.UPLOAD_INPUT_TYPE_MANUAL);
        bioData.setUploaded(WebServiceConnection.DATA_IS_NOT_UPLOAD);
        bioDataAdapter.createGlucose(bioData);
        //        webServiceConnection.getMessageDialog(getString(R.string.message_title), getString(R.string.user_save_blood_glucose), getContext()).show();
        new AlertDialog.Builder(AddMeasureGlucoseActivity.this)
                .setTitle(getString(R.string.message_title))
                .setMessage(getString(R.string.user_save_blood_glucose))
                .setPositiveButton(getString(R.string.msg_confirm),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                final ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(AddMeasureGlucoseActivity.this
                                        , getString(R.string.webview_loading_title)
                                        , getString(R.string.msg_tokenget), false, true);
                                pb.show();

                                //上傳資料
                                //        final NetService netService = new NetService();
                                final ArrayList<BioData> listBioData = bioDataAdapter.getUploaded();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            while (true) {
                                                Log.i(TAG, "manual upload data");
                                                //                    String reUpdateResponse = netService.CallUploadVitalSign(user, listBioData, false);
                                                JSONObject reUpdateResponse = webServiceConnection.addMeasureResource(prf.getString("access_token", ""), listBioData, false);
                                                //                    if (reUpdateResponse != null && reUpdateResponse.equals("{\"Message\" : \"A01\"}")) {
                                                if (reUpdateResponse != null && reUpdateResponse.getString("message").toString().equals("Success.")) {
                                                    // 更新sql lite資料庫
                                                    bioDataAdapter.updataUploaded(listBioData);
                                                    //                                                    Log.i(TAG, "資料重新上傳成功");
                                                    Looper.prepare();
                                                    Toast.makeText(AddMeasureGlucoseActivity.this, getString(R.string.msg_net_reUpdate), Toast.LENGTH_LONG).show();
                                                    Looper.loop();
                                                    break;
                                                } else {
                                                    Looper.prepare();
                                                    Toast.makeText(AddMeasureGlucoseActivity.this, getString(R.string.msg_net_faild), Toast.LENGTH_LONG).show();
                                                    Looper.loop();
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();

                                //返回行事曆檢視畫面
                                pb.dismiss();
                                finish();
                            }
                        }).show();
        //清除edittext
        rbLimosis.setChecked(false);
        rbAfterMeals.setChecked(false);
        rbUsually.setChecked(true);
        etBloodGlucoseValue.setText("");
        editTextRecordDateTime.setText(sdf.format(new Date()));
    }

    private void CancelAlertDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddMeasureGlucoseActivity.this);
        dialogBuilder
                .setTitle(getResources().getString(R.string.new_measure_cancel_title))
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
