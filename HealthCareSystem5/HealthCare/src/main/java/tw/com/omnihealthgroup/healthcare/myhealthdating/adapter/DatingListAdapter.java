package tw.com.omnihealthgroup.healthcare.myhealthdating.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.WebServiceConnection;
import tw.com.omnihealthgroup.healthcare.myhealthdating.AfternoonTeaFragment;
import tw.com.omnihealthgroup.healthcare.myhealthdating.BreakFastFragment;
import tw.com.omnihealthgroup.healthcare.myhealthdating.DessertFragment;
import tw.com.omnihealthgroup.healthcare.myhealthdating.DinnerFragment;
import tw.com.omnihealthgroup.healthcare.myhealthdating.LunchFragment;
import tw.com.omnihealthgroup.healthcare.myhealthdating.SupperFragment;
import tw.com.omnihealthgroup.healthcare.myhealthdating.dbo.FoodUserDatingDAO;
import tw.com.omnihealthgroup.healthcare.myhealthdating.object.FoodDefinition;
import tw.com.omnihealthgroup.healthcare.myhealthdating.object.FoodUserDating;

public class DatingListAdapter extends BaseAdapter implements Filterable {
    private static final String TAG = "DatingListAdapter";
    private Context context;
    private LayoutInflater adapterLayoutInflater;
    private List<FoodDefinition> datingshowlist;
    private List<FoodDefinition> mOriginalValues;
    private String dating_tpye = null, day_dating = null;
    private MyFilter filter;

    private SharedPreferences prf;
    private WebServiceConnection webServiceConnection;

    public DatingListAdapter(Context context, List<FoodDefinition> datingshowlist, String dating_tpye, String day_dating) {
        this.context = context;
        //        this.mOriginalValues = datingshowlist;
        this.datingshowlist = datingshowlist;
        this.dating_tpye = dating_tpye;
        this.day_dating = day_dating;
        adapterLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return datingshowlist.size();
    }

    @Override
    public Object getItem(int position) {
        return datingshowlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder eventListViewHolder;
        eventListViewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = adapterLayoutInflater.inflate(R.layout.dialogfragment_view_listview_custom, parent, false);

            eventListViewHolder.middleLine = (ImageView) convertView.findViewById(R.id.dialog_middle_line);
            eventListViewHolder.dating_add = (ImageView) convertView.findViewById(R.id.dating_add);
            eventListViewHolder.dayTimeText = (TextView) convertView.findViewById(R.id.dialog_day_time_text);
            eventListViewHolder.dayEventTitle = (TextView) convertView.findViewById(R.id.dialog_day_event_title);
            eventListViewHolder.dayEventContent = (TextView) convertView.findViewById(R.id.dialog_day_event_content);

            convertView.setTag(eventListViewHolder);

        } else {
            eventListViewHolder = (ViewHolder) convertView.getTag();
        }

        eventListViewHolder.dayTimeText.setText(datingshowlist.get(position).getType());
        eventListViewHolder.dayEventTitle.setText(datingshowlist.get(position).getName());
        if (datingshowlist.get(position).getContent() != null
                && !datingshowlist.get(position).getContent().toString().trim().equals("null")) {
            eventListViewHolder.dayEventContent.setVisibility(View.VISIBLE);
            eventListViewHolder.dayEventContent.setText(datingshowlist.get(position).getContent());
        } else {
            eventListViewHolder.dayEventContent.setVisibility(View.GONE);
        }

        eventListViewHolder.middleLine.setBackgroundResource(R.mipmap.eventtagblue);

