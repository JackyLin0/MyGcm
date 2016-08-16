package tw.com.omnihealthgroup.healthcare.myhealthcalendar.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.LinkedList;

import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.dbo.CalendarData;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.dbo.EventClassifyData;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.dbo.ScheduleDAO;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.dbo.SynchroDateModule;

public class MonthViewListAdapter extends BaseAdapter {
    private static final String TAG = "MonthViewListAdapter";
    private Context context;
    private LayoutInflater monthViewListAdapterLayoutInflater;
    private EventClassifyData eventClassifyData;
    private int howManyWeeks;
    private ScheduleDAO scheduleDAO;

    public MonthViewListAdapter(Context context) {
        this.context = context;
        monthViewListAdapterLayoutInflater = LayoutInflater.from(context);
        eventClassifyData = EventClassifyData.EVENT_CLASSIFY_DATA;
        SynchroDateModule synchroDateModule = SynchroDateModule.getSingleton();
        synchroDateModule.registerBaseAdapter(this);   // 註冊監聽與toolbar的日期同步

        /**
         * 設定總星期數（總列數）
         */
        //計算今年與明年列數的日期物件
        Calendar thisYearCalendar = Calendar.getInstance();
        thisYearCalendar.set(thisYearCalendar.get(Calendar.YEAR), thisYearCalendar.get(Calendar.MONTH), 1);
        //計算去年列數的日期物件
        Calendar lastYearCalendar = Calendar.getInstance();
        lastYearCalendar.set(lastYearCalendar.get(Calendar.YEAR), lastYearCalendar.get(Calendar.MONTH), 1);
        lastYearCalendar.add(Calendar.YEAR, -1);
        int lastYearDays = lastYearCalendar.getActualMaximum(Calendar.DAY_OF_YEAR);  //去年有幾天
        Log.v(TAG, String.valueOf(lastYearDays));

        howManyWeeks = (int) Math.floor((lastYearDays - (lastYearCalendar.get(Calendar.DAY_OF_YEAR))) / 7); //去年本月份到年底有幾週
        int thisYearDays = thisYearCalendar.getActualMaximum(Calendar.DAY_OF_YEAR);  //今年有幾天
        howManyWeeks += (thisYearDays / 7); //去年週數 + 今年週數
        Log.v(TAG, String.valueOf(howManyWeeks));

        thisYearCalendar.add(Calendar.YEAR, 1);
        int beyondYearDays = thisYearCalendar.getActualMaximum(Calendar.DAY_OF_YEAR); //明年有幾天
        Log.v(TAG, String.valueOf(thisYearCalendar));

        thisYearCalendar.add(Calendar.YEAR, 2);
        int beyondYearDays_two = thisYearCalendar.getActualMaximum(Calendar.DAY_OF_YEAR); //後年有幾天
        Log.v(TAG, String.valueOf(beyondYearDays_two));

        thisYearCalendar.add(Calendar.YEAR, 3);
        int beyondYearDays_three = thisYearCalendar.getActualMaximum(Calendar.DAY_OF_YEAR); //大後年有幾天
        Log.v(TAG, String.valueOf(beyondYearDays_three));

        howManyWeeks = (int) (howManyWeeks + Math.floor(beyondYearDays / 7)
                + Math.floor(beyondYearDays_two / 7)
                + Math.floor(beyondYearDays_three / 7) + 3);//去年週數 + 今年週數 + 明年週數 + 3週緩衝置底緩衝列數

        Log.v(TAG, String.valueOf(howManyWeeks));

        //初始化資料庫物件
        scheduleDAO = new ScheduleDAO(context);
    }

