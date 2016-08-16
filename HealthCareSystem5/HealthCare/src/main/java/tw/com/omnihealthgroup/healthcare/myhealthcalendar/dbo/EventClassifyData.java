package tw.com.omnihealthgroup.healthcare.myhealthcalendar.dbo;

import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Calendar;
import java.util.LinkedList;

import tw.com.omnihealthgroup.healthcare.R;

public class EventClassifyData {
    private static final String TAG = "EventClassifyData";
    public static EventClassifyData EVENT_CLASSIFY_DATA = new EventClassifyData();
    public static boolean DAYVIEW_NOTIFY_CHANGED = false;

    private Calendar dayViewCalendarData;
    private int eventClassify, toolBarYear, toolBarMonth;
    private String monthWeekDay;
    private int monthDay;
    private int middleLineColor;

    private EventClassifyData() {
    }

    /**
     * 取得選擇的事件類型
     */
    public int getEventClassify() {
        return eventClassify;
    }

    /**
     * 配置事件類型以判斷該顯示哪種事件到畫面中
     */
    public void setEventClassify(int eventPosition) {
        eventClassify = eventPosition;
    }

    /**
     * 取得ToolBar選取到的年
     *
     * @return toolBarYear
     */
    public int getToolBarYear() {
        return toolBarYear;
    }

    /**
     * 傳入ToolBar選取到的年
     *
     * @param toolBarYear ToolBar的年
     */
    public void setToolBarYear(int toolBarYear) {
        this.toolBarYear = toolBarYear;
    }

    /**
     * 取得ToolBar選取到的月
     *
     * @return toolBarMonth
     */
    public int getToolBarMonth() {
        return toolBarMonth;
    }

    /**
     * 傳入ToolBar選取到的月
     *
     * @param toolBarMonth ToolBar的月
     */
    public void setToolBarMonth(int toolBarMonth) {
        this.toolBarMonth = toolBarMonth;
    }

    /**
     * 初始化設定單月檢視頁面的日期
     */
    public void setCalendarData() {
        dayViewCalendarData = Calendar.getInstance();
        dayViewCalendarData.set(EVENT_CLASSIFY_DATA.getToolBarYear(),
                (EVENT_CLASSIFY_DATA.getToolBarMonth() - 1), 1);
    }

