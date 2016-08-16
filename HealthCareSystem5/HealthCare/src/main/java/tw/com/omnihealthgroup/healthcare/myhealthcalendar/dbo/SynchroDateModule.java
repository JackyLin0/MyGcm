package tw.com.omnihealthgroup.healthcare.myhealthcalendar.dbo;

import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class SynchroDateModule {
    private static final String TAG = "SynchroDateModule";
    /**
     * Singleton Pattern 讓物件單一且可以在不同Activity之間傳遞
     * （此寫法不是很完整）
     */
    static SynchroDateModule singleton = new SynchroDateModule();
    public final ArrayList<Integer> synchroYear = new ArrayList<>();
    public final ArrayList<Integer> synchroMonth = new ArrayList<>();

    public static SynchroDateModule getSingleton() {
        return singleton;
    }

    static final long MilliSeconds_IN_ONE_WEEK = 1000 * 60 * 60 * 24 * 7L;

    //    final int baseMonth;
    //    final int baseYear;
    final Calendar baseCalendar;

    int nowMonth;
    int nowYear;
    int position;
    int toolBarMonth;
    ArrayList<Integer> dateSpinnerPositionList = new ArrayList<>();
    Integer[] dateSpinnerPosition;
    Set<BaseAdapter> baseAdapterSet = new HashSet<>();

    SynchroDateModule() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        nowYear = calendar.get(Calendar.YEAR);
        nowMonth = calendar.get(Calendar.MONTH);
        baseCalendar = Calendar.getInstance();
        baseCalendar.set(nowYear, nowMonth, 1, 0, 0, 0);
    }

    public int getNowMonth() {
        return nowMonth;
    }

    public int getNowYear() {
        return nowYear;
    }

    public void setDateSpinnerPosition(int position) {
        dateSpinnerPositionList.add(position);
        dateSpinnerPosition = new Integer[dateSpinnerPositionList.size()];
        dateSpinnerPosition = dateSpinnerPositionList.toArray(dateSpinnerPosition);
    }

    public int getWeekInMonthPosition() {
        return position;
    }

    public void setSynchroDate(int nowYear, int nowMonth) {
        //        System.out.println(TAG + "SELECTION nowYear / " + nowYear);
        if (nowYear == Calendar.getInstance().get(Calendar.YEAR)) {
            toolBarMonth = nowMonth + 12;
        } else if (nowYear > Calendar.getInstance().get(Calendar.YEAR)) {
            if (nowYear - Calendar.getInstance().get(Calendar.YEAR) == 1) {
                toolBarMonth = nowMonth + 24;
            } else if (nowYear - Calendar.getInstance().get(Calendar.YEAR) == 2) {
                toolBarMonth = nowMonth + 36;
            } else if (nowYear - Calendar.getInstance().get(Calendar.YEAR) == 3) {
                toolBarMonth = nowMonth + 48;
            }
        } else {
            toolBarMonth = nowMonth;
        }
        //        System.out.println(TAG + "SELECTION toolBarMonth / " + toolBarMonth);
        toolBarMonth = toolBarMonth - Calendar.getInstance().get(Calendar.MONTH);
    }

    public int getSynchroMonth() {
        return toolBarMonth;
    }

    public void setSynchroMonthAndYear(int nowYear, int nowMonth) {
        this.nowYear = nowYear;
        this.nowMonth = nowMonth;
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.set(this.nowYear, this.nowMonth, 1, 12, 0, 0);
        position = (int) (Math.ceil((nowCalendar.getTimeInMillis() - baseCalendar.getTimeInMillis()) / MilliSeconds_IN_ONE_WEEK));
        //        System.out.println(TAG + "IM HERE " + nowYear + " " + nowMonth);
        for (BaseAdapter baseAdapter : baseAdapterSet)
            baseAdapter.notifyDataSetChanged(); // for-each寫法
    }

    /**
     * 透過在HashSet<>中將傳入的變數adapter加入 / 移除，來達到註冊 / 註銷監聽的作用
     */
    public void registerBaseAdapter(BaseAdapter adapter) {
        baseAdapterSet.add(adapter);
    }

    public void unregisterBaseAdapter(BaseAdapter adapter) {
        baseAdapterSet.remove(adapter);
    }
}
