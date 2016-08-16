package tw.com.omnihealthgroup.healthcare.myhealthvideo.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import tw.com.omnihealthgroup.healthcare.R;

/**
 * Created by Administrator on 2016/6/22.
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private static final String TAG = "VideoAdapter";
    private ArrayList<String> mDataset;
    private ArrayList<String> mPictureset;

    Context mcontext;

    // Provide a suitable constructor (depends on the kind of dataset)
    public VideoAdapter(Context context, ArrayList<String> myDataset, ArrayList<String> myPictureset) {
        mDataset = myDataset;
        mPictureset = myPictureset;
        mcontext = context;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView mcardView;
        public ImageView mimageView;
        public TextView mtextView;

        public ViewHolder(View v) {
            super(v);
            mcardView = (CardView) v.findViewById(R.id.cardview_item_video);
            mimageView = (ImageView) v.findViewById(R.id.image_view);
            mtextView = (TextView) v.findViewById(R.id.text_view);

        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_item_video, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final VideoAdapter.ViewHolder holder, int position) {
        holder.mtextView.setText(mDataset.get(position));
        Picasso.with(holder.mimageView.getContext()).load(mPictureset.get(position)).into(holder.mimageView);

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void add(String text, int position) {
        mDataset.add(position, text);
        mPictureset.add(position, text);
        notifyItemInserted(position);  //有一个新项插入到了 position 位置
        //        notifyItemRangeInserted(position, count) - 在 position 位置插入了 count 个新项目
    }

    public void remove(int position) {
        mDataset.remove(position);
        mPictureset.remove(position);
        notifyItemRemoved(position);
        //        notifyItemRangeRemoved(position, count)
    }
}
