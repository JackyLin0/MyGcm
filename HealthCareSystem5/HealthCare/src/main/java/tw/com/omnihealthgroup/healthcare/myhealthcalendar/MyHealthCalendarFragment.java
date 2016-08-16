package tw.com.omnihealthgroup.healthcare.myhealthcalendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import tw.com.omnihealthgroup.drawerframework.DrawerFrameworkMainFragment;
import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.util.ShowMEProgressDiaLog;
import tw.com.omnihealthgroup.healthcare.WebServiceConnection;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.adapter.ToolBarEventTypeSpinnerAdapter;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.adapter.ToolBarYearMonthSpinnerAdapter;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.dbo.CalendarData;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.dbo.DialogEventData;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.dbo.EventClassifyData;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.dbo.GetScheduleData;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.dbo.ScheduleDAO;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.dbo.SynchroDateModule;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Clinic;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Consultation;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Examination;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Meeting;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Operation;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.object.Personal;

/**
 * Created by Administrator on 2016/5/23.
 */
public class MyHealthCalendarFragment extends DrawerFrameworkMainFragment {
    private static final String TAG = "MyHealthCalendarFragment";
    private WebView myBrowser = null;
    private String authCode = null;

    private SharedPreferences prf;
    private WebServiceConnection webServiceConnection;

    private View rootView;
    private Fragment monthViewFragment, dayViewFragment;
    public static boolean monthSwitchToDay;

    private ProgressDialog showMEProgressDiaLog;

    private Spinner selectMonth, selectClassify;
    private ToolBarYearMonthSpinnerAdapter selectMonthAdapter;
    private ImageView schesuleModeChanged;

    private int getSynchroDate;

    private Integer[] synchroMonthArray;
    private Integer[] synchroYearArray;

    public DialogFragment weekEventDialogFragment;

    // interface
    private CallBackMonthView callBackMonthView;
    private CallBackDayView callBackDayView;

    public MyHealthCalendarFragment() {
        // Required empty public constructor
    }

    /**
     * 載入共用ToolBar
     *
     * @param toolbar toolbar
     */
    @Override
    protected void onSetToolbar(Toolbar toolbar) {
        super.onSetToolbar(toolbar);
        toolbar.setTitle("");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        prf = getActivity().getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
        webServiceConnection = new WebServiceConnection();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_myhealthcalendar, container, false);
        Log.v(TAG, "onCreate");

        //        checkView();
        loadPageView();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //        Toast.makeText(getActivity(), "onResume", Toast.LENGTH_SHORT).show();
        Log.v(TAG, "onResume");

        checkView();

        //        if (flag == true) {
        //            Log.v(TAG, "onResume");
        //            return;
        //        } else {
        //            Log.v(TAG, "onResume_Load");
        //            checkView();
        //            flag = true;
        //        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
        //        flag = false;

        //初始化ToolBar的日期，避免因使用互聯通內其他功能，造成日期累加問題
        CalendarData.ToolBarCalendarData.initToolBarYearAndMonth();
        if (weekEventDialogFragment != null) {
            weekEventDialogFragment.dismiss();
        }
        //        Toast.makeText(getActivity(), "onPause", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");

        //初始化ToolBar的日期，避免因同時開啟太多APP造成日期累加問題
        CalendarData.ToolBarCalendarData.initToolBarYearAndMonth();
        //        Toast.makeText(getActivity(), "onDestroy", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v(TAG, "onDestroyView");

