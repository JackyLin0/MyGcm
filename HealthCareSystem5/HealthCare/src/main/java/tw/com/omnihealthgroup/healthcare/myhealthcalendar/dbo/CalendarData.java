package tw.com.omnihealthgroup.healthcare.myhealthcalendar.dbo;

import java.util.Calendar;

public class CalendarData {
    private static final String TAG = "CalendarData";
    public static CalendarData ToolBarCalendarData = new CalendarData(false);
    public static CalendarData WeekViewCalendarData = new CalendarData(true);

    final Calendar THIS_CALENDAR;
    final int THIS_YEAR;
    final int THIS_MONTH;
    final boolean ifNeedSetSunDayWhenInit;
    String weekViewDateTag;


    /**
     * 初始化ToolBar和View的日期物件
     *
     * @param ifNeedSetSunDayWhenInit 判斷是ToolBar還是View要使用的Calendar物件
     */
    private CalendarData(boolean ifNeedSetSunDayWhenInit) {
        THIS_CALENDAR = Calendar.getInstance();
        THIS_CALENDAR.add(Calendar.YEAR, -1);   //從當月份的一年前開始
        THIS_YEAR = THIS_CALENDAR.get(Calendar.YEAR);
        THIS_MONTH = THIS_CALENDAR.get(Calendar.MONTH);
        this.ifNeedSetSunDayWhenInit = ifNeedSetSunDayWhenInit;
    }

    /**
     * 初始化 Calendar，
     * 判斷要初始化的 Calendar 是 ToolBarCalendarData 還是 WeekViewCalendarData，
     * 若是 WeekViewCalendarData則多設定星期幾為每週第一天和一週至少有幾天。
     *
     * @param dataId 每月的第一週參數，帶入ListView的position
     */
    public void setCalendarInit(int dataId) {
        THIS_CALENDAR.set(THIS_YEAR, THIS_MONTH, 1);    // 設定起始的年月日
        THIS_CALENDAR.set(Calendar.WEEK_OF_MONTH, dataId);  // 設定每個月的第一週

        //Calendar是WeekViewCalendarData時才需要
        if (ifNeedSetSunDayWhenInit) {
            THIS_CALENDAR.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);   // 設定星期幾為每週第一天
            THIS_CALENDAR.setMinimalDaysInFirstWeek(1);
        }
    }

    /**
     * 取得 THIS_CALENDAR 當前的年
     *
     * @return int
     */
    public int getYear() {
        return THIS_CALENDAR.get(Calendar.YEAR);
    }

    /**
     * 設定 THIS_CALENDAR 當前的年
     *
     * @param value 傳入的年
     */
    public void setYear(int value) {
        THIS_CALENDAR.set(Calendar.YEAR, value);
    }

    /**
     * 取得 THIS_CALENDAR 當前的月
     *
     * @return int
     */
    public int getMonth() {
        return THIS_CALENDAR.get(Calendar.MONTH);
    }

    /**
     * 設定 THIS_CALENDAR 當前的月
     *
     * @param value 傳入的月
     */
    public void setMonth(int value) {
        THIS_CALENDAR.set(Calendar.MONTH, value);
    }

    /**
     * 取得 THIS_CALENDAR 當前的日
     *
     * @return int
     */
    public int getDate() {
        return THIS_CALENDAR.get(Calendar.DATE);
    }

    /**
     * 設定 THIS_CALENDAR 當前的日
     *
     * @param value 傳入的日
     */
    public void setDate(int value) {
        THIS_CALENDAR.set(Calendar.DATE, value);
    }

    /**
     * 取得 THIS_CALENDAR 的日為該週的第幾日
     *
     * @return int
     */
    public int getDayInWeek() {
        return THIS_CALENDAR.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 取得 THIS_CALENDAR 的 DAY_OF_MONTH 在該月的第幾週，
     * 例如 DAY_OF_MONTH＝1～7 為該月的第一週（DAY_OF_WEEK_IN_MONTH＝1）
     *
     * @return int
     */
    public int getWeekInMonth() {
        return THIS_CALENDAR.get(Calendar.DAY_OF_WEEK_IN_MONTH);
    }

    /**
     * 取得 THIS_CALENDAR 的週為該年的第幾週
     *
     * @return int
     */
    public int getWeekInYear() {
        return THIS_CALENDAR.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 取得 THIS_CALENDAR 的週為該月的第幾週
     *
     * @return int
     */
    public int getWeekOfMonth() {
        return THIS_CALENDAR.get(Calendar.WEEK_OF_MONTH);
    }

    /**
     * 取得 THIS_CALENDAR 的日為該月的第幾日
     *
     * @return int
     */
    public int getDayInMonth() {
        return THIS_CALENDAR.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 取得 THIS_CALENDAR 的UNIX時間戳記
     *
     * @return long
     */
    public long getCalenderTimeInMills() {
        return THIS_CALENDAR.getTimeInMillis();
    }

    /**
     * 設置 THIS_CALENDAR 的UNIX時間戳記
     *
     * @param calenderTimeInMills long
     */
    public void setCalenderTimeInMills(long calenderTimeInMills) {
        THIS_CALENDAR.setTimeInMillis(calenderTimeInMills);
    }

    /**
     * THIS_CALENDAR 當前的月增加 num
     *
     * @param num 月增加的數量
     * @return int
     */
    public int addMonth(int num) {
        THIS_CALENDAR.add(Calendar.MONTH, num);
        return THIS_CALENDAR.get(Calendar.MONTH);
    }

    /**
     * THIS_CALENDAR 當前的年增加 num
     *
     * @param num 年增加的數量
     * @return int
     */
    public int addYear(int num) {
        THIS_CALENDAR.add(Calendar.YEAR, num);
        return THIS_CALENDAR.get(Calendar.YEAR);
    }

    /**
     * THIS_CALENDAR 當前的日增加 num
     *
     * @param addDay 日增加的數量
     */
    public void addDay(int addDay) {
        THIS_CALENDAR.add(Calendar.DATE, addDay);
    }

    public String getWeekViewDateTag() {
        //        weekViewDateTag = String.format("%s", String.valueOf(getYear()) + String.valueOf(getMonth()) + String.valueOf(getDate()));
        weekViewDateTag = Integer.toString(getDate());
        return weekViewDateTag;
    }

    /**
     * 初始化ToolBar日期，主要用在onDestroy時復原日期，以免因裝置載入過多app導致頁面強制關閉時，
     * 造成ToolBar日期繼續累加的狀況
     */
    public void initToolBarYearAndMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        ToolBarCalendarData.setYear(calendar.get(Calendar.YEAR));
        ToolBarCalendarData.setMonth(calendar.get(Calendar.MONTH));
    }
}