    /**
     * 取得顯示月份的天數
     *
     * @return 當月份天數的最大值
     */
    public int howManyDaysInMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, getToolBarMonth() - 1);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 判斷DayView要顯示的事件類型
     */
    public void chooseDayEventClassify(LinearLayout[] linearLayoutArray) {
        if (EVENT_CLASSIFY_DATA != null) {
            //全部隱藏
            for (int i = 0; i < linearLayoutArray.length; i++) {
                linearLayoutArray[i].setVisibility(View.GONE);
            }
            //總覽
            if (EVENT_CLASSIFY_DATA.getEventClassify() == 0) {
                for (int i = 0; i < linearLayoutArray.length; i++) {
                    linearLayoutArray[i].setVisibility(View.VISIBLE);
                }
                linearLayoutArray[0].setVisibility(View.GONE);
            } else {
                switch (EVENT_CLASSIFY_DATA.getEventClassify()) {
                    case 0: //總覽
                        linearLayoutArray[0].setVisibility(View.VISIBLE);
                        break;
                    case 1: //門診 - 一般記事
                        linearLayoutArray[1].setVisibility(View.VISIBLE);
                        break;
                    case 2: //會診 - 重要活動
                        linearLayoutArray[2].setVisibility(View.VISIBLE);
                        break;
                    case 3: //會議 - 用藥提醒
                        linearLayoutArray[3].setVisibility(View.VISIBLE);
                        break;
                    case 4: //開刀 - 量測提醒
                        linearLayoutArray[4].setVisibility(View.VISIBLE);
                        break;
                    case 5: //私人行程 - 預約看診
                        linearLayoutArray[5].setVisibility(View.VISIBLE);
                        break;
                    case 6: //健康檢查
                        linearLayoutArray[6].setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        } else {
            Log.e("ERROR", "chooseEventClassify : EVENT_CLASSIFY_DATA = null");
        }

    }

    /**
     * 判斷MonthView要顯示的事件類型
     */
    public void chooseMonthEventClassify(
            int i, LinkedList<LinearLayout> eventBarlist,
            LinkedList<FrameLayout> dateViewList, int[] relativeId,
            int eventTable) {
        for (int p = 1; p < relativeId.length; p++) {
            dateViewList.get(i).findViewById(relativeId[p]).setVisibility(View.GONE);
        }
        if (EVENT_CLASSIFY_DATA != null) {
            //總覽
            if (EVENT_CLASSIFY_DATA.getEventClassify() == 0) {
                eventBarlist.get(i).setVisibility(View.VISIBLE);
                dateViewList.get(i).findViewById(R.id.date_event_dot).setVisibility(View.GONE);
                dateViewList.get(i).findViewById(relativeId[eventTable]).setVisibility(View.VISIBLE);
            } else {
                eventBarlist.get(i).setVisibility(View.GONE);
                dateViewList.get(i).findViewById(R.id.date_event_dot).setVisibility(View.VISIBLE);
            }
        } else {
            Log.e("ERROR", "chooseEventClassify : EVENT_CLASSIFY_DATA = null");
        }
    }

    public void hideMonthEventClassify(LinkedList<RelativeLayout> eventDotlist, LinkedList<LinearLayout> eventBarlist) {
        if (EVENT_CLASSIFY_DATA != null) {
            //全部隱藏
            for (int i = 0; i < 7; i++) {
                eventDotlist.get(i).setVisibility(View.GONE);
                eventBarlist.get(i).setVisibility(View.GONE);
            }
        }
    }

    /**
     * 取得事件類型來顯示middle line
     */
    public int getMiddleLineColor() {

        switch (EVENT_CLASSIFY_DATA.getEventClassify()) {
            case 0:
                break;
            case 1:
                middleLineColor = R.mipmap.eventtaggreen;
                break;
            case 2:
                middleLineColor = R.mipmap.eventtagpurple;
                break;
            case 3:
                middleLineColor = R.mipmap.eventtagyellow;
                break;
            case 4:
                middleLineColor = R.mipmap.eventtagblue;
                break;
            case 5:
                middleLineColor = R.mipmap.eventtaggracegreen;
                break;
            case 6:
                middleLineColor = R.mipmap.eventtaggraceblue;
                break;
        }
        return middleLineColor;
    }

    /**
     * 配置當月每天為星期幾
     */
    public void setCalendarWeek(Resources getWeekResource) {
        switch (dayViewCalendarData.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                monthWeekDay = getWeekResource.getString(R.string.week_sun_day);
                break;
            case 2:
                monthWeekDay = getWeekResource.getString(R.string.week_mon_day);
                break;
            case 3:
                monthWeekDay = getWeekResource.getString(R.string.week_tue_day);
                break;
            case 4:
                monthWeekDay = getWeekResource.getString(R.string.week_wed_day);
                break;
            case 5:
                monthWeekDay = getWeekResource.getString(R.string.week_thu_day);
                break;
            case 6:
                monthWeekDay = getWeekResource.getString(R.string.week_fri_day);
                break;
            case 7:
                monthWeekDay = getWeekResource.getString(R.string.week_sat_day);
                break;
        }
        //        monthWeekDayList.add(monthWeekDay);
        //        monthWeekDayArray = monthWeekDayList.toArray(monthWeekDayArray);
    }

    /**
     * 取得當月每天為星期幾
     *
     * @return 儲存當天星期的String
     */
    public String getCalendarWeek() {
        return monthWeekDay;
    }

    /**
     * 配置當月每天的日期
     */
    public void setCalendarDay() {
        if (EVENT_CLASSIFY_DATA != null) {
            monthDay = dayViewCalendarData.get(Calendar.DATE);
            dayViewCalendarData.add(Calendar.DATE, 1);
        } else {
            Log.e("ERROR", "setCalendarDay : EVENT_CLASSIFY_DATA = null");
        }
    }

    /**
     * 取得當月每天的日期
     *
     * @return 儲存當天日期的String
     */
    public int getCalendarDay() {
        return monthDay;
    }
}
