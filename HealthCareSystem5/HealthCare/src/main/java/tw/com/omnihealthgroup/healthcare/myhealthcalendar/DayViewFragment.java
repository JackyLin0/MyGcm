package tw.com.omnihealthgroup.healthcare.myhealthcalendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.adapter.DayViewAdapter;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.dbo.EventClassifyData;

public class DayViewFragment extends Fragment implements MyHealthCalendarFragment.CallBackDayView {
    private static final String TAG = "DayViewFragment";

    private DialogFragment dayEventDialogFragment;
    private View rootView;
    private ListView dayEventListView;
    private DayViewAdapter dayViewAdapter;

    public DayViewFragment() {
        super();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        //        prf = getActivity().getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
        //        webServiceConnection = new WebServiceConnection();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dayview_main, container, false);
        Log.v(TAG, "onCreateView");

        initListView();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
        //        if (prf.getInt("day_list_position", 0) > 0) {
        //            dayEventListView.setSelection(8);
        //        } else {
        dayEventListView.setSelection(0);
        //        }

        if (dayViewAdapter != null) {
            Log.v(TAG, "dayViewAdapter.notifyDataSetChanged()");
            dayViewAdapter.notifyDataSetChanged();
        }

        // 監聽返回鍵
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button
                    MyHealthCalendarFragment.monthSwitchToDay = true;
                    getFragmentManager().popBackStack();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");

        if (dayEventDialogFragment != null) {
            dayEventDialogFragment.dismiss();
        }
        //        System.out.println("onPause");

        //        prf.edit().putInt("day_list_position", position).commit();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
        //        System.out.println("onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
        //        System.out.println("onDestroy");
    }

    /**
     * 初始化單月事件列表與單日事件dialog
     */
    public void initListView() {
        dayEventListView = (ListView) rootView.findViewById(R.id.day_Calender_View);
        //        dayEventListView = (DayViewDetailListView) v.findViewById(R.id.day_Calender_View);
        dayViewAdapter = new DayViewAdapter(getActivity());
        dayEventListView.setAdapter(dayViewAdapter);
        dayEventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v(TAG, String.valueOf(position));
                Bundle bundle = new Bundle();
                bundle.putInt("DayEventYear", EventClassifyData.EVENT_CLASSIFY_DATA.getToolBarYear());
                bundle.putInt("DayEventMonth", EventClassifyData.EVENT_CLASSIFY_DATA.getToolBarMonth());
                bundle.putInt("DayEventDate", (Integer) view.getTag(R.id.dayevent_date));
                bundle.putString("DayEventDayInWeek", (String) view.getTag(R.id.dayevent_day_in_week));
                dayEventDialogFragment = new ShowDayEventFragment();
                dayEventDialogFragment.setArguments(bundle);
                //                dayEventDialogFragment = ShowDayEventFragment.newInstance(
                //                        EventClassifyData.EVENT_CLASSIFY_DATA.getToolBarYear(),
                //                        EventClassifyData.EVENT_CLASSIFY_DATA.getToolBarMonth(),
                //                        (Integer) view.getTag(R.id.dayevent_date),
                //                        (String) view.getTag(R.id.dayevent_day_in_week)
                //                );
                dayEventDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PageTransparent);
                dayEventDialogFragment.show(getActivity().getSupportFragmentManager(), "DayEventDialog");
            }
        });

        //        dayEventListView.setOnScrollListener(new AbsListView.OnScrollListener() {
        //            @Override
        //            public void onScrollStateChanged(AbsListView view, int scrollState) {
        //                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
        //                    position = view.getFirstVisiblePosition();//获取在总的列表条数中的索引编号
        //                    //                    View itemView = view.getChildAt(0);                    //获取在可视的第一个列表项
        //                    //                    listViemItemTop = (itemView == null) ? 0 : itemView.getTop();//获取第一个列表项相对于屏幕顶部的位置
        //                    Log.v("position", String.valueOf(position));
        //
        //                }
        //            }
        //
        //            @Override
        //            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //
        //            }
        //        });
    }

    @Override
    public void refreshDayEventClassify() {
        if (dayViewAdapter != null) {
            dayViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void refreshCalendarDayAndWeek() {
        if (dayViewAdapter != null) {
            EventClassifyData.DAYVIEW_NOTIFY_CHANGED = true;
            dayEventListView.setAdapter(dayViewAdapter);
            dayViewAdapter.notifyDataSetChanged();
        }
    }
}
