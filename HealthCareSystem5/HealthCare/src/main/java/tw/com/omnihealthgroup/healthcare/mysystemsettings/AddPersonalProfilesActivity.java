package tw.com.omnihealthgroup.healthcare.mysystemsettings;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tw.com.omnihealthgroup.healthcare.util.GSONUtil;
import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.util.ShowMEProgressDiaLog;
import tw.com.omnihealthgroup.healthcare.WebServiceConnection;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.User;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.UserAdapter;

public class AddPersonalProfilesActivity extends AppCompatActivity {
    private static final String TAG = "AddPersonalProfilesActivity";

    private Button newEventCancel, newEventCommit;
    private WebServiceConnection webServiceConnection;
    private SharedPreferences prf;

    private static EditText edituserIDNO, edituserName, edituserEngName, edituserSex, edituserBirthDay, edituserNationality, edituserHomeTEL, edituserMobile, edituserEMail, edituserBlood, edituserRhType, edituserMarried;
    //    ArrayList<String> userdata;
    private Map<String, String> userdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalprofiles_add_new);
        Log.v(TAG, "onCreate");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        prf = getApplicationContext().getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
        webServiceConnection = new WebServiceConnection();

        initView();
        loadPageView();
    }

    @Override
    public void onBackPressed() {
        //        super.onBackPressed();
        Log.v(TAG, "onBackPressed");

        CancelAlertDialog();
    }

    private void initView() {
        newEventCancel = (Button) findViewById(R.id.new_event_btn_cancel);
        newEventCancel.setTextColor(getResources().getColor(R.color.white_color));
        newEventCommit = (Button) findViewById(R.id.new_event_btn_commit);
        newEventCommit.setTextColor(getResources().getColor(R.color.white_color));

        edituserIDNO = (EditText) findViewById(R.id.edituserIDNO);
        edituserName = (EditText) findViewById(R.id.edituserName);
        edituserEngName = (EditText) findViewById(R.id.edituserEngName);
        edituserSex = (EditText) findViewById(R.id.edituserSex);
        edituserBirthDay = (EditText) findViewById(R.id.edituserBirthDay);
        edituserNationality = (EditText) findViewById(R.id.edituserNationality);
        edituserHomeTEL = (EditText) findViewById(R.id.edituserHomeTEL);
        edituserMobile = (EditText) findViewById(R.id.edituserMobile);
        edituserEMail = (EditText) findViewById(R.id.edituserEMail);
        edituserBlood = (EditText) findViewById(R.id.edituserBlood);
        edituserRhType = (EditText) findViewById(R.id.edituserRhType);
        edituserMarried = (EditText) findViewById(R.id.edituserMarried);

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

        edituserBirthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 設定初始日期
                //                showTruitonTimePickerDialog(v);
                showTruitonDatePickerDialog(v);
            }
        });

        edituserSex.setOnClickListener(new View.OnClickListener() {
            String[] eventType = getResources().getStringArray(R.array.user_sex);
            ArrayList<String> textArrayList = new ArrayList<>(Arrays.asList(eventType));

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AddPersonalProfilesActivity.this)
                        .setItems(textArrayList.toArray(new String[textArrayList.size()]), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = textArrayList.get(which);
                                edituserSex.setText(name);
                            }
                        })
                        .show();
            }
        });

        edituserNationality.setOnClickListener(new View.OnClickListener() {
            String[] eventType = getResources().getStringArray(R.array.user_nationality);
            ArrayList<String> textArrayList = new ArrayList<>(Arrays.asList(eventType));

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AddPersonalProfilesActivity.this)
                        .setItems(textArrayList.toArray(new String[textArrayList.size()]), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = textArrayList.get(which);
                                edituserNationality.setText(name);
                            }
                        })
                        .show();
            }
        });

        edituserBlood.setOnClickListener(new View.OnClickListener() {
            String[] eventType = getResources().getStringArray(R.array.user_blood);
            ArrayList<String> textArrayList = new ArrayList<>(Arrays.asList(eventType));

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AddPersonalProfilesActivity.this)
                        .setItems(textArrayList.toArray(new String[textArrayList.size()]), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = textArrayList.get(which);
                                edituserBlood.setText(name);
                            }
                        })
                        .show();
            }
        });

        edituserRhType.setOnClickListener(new View.OnClickListener() {
            String[] eventType = getResources().getStringArray(R.array.user_rhtype);
            ArrayList<String> textArrayList = new ArrayList<>(Arrays.asList(eventType));

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AddPersonalProfilesActivity.this)
                        .setItems(textArrayList.toArray(new String[textArrayList.size()]), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = textArrayList.get(which);
                                edituserRhType.setText(name);
                            }
                        })
                        .show();
            }
        });

        edituserMarried.setOnClickListener(new View.OnClickListener() {
            String[] eventType = getResources().getStringArray(R.array.user_married);
            ArrayList<String> textArrayList = new ArrayList<>(Arrays.asList(eventType));

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AddPersonalProfilesActivity.this)
                        .setItems(textArrayList.toArray(new String[textArrayList.size()]), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = textArrayList.get(which);
                                edituserMarried.setText(name);
                            }
                        })
                        .show();
            }
        });
    }

    /**
     * 初始化完成按鈕與取消按鈕的監聽器
     */
    private void loadPageView() {
        final ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(AddPersonalProfilesActivity.this
                , getString(R.string.webview_loading_title)
                , getString(R.string.msg_tokenget), false, true);
        pb.show();

        UserAdapter userAdapter = new UserAdapter(AddPersonalProfilesActivity.this);
        List<User> userList = userAdapter.getAllUser();
        Log.v(TAG + "userList", userList.toString());
        userdata = new HashMap<>();
        for (User user : userList) {
            try {
                if (user.getUserUniqueId() != null) {
                    userdata.put("getUserUniqueId", user.getUserUniqueId());
                } else {
                    userdata.put("getUserUniqueId", "");
                }

                if (user.getRfid() != null) {
                    userdata.put("getRfid", user.getRfid());
                } else {
                    userdata.put("getRfid", "");
                }

                if (user.getUserIDNO() != null) {
                    userdata.put("getUserIDNO", user.getUserIDNO());
                } else {
                    userdata.put("getUserIDNO", "");
                }

                if (user.getName() != null) {
                    userdata.put("getName", user.getName());
                } else {
                    userdata.put("getName", "");
                }

                if (user.getNickname() != null) {
                    userdata.put("getNickname", user.getNickname());
                } else {
                    userdata.put("getNickname", "");
                }

                if (user.getGender() != null) {
                    userdata.put("getGender", user.getGender());
                } else {
                    userdata.put("getGender", "");
                }

                if (user.getBirthday() != null) {
                    userdata.put("getBirthday", user.getBirthday());
                } else {
                    userdata.put("getBirthday", "");
                }

                if (user.getUserNationality() != null) {
                    userdata.put("getUserNationality", user.getUserNationality());
                } else {
                    userdata.put("getUserNationality", "");
                }

                if (user.getPhone() != null) {
                    userdata.put("getPhone", user.getPhone());
                } else {
                    userdata.put("getPhone", "");
                }

                if (user.getMobile() != null) {
                    userdata.put("getMobile", user.getMobile());
                } else {
                    userdata.put("getMobile", "");
                }

                if (user.getEmail() != null) {
                    userdata.put("getEmail", user.getEmail());
                } else {
                    userdata.put("getEmail", "");
                }

                if (user.getUserBlood() != null) {
                    userdata.put("getUserBlood", user.getUserBlood());
                } else {
                    userdata.put("getUserBlood", "");
                }

                if (user.getUserRhType() != null) {
                    userdata.put("getUserRhType", user.getUserRhType());
                } else {
                    userdata.put("getUserRhType", "");
                }

                if (user.getUserMarried() != null) {
                    userdata.put("getUserMarried", user.getUserMarried());
                } else {
                    userdata.put("getUserMarried", "");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (user.getUserIDNO() != null) {
                    edituserIDNO.setText(userdata.get("getUserIDNO"));
                } else {
                    edituserIDNO.setHint(getString(R.string.user_IDNO_message));
                }

                if (user.getName() != null) {
                    edituserName.setText(user.getName());
                } else {
                    edituserName.setHint(getString(R.string.user_message));
                }

                if (user.getNickname() != null) {
                    edituserEngName.setText(user.getNickname());
                } else {
                    edituserEngName.setHint(getString(R.string.user_message));
                }

                if (user.getGender().equals("Unknow")) {
                    edituserSex.setHint(getString(R.string.user_message));
                } else if (user.getGender().equals("Male")) {
                    edituserSex.setText("男性");
                } else if (user.getGender().equals("Female")) {
                    edituserSex.setText("女性");
                }

                if (user.getBirthday() != null) {
                    edituserBirthDay.setText(user.getBirthday());
                } else {
                    edituserBirthDay.setHint(getString(R.string.user_message));
                }

                if (user.getUserNationality().equals("0")) {
                    edituserNationality.setHint(getString(R.string.user_message));
                } else if (user.getUserNationality().equals("Taiwan")) {
                    edituserNationality.setText("台灣");
                }

                if (user.getPhone() != null) {
                    edituserHomeTEL.setText(user.getPhone());
                } else {
                    edituserHomeTEL.setHint(getString(R.string.user_message));
                }

                if (user.getMobile() != null) {
                    edituserMobile.setText(user.getMobile());
                } else {
                    edituserMobile.setHint(getString(R.string.user_message));
                }

                if (user.getEmail() != null) {
                    edituserEMail.setText(user.getEmail());
                } else {
                    edituserEMail.setHint(getString(R.string.user_message));
                }

                if (user.getUserBlood().equals("0")) {
                    edituserBlood.setHint(getString(R.string.user_message));
                } else if (user.getUserBlood().equals("A")) {
                    edituserBlood.setText("A型");
                } else if (user.getUserBlood().equals("B")) {
                    edituserBlood.setText("B型");
                } else if (user.getUserBlood().equals("O")) {
                    edituserBlood.setText("O型");
                } else if (user.getUserBlood().equals("AB")) {
                    edituserBlood.setText("AB型");
                }

                if (user.getUserRhType().equals("0")) {
                    edituserRhType.setHint(getString(R.string.user_message));
                } else if (user.getUserRhType().equals("Rh+")) {
                    edituserRhType.setText("Rh陽性");
                } else if (user.getUserRhType().equals("Rh-")) {
                    edituserRhType.setText("RH陰性");
                }

                if (user.getUserMarried().equals("0")) {
                    edituserMarried.setHint(getString(R.string.user_message));
                } else if (user.getUserMarried().equals("Unmarried")) {
                    edituserMarried.setText("未婚");
                } else if (user.getUserMarried().equals("Married")) {
                    edituserMarried.setText("已婚");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        pb.dismiss();
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
            edituserBirthDay.setText(year + "-" + (month + 1) + "-" + day);
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
            edituserBirthDay.setText(edituserBirthDay.getText() + " " + hourOfDay + ":" + minute + ":" + second + "." + millisecond);
            try {
                Log.v(TAG + "edituserBirthDay", edituserBirthDay.getText().toString());
                Date date = formatter.parse(edituserBirthDay.getText().toString());
                String timeStr = formatter.format(date);
                edituserBirthDay.setText(timeStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveKeyInData() {
        User user = new User();
        try {
            user.setUserUniqueId(userdata.get("getUserUniqueId"));
            user.setRfid(userdata.get("getRfid"));
            user.setUserIDNO(userdata.get("getUserIDNO"));

            if (!edituserName.getText().toString().trim().equals("")
                    && edituserName.getText().toString().trim() != getString(R.string.user_message)) {
                user.setName(edituserName.getText().toString().trim());
            } else {
                if (userdata.get("getName").length() > 0) {
                    user.setName(userdata.get("getName"));
                } else {
                    user.setName("");
                }
            }

            if (!edituserEngName.getText().toString().trim().equals("")
                    && edituserEngName.getText().toString().trim() != getString(R.string.user_message)) {
                user.setNickname(edituserEngName.getText().toString().trim());
            } else {
                if (userdata.get("getNickname").length() > 0) {
                    user.setNickname(userdata.get("getNickname"));
                } else {
                    user.setNickname("");
                }
            }

            if (!edituserSex.getText().toString().trim().equals("")
                    && edituserSex.getText().toString().trim() != getString(R.string.user_message)) {
                switch (edituserSex.getText().toString().trim()) {
                    case "未知":
                        user.setGender("Unknow");
                        break;
                    case "男性":
                        user.setGender("Male");
                        break;
                    case "女性":
                        user.setGender("Female");
                        break;
                }
            } else {
                if (userdata.get("getGender").length() > 0) {
                    user.setGender(userdata.get("getGender"));
                } else {
                    user.setGender("Unknow");
                }
            }

            if (!edituserBirthDay.getText().toString().trim().equals("")
                    && edituserBirthDay.getText().toString().trim() != getString(R.string.user_message)) {
                user.setBirthday(edituserBirthDay.getText().toString().trim());
            } else {
                if (userdata.get("getBirthday").length() > 0) {
                    user.setBirthday(userdata.get("getBirthday"));
                } else {
                    user.setBirthday("");
                }
            }

            if (!edituserNationality.getText().toString().trim().equals("")
                    && edituserNationality.getText().toString().trim() != getString(R.string.user_message)) {
                switch (edituserNationality.getText().toString().trim()) {
                    case "未知":
                        user.setUserNationality("0");
                        break;
                    case "台灣":
                        user.setUserNationality("Taiwan");
                        break;
                }
            } else {
                if (userdata.get("getUserNationality").length() > 0) {
                    user.setUserNationality(userdata.get("getUserNationality"));
                } else {
                    user.setUserNationality("0");
                }
            }

            if (!edituserHomeTEL.getText().toString().trim().equals("")
                    && edituserHomeTEL.getText().toString().trim() != getString(R.string.user_message)) {
                user.setPhone(edituserHomeTEL.getText().toString().trim());
            } else {
                if (userdata.get("getPhone").length() > 0) {
                    user.setPhone(userdata.get("getPhone"));
                } else {
                    user.setPhone("");
                }
            }

            if (!edituserMobile.getText().toString().trim().equals("")
                    && edituserMobile.getText().toString().trim() != getString(R.string.user_message)) {
                user.setMobile(edituserMobile.getText().toString().trim());
            } else {
                if (userdata.get("getMobile").length() > 0) {
                    user.setMobile(userdata.get("getMobile"));
                } else {
                    user.setMobile("");
                }
            }

            if (!edituserEMail.getText().toString().trim().equals("")
                    && edituserEMail.getText().toString().trim() != getString(R.string.user_message)) {
                user.setEmail(edituserEMail.getText().toString().trim());
            } else {
                if (userdata.get("getEmail").length() > 0) {
                    user.setEmail(userdata.get("getEmail"));
                } else {
                    user.setEmail("");
                }
            }

            if (!edituserBlood.getText().toString().trim().equals("")
                    && edituserBlood.getText().toString().trim() != getString(R.string.user_message)) {
                switch (edituserBlood.getText().toString().trim()) {
                    case "未知":
                        user.setUserBlood("0");
                        break;
                    case "A型":
                        user.setUserBlood("A");
                        break;
                    case "B型":
                        user.setUserBlood("B");
                        break;
                    case "O型":
                        user.setUserBlood("O");
                        break;
                    case "AB型":
                        user.setUserBlood("AB");
                        break;
                }
            } else {
                if (userdata.get("getUserBlood").length() > 0) {
                    user.setUserBlood(userdata.get("getUserBlood"));
                } else {
                    user.setUserBlood("0");
                }
            }

            if (!edituserRhType.getText().toString().trim().equals("")
                    && edituserRhType.getText().toString().trim() != getString(R.string.user_message)) {
                switch (edituserRhType.getText().toString().trim()) {
                    case "未知":
                        user.setUserRhType("0");
                        break;
                    case "Rh陽性":
                        user.setUserRhType("Rh+");
                        break;
                    case "RH陰性":
                        user.setUserRhType("Rh-");
                        break;
                }
            } else {
                if (userdata.get("getUserRhType").length() > 0) {
                    user.setUserRhType(userdata.get("getUserRhType"));
                } else {
                    user.setUserRhType("0");
                }
            }

            if (!edituserMarried.getText().toString().trim().equals("")
                    && edituserMarried.getText().toString().trim() != getString(R.string.user_message)) {
                switch (edituserMarried.getText().toString().trim()) {
                    case "未知":
                        user.setUserMarried("0");
                        break;
                    case "未婚":
                        user.setUserMarried("Unmarried");
                        break;
                    case "已婚":
                        user.setUserMarried("Married");
                        break;
                }
            } else {
                if (userdata.get("getUserMarried").length() > 0) {
                    user.setUserMarried(userdata.get("getUserMarried"));
                } else {
                    user.setUserMarried("0");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final UserAdapter userAdapter = new UserAdapter(AddPersonalProfilesActivity.this);
        userAdapter.delAllUser();
        userAdapter.createtUser(user);

        //        webServiceConnection.getMessageDialog(getString(R.string.message_title), getString(R.string.user_save_blood_pressure), getContext()).show();
        new AlertDialog.Builder(AddPersonalProfilesActivity.this)
                .setTitle(getString(R.string.message_title))
                .setMessage(getString(R.string.user_save_person))
                .setPositiveButton(getString(R.string.msg_confirm),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                final ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(AddPersonalProfilesActivity.this
                                        , getString(R.string.webview_loading_title)
                                        , getString(R.string.msg_tokenget), false, true);
                                pb.show();

                                //        final NetService netService = new NetService();
                                //                                final ArrayList<BioData> listBioData = bioDataAdapter.getUploaded();
                                final ArrayList<User> listUserData = userAdapter.getAllUser();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            while (true) {
                                                Log.i(TAG, "manual upload data");
                                                //                    String reUpdateResponse = netService.CallUploadVitalSign(user, listBioData, false);
                                                JSONObject reUpdateResponse = webServiceConnection.editUserinfo(prf.getString("access_token", ""), listUserData);
                                                //                    if (reUpdateResponse != null && reUpdateResponse.equals("{\"Message\" : \"A01\"}")) {
                                                if (reUpdateResponse != null && reUpdateResponse.getString("message").toString().equals("Success.")) {
                                                    // 更新sql lite資料庫
                                                    //                                                    userAdapter.createtUser(listUserData);
                                                    uploaddata(reUpdateResponse);
                                                    //                                                    Log.i(TAG, "資料重新上傳成功");
                                                    Looper.prepare();
                                                    Toast.makeText(AddPersonalProfilesActivity.this, getString(R.string.msg_net_reUpdate), Toast.LENGTH_LONG).show();
                                                    Looper.loop();
                                                    break;
                                                } else {
                                                    Looper.prepare();
                                                    Toast.makeText(AddPersonalProfilesActivity.this, getString(R.string.msg_net_faild), Toast.LENGTH_LONG).show();
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
        loadPageView();
    }

    private void uploaddata(JSONObject response) {
        try {
            Map<String, String> responsemap = new HashMap<>();
            responsemap = GSONUtil.GsonToMaps(response.getJSONObject("result").toString());
            Log.v(TAG + "responsemap", responsemap.toString());

            UserAdapter userAdapter = new UserAdapter(AddPersonalProfilesActivity.this);
            userAdapter.delAllUser();
            User user = new User();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = formatter.parse(responsemap.get("userBirthDay"));
            String timeStr = formatter.format(date);

            user.setUserUniqueId(responsemap.get("userUniqueId"));
            user.setRfid(responsemap.get("rfid"));
            user.setUserIDNO(responsemap.get("userIDNO"));
            user.setName(responsemap.get("userName"));
            user.setNickname(responsemap.get("userEngName"));
            user.setGender(responsemap.get("userSex"));
            user.setBirthday(timeStr);
            user.setUserNationality(responsemap.get("userNationality"));
            user.setPhone(responsemap.get("userHomeTEL"));
            user.setMobile(responsemap.get("userMobile"));
            user.setEmail(responsemap.get("userEMail"));
            user.setUserBlood(responsemap.get("userBlood"));
            user.setUserRhType(responsemap.get("userRhType"));
            user.setUserMarried(responsemap.get("userMarried"));

            userAdapter.createtUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CancelAlertDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddPersonalProfilesActivity.this);
        dialogBuilder
                .setTitle(getResources().getString(R.string.new_personal_cancel_title))
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
