package tw.com.omnihealthgroup.healthcare.myhealthdating;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.WebServiceConnection;

/**
 * Created by Administrator on 2016/7/26.
 */
public class DatingImageFragment extends DialogFragment {
    private static final String TAG = "DatingImageFragment";
    private View rootView;
    private ImageView dating_image;

    private WebServiceConnection webServiceConnection;
    private SharedPreferences prf;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        prf = getContext().getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
        webServiceConnection = new WebServiceConnection();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialogfragment_image_dating, container, false);
        Log.v(TAG, "onCreateView");

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //editview 不會自動跳出鍵盤 in DialogFragment
        initView();
        initListView();

        return rootView;
    }

    private void initView() {
        dating_image = (ImageView) rootView.findViewById(R.id.dating_image);

    }

    private void initListView() {
        if (getArguments().getParcelable("bitmap") != null) {
            Bitmap bitmap = getArguments().getParcelable("bitmap");
            dating_image.setImageBitmap(bitmap);
        }
    }

}
