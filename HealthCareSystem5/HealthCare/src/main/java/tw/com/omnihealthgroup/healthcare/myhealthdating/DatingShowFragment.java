package tw.com.omnihealthgroup.healthcare.myhealthdating;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.WebServiceConnection;
import tw.com.omnihealthgroup.healthcare.myhealthdating.adapter.DatingShowAdapter;
import tw.com.omnihealthgroup.healthcare.myhealthdating.dbo.FoodDefinitionDAO;
import tw.com.omnihealthgroup.healthcare.myhealthdating.object.FoodDefinition;

/**
 * Created by Administrator on 2016/7/26.
 */
public class DatingShowFragment extends DialogFragment {
    private static final String TAG = "DatingShowFragment";
    private View rootView;
    private WebServiceConnection webServiceConnection;
    private SharedPreferences prf;
    private EditText serachText;
    private ImageButton dialogAddNewEventBtn;
    private TextView dialogDateText;
    private GridView gridView;
    private DatingShowAdapter datingShowAdapter;
    private String dating_tpye = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        prf = getContext().getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
        webServiceConnection = new WebServiceConnection();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialogfragment_show_dating, container, false);
        Log.v(TAG, "onCreateView");

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //editview 不會自動跳出鍵盤 in DialogFragment
        initView();
        initListView();

        return rootView;
    }

    private void initView() {
        dialogAddNewEventBtn = (ImageButton) rootView.findViewById(R.id.dialog_addnewevent_btn);
        //  取得日期
        dialogDateText = (TextView) rootView.findViewById(R.id.dialog_show_date);
        dialogDateText.setText(String.format("%s", getArguments().getInt("DayEventMonth") + "月" + getArguments().getInt("DayEventDate") + "日") + " " + dialogDateText.getText());

        serachText = (EditText) rootView.findViewById(R.id.dialog_show_data);
        gridView = (GridView) rootView.findViewById(R.id.dialog_event_list);
        gridView.setNumColumns(3);
    }

    private void initListView() {
        FoodDefinitionDAO foodDefinitionDAO = new FoodDefinitionDAO(getContext());
        final List<FoodDefinition> searchDayEventList = foodDefinitionDAO.getfood_type();

        if (getArguments().getString("dating_tpye") != null) {
            dating_tpye = getArguments().getString("dating_tpye");
            Log.v(TAG, dating_tpye);
        }

        datingShowAdapter = new DatingShowAdapter(getContext(), gridView, searchDayEventList);
        gridView.setAdapter(datingShowAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putInt("DayEventYear", Integer.parseInt(prf.getString("day_dating", "").substring(0, 4)));
                bundle.putInt("DayEventMonth", Integer.parseInt(prf.getString("day_dating", "").substring(5, 7)));
                bundle.putInt("DayEventDate", Integer.parseInt(prf.getString("day_dating", "").substring(8, 10)));
                bundle.putString("FoodDefinitionDAO_type", searchDayEventList.get(position).getType());
                bundle.putString("dating_tpye", dating_tpye);

                DialogFragment dialogFragment = new DatingListFragment();
                dialogFragment.setArguments(bundle);
                dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PageTransparent);
                dialogFragment.show(getActivity().getSupportFragmentManager(), "DatingListFragment");
                dismiss();
            }
        });

        // 新增事件按鈕的監聽
        dialogAddNewEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("DayEventYear", Integer.parseInt(prf.getString("day_dating", "").substring(0, 4)));
                bundle.putInt("DayEventMonth", Integer.parseInt(prf.getString("day_dating", "").substring(5, 7)));
                bundle.putInt("DayEventDate", Integer.parseInt(prf.getString("day_dating", "").substring(8, 10)));
                bundle.putString("dating_tpye", dating_tpye);

                if (serachText.getText() != null) {
                    bundle.putString("SerachText", serachText.getText().toString().trim());
                }

                DialogFragment dialogFragment = new DatingListFragment();
                dialogFragment.setArguments(bundle);
                dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PageTransparent);
                dialogFragment.show(getActivity().getSupportFragmentManager(), "DatingListFragment");
                dismiss();
            }
        });
    }

}
