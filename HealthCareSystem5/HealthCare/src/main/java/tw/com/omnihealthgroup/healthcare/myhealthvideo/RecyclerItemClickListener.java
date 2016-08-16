package tw.com.omnihealthgroup.healthcare.myhealthvideo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2016/6/23.
 */
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private static final String TAG = "RecyclerItemClickListener";
    private View childView;
    private RecyclerView touchView;

    public RecyclerItemClickListener(Context context, final OnItemClickListener mListener) {
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent ev) {
                if (childView != null && mListener != null) {
                    mListener.onItemClick(childView, touchView.getChildPosition(childView));
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent ev) {
                if (childView != null && mListener != null) {
                    mListener.onLongClick(childView, touchView.getChildPosition(childView));
                }
            }
        });
    }

    GestureDetector mGestureDetector;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);

        public void onLongClick(View view, int posotion);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
        childView = rv.findChildViewUnder(e.getX(), e.getY());
        touchView = rv;
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
