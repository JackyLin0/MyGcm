package tw.com.omnihealthgroup.healthcare.myhealthcalendar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.WebServiceConnection;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.adapter.EventViewAdapter;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.adapter.MonthViewListAdapter;
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
import tw.com.omnihealthgroup.healthcare.myhealthvideo.RecyclerItemClickListener;

/**
 * 行事曆月檢視頁面
 */
public class MonthViewFragment extends Fragment implements MyHealthCalendarFragment.CallBackMonthView {
    private static final String TAG = "MonthViewFragment";
    private View MonthView;
    private ListView monthViewListView;
    private RecyclerView eventViewListView;

    private MonthViewListAdapter weekDateAdapter;
    private EventViewAdapter eventViewAdapter;
    private AlphaInAnimationAdapter alphaAdapter;
    private TextView eventTextView;
    private LinkedHashMap<String, List<Object>> dayEventCheckList;
    private int listCount, eventTable;
    private String eventDataStr = null, datatemp = "1";
    private boolean flag = true;

    private SharedPreferences prf;
    private WebServiceConnection webServiceConnection;

    public MonthViewFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        prf = getActivity().getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
        webServiceConnection = new WebServiceConnection();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MonthView = inflater.inflate(R.layout.fragment_monthview_main, container, false);
        Log.v(TAG, "onCreateView");

        initListView();
        initHandler();

        //        eventListView();

        return MonthView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v(TAG, "onViewCreated");

        //指定到現在的月份
        Calendar nowCalendar = Calendar.getInstance();
        synchroMonthAndToolbar(nowCalendar.get(Calendar.YEAR), nowCalendar.get(Calendar.MONTH));

        flag = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");

        int goToPosition = SynchroDateModule.getSingleton().getWeekInMonthPosition();
        checkedMonthSynchro(goToPosition);
        weekDateAdapter.notifyDataSetChanged();
        //        Toast.makeText(getActivity(), "MonthView onResume", Toast.LENGTH_SHORT).show();