        eventListViewHolder.dating_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeStr = null;
                try {
                    FoodUserDatingDAO foodUserDatingDAO = new FoodUserDatingDAO(context);
                    FoodUserDating foodUserDating = new FoodUserDating();

                    try {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Calendar c = Calendar.getInstance();
                        Date date = format.parse(day_dating + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));
                        timeStr = format.format(date);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    foodUserDating.setType(datingshowlist.get(position).getType());
                    foodUserDating.setName(datingshowlist.get(position).getName());
                    foodUserDating.setContent(datingshowlist.get(position).getContent());
                    foodUserDating.setCooktype("未進行其他烹調加工");
                    foodUserDating.setUnit(datingshowlist.get(position).getUnit());
                    foodUserDating.setAmount("1");
                    foodUserDating.setAmountunit(datingshowlist.get(position).getAmountunit());

                    foodUserDating.setMoisturesum(datingshowlist.get(position).getMoisture());
                    foodUserDating.setProteinsum(datingshowlist.get(position).getProtein());
                    foodUserDating.setFatsum(datingshowlist.get(position).getFat());
                    foodUserDating.setSugarsum(datingshowlist.get(position).getSugar());
                    foodUserDating.setMealtype(dating_tpye);
                    foodUserDating.setImage("null");

                    foodUserDating.setRefimgsn(datingshowlist.get(position).getRefimgsn());
                    foodUserDating.setMoisture(datingshowlist.get(position).getMoisture());
                    foodUserDating.setProtein(datingshowlist.get(position).getProtein());
                    foodUserDating.setFat(datingshowlist.get(position).getFat());
                    foodUserDating.setSugar(datingshowlist.get(position).getSugar());
                    foodUserDating.setNote(datingshowlist.get(position).getNote());
                    foodUserDating.setStatus(datingshowlist.get(position).getStatus());
                    foodUserDating.setCrTime(timeStr);
                    foodUserDating.setMdTime(datingshowlist.get(position).getCrTime());

                    foodUserDatingDAO.insert(foodUserDating);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    FoodUserDatingDAO foodUserDatingDAO = new FoodUserDatingDAO(context);
                    List<FoodUserDating> searchDayEventList = foodUserDatingDAO.getfood_type_data(day_dating, dating_tpye);
                    switch (dating_tpye) {
                        case "BreakFastFragment":
                            BreakFastFragment breakFastFragment = new BreakFastFragment();
                            breakFastFragment.Refresh(searchDayEventList);
                            break;
                        case "DessertFragment":
                            DessertFragment dessertFragment = new DessertFragment();
                            dessertFragment.Refresh(searchDayEventList);
                            break;
                        case "LunchFragment":
                            LunchFragment lunchFragment = new LunchFragment();
                            lunchFragment.Refresh(searchDayEventList);
                            break;
                        case "AfternoonTeaFragment":
                            AfternoonTeaFragment afternoonTeaFragment = new AfternoonTeaFragment();
                            afternoonTeaFragment.Refresh(searchDayEventList);
                            break;
                        case "DinnerFragment":
                            DinnerFragment dinnerFragment = new DinnerFragment();
                            dinnerFragment.Refresh(searchDayEventList);
                            break;
                        case "SupperFragment":
                            SupperFragment supperFragment = new SupperFragment();
                            supperFragment.Refresh(searchDayEventList);
                            break;
                    }
                    Toast toast = Toast.makeText(context, "食物添加完成", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER | Gravity.TOP, 0, 180);
                    toast.show();
                }
            }
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new MyFilter();
        }
        return filter;
    }

    private class ViewHolder {
        ImageView middleLine, dating_add;
        TextView dayTimeText, dayEventTitle, dayEventContent;
    }

    private class MyFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            constraint = constraint.toString();
            FilterResults result = new FilterResults();
            if (mOriginalValues == null) {
                synchronized (this) {
                    mOriginalValues = new ArrayList<FoodDefinition>(datingshowlist);
                }
            }
            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<FoodDefinition> filteredItems = new ArrayList<FoodDefinition>();
                for (int i = 0, l = mOriginalValues.size(); i < l; i++) {
                    FoodDefinition foodDefinition = mOriginalValues.get(i);
                    if (foodDefinition.getName().contains(constraint)) {
                        //filteredItems.add(mOriginalValues.get((i/4)*4));
                        filteredItems.add(foodDefinition);
                    }
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    ArrayList<FoodDefinition> list = new ArrayList<FoodDefinition>(mOriginalValues);
                    result.values = list;
                    result.count = list.size();
                }
            }
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            datingshowlist = (ArrayList<FoodDefinition>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}