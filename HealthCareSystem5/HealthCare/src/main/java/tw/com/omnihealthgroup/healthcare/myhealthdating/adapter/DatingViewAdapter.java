package tw.com.omnihealthgroup.healthcare.myhealthdating.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.myhealthdating.object.FoodUserDating;

/**
 * Created by Administrator on 2016/6/22.
 */
public class DatingViewAdapter extends RecyclerView.Adapter<DatingViewAdapter.ViewHolder> {
    private static final String TAG = "DatingViewAdapter";
    //    private LinkedHashMap<String, List<Object>> dayEventList;
    //    private LinkedList<String> dayEventTimeList = new LinkedList<>();
    //    private LinkedList<String> dayEventSubKindList = new LinkedList<>();
    //    private LinkedList<String> dayEventTitleList = new LinkedList<>();
    //    private LinkedList<Integer> dayEventColorList = new LinkedList<>();
    private List<FoodUserDating> datingshowlist;


    private int listCount = 0;
    private int table;
    private Context context;

    // Provide a suitable constructor (depends on the kind of dataset)
    //    public EventViewAdapter(Context context, ArrayList<String> myDataset, ArrayList<String> myPictureset) {
    public DatingViewAdapter(Context context, List<FoodUserDating> datingshowlist) {
        //        mDataset = myDataset;
        //        mPictureset = myPictureset;
        this.context = context;
        this.datingshowlist = datingshowlist;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView mcardView;
        public TextView dialog_name_dating, dialog_moisture_dating, dialog_sugar_dating, dialog_protein_dating, dialog_fat_dating;

        public ImageView middleLine;

        public ViewHolder(View v) {
            super(v);
            mcardView = (CardView) v.findViewById(R.id.cardview_item_dating);
            dialog_name_dating = (TextView) v.findViewById(R.id.dialog_name_dating);
            dialog_moisture_dating = (TextView) v.findViewById(R.id.dialog_moisture_dating);
            dialog_sugar_dating = (TextView) v.findViewById(R.id.dialog_sugar_dating);
            dialog_protein_dating = (TextView) v.findViewById(R.id.dialog_protein_dating);
            dialog_fat_dating = (TextView) v.findViewById(R.id.dialog_fat_dating);

        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DatingViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_item_dating, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final DatingViewAdapter.ViewHolder holder, int position) {
        holder.dialog_name_dating.setText(datingshowlist.get(position).getName());
        holder.dialog_moisture_dating.setText("【 " + datingshowlist.get(position).getAmount() + datingshowlist.get(position).getUnit() + "，" + datingshowlist.get(position).getMoisturesum() + " 大卡 】");
        holder.dialog_sugar_dating.setText("【醣類：" + datingshowlist.get(position).getSugarsum() + " g】");
        holder.dialog_protein_dating.setText("【蛋白質：" + datingshowlist.get(position).getProteinsum() + " g】");
        holder.dialog_fat_dating.setText("【脂肪：" + datingshowlist.get(position).getFatsum() + " g】");
    }

    @Override
    public int getItemCount() {
        return datingshowlist.size();
    }

    public void add(String text, int position) {
        //        mDataset.add(position, text);
        //        mPictureset.add(position, text);
        notifyItemInserted(position);  //有一个新项插入到了 position 位置
        //        notifyItemRangeInserted(position, count) - 在 position 位置插入了 count 个新项目
    }

    public void remove(int position) {
        //        mDataset.remove(position);
        //        mPictureset.remove(position);
        notifyItemRemoved(position);
        //        notifyItemRangeRemoved(position, count)
    }


}
