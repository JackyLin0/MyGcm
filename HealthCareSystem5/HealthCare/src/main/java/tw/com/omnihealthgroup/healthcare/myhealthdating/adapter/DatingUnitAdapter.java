package tw.com.omnihealthgroup.healthcare.myhealthdating.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import tw.com.omnihealthgroup.healthcare.R;

/**
 * Created by Administrator on 2016/6/22.
 */
public class DatingUnitAdapter extends RecyclerView.Adapter<DatingUnitAdapter.ViewHolder> {
    private static final String TAG = "DatingUnitAdapter";
    private ArrayList<String> myDataset;
    private Context context;

    // Provide a suitable constructor (depends on the kind of dataset)
    public DatingUnitAdapter(Context context, ArrayList<String> myDataset) {
        this.myDataset = myDataset;
        this.context = context;
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mimageView;
        public TextView mtextView;

        public ViewHolder(View v) {
            super(v);
            mtextView = (TextView) v.findViewById(R.id.text_view);

        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public DatingUnitAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reycleview_item_dating, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final DatingUnitAdapter.ViewHolder holder, int position) {
        holder.mtextView.setText(myDataset.get(position));
//        Picasso.with(holder.mimageView.getContext()).load(mPictureset.get(position)).into(holder.mimageView);

    }

    @Override
    public int getItemCount() {
        return myDataset.size();
    }

    public void add(String text, int position) {
        myDataset.add(position, text);
//        mPictureset.add(position, text);
        notifyItemInserted(position);  //有一个新项插入到了 position 位置
        //        notifyItemRangeInserted(position, count) - 在 position 位置插入了 count 个新项目
    }

    public void remove(int position) {
        myDataset.remove(position);
//        mPictureset.remove(position);
        notifyItemRemoved(position);
        //        notifyItemRangeRemoved(position, count)
    }
}