    @Override
    public int getCount() {
        return howManyWeeks;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position + 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CalendarData.WeekViewCalendarData.setCalendarInit((int) getItemId(position));
        final MonthViewHolder monthViewHolder;
        if (convertView == null) {
            monthViewHolder = new MonthViewHolder();
            convertView = monthViewListAdapterLayoutInflater.inflate(
                    R.layout.monthview_weekview, parent, false);

            // 產生每日的View實體物件
            for (int i = 0; i < monthViewHolder.textViewArray.length; i++) {
                monthViewHolder.textViewArray[i] =
                        (TextView) convertView
                                .findViewById(MonthViewHolder.tvId[i])
                                .findViewById(R.id.monthview_date);

                monthViewHolder.dateEventDotList.add(
                        (RelativeLayout) convertView
                                .findViewById(MonthViewHolder.tvId[i])
                                .findViewById(R.id.date_event_dot)
                );

                // DateEventBarVisibility
                monthViewHolder.dateEventBarList.add(
                        (LinearLayout) convertView
                                .findViewById(MonthViewHolder.tvId[i])
                                .findViewById(R.id.date_event_bar)
                );
                monthViewHolder.dateViewList.add(
                        (FrameLayout) convertView.findViewById(MonthViewHolder.tvId[i])
                );
            }
            convertView.setTag(monthViewHolder);
        } else {
            monthViewHolder = (MonthViewHolder) convertView.getTag();
        }

        // 取得日期與畫面設定
        boolean checkedMonth;
        boolean checkedYear;
        //設定起始日期
        final Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(
                CalendarData.WeekViewCalendarData.getYear(),
                CalendarData.WeekViewCalendarData.getMonth(),
                CalendarData.WeekViewCalendarData.getDate()
        );

        for (int i = 0; i < 7; i++) {
            checkedMonth =
                    CalendarData.WeekViewCalendarData.getMonth()
                            == SynchroDateModule.getSingleton().getNowMonth();
            checkedYear =
                    CalendarData.WeekViewCalendarData.getYear()
                            == Calendar.getInstance().get(Calendar.YEAR);

            // 填入日期，並紀錄日期的資訊
            monthViewHolder.textViewArray[i].setTag( // 紀錄所在ListView列數
                    R.id.synchro_position,
                    position
            );
            monthViewHolder.textViewArray[i].setTag( // 紀錄年份
                    R.id.synchro_year,
                    CalendarData.WeekViewCalendarData.getYear()
            );
            monthViewHolder.textViewArray[i].setTag( // 紀錄月份
                    R.id.synchro_month,
                    CalendarData.WeekViewCalendarData.getMonth()
            );
            monthViewHolder.textViewArray[i].setTag( // 紀錄日期
                    R.id.synchro_date,
                    CalendarData.WeekViewCalendarData.getDate()
            );
            monthViewHolder.textViewArray[i].setTag( // 紀錄在本月第幾天
                    R.id.synchro_day_in_month,
                    CalendarData.WeekViewCalendarData.getDayInMonth()
            );
            monthViewHolder.textViewArray[i].setTag( // 紀錄在本週第幾天
                    R.id.synchro_day_in_week,
                    CalendarData.WeekViewCalendarData.getDayInWeek()
            );
            monthViewHolder.textViewArray[i].setTag( // 紀錄在本月第幾週
                    R.id.synchro_week_in_month,
                    CalendarData.WeekViewCalendarData.getWeekOfMonth()
            );

            // 當日期為該月份1號時
            if (CalendarData.WeekViewCalendarData.getDayInMonth() == 1) {
                SynchroDateModule.getSingleton().setDateSpinnerPosition(position);
            }

            // 寫上日期
            monthViewHolder.textViewArray[i]
                    .setText(String.format(
                            "%d",
                            CalendarData.WeekViewCalendarData.getDate()
                    ));

            // 確認是否為今天
            boolean checkedDay =
                    Integer.parseInt((monthViewHolder.textViewArray[i].getText()).toString())
                            == Calendar.getInstance().get(Calendar.DATE);

            // 週間日期的背景色（白底灰框）
            monthViewHolder.textViewArray[i]
                    .setBackgroundResource(
                            R.drawable.shape_monthview_week_date
                    );

            // 填入週末背景
            if (i == 0 || i == 6) {
                // 週末日期的背景色（灰底灰框）
                monthViewHolder.textViewArray[i]
                        .setBackgroundResource(
                                R.drawable.shape_monthview_weekend_date
                        );
            }

            // 填入日期數字顏色
            if (checkedMonth) {
                // 當月數字顏色
                monthViewHolder.textViewArray[i].setTextColor(0xff000000);
            } else {
                // 非當月數字顏色
                monthViewHolder.textViewArray[i].setTextColor(0xffcccccc);
            }
            // 填入今天的外框與顏色
            if (
                    checkedDay
                            && (
                            CalendarData.WeekViewCalendarData.getMonth()
                                    == Calendar.getInstance().get(Calendar.MONTH)
                    )
                            && checkedYear
                    ) {
                // 橘色外框
                monthViewHolder.textViewArray[i].setBackgroundResource(R.mipmap.touchedtoday);
                // 橘色數字
                monthViewHolder.textViewArray[i].setTextColor(0xfffd6334);
            }
            //            // 日期 + 1
            CalendarData.WeekViewCalendarData.addDay(1);

            //初始化事件篩選
            monthViewHolder.dateViewList
                    .get(i)
                    .findViewById(R.id.date_event_dot)
                    .setVisibility(View.GONE);
            monthViewHolder.dateEventBarList.get(i).setVerticalGravity(View.GONE);
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        //事件類別的顯示與篩選
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 7; i++) {
                    final int dayInWeek = i;
                    int table = eventClassifyData.getEventClassify();
                    //組合查詢日期的字串
                    StringBuilder startTime = new StringBuilder();
                    startTime.append(String.valueOf(startCalendar.get(Calendar.YEAR)));
                    if ((startCalendar.get(Calendar.MONTH) + 1) < 10) {
                        startTime.append("0");
                    }
                    startTime.append(String.valueOf(startCalendar.get(Calendar.MONTH) + 1));
                    if (startCalendar.get(Calendar.DATE) < 10) {
                        startTime.append("0");
                    }
                    startTime.append(String.valueOf(startCalendar.get(Calendar.DATE)));

                    //紀錄有事件的類別
                    //初始化總覽
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            monthViewHolder.dateEventBarList
                                    .get(dayInWeek)
                                    .setVerticalGravity(View.VISIBLE);
                            for (int j = 1; j < (ScheduleDAO.TABLE_ARRAY.length); j++) {
                                monthViewHolder.dateViewList
                                        .get(dayInWeek)
                                        .findViewById(monthViewHolder.relativeId[j])
                                        .setVisibility(View.GONE);
                            }
                        }
                    });
                    //顯示當日有無事件
                    if (table == 0) {   //總覽
                        for (int k = 1; k < ScheduleDAO.TABLE_ARRAY.length; k++) {
                            //顯示當日有哪些事件
                            if (scheduleDAO.hasDateEvent(k, startTime.toString())) {
                                final int eventType = k;
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        monthViewHolder.dateViewList
                                                .get(dayInWeek)
                                                .findViewById(monthViewHolder.relativeId[eventType])
                                                .setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        }
                    } else {    //其他事件類別
                        //顯示當日有哪些事件
                        if (scheduleDAO.hasDateEvent(table, startTime.toString())) {
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    monthViewHolder.dateViewList.get(dayInWeek).findViewById(R.id.date_event_dot).setVisibility(View.VISIBLE);
                                    monthViewHolder.dateEventBarList.get(dayInWeek).setVerticalGravity(View.GONE);
                                }
                            });
                        }
                    }
                    // 日期 + 1
                    startCalendar.add(Calendar.DATE, 1);
                }
            }
        }).start();
        return convertView;
    }

    public static class MonthViewHolder {
        // 日期
        static int[] tvId = new int[]{R.id.sun_d, R.id.mon_d, R.id.tue_d,
                R.id.wed_d, R.id.thu_d, R.id.fri_d, R.id.sat_d};
        TextView monthViewDate_sun, monthViewDate_mon, monthViewDate_tue, monthViewDate_wed,
                monthViewDate_thu, monthViewDate_fri, monthViewDate_sat;
        TextView[] textViewArray = new TextView[]{
                monthViewDate_sun, monthViewDate_mon, monthViewDate_tue,
                monthViewDate_wed, monthViewDate_thu, monthViewDate_fri,
                monthViewDate_sat};

        // 事件類型
        int[] relativeId = new int[]{
                0,
                R.id.date_event_green, R.id.date_event_purple,
                R.id.date_event_yellow, R.id.date_event_blue,
                R.id.date_event_gracegreen, R.id.date_event_graceblue
        };
        LinkedList<LinearLayout> dateEventBarList = new LinkedList<>();
        LinkedList<RelativeLayout> dateEventDotList = new LinkedList<>();
        LinkedList<FrameLayout> dateViewList = new LinkedList<>();
    }
}