        if (!datatemp.equals("1") && flag == true) {
            eventDataStr = datatemp;
        } else {
            eventDataStr = new SimpleDateFormat("yyyyMM").format(new Date());
            flag = true;
        }
        eventListView(eventDataStr);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
        //        Toast.makeText(getActivity(), "MonthView onPause", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
        //        Toast.makeText(getActivity(), "MonthView onStop", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
        //        Toast.makeText(getActivity(), "MonthView onDestroy", Toast.LENGTH_SHORT).show();
    }

    private void initListView() {
        // 初始化Calendar
        monthViewListView = (ListView) MonthView.findViewById(R.id.calendar_listView);
        eventViewListView = (RecyclerView) MonthView.findViewById(R.id.eventview_listView);

        eventTextView = (TextView) MonthView.findViewById(R.id.calendar_no_message);

        weekDateAdapter = new MonthViewListAdapter(getActivity());
        monthViewListView.setAdapter(weekDateAdapter);

        //创建默认的线性LayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        eventViewListView.setLayoutManager(linearLayoutManager);//这里用线性显示 类似于listview
        //        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));//这里用线性宫格显示 类似于grid view
        //        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL));//这里用线性宫格显示 类似于瀑布流
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        eventViewListView.setHasFixedSize(true);

        eventViewAdapter = new EventViewAdapter(getContext());
        alphaAdapter = new AlphaInAnimationAdapter(eventViewAdapter);
        eventViewListView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));
    }

    public void initHandler() {
        eventViewListView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.d(TAG, "onItemClick : postion " + position);
                        //                                                synchroMonthAndToolbar(Integer.parseInt(todayString.substring(0, 4)), Integer.parseInt(todayString.substring(4, 6)) - 1);

                        final ArrayList<String> arrayList = new ArrayList<>();
                        String count = "1";
                        int num = 0;
                        for (int i = 0; i < dayEventCheckList.get(ScheduleDAO.TABLE_CLINIC).size(); i++) {
                            //                            Clinic clinic = (Clinic) dayEventCheckList.get(ScheduleDAO.TABLE_CLINIC).get(i);
                            //                            if (!clinic.getClinicMsgPk().equals(count)) {
                            arrayList.add("clinic" + i);
                            //                                count = clinic.getClinicMsgPk();
                            //                                num++;
                            //                            }
                        }
                        //                        count = "1";
                        //                        num = 0;
                        for (int i = 0; i < dayEventCheckList.get(ScheduleDAO.TABLE_CONSULTATION).size(); i++) {
                            //                            Consultation consultation = (Consultation) dayEventCheckList.get(ScheduleDAO.TABLE_CONSULTATION).get(i);
                            //                            if (!consultation.getConsultationMsgPk().equals(count)) {
                            arrayList.add("consultation" + i);
                            //                                count = consultation.getConsultationMsgPk();
                            //                                num++;
                            //                            }
                        }
                        //                        count = "1";
                        //                        num = 0;
                        for (int i = 0; i < dayEventCheckList.get(ScheduleDAO.TABLE_OPERATE).size(); i++) {
                            //                            Operation operation = (Operation) dayEventCheckList.get(ScheduleDAO.TABLE_OPERATE).get(i);
                            //                            if (!operation.getOperateMsgPk().equals(count)) {
                            arrayList.add("operation" + i);
                            //                                count = operation.getOperateMsgPk();
                            //                                num++;
                            //                            }
                        }
                        //                        count = "1";
                        //                        num = 0;
                        for (int i = 0; i < dayEventCheckList.get(ScheduleDAO.TABLE_MEETING).size(); i++) {
                            //                            Meeting meeting = (Meeting) dayEventCheckList.get(ScheduleDAO.TABLE_MEETING).get(i);
                            //                            if (!meeting.getMeetingMsgPk().equals(count)) {
                            arrayList.add("meeting" + i);
                            //                                count = meeting.getMeetingMsgPk();
                            //                                num++;
                            //                            }
                        }
                        //                        count = "1";
                        //                        num = 0;
                        for (int i = 0; i < dayEventCheckList.get(ScheduleDAO.TABLE_PERSONAL).size(); i++) {
                            //                            Personal personal = (Personal) dayEventCheckList.get(ScheduleDAO.TABLE_PERSONAL).get(i);
                            //                            if (!personal.getPersonalMsgPk().equals(count)) {
                            arrayList.add("personal" + i);
                            //                                count = personal.getPersonalMsgPk();
                            //                                num++;
                            //                            }
                        }
                        //                        count = "1";
                        //                        num = 0;
                        for (int i = 0; i < dayEventCheckList.get(ScheduleDAO.TABLE_EXAMINATION).size(); i++) {
                            //                            Examination examination = (Examination) dayEventCheckList.get(ScheduleDAO.TABLE_EXAMINATION).get(i);
                            //                            if (!examination.getExaminationMsgPk().equals(count)) {
                            arrayList.add("examination" + i);
                            //                                count = examination.getExaminationMsgPk();
                            //                                num++;
                            //                            }
                        }

                        editEvent(position, arrayList);
                    }

                    @Override
                    public void onLongClick(View view, int posotion) {
                        Log.d(TAG, "onLongClick position : " + posotion);
                    }
                }));

        monthViewListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE:    // 滾動停止
                        try {
                            int childPosition;
                            if (monthViewListView.getFirstVisiblePosition() == 0) {
                                childPosition = 1;
                            } else if (monthViewListView.getLastVisiblePosition() == (monthViewListView.getCount() - 1)) {
                                childPosition = 6;
                            } else {
                                childPosition = 4;
                            }
                            TextView monthViewDate_sun = (TextView) monthViewListView
                                    .getChildAt(childPosition)
                                    .findViewById(R.id.sun_d)
                                    .findViewById(R.id.monthview_date);
                            int synchroYear = (int) monthViewDate_sun.getTag(R.id.synchro_year);
                            int synchroMonth = (int) monthViewDate_sun.getTag(R.id.synchro_month);
                            int synchroDay = (int) monthViewDate_sun.getTag(R.id.synchro_date);
                            System.out.println("MonthViewFragment : " + synchroYear + " / " + synchroMonth + " / " + synchroDay);

                            //處理滾動停止後撈取後台資料
                            String fromDate, toDate;
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(synchroYear, synchroMonth, 1);
                            calendar.add(Calendar.MONTH, -1);
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(String.valueOf(calendar.get(Calendar.YEAR)));
                            stringBuilder.append("-");
                            if ((calendar.get(Calendar.MONTH) + 1) < 10) {
                                stringBuilder.append("0");
                            }
                            stringBuilder.append(String.valueOf(calendar.get(Calendar.MONTH) + 1));
                            stringBuilder.append("-01 00:00:00");

                            fromDate = stringBuilder.toString();

                            calendar.add(Calendar.MONTH, 2);
                            stringBuilder.delete(0, stringBuilder.length());
                            stringBuilder.append(String.valueOf(calendar.get(Calendar.YEAR)));
                            stringBuilder.append("-");
                            if ((calendar.get(Calendar.MONTH) + 1) > 10) {
                                stringBuilder.append("0");
                            }
                            stringBuilder.append(String.valueOf(calendar.get(Calendar.MONTH) + 1));
                            stringBuilder.append("-");
                            stringBuilder.append(String.valueOf(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)));
                            stringBuilder.append(" 23:59:59");
                            toDate = stringBuilder.toString();

                            connectData(fromDate, toDate);

                            // 帶入時間與ToolBar取得同步
                            synchroMonthAndToolbar(synchroYear, synchroMonth);

                            StringBuilder synchroMonth_Sbr = new StringBuilder();
                            if ((synchroMonth + 1) < 10) {
                                synchroMonth_Sbr.append("0");
                            }
                            synchroMonth_Sbr.append(synchroMonth + 1);
                            String dataStr = String.valueOf(synchroYear) + String.valueOf(synchroMonth_Sbr);

                            if (!dataStr.equals(datatemp)) {
                                eventListView(dataStr);
                            }

                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    public void eventListView(final String todayString) {
        datatemp = todayString;
        try {
            //載入當日資料
            final ScheduleDAO scheduleDAO = new ScheduleDAO(getContext());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    dayEventCheckList = new LinkedHashMap<>();
                    eventTable = EventClassifyData.EVENT_CLASSIFY_DATA.getEventClassify();
                    try {
                        listCount = 0;
                        for (int i = 0; i < ScheduleDAO.TABLE_ARRAY.length; i++) {
                            List<Object> searchDayEventList = scheduleDAO.getDayEvent_month(i, todayString);
                            switch (ScheduleDAO.TABLE_ARRAY[i]) {
                                case ScheduleDAO.TABLE_CLINIC:
                                    dayEventCheckList.put(ScheduleDAO.TABLE_CLINIC, searchDayEventList);
                                    listCount = listCount + searchDayEventList.size();

                                    Log.v(TAG + "dayEventCheckList", String.valueOf(dayEventCheckList));
                                    break;

                                case ScheduleDAO.TABLE_CONSULTATION:
                                    dayEventCheckList.put(ScheduleDAO.TABLE_CONSULTATION, searchDayEventList);
                                    listCount = listCount + searchDayEventList.size();
                                    break;

                                case ScheduleDAO.TABLE_OPERATE:
                                    dayEventCheckList.put(ScheduleDAO.TABLE_OPERATE, searchDayEventList);
                                    listCount = listCount + searchDayEventList.size();
                                    break;

                                case ScheduleDAO.TABLE_MEETING:
                                    dayEventCheckList.put(ScheduleDAO.TABLE_MEETING, searchDayEventList);
                                    listCount = listCount + searchDayEventList.size();
                                    break;

                                case ScheduleDAO.TABLE_PERSONAL:
                                    dayEventCheckList.put(ScheduleDAO.TABLE_PERSONAL, searchDayEventList);
                                    listCount = listCount + searchDayEventList.size();
                                    break;

                                case ScheduleDAO.TABLE_EXAMINATION:
                                    dayEventCheckList.put(ScheduleDAO.TABLE_EXAMINATION, searchDayEventList);
                                    listCount = listCount + searchDayEventList.size();
                                    break;
                            }
                        }

                        //                        }
                        //                        dialogEventListAdapter.setCount(listCount);
                        //                        dialogEventListAdapter.setDayEventList(dayEventCheckList, eventTable);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                eventViewAdapter = new EventViewAdapter(getContext());
                                alphaAdapter = new AlphaInAnimationAdapter(eventViewAdapter);

                                if (listCount == 0) {
                                    eventTextView.setText(todayString.substring(0, 4) + "年" + todayString.substring(4, 6) + "月" + "\n" + getResources().getString(R.string.calendar_no_message));
                                    eventTextView.setVisibility(View.VISIBLE);
                                    eventViewListView.setVisibility(View.GONE);
                                } else {
                                    eventTextView.setVisibility(View.GONE);
                                    eventViewListView.setVisibility(View.VISIBLE);

                                    eventViewAdapter.setCount(listCount);
                                    eventViewAdapter.setDayEventList(dayEventCheckList, eventTable);
                                    eventViewListView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));
                                    //                                eventViewAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkedMonthSynchro(int snychroPosition) {
        //        if (Math.abs(monthViewListView.getFirstVisiblePosition() - snychroPosition) > 12) {
        monthViewListView.setSelection(snychroPosition);
        //        } else {
        //            monthViewListView.smoothScrollToPositionFromTop(snychroPosition, 0, 500);
        //        }
        //        System.out.println("position : " + snychroPosition);
    }

    @Override
    public void refreshMonthViewEventClassify() {
        if (weekDateAdapter != null) {
            weekDateAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void synchroMonthAndToolbar(int synchroYear, int synchroMonth) {
        SynchroDateModule.getSingleton().setSynchroMonthAndYear(synchroYear, synchroMonth);
        int getSynchroDate = SynchroDateModule.getSingleton().getSynchroMonth();
        SynchroDateModule.getSingleton().setSynchroDate(synchroYear, synchroMonth);

        if (getSynchroDate != SynchroDateModule.getSingleton().getSynchroMonth()) {
            // 自動磁性滑動貼齊頂部
            int synchroPosition = SynchroDateModule.getSingleton().getWeekInMonthPosition();
            //            monthViewListView.smoothScrollToPositionFromTop(synchroPosition, 0);
            //            weekDateAdapter.notifyDataSetChanged();
            System.out.println(TAG + "synchroPosition : " + synchroPosition);
        }
    }


    //    private void editEvent(AlertDialog alertDialog, int position, ArrayList<String> arrayList) {
    private void editEvent(int position, ArrayList<String> arrayList) {
        //        alertDialog.dismiss();
        Bundle bundle = new Bundle();

        switch (arrayList.get(position).substring(0, 5)) {
            //            case "clinic":
            case "clini":
                Clinic clinicObj = null;
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(position).equals("clinic" + i)) {
                        clinicObj = (Clinic) dayEventCheckList.get(ScheduleDAO.TABLE_CLINIC).get(i);
                    }
                }
                //                bundle.putInt("StartedYear", getArguments().getInt("FragmentYear"));
                //                bundle.putInt("StartedMonth", (getArguments().getInt("FragmentMonth") - 1));
                //                bundle.putInt("StartedDate", getArguments().getInt("FragmentDate"));
                bundle.putSerializable("clinicObj", clinicObj);

                break;

            //            case "consultation":
            case "consu":
                Consultation consultationObj = null;
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(position).equals("consultation" + i)) {
                        consultationObj = (Consultation) dayEventCheckList.get(ScheduleDAO.TABLE_CONSULTATION).get(i);
                    }
                }
                //                bundle.putInt("StartedYear", getArguments().getInt("FragmentYear"));
                //                bundle.putInt("StartedMonth", (getArguments().getInt("FragmentMonth") - 1));
                //                bundle.putInt("StartedDate", getArguments().getInt("FragmentDate"));
                bundle.putSerializable("consultationObj", consultationObj);

                break;

            //            case "operation":
            case "opera":
                Operation operationObj = null;
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(position).equals("operation" + i)) {
                        operationObj = (Operation) dayEventCheckList.get(ScheduleDAO.TABLE_OPERATE).get(i);
                    }
                }
                //                bundle.putInt("StartedYear", getArguments().getInt("FragmentYear"));
                //                bundle.putInt("StartedMonth", (getArguments().getInt("FragmentMonth") - 1));
                //                bundle.putInt("StartedDate", getArguments().getInt("FragmentDate"));
                bundle.putSerializable("operationObj", operationObj);

                break;

            //            case "meeting":
            case "meeti":
                Meeting meetingObj = null;
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(position).equals("meeting" + i)) {
                        meetingObj = (Meeting) dayEventCheckList.get(ScheduleDAO.TABLE_MEETING).get(i);
                    }
                }
                //                bundle.putInt("StartedYear", getArguments().getInt("FragmentYear"));
                //                bundle.putInt("StartedMonth", (getArguments().getInt("FragmentMonth") - 1));
                //                bundle.putInt("StartedDate", getArguments().getInt("FragmentDate"));
                bundle.putSerializable("meetingObj", meetingObj);

                break;

            //            case "personal":
            case "perso":
                Personal personalObj = null;
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(position).equals("personal" + i)) {
                        personalObj = (Personal) dayEventCheckList.get(ScheduleDAO.TABLE_PERSONAL).get(i);
                    }
                }
                //                bundle.putInt("StartedYear", getArguments().getInt("FragmentYear"));
                //                bundle.putInt("StartedMonth", (getArguments().getInt("FragmentMonth") - 1));
                //                bundle.putInt("StartedDate", getArguments().getInt("FragmentDate"));
                bundle.putSerializable("personalObj", personalObj);

                break;

            //            case "examination":
            case "exami":
                Examination examinationObj = null;
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(position).equals("examination" + i)) {
                        examinationObj = (Examination) dayEventCheckList.get(ScheduleDAO.TABLE_EXAMINATION).get(i);
                    }
                }
                //                bundle.putInt("StartedYear", getArguments().getInt("FragmentYear"));
                //                bundle.putInt("StartedMonth", (getArguments().getInt("FragmentMonth") - 1));
                //                bundle.putInt("StartedDate", getArguments().getInt("FragmentDate"));
                bundle.putSerializable("examinationObj", examinationObj);

                break;
        }

        Intent intent = new Intent();
        intent.setClass(getActivity(), AddNewEventActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    /**
     * 連接網路取得行事曆各頁面資料，
     * 首次下載資料的時候會從最早的日期開始下載（花費時間較長），
     * 之後便會從最後一次下載的資料日期開始下載。
     */
    private void connectData(final String fromDate, final String toDate) {
        final ScheduleDAO scheduleDAO = new ScheduleDAO(getContext());
        final int msgPkTableSize = scheduleDAO.getAll(ScheduleDAO.TABLE_LAST_MSG_PK).size();
        //        String loadingMessage =
        //                (msgPkTableSize > 0) ?
        //                        getResources().getString(R.string.download_data) :
        //                        getResources().getString(R.string.download_data_first);
        //        final ProgressDialog showMEProgressDiaLog = new ShowMEProgressDiaLog(
        //                getContext(),
        //                "",
        //                loadingMessage,
        //                true,
        //                false
        //        );
        //        showMEProgressDiaLog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //                WebServiceConnection connectServerData = new WebServiceConnection();
                LinkedList<JSONObject> GET_JSON_OBJ_LIST = new LinkedList<>();
                try {
                    if (msgPkTableSize > 0) {
                        System.out.println(TAG + "msgPkTableSize / " + msgPkTableSize);
                    }
                    //                        connectServerData.getScheduleAndNewsData(String.valueOf(MyAccountManager.GetAccountName()), lastMsgPk);
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
                    //                    WebServiceConnection.GET_JSON_OBJ_LIST.clear();
                    GET_JSON_OBJ_LIST.clear();
                    //                    showMEProgressDiaLog.dismiss();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            weekDateAdapter.notifyDataSetChanged();
                        }
                    });
                    //                            System.out.println(TAG + "LAST MsgPk / " + lastMsgPk);
                }
            }
        }).start();
    }
}
