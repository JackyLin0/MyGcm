package tw.com.omnihealthgroup.healthcare.myhealthcalendar.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.dbo.CalendarData;
import tw.com.omnihealthgroup.healthcare.myhealthcalendar.dbo.SynchroDateModule;

public class ToolBarYearMonthSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {
    private static final String TAG = "ToolBarYearMonthSpinnerAdapter";
    private LayoutInflater tbymAdapterLayoutInflater;
    private CalendarData toolBarCalendarData = CalendarData.ToolBarCalendarData;
    private ArrayList<String> textArrayList;

    public ToolBarYearMonthSpinnerAdapter(Context c, int year, int month) {
        tbymAdapterLayoutInflater = LayoutInflater.from(c);
        textArrayList = caculateTitleDate(year, month);
        SynchroDateModule synchroDateModule = SynchroDateModule.getSingleton();

        // 註冊監聽日期變動（觀察者模式）
        registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
            }
        });
        synchroDateModule.registerBaseAdapter(this);
    }

    @Override
    public int getCount() {
        return textArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return textArrayList.get(position);
    }

    /**
     * position隨MonthViewList變動而改變
     */
    @Override
    public long getItemId(int position) {
        return SynchroDateModule.getSingleton().getSynchroMonth();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder spinDateHolder;
        if (convertView == null) {
            convertView = tbymAdapterLayoutInflater.inflate(R.layout.spinner_date_list, parent, false);
            spinDateHolder = new ViewHolder();
            spinDateHolder.spinDateText = (TextView) convertView.findViewById(R.id.spin_date_text);
            spinDateHolder.spinDateText.setText(getItem(position).toString());
            spinDateHolder.spinDateText.setTextColor(convertView.getResources().getColor(R.color.white_color));
            convertView.setTag(spinDateHolder);
        } else {
            spinDateHolder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    /**
     * 無論view是否生成都要對spinDateText的值根據position做變動。
     * （getDropDownView的selection會return到getView上顯示出來）
     */
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder spinDateHolder;
        if (convertView == null) {
            view = tbymAdapterLayoutInflater.inflate(R.layout.spinner_date_list, parent, false);
            spinDateHolder = new ViewHolder();
            spinDateHolder.spinDateText = (TextView) view.findViewById(R.id.spin_date_text);
            spinDateHolder.spinDateText.setTextColor(view.getResources().getColor(R.color.white_color));
            view.setTag(spinDateHolder);
        } else {
            view = convertView;
            spinDateHolder = (ViewHolder) view.getTag();
        }
        spinDateHolder.spinDateText.setText(getItem(position).toString());

        return view;
    }

    private class ViewHolder {
        TextView spinDateText;
    }

    /**
     * toolbar日期的判斷式
     */
    public ArrayList<String> caculateTitleDate(int currentYear, int currentMonth) {
        textArrayList = new ArrayList<>();
        toolBarCalendarData.setYear(currentYear);
        toolBarCalendarData.setMonth(currentMonth);

        for (int i = 0; i < (60 - Calendar.getInstance().get(Calendar.MONTH)); i++) {
            SynchroDateModule.getSingleton().synchroMonth.add((toolBarCalendarData.getMonth() + 1));
            SynchroDateModule.getSingleton().synchroYear.add(toolBarCalendarData.getYear());
            textArrayList.add(String.valueOf(toolBarCalendarData.getYear() + "年" + (toolBarCalendarData.getMonth() + 1) + "月"));
            toolBarCalendarData.addMonth(1);
        }
        return textArrayList;
    }
}
