package tw.com.omnihealthgroup.healthcare.myhealthcalendar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import tw.com.omnihealthgroup.healthcare.R;

public class ToolBarEventTypeSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {
    private static final String TAG = "ToolBarEventTypeSpinnerAdapter";
    private LayoutInflater tbetAdapterLayoutInflater;
    private ArrayList<String> textArrayList;

    public ToolBarEventTypeSpinnerAdapter(Context context) {
        tbetAdapterLayoutInflater = LayoutInflater.from(context);
        String[] eventType = context.getResources().getStringArray(R.array.event_type);
        textArrayList = new ArrayList<>(Arrays.asList(eventType));

    }

    @Override
    public int getCount() {
        return textArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return textArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder spinDateHolder;
        if (convertView == null) {
            convertView = tbetAdapterLayoutInflater.inflate(R.layout.spinner_date_list, parent, false);
            spinDateHolder = new ViewHolder();
            spinDateHolder.spinTypeText = (TextView) convertView.findViewById(R.id.spin_date_text);
            spinDateHolder.spinTypeText.setText(getItem(position).toString());
            spinDateHolder.spinTypeText.setTextColor(convertView.getResources().getColor(R.color.white_color));
            convertView.setTag(spinDateHolder);
        } else {
            spinDateHolder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder spinDateHolder;
        if (convertView == null) {
            view = tbetAdapterLayoutInflater.inflate(R.layout.spinner_date_list, parent, false);
            spinDateHolder = new ViewHolder();
            spinDateHolder.spinTypeText = (TextView) view.findViewById(R.id.spin_date_text);
            spinDateHolder.spinTypeText.setText(getItem(position).toString());
            spinDateHolder.spinTypeText.setTextColor(view.getResources().getColor(R.color.white_color));
            view.setTag(spinDateHolder);
        } else {
            view = convertView;
            spinDateHolder = (ViewHolder) view.getTag();
        }
//        spinDateHolder.spinTypeText.setText(getItem(position).toString());

        return view;
    }

    private class ViewHolder {
        TextView spinTypeText;
    }
}
