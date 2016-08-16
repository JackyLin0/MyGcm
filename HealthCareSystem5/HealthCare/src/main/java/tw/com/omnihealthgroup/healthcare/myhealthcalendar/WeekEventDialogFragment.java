package tw.com.omnihealthgroup.healthcare.myhealthcalendar;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.adapter.DialogViewPagerAdapter;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.dbo.DialogEventData;

public class WeekEventDialogFragment extends DialogFragment {
    private static final String TAG = "WeekEventDialogFragment";
    private DialogEventData dialogEventData = DialogEventData.showDialogEventData;

    public WeekEventDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.dialogfragment_viewpager, container, false);
        Log.v(TAG, "onCreateView");

        //  寬度滿板
        WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
        getDialog().getWindow().setGravity(Gravity.CENTER);
        Window win = getDialog().getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);

        //  初始化ViewPager
        ViewPager dialogViewPager = (ViewPager) dialogView.findViewById(R.id.main_viewpager);
        dialogViewPager.setPageTransformer(true, new PageScrollOffsetTransformer());    // 調整左右暫存頁面外露
        dialogViewPager.setOffscreenPageLimit(2);   // 左右各預備2個頁面
        ArrayList<Fragment> dialogFragmentList = getDialogFragments();
        DialogViewPagerAdapter dialogPagerAdapter = new DialogViewPagerAdapter(getChildFragmentManager(), dialogFragmentList);
        dialogViewPager.setAdapter(dialogPagerAdapter);
        dialogViewPager.setCurrentItem(dialogEventData.getDialogDayOfWeek() - 1);

        // 監聽dialogViewPager
        dialogViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            // 頁面滑動中
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //                System.out.println("onPageScrolled");
            }

            @Override
            public void onPageSelected(int position) {
                //                System.out.println("onPageSelected");
            }

            // 頁面狀態改變時
            @Override
            public void onPageScrollStateChanged(int state) {

                switch (state) {
                    case 0: //停止滑動
                        break;
                    case 1: //開始滑動
                        break;
                    case 2: //手指放開
                        break;
                }
            }
        });
        return dialogView;
    }

    private ArrayList getDialogFragments() {
        dialogEventData.setDialogDateArray();

        ArrayList<Fragment> dialogWeekList = new ArrayList<>();
        String[] dialogWeekday = getResources().getStringArray(R.array.dialog_weekday);
        for (int i = 0; i < 7; i++) {
            //(星期, ID, 月, 日)
            dialogWeekList.add(
                    ShowWeekEventFragment.newInstance(
                            dialogWeekday[i],
                            i,
                            dialogEventData.getDialogYearArray()[i],
                            dialogEventData.getDialogMonthArray()[i],
                            dialogEventData.getDialogDateArray()[i]
                    )
            );
        }
        // 建立Array到CalendarData紀錄一週的年月日與星期
        return dialogWeekList;
    }
}
