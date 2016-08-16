package tw.com.omnihealthgroup.healthcare.myhealthdating;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import tw.com.omnihealthgroup.drawerframework.DrawerFrameworkMainFragment;
import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.WebServiceConnection;
import tw.com.omnihealthgroup.healthcare.myhealthdating.adapter.DatingViewAdapter;
import tw.com.omnihealthgroup.healthcare.myhealthdating.dbo.FoodUserDatingDAO;
import tw.com.omnihealthgroup.healthcare.myhealthdating.object.FoodUserDating;
import tw.com.omnihealthgroup.healthcare.myhealthvideo.RecyclerItemClickListener;

/**
 * Created by Administrator on 2016/7/25.
 */
public class DessertFragment extends DrawerFrameworkMainFragment {
    private static final String TAG = "DessertFragment";
    private View rootView;

    private DatingViewAdapter datingViewAdapter;
    private AlphaInAnimationAdapter alphaAdapter;
    private static RecyclerView datingView;
    private static TextView dating_moisture, dating_moisture_number, dating_sugar, dating_protein, dating_fat;
    private static LinearLayout linearLayout1, linearLayout2, linearLayout3;
    private static List<FoodUserDating> searchDayEventList;

    private String datatemp = "1";
    private LinearLayoutManager linearLayoutManager;
    private double e001i = 0f, e002i = 0f, e003i = 0f, e004i = 0f;

