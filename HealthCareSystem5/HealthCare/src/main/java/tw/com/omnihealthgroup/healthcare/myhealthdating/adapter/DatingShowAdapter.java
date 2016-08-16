package tw.com.omnihealthgroup.healthcare.myhealthdating.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.List;

import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.myhealthdating.object.FoodDefinition;

public class DatingShowAdapter extends BaseAdapter {
    private static final String TAG = "DatingShowAdapter";
    private Context context;
    GridView gridView;
    private LayoutInflater adapterLayoutInflater;
    private List<FoodDefinition> datingshowlist;

    public DatingShowAdapter(Context context, GridView gridView, List<FoodDefinition> datingshowlist) {
        this.context = context;
        this.gridView = gridView;
        this.datingshowlist = datingshowlist;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder eventListViewHolder;
        eventListViewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = adapterLayoutInflater.inflate(R.layout.dialogfragment_view_gridview_dating, parent, false);

            eventListViewHolder.dating_showtype = (TextView) convertView.findViewById(R.id.dating_showtype);

            convertView.setTag(eventListViewHolder);
            //            eventListViewHolder.update();

        } else {
            eventListViewHolder = (ViewHolder) convertView.getTag();
        }

        eventListViewHolder.dating_showtype.setText(datingshowlist.get(position).getType());
        if (position < 3) {
            eventListViewHolder.dating_showtype.setBackgroundResource(R.mipmap.show_dating_type1);
        } else if (position > 2 && position < 6) {
            eventListViewHolder.dating_showtype.setBackgroundResource(R.mipmap.show_dating_type2);
        } else if (position > 5 && position < 9) {
            eventListViewHolder.dating_showtype.setBackgroundResource(R.mipmap.show_dating_type3);
        } else if (position > 8 && position < 12) {
            eventListViewHolder.dating_showtype.setBackgroundResource(R.mipmap.show_dating_type1);
        } else if (position > 11 && position < 15) {
            eventListViewHolder.dating_showtype.setBackgroundResource(R.mipmap.show_dating_type2);
        } else if (position > 17 && position < 18) {
            eventListViewHolder.dating_showtype.setBackgroundResource(R.mipmap.show_dating_type3);
        } else {
            eventListViewHolder.dating_showtype.setBackgroundResource(R.mipmap.show_dating_type1);
        }

        //        eventListViewHolder.dating_showtype.setTag(position);

        return convertView;
    }

    private class ViewHolder {
        TextView dating_showtype;

        public void update() {
            // 精确计算GridView的item高度
            dating_showtype.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            int position = (Integer) dating_showtype.getTag();

                            // 这里是保证同一行的item高度是相同的！！也就是同一行是齐整的 height相等

                            if (position > 0) {
                                View v = (View) dating_showtype.getTag();
                                int height = v.getHeight();

                                View view = gridView.getChildAt(position - 1);
                                int lastheight = view.getHeight();

                                // 得到同一行的最后一个item和前一个item想比较，把谁的height大，就把两者中
                                // height小的item的高度设定为height较大的item的高度一致，也就是保证同一
                                // // 行高度相等即可

                                if (height > lastheight) {
                                    view.setLayoutParams(new GridView.LayoutParams(
                                            GridView.LayoutParams.FILL_PARENT,
                                            height));
                                } else if (height < lastheight) {
                                    v.setLayoutParams(new GridView.LayoutParams(
                                            GridView.LayoutParams.FILL_PARENT,
                                            lastheight));
                                }
                            }
                        }
                    });
        }
    }
}
