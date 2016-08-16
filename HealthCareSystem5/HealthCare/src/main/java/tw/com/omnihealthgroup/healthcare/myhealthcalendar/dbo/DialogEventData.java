package tw.com.omnihealthgroup.healthcare.myhealthcalendar.dbo;


import java.util.ArrayList;
import java.util.Calendar;

public class DialogEventData {
    private static final String TAG = "DialogEventData";
    public static Calendar DialogEventCalendarData = Calendar.getInstance();
    public static DialogEventData showDialogEventData = new DialogEventData();
    int startFragment;
    Integer[] yearArray, monthArray, dateArray;

    /**
     * 取得點選日期的年
     *
     * @return DialogEventCalendarData中的年
     */
    public int getStartDialogYear() {
        return DialogEventCalendarData.get(Calendar.YEAR);
    }

    /**
     * 設定點選日期的年到DialogEventCalendarData中
     *
     * @param startDialogYear 年
     */
    public void setStartDialogYear(int startDialogYear) {
        DialogEventCalendarData.set(Calendar.YEAR, startDialogYear);
    }

    /**
     * 取得點選日期的月
     *
     * @return DialogEventCalendarData中的月
     */
    public int getStartDialogMonth() {
        return DialogEventCalendarData.get(Calendar.MONTH) + 1;
    }

    /**
     * 設定點選日期的月到DialogEventCalendarData中
     *
     * @param startDialogMonth 月
     */
    public void setStartDialogMonth(int startDialogMonth) {
        DialogEventCalendarData.set(Calendar.MONTH, startDialogMonth);
    }

    /**
     * 取得點選日期的日
     *
     * @return DialogEventCalendarData中的日
     */
    public int getStartDialogDate() {
        return DialogEventCalendarData.get(Calendar.DATE);
    }

    /**
     * 設定點選日期的日到DialogEventCalendarData中
     *
     * @param startDialogDate 日
     */
    public void setStartDialogDate(int startDialogDate) {
        DialogEventCalendarData.set(Calendar.DATE, startDialogDate);
    }

    /**
     * 計算本週第一天（週日）的日期
     */
    public void transDateToWeek() {
        DialogEventCalendarData.add(Calendar.DATE, -(showDialogEventData.getDialogDayOfWeek() - 1));
    }

    /**
     * 取得點選日期在本週的第幾天
     *
     * @return 本週的第幾天
     */
    public int getDialogDayOfWeek() {
        return this.startFragment;
    }

    /**
     * 設定點選日期在本週的第幾天
     *
     * @param startFragment 被點選的Fragment頁面
     */
    public void setDialogDayOfWeek(int startFragment) {
        this.startFragment = startFragment;
    }

    /**
     * 取得本週各天日期陣列
     *
     * @return 本週各天日期陣列
     */
    public Integer[] getDialogDateArray() {
        return dateArray;
    }

    /**
     * 取得本週各天月份陣列
     *
     * @return 本週各天月份陣列
     */
    public Integer[] getDialogMonthArray() {
        return monthArray;
    }

    /**
     * 取得本週各天月份陣列
     *
     * @return 本週各天月份陣列
     */
    public Integer[] getDialogYearArray() {
        return yearArray;
    }

    /**
     * 將本週各天月份日期加入陣列中
     */
    public void setDialogDateArray() {
        showDialogEventData.transDateToWeek();

        ArrayList<Integer> dateArrayList = new ArrayList<>();
        ArrayList<Integer> monthArrayList = new ArrayList<>();
        ArrayList<Integer> yearArrayList = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            yearArrayList.add(showDialogEventData.getStartDialogYear());
            monthArrayList.add(showDialogEventData.getStartDialogMonth());
            dateArrayList.add(showDialogEventData.getStartDialogDate());
            DialogEventCalendarData.add(Calendar.DATE, 1);
        }

        yearArray = yearArrayList.toArray(new Integer[yearArrayList.size()]);
        monthArray = monthArrayList.toArray(new Integer[monthArrayList.size()]);
        dateArray = dateArrayList.toArray(new Integer[dateArrayList.size()]);
    }
}