    private SharedPreferences prf;
    private WebServiceConnection webServiceConnection;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        prf = getActivity().getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
        webServiceConnection = new WebServiceConnection();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tabpage_recaview, container, false);
        Log.v(TAG, "onCreateView");

        initView();
        initHandler();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");

        String timeStr = prf.getString("day_dating", "");
        //        timeStr = timeStr.replace("-", "");
        eventListView(timeStr);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v(TAG, "onDestroyView");

    }

    private void initView() {
        datingView = (RecyclerView) rootView.findViewById(R.id.datingView);

        linearLayout1 = (LinearLayout) rootView.findViewById(R.id.linearLayout1);
        linearLayout2 = (LinearLayout) rootView.findViewById(R.id.linearLayout2);
        linearLayout3 = (LinearLayout) rootView.findViewById(R.id.linearLayout3);

        dating_moisture = (TextView) rootView.findViewById(R.id.dating_moisture);
        dating_moisture_number = (TextView) rootView.findViewById(R.id.dating_moisture_number);
        dating_sugar = (TextView) rootView.findViewById(R.id.dating_sugar);
        dating_protein = (TextView) rootView.findViewById(R.id.dating_protein);
        dating_fat = (TextView) rootView.findViewById(R.id.dating_fat);

    }

    private void initHandler() {
        datingView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.d(TAG, "onItemClick : postion " + position);
                        FoodUserDating foodUserDating = new FoodUserDating();
                        foodUserDating = searchDayEventList.get(position);
                        Log.v(TAG, foodUserDating.getName());

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("foodUserDating", foodUserDating);
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), AddNewDatingActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongClick(View view, int posotion) {
                        Log.d(TAG, "onLongClick position : " + posotion);
                    }
                }));

        linearLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("DayEventYear", Integer.parseInt(prf.getString("day_dating", "").substring(0, 4)));
                bundle.putInt("DayEventMonth", Integer.parseInt(prf.getString("day_dating", "").substring(5, 7)));
                bundle.putInt("DayEventDate", Integer.parseInt(prf.getString("day_dating", "").substring(8, 10)));
                bundle.putString("dating_tpye", TAG);

                try {
                    dataCount();
                    initCount();

                    DecimalFormat nf = new DecimalFormat("0.00");
                    bundle.putString("Proteinsum", nf.format(e002i));
                    bundle.putString("Fatsum", nf.format(e003i));
                    bundle.putString("Sugarsum", nf.format(e004i));

                    bundle.putString("dating_moisture", dating_moisture.getText().toString().trim());
                    bundle.putString("dating_moisture_number", dating_moisture_number.getText().toString().trim());

                    bundle.putString("dating_protein", dating_protein.getText().toString().trim());
                    bundle.putString("dating_fat", dating_fat.getText().toString().trim());
                    bundle.putString("dating_sugar", dating_sugar.getText().toString().trim());

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    e001i = 0f;
                    e002i = 0f;
                    e003i = 0f;
                    e004i = 0f;
                }

                DialogFragment dialogFragment = new DatingPieChartFragment();
                dialogFragment.setArguments(bundle);
                dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PageTransparent);
                dialogFragment.show(getActivity().getSupportFragmentManager(), "DatingPieChartFragment");
            }
        });
    }

    private void eventListView(final String todayString) {
        datatemp = todayString;

        final FoodUserDatingDAO foodUserDatingDAO = new FoodUserDatingDAO(getContext());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    searchDayEventList = foodUserDatingDAO.getfood_type_data(datatemp, TAG);
                    Log.v(TAG, searchDayEventList.toString());

                    dataCount();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (searchDayEventList.size() < 1) {
                                linearLayout1.setVisibility(View.VISIBLE);
                                linearLayout2.setVisibility(View.GONE);
                            } else {
                                linearLayout1.setVisibility(View.GONE);
                                linearLayout2.setVisibility(View.VISIBLE);

                                initAdapter();
                                initCount();
                            }
                        }
                    });
                }
            }
        }).start();

    }

    private void dataCount() {
        e001i = 0f;
        e002i = 0f;
        e003i = 0f;
        e004i = 0f;

        for (int i = 0; i < searchDayEventList.size(); i++) {
            e001i = e001i + Double.parseDouble(searchDayEventList.get(i).getMoisturesum());
            e002i = e002i + Double.parseDouble(searchDayEventList.get(i).getProteinsum());
            e003i = e003i + Double.parseDouble(searchDayEventList.get(i).getFatsum());
            e004i = e004i + Double.parseDouble(searchDayEventList.get(i).getSugarsum());
        }
    }

    private void initCount() {
        DecimalFormat nf = new DecimalFormat("0.00");
        dating_moisture.setText("午前點心總熱量：");
        dating_moisture_number.setText(nf.format(e001i).substring(0, nf.format(e001i).length() - 3) + " 大卡/餐");

        Log.v(TAG, nf.format(e002i / (e002i + e003i + e004i) * 100));
        Log.v(TAG, nf.format(e003i / (e002i + e003i + e004i) * 100));
        Log.v(TAG, nf.format(e004i / (e002i + e003i + e004i) * 100));

        double a = new BigDecimal(e002i / (e002i + e003i + e004i) * 100)
                .setScale(0, BigDecimal.ROUND_HALF_UP)
                .doubleValue();

        double b = new BigDecimal(e003i / (e002i + e003i + e004i) * 100)
                .setScale(0, BigDecimal.ROUND_HALF_UP)
                .doubleValue();

        double c = new BigDecimal(e004i / (e002i + e003i + e004i) * 100)
                .setScale(0, BigDecimal.ROUND_HALF_UP)
                .doubleValue();

        dating_protein.setText("蛋白質：" + nf.format(a).substring(0, nf.format(a).length() - 3) + " %");
        dating_fat.setText("脂肪：" + nf.format(b).substring(0, nf.format(b).length() - 3) + " %");
        dating_sugar.setText("醣類：" + nf.format(c).substring(0, nf.format(c).length() - 3) + " %");
    }

    private void initAdapter() {
        //创建默认的线性LayoutManager
        linearLayoutManager = new LinearLayoutManager(getContext());
        datingView.setLayoutManager(linearLayoutManager);//这里用线性显示 类似于listview
        //        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));//这里用线性宫格显示 类似于grid view
        //        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL));//这里用线性宫格显示 类似于瀑布流
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        datingView.setHasFixedSize(true);

        datingViewAdapter = new DatingViewAdapter(getContext(), searchDayEventList);
        alphaAdapter = new AlphaInAnimationAdapter(datingViewAdapter);
        datingView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));
    }


    public void Refresh(List<FoodUserDating> searchDayEventList) {
        Log.v(TAG, searchDayEventList.toString());
        this.searchDayEventList.clear();
        this.searchDayEventList = searchDayEventList;
        try {
            if (searchDayEventList.size() < 1) {
                linearLayout1.setVisibility(View.VISIBLE);
                linearLayout2.setVisibility(View.GONE);
            } else {
                linearLayout1.setVisibility(View.GONE);
                linearLayout2.setVisibility(View.VISIBLE);

                initAdapter();
                dataCount();
                initCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}


