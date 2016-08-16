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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DecimalFormat;
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

public class AddMeasureBodyActivity extends AppCompatActivity {
    private static final String TAG = "AddMeasureBodyActivity";

    private Button newEventCancel, newEventCommit;
    private static EditText editTextRecordDateTime;
    private WebServiceConnection webServiceConnection;
    private SharedPreferences prf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurebody_add_new);
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

        boolean isStartMainService = webServiceConnection.isServiceRunning(AddMeasureBodyActivity.this, MainService.class.getName());
        if (isStartMainService) {
            //  關閉BT3.0的Service
            Intent btServiceIntent = new Intent(AddMeasureBodyActivity.this, GetBlueToothDeviceDataService.class);
            AddMeasureBodyActivity.this.stopService(btServiceIntent);
            //  關閉解析BT3.0的Service
            Intent MainServiceIntent = new Intent(AddMeasureBodyActivity.this, MainService.class);
            AddMeasureBodyActivity.this.stopService(MainServiceIntent);
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

    private void showTruitonDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void showTruitonTimePickerDialog(View v) {
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
        //身理體態資料
        EditText editTextRecordDateTime = (EditText) findViewById(R.id.editTextRecordDateTime);
        EditText editTextBodyHeight = (EditText) findViewById(R.id.editTextBodyHeight);
        EditText editTextBodyWidth = (EditText) findViewById(R.id.editTextBodyWidth);
        EditText editTextTemperature = (EditText) findViewById(R.id.editTextTemperature);
        EditText editTextHeadCircumference = (EditText) findViewById(R.id.editTextHeadCircumference);
        EditText editTextArmCircumference = (EditText) findViewById(R.id.editTextArmCircumference);
        EditText editTextWaistline = (EditText) findViewById(R.id.editTextWaistline);
        EditText editTextHips = (EditText) findViewById(R.id.editTextHips);
        String recorderDateTime = editTextRecordDateTime.getText().toString();

        //UserData
        //        UserAdapter userAdapter = new UserAdapter(AddMeasureBodyActivity.this);
        //        final User user = userAdapter.getUserUIdAndPassword();
        UserAdapter userAdapter = new UserAdapter(AddMeasureBodyActivity.this);
        List<User> userList = userAdapter.getAllUser();
        String UserName = null;
        for (User user : userList) {
            Log.v(TAG + "userName", user.getName().toString());
            UserName = user.getName();
        }

        GcmUtil gcmUtil = new GcmUtil();

        final BioDataAdapter bioDataAdapter = new BioDataAdapter(AddMeasureBodyActivity.this);
        //        bioData.set_id(recorderDateTime + user.getUid());
        //        bioData.setUserId(user.getUid());

        Date recordDate;
        try {
            recordDate = sdf.parse(recorderDateTime);
        } catch (ParseException e) {
            Log.e(TAG, "recorderDateTime ParseException : " + e);
            e.printStackTrace();
            webServiceConnection.editTextAlertDialog(AddMeasureBodyActivity.this, getString(R.string.user_alert_message5));
            return;
        }

        if (editTextRecordDateTime.getText().toString().trim().equals("")) {
            webServiceConnection.editTextAlertDialog(AddMeasureBodyActivity.this, getString(R.string.user_enter_blood_glucose_time));
            return;
        } else if (recordDate.after(new Date())) {
            webServiceConnection.editTextAlertDialog(AddMeasureBodyActivity.this, getString(R.string.user_alert_message6));
            editTextRecordDateTime.setText(sdf.format(new Date()));
            return;
        }

        if (!editTextBodyHeight.getText().toString().trim().equals("")
                || !editTextBodyWidth.getText().toString().trim().equals("")) {
            if (editTextBodyHeight.getText().toString().trim().equals("")) {
                webServiceConnection.editTextAlertDialog(AddMeasureBodyActivity.this, getString(R.string.user_enter_body_height));
                return;
            } else if (Integer.parseInt(editTextBodyHeight.getText().toString().trim()) > 250 || Integer.parseInt(editTextBodyHeight.getText().toString().trim()) < 10) {
                webServiceConnection.editTextAlertDialog(AddMeasureBodyActivity.this, getString(R.string.user_enter_body_height2));
                return;
            } else if (editTextBodyWidth.getText().toString().trim().equals("")) {
                webServiceConnection.editTextAlertDialog(AddMeasureBodyActivity.this, getString(R.string.user_enter_body_width));
                return;
            } else if (Integer.parseInt(editTextBodyWidth.getText().toString().trim()) > 300 || Integer.parseInt(editTextBodyWidth.getText().toString().trim()) < 10) {
                webServiceConnection.editTextAlertDialog(AddMeasureBodyActivity.this, getString(R.string.user_enter_body_width2));
                return;
            } else {
                BioData bioData = new BioData();

                bioData.setBodyHeight(editTextBodyHeight.getText().toString());
                bioData.setBodyWeight(editTextBodyWidth.getText().toString());

                DecimalFormat nf = new DecimalFormat("0.00");
                double e001i = Double.parseDouble(editTextBodyHeight.getText().toString()) / 100;
                double e002i = Double.parseDouble(editTextBodyWidth.getText().toString());
                double BMI = e002i / (e001i * e001i);
                Log.v(TAG + "BMI", nf.format(BMI));

                bioData.setBmi(nf.format(BMI));

                //                bioData.set_id(recorderDateTime + gcmUtil.getDeviceSerail(AddMeasureBodyActivity.this));
                bioData.setUserId(UserName);
                bioData.setDeviceTime(recorderDateTime);
                bioData.setInputType(webServiceConnection.UPLOAD_INPUT_TYPE_MANUAL);
                bioData.setDeviceType(webServiceConnection.BIODATA_DEVICE_TYPE_WEIGHT);

                bioData.setUploaded(webServiceConnection.DATA_IS_NOT_UPLOAD);
                bioDataAdapter.createBodyHeight(bioData);
            }
            //        } else {
            //            webServiceConnection.initToast(getString(R.string.user_enter_body_height), AddMeasureBodyActivity.this);
            //            return;
        }

        if (!editTextTemperature.getText().toString().trim().equals("")) {
            if (Integer.parseInt(editTextTemperature.getText().toString().trim()) > 43 || Integer.parseInt(editTextTemperature.getText().toString().trim()) < 28) {
                webServiceConnection.editTextAlertDialog(AddMeasureBodyActivity.this, getString(R.string.user_enter_temperature2));
                return;
            } else {
                BioData bioData = new BioData();
                bioData.setTemperature(editTextTemperature.getText().toString());

                //                bioData.set_id(recorderDateTime + gcmUtil.getDeviceSerail(AddMeasureBodyActivity.this));
                bioData.setUserId(UserName);
                bioData.setDeviceTime(recorderDateTime);
                bioData.setInputType(webServiceConnection.UPLOAD_INPUT_TYPE_MANUAL);
                bioData.setDeviceType("4");

                bioData.setUploaded(webServiceConnection.DATA_IS_NOT_UPLOAD);
                bioDataAdapter.createBodyTemperature(bioData);
            }
            //        } else {
            //            webServiceConnection.initToast(getString(R.string.user_enter_temperature), AddMeasureBodyActivity.this);
            //            return;
        }

        if (!editTextHeadCircumference.getText().toString().trim().equals("")) {
            if (Integer.parseInt(editTextHeadCircumference.getText().toString().trim()) > 90 || Integer.parseInt(editTextHeadCircumference.getText().toString().trim()) < 10) {
                webServiceConnection.editTextAlertDialog(AddMeasureBodyActivity.this, getString(R.string.user_enter_head_circumference2));
                return;
            } else {
                BioData bioData = new BioData();
                bioData.setHeadcircumference(editTextHeadCircumference.getText().toString());

                //                bioData.set_id(recorderDateTime + gcmUtil.getDeviceSerail(AddMeasureBodyActivity.this));
                bioData.setUserId(UserName);
                bioData.setDeviceTime(recorderDateTime);
                bioData.setInputType(webServiceConnection.UPLOAD_INPUT_TYPE_MANUAL);
                bioData.setDeviceType("5");

                bioData.setUploaded(webServiceConnection.DATA_IS_NOT_UPLOAD);
                bioDataAdapter.createBodyHeadCircumference(bioData);

            }
            //        } else {
            //            webServiceConnection.initToast(getString(R.string.user_enter_head_circumference), AddMeasureBodyActivity.this);
            //            return;
        }

        if (!editTextArmCircumference.getText().toString().trim().equals("")) {
            if (Integer.parseInt(editTextArmCircumference.getText().toString().trim()) > 100 || Integer.parseInt(editTextArmCircumference.getText().toString().trim()) < 10) {
                webServiceConnection.editTextAlertDialog(AddMeasureBodyActivity.this, getString(R.string.user_enter_arm_circumference2));
                return;
            } else {
                BioData bioData = new BioData();
                bioData.setArmcircumference(editTextArmCircumference.getText().toString());

                //                bioData.set_id(recorderDateTime + gcmUtil.getDeviceSerail(AddMeasureBodyActivity.this));
                bioData.setUserId(UserName);
                bioData.setDeviceTime(recorderDateTime);
                bioData.setInputType(webServiceConnection.UPLOAD_INPUT_TYPE_MANUAL);
                bioData.setDeviceType("6");

                bioData.setUploaded(webServiceConnection.DATA_IS_NOT_UPLOAD);
                bioDataAdapter.createBodyArmCircumference(bioData);

            }
            //        } else {
            //            webServiceConnection.initToast(getString(R.string.user_enter_arm_circumference), AddMeasureBodyActivity.this);
            //            return;
        }

        if (!editTextWaistline.getText().toString().trim().equals("")) {
            if (Integer.parseInt(editTextWaistline.getText().toString().trim()) > 100 || Integer.parseInt(editTextWaistline.getText().toString().trim()) < 10) {
                webServiceConnection.editTextAlertDialog(AddMeasureBodyActivity.this, getString(R.string.user_enter_waistline2));
                return;
            } else {
                BioData bioData = new BioData();
                bioData.setWaistline(editTextWaistline.getText().toString());

                //                bioData.set_id(recorderDateTime + gcmUtil.getDeviceSerail(AddMeasureBodyActivity.this));
                bioData.setUserId(UserName);
                bioData.setDeviceTime(recorderDateTime);
                bioData.setInputType(webServiceConnection.UPLOAD_INPUT_TYPE_MANUAL);
                bioData.setDeviceType("7");

                bioData.setUploaded(webServiceConnection.DATA_IS_NOT_UPLOAD);
                bioDataAdapter.createBodyWaistline(bioData);
            }
            //        } else {
            //            webServiceConnection.initToast(getString(R.string.user_enter_waistline), AddMeasureBodyActivity.this);
            //            return;
        }

        if (!editTextHips.getText().toString().trim().equals("")) {
            if (Integer.parseInt(editTextHips.getText().toString().trim()) > 100 || Integer.parseInt(editTextHips.getText().toString().trim()) < 10) {
                webServiceConnection.editTextAlertDialog(AddMeasureBodyActivity.this, getString(R.string.user_enter_hips2));
                return;
            } else {
                BioData bioData = new BioData();
                bioData.setHips(editTextHips.getText().toString());

                //                bioData.set_id(recorderDateTime + gcmUtil.getDeviceSerail(AddMeasureBodyActivity.this));
                bioData.setUserId(UserName);
                bioData.setDeviceTime(recorderDateTime);
                bioData.setInputType(webServiceConnection.UPLOAD_INPUT_TYPE_MANUAL);
                bioData.setDeviceType("8");

                bioData.setUploaded(webServiceConnection.DATA_IS_NOT_UPLOAD);
                bioDataAdapter.createBodyHips(bioData);
            }
            //        } else {
            //            webServiceConnection.initToast(getString(R.string.user_enter_hips), AddMeasureBodyActivity.this);
            //            return;
        }

        if (editTextBodyHeight.getText().toString().trim().equals("")
                && editTextBodyWidth.getText().toString().trim().equals("")
                && editTextTemperature.getText().toString().trim().equals("")
                && editTextHeadCircumference.getText().toString().trim().equals("")
                && editTextArmCircumference.getText().toString().trim().equals("")
                && editTextWaistline.getText().toString().trim().equals("")
                && editTextHips.getText().toString().trim().equals("")) {
            webServiceConnection.editTextAlertDialog(AddMeasureBodyActivity.this, getString(R.string.user_enter_faild));
            return;
        }


        //        webServiceConnection.getMessageDialog(getString(R.string.message_title), getString(R.string.user_save_blood_pressure), getContext()).show();
        new AlertDialog.Builder(AddMeasureBodyActivity.this)
                .setTitle(getString(R.string.message_title))
                .setMessage(getString(R.string.user_save_body))
                .setPositiveButton(getString(R.string.msg_confirm),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                final ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(AddMeasureBodyActivity.this
                                        , getString(R.string.webview_loading_title)
                                        , getString(R.string.msg_tokenget), false, true);
                                pb.show();

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
                                                    Toast.makeText(AddMeasureBodyActivity.this, getString(R.string.msg_net_reUpdate), Toast.LENGTH_LONG).show();
                                                    Looper.loop();
                                                    break;
                                                } else {
                                                    Looper.prepare();
                                                    Toast.makeText(AddMeasureBodyActivity.this, getString(R.string.msg_net_faild), Toast.LENGTH_LONG).show();
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
        editTextBodyHeight.setText("");
        editTextBodyWidth.setText("");
        editTextTemperature.setText("");
        editTextHeadCircumference.setText("");
        editTextArmCircumference.setText("");
        editTextWaistline.setText("");
        editTextHips.setText("");
        editTextRecordDateTime.setText(sdf.format(new Date()));
    }

    private void CancelAlertDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddMeasureBodyActivity.this);
        dialogBuilder.setTitle(getResources().getString(R.string.new_measure_cancel_title))
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