        new TokenExp().cancel(true);
        new TokenGet().cancel(true);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        //        Toast.makeText(getActivity(), "onLowMemory", Toast.LENGTH_SHORT).show();
    }

    private void checkView() {
        String timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String checkLogin = webServiceConnection.DateCompare(timeStr, prf.getString("take_time", ""));

        if (webServiceConnection.isOnline(getContext())) {
            if (checkLogin.equals("true")) {
                //                loadPageView();
                return;
            } else if (checkLogin.equals("exToke")) {
                new TokenExp().execute();
            } else {
                new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.message_title))
                        .setMessage(getString(R.string.msg_retokenget))
                        .setPositiveButton(getString(R.string.msg_confirm),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        try {
                                            getAuthRequest(getContext(), R.layout.auth_dialog, R.id.webv);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).show();
            }
        } else {
            new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.msg_connect_faild_title))
                    .setMessage(getString(R.string.msg_connect_faild))
                    .setPositiveButton(getString(R.string.msg_confirm),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                                    getContext().startActivity(intent);
                                }
                            }).show();
        }
    }

    private void loadPageView() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH) - 24;
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        int millisecond = c.get(Calendar.MILLISECOND);

        String timeStr = null;
        try {
            Date date = formatter.parse(String.valueOf(year + "-" + (month + 1) + "-" + day + " " + hour + ":" + minute + ":" + second + "." + millisecond));
            timeStr = formatter.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String timeEnd = null;
        try {
            Date date = formatter.parse(String.valueOf(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH) + 24
                    + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND) + "." + c.get(Calendar.MILLISECOND)));
            timeEnd = formatter.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //        String fromDate, toDate;
        //        Calendar calendar = Calendar.getInstance();
        //        calendar.add(Calendar.MONTH, -1);
        //        StringBuilder stringBuilder = new StringBuilder();
        //        stringBuilder.append(String.valueOf(calendar.get(Calendar.YEAR)));
        //        stringBuilder.append("-");
        //        if ((calendar.get(Calendar.MONTH) + 1) < 10) {
        //            stringBuilder.append("0");
        //        }
        //        stringBuilder.append(String.valueOf(calendar.get(Calendar.MONTH) + 1));
        //        stringBuilder.append("-01 00:00:00");
        //
        //        fromDate = stringBuilder.toString();
        //
        //        calendar.add(Calendar.MONTH, 2);
        //        stringBuilder.delete(0, stringBuilder.length());
        //        stringBuilder.append(String.valueOf(calendar.get(Calendar.YEAR)));
        //        stringBuilder.append("-");
        //        if ((calendar.get(Calendar.MONTH) + 1) > 10) {
        //            stringBuilder.append("0");
        //        }
        //        stringBuilder.append(String.valueOf(calendar.get(Calendar.MONTH) + 1));
        //        stringBuilder.append("-");
        //        stringBuilder.append(String.valueOf(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)));
        //        stringBuilder.append(" 23:59:59");
        //        toDate = stringBuilder.toString();

        //        connectData("2015-11-01 00:00:00", "2016-05-30 23:59:59");
        connectData(timeStr, timeEnd);
    }

    /**
     * 連接網路取得行事曆各頁面資料，
     * 首次下載資料的時候會從最早的日期開始下載（花費時間較長），
     * 之後便會從最後一次下載的資料日期開始下載。
     */
    private void connectData(final String fromDate, final String toDate) {
        final ScheduleDAO scheduleDAO = new ScheduleDAO(getContext());
        final int msgPkTableSize = scheduleDAO.getAll(ScheduleDAO.TABLE_LAST_MSG_PK).size();
        String loadingMessage =
                (msgPkTableSize > 0) ?
                        getResources().getString(R.string.download_data) :
                        getResources().getString(R.string.download_data_first);
        showMEProgressDiaLog = new ShowMEProgressDiaLog(
                getContext(),
                "",
                loadingMessage,
                true,
                false
        );
        showMEProgressDiaLog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                LinkedList<JSONObject> GET_JSON_OBJ_LIST = new LinkedList<>();
                //                connectServerData = new WebServiceConnection();
                try {
                    if (msgPkTableSize > 0) {
                        System.out.println(TAG + "msgPkTableSize / " + msgPkTableSize);
                    }
                    //                    connectServerData.getScheduleAndNewsData(String.valueOf(MyAccountManager.GetAccountName()), lastMsgPk);
                    //                    connectServerData.getScheduleAndNewsData("Schedule", "0DM165", "C", fromDate, toDate);
                    JSONObject response = webServiceConnection.queryCaleinfo(prf.getString("access_token", ""), fromDate, toDate);
                    if (response != null) {
                        Log.v(TAG + "response", response.toString());
                        if (response.getString("message").equals("Success.")) {
                            JSONArray jsonArray = response.getJSONArray("result");
                            // 取得JSON物件列表，並分類放入List內

                            //                            boolean falg_23 = true, falg_24 = true, falg_25 = true, falg_26 = true, falg_27 = true, falg_28 = true;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                //                                switch (jsonArray.getJSONObject(i).getString("eventType")) {
                                //                                    case "23":
                                //                                        if (falg_23 == true) {
                                Clinic clinic = new Clinic();
                                scheduleDAO.delete_table(ScheduleDAO.TABLE_CLINIC, clinic);
                                //                                            falg_23 = false;
                                //                                        }
                                //                                        break;
                                //                                    case "24":
                                //                                        if (falg_24 == true) {
                                Consultation consultation = new Consultation();
                                scheduleDAO.delete_table(ScheduleDAO.TABLE_CONSULTATION, consultation);
                                //                                            falg_24 = false;
                                //                                        }
                                //                                        break;
                                //                                    case "25":
                                //                                        if (falg_25 == true) {
                                Operation operation = new Operation();
                                scheduleDAO.delete_table(ScheduleDAO.TABLE_OPERATE, operation);
                                //                                            falg_25 = false;
                                //                                        }
                                //                                        break;
                                //                                    case "26":
                                //                                        if (falg_26 == true) {
                                Meeting meeting = new Meeting();
                                scheduleDAO.delete_table(ScheduleDAO.TABLE_MEETING, meeting);
                                //                                            falg_26 = false;
                                //                                        }
                                //                                        break;
                                //                                    case "27":
                                //                                        if (falg_27 == true) {
                                Personal personal = new Personal();
                                scheduleDAO.delete_table(ScheduleDAO.TABLE_PERSONAL, personal);
                                //                                            falg_27 = false;
                                //                                        }
                                //                                        break;
                                //                                    case "28":
                                //                                        if (falg_28 == true) {
                                Examination examination = new Examination();
                                scheduleDAO.delete_table(ScheduleDAO.TABLE_EXAMINATION, examination);
                                //                                            falg_28 = false;
                                //                                        }
                                //                                        break;
                                //                                }
                                GET_JSON_OBJ_LIST.add(jsonArray.getJSONObject(i));
                            }
                            Log.v(TAG + "GET_JSON_OBJ_LIST", GET_JSON_OBJ_LIST.toString());

                        } else {
                            Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    GetScheduleData getScheduleData = new GetScheduleData(getContext(), GET_JSON_OBJ_LIST);
                    getScheduleData.getClinicSchedule();
                    getScheduleData.getConsultationSchedule();
                    getScheduleData.getOperationSchedule();
                    getScheduleData.getMeetingSchedule();
                    getScheduleData.getPersonalSchedule();
                    getScheduleData.getExaminationSchedule();
                    //清空儲存撈取資料的List
                    GET_JSON_OBJ_LIST.clear();
                    showMEProgressDiaLog.dismiss();
                    //                            System.out.println(TAG + "LAST MsgPk / " + lastMsgPk);
                }

                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            monthViewFragment = new MonthViewFragment();
                            dayViewFragment = new DayViewFragment();

                            Fragment fragment = null;
                            if (ShowDayEventFragment.flag != null
                                    && ShowDayEventFragment.flag.equals("del")) {
                                fragment = dayViewFragment;
                                monthSwitchToDay = false;

                                ShowDayEventFragment.flag = null;
                            } else {
                                fragment = monthViewFragment;
                                monthSwitchToDay = true;
                            }

                            getChildFragmentManager()
                                    .beginTransaction()
                                    .add(R.id.main_container, fragment)
                                    .commit();

                            //open interface
                            callBackMonthView = (CallBackMonthView) monthViewFragment;
                            callBackDayView = (CallBackDayView) dayViewFragment;

                            initToolBarItems();
                            initHandler();
                            //                        if (getArguments() != null) {
                            //                            String kindCode = getArguments().getString("kindcode");
                            //                            String msgPk = getArguments().getString("msgpk");
                            //                            Log.i(TAG, "MSG_PK: " + msgPk);
                            //                            showWeekEventFromNotify(scheduleDAO.searchNotifyDate(kindCode, msgPk));
                            //                        }
                            //                        Toast.makeText(getActivity(), "DATA LOADING FINISH", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 初始化ToolBar中的元件
     */
    private void initToolBarItems() {
        CalendarData toolBarCalendarData = CalendarData.ToolBarCalendarData;
        schesuleModeChanged = (ImageView) rootView.findViewById(R.id.schesule_mode_changed);
        selectMonth = (Spinner) rootView.findViewById(R.id.spinner_month);
        selectClassify = (Spinner) rootView.findViewById(R.id.spinner_classify);

        // 初始化選擇日期的Adapter
        selectMonthAdapter = new ToolBarYearMonthSpinnerAdapter(rootView.getContext(), toolBarCalendarData.getYear(), toolBarCalendarData.getMonth());
        selectMonth.setAdapter(selectMonthAdapter);
        selectMonth.setEnabled(false); //实现不可选

        // 初始化選擇事件類型的Adapter
        ToolBarEventTypeSpinnerAdapter selectClassifyAdapter = new ToolBarEventTypeSpinnerAdapter(rootView.getContext());
        selectClassify.setAdapter(selectClassifyAdapter);
    }

    /**
     * 初始化監聽器
     */
    private void initHandler() {
        //  新增事件按鈕的監聽
        ImageView addNewEvnet = (ImageView) rootView.findViewById(R.id.add_new_date_event_btn);
        addNewEvnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                Bundle bundle = new Bundle();
                bundle.putInt("StartedYear", calendar.get(Calendar.YEAR));
                bundle.putInt("StartedMonth", calendar.get(Calendar.MONTH));
                bundle.putInt("StartedDate", calendar.get(Calendar.DATE));
                Intent intent = new Intent();
                intent.setClass(rootView.getContext(), AddNewEventActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //  切換日/月檢視的監聽
        schesuleModeChanged.setOnClickListener(new View.OnClickListener() {
            ProgressDialog progressdialog;

            @Override
            public void onClick(View v) {
                progressdialog = ProgressDialog.show(rootView.getContext(), "", "載入中...", true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            changedFragment(monthSwitchToDay);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            progressdialog.dismiss();
                        }
                    }
                }).start();
            }
        });

        //  ToolBar選擇日期的監聽
        selectMonth.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            int getNowYear;
            int getNowMonth;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (getSynchroDate != SynchroDateModule.getSingleton().getSynchroMonth()) {
                    selectMonth.setSelection(SynchroDateModule.getSingleton().getSynchroMonth(), true);
                    selectMonthAdapter.notifyDataSetChanged();
                    getSynchroDate = SynchroDateModule.getSingleton().getSynchroMonth();
                } else {
                    synchroMonthArray = new Integer[]{};
                    synchroYearArray = new Integer[]{};
                    synchroMonthArray = SynchroDateModule.getSingleton().synchroMonth.toArray(synchroMonthArray);
                    synchroYearArray = SynchroDateModule.getSingleton().synchroYear.toArray(synchroYearArray);
                    //                    getNowYear = SynchroDateModule.getSingleton().getNowYear();

                    // 紀錄ToolBar選取到的年、月
                    getNowYear = synchroYearArray[position];
                    getNowMonth = synchroMonthArray[position];
                    EventClassifyData.EVENT_CLASSIFY_DATA.setToolBarYear(getNowYear);
                    EventClassifyData.EVENT_CLASSIFY_DATA.setToolBarMonth(getNowMonth);
                    callBackDayView.refreshCalendarDayAndWeek();

                    SynchroDateModule.getSingleton().setSynchroMonthAndYear(getNowYear, (synchroMonthArray[position] - 1));
                    callBackMonthView.checkedMonthSynchro(SynchroDateModule.getSingleton().getWeekInMonthPosition());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 事件類型選擇的監聽
        selectClassify.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EventClassifyData.EVENT_CLASSIFY_DATA.setEventClassify(position);
                callBackDayView.refreshDayEventClassify();
                callBackMonthView.refreshMonthViewEventClassify();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(rootView.getContext(), "您沒有選擇任何項目", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 日/月檢視切換
     *
     * @param changedPage true為月檢視，false為日檢視
     */
    private void changedFragment(boolean changedPage) {
        android.support.v4.app.FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        if (changedPage) {
            // 切換至日檢視
            fragmentTransaction.replace(R.id.main_container, dayViewFragment)
                    .addToBackStack(null)
                    .commit();
            monthSwitchToDay = false;
            //            scheduleMainView.findViewById(android.R.id.content).setTag(monthSwitchToDay);
        } else {
            // 切換至月檢視
            fragmentTransaction.replace(R.id.main_container, monthViewFragment)
                    .addToBackStack(null)
                    .commit();
            monthSwitchToDay = true;
        }
    }

    private void showWeekEventFromNotify(String notifyEventDate) {
        Log.d(TAG, notifyEventDate);
        int yearTag = Integer.valueOf(notifyEventDate.substring(0, 4));
        int instanceYear = Calendar.getInstance().get(Calendar.YEAR);
        if (yearTag == instanceYear
                || yearTag == (instanceYear - 1)
                || yearTag == (instanceYear + 1)) {
            //  取得第一個顯示的fragment編號
            DialogEventData dialogEventData = DialogEventData.showDialogEventData;
            dialogEventData.setDialogDayOfWeek(CalculateDayOfWeek(notifyEventDate));
            dialogEventData.setStartDialogYear(yearTag);
            dialogEventData.setStartDialogMonth(Integer.valueOf(notifyEventDate.substring(4, 6)) - 1);
            dialogEventData.setStartDialogDate(Integer.valueOf(notifyEventDate.substring(6)));

            weekEventDialogFragment = new WeekEventDialogFragment();
            weekEventDialogFragment.setStyle(
                    DialogFragment.STYLE_NORMAL,
                    R.style.PageTransparent
            );
            weekEventDialogFragment.show(
                    getActivity().getSupportFragmentManager(), "WeekEventDialog");
        }
    }

    /**
     * 計算某日是星期幾
     *
     * @param theDate 推播通知指定的日期
     * @return int
     */
    private int CalculateDayOfWeek(String theDate) {
        int d = Integer.valueOf(theDate.substring(6));   //日期
        int m = Integer.valueOf(theDate.substring(4, 6)) - 2;   //月數 - 2（1月為11月，2月為12月）
        if (m < 1) {
            if (m == 0) {
                m = 12;
            } else if (m < 0) {
                m = 11;
            }
        }
        int y = Integer.valueOf(theDate.substring(2, 4));   //年數後2位（1、2月份y - 1）
        if ((Integer.valueOf(theDate.substring(4, 6))) < 3) {
            y--;
        }
        int c = Integer.valueOf(theDate.substring(0, 2));   //世紀數- 1
        //套用高斯公式計算公曆（格里曆）
        Log.d(TAG, d + " / " + m + " / " + y + " / " + c);
        int w = (int) (d + ((2.6 * m) - 0.2) + (5 * (y % 4)) + (3 * y) + (5 * (c % 4))) % 7;
        w++;
        return w;
    }

    /**
     * 與MonthViewFragment做接口
     */
    public interface CallBackMonthView {
        void checkedMonthSynchro(int poi);

        void refreshMonthViewEventClassify();

        void synchroMonthAndToolbar(int synchroYear, int synchroMonth);
    }

    /**
     * 與DayViewFragment做接口
     */
    public interface CallBackDayView {
        void refreshDayEventClassify();

        void refreshCalendarDayAndWeek();
    }

    /**
     * GetAuthCode
     */
    protected void getAuthRequest(final Context context, int dialogid, int webid) {
        final Dialog auth_dialog = new Dialog(context);
        final ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(context, getString(R.string.message_title), getString(R.string.msg_tokenget), true, false);
        pb.show();

        auth_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //去除Dialog標題
        auth_dialog.setContentView(dialogid);
        myBrowser = (WebView) auth_dialog.findViewById(webid);
        webServiceConnection.setBrowserProperty(myBrowser);
        webServiceConnection.setWebviewProperty(myBrowser, getContext());

        String webSiteStr = webServiceConnection.AuthServer + "/" + webServiceConnection.AuthRequest
                + "?response_type=code"
                + "&client_id=" + webServiceConnection.CLIENT_ID
                //                + "&secret_key=" + webServiceConnection.CLIENT_SECRET
                + "&redirect_url=" + webServiceConnection.REDIRECT_URI
                + "&display=page"
                + "&scope=user_profile"
                + "&scope=measure_data"
                + "&scope=health_report"
                + "&state=Oauth_Call"
                + "&access_type=online"
                + "&prompt=none"
                + "&login_hint=email";

        Log.v(TAG + "webSiteStr", webSiteStr);
        myBrowser.loadUrl(webSiteStr);

        myBrowser.setWebViewClient(new WebViewClient() {
            boolean authComplete = false;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pb.dismiss();
                if (url.contains("?code=") && authComplete != true) {
                    Uri uri = Uri.parse(url);
                    authCode = uri.getQueryParameter("code");
                    Log.i("", "CODE : " + authCode);
                    authComplete = true;
                    //                    Toast.makeText(context, "Authorization Code is: " + authCode, Toast.LENGTH_SHORT).show();
                    pb.dismiss();
                    auth_dialog.dismiss();
                    new TokenGet().execute();

                } else if (url.contains("error=access_denied")) {
                    Log.i("", "ACCESS_DENIED_HERE");
                    authComplete = true;
                    Toast.makeText(context, getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                    auth_dialog.dismiss();
                }
            }
        });
        auth_dialog.show();
        auth_dialog.setCancelable(true);
    }

    protected class TokenGet extends AsyncTask<String, String, JSONObject> {
        ShowMEProgressDiaLog pb;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb = new ShowMEProgressDiaLog(getContext(), getString(R.string.webview_loading_title), getString(R.string.msg_tokenget), true, false);
            //            pDialog = new ProgressDialog(getContext());
            //            pDialog.setMessage("連接資料庫中，請稍後...");
            //            pDialog.setIndeterminate(false);
            //            pDialog.setCancelable(true);
            //            Code = pref.getString("Code", "");
            pb.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject response = null;
            if (!isCancelled()) {
                try {
                    response = webServiceConnection.getTokenRequest(authCode, webServiceConnection.CLIENT_ID, webServiceConnection.CLIENT_SECRET);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            try {
                if (response != null) {
                    Log.v(TAG + "response", response.toString());
                    if (response.getString("message").equals("Success")) {
                        String access_token = response.getJSONObject("result").getString("access_token");
                        String refresh_token = response.getJSONObject("result").getString("refresh_token");
                        String take_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        Log.d(TAG + "access_token", access_token);
                        Log.d(TAG + "refresh_token", refresh_token);
                        Log.d(TAG + "take_time", take_time);

                        prf.edit().putString("access_token", access_token)
                                .putString("refresh_token", refresh_token)
                                .putString("take_time", take_time)
                                .commit();

                        pb.dismiss();
                        //                        loadPageView();
                        return;
                    } else {
                        Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
            } finally {
                pb.dismiss();
            }
        }
    }

    protected class TokenExp extends AsyncTask<String, String, JSONObject> {
        ShowMEProgressDiaLog pb;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb = new ShowMEProgressDiaLog(getContext(), getString(R.string.webview_loading_title), getString(R.string.msg_tokenexp), true, false);
            //            pDialog = new ProgressDialog(getContext());
            //            pDialog.setMessage("連接資料庫中，請稍後...");
            //            pDialog.setIndeterminate(false);
            //            pDialog.setCancelable(true);
            //            Code = pref.getString("Code", "");
            pb.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject response = null;
            if (!isCancelled()) {
                try {
                    response = webServiceConnection.getTokenExRequest(prf.getString("access_token", ""), prf.getString("refresh_token", ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            try {
                if (response != null) {
                    Log.v(TAG + "response", response.toString());
                    if (response.getString("message").equals("Success")) {
                        String access_token = response.getJSONObject("result").getString("access_token");
                        String refresh_token = response.getJSONObject("result").getString("refresh_token");
                        String take_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        Log.d(TAG + "access_token", access_token);
                        Log.d(TAG + "refresh_token", refresh_token);
                        Log.d(TAG + "take_time", take_time);

                        prf.edit().putString("access_token", access_token)
                                .putString("refresh_token", refresh_token)
                                .putString("take_time", take_time)
                                .commit();

                        pb.dismiss();
                        //                        loadPageView();
                        return;
                    } else {
                        Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
            } finally {
                pb.dismiss();
            }
        }
    }
}
