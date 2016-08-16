package tw.com.omnihealthgroup.healthcare.myhealthdating;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;
import java.util.List;

import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.WebServiceConnection;
import tw.com.omnihealthgroup.healthcare.myhealthdating.dbo.FoodDefinitionDAO;
import tw.com.omnihealthgroup.healthcare.myhealthdating.object.FoodDefinition;

/**
 * Created by Administrator on 2016/7/26.
 */
public class DatingPieChartFragment extends DialogFragment {
    private static final String TAG = "DatingPieChartFragment";
    private View rootView;
    private String sugar = null, protein = null, fat = null;
    private static TextView dating_moisture, dating_moisture_number, dating_sugar, dating_protein, dating_fat;
    private PieChart pieChart;

    private SharedPreferences prf;
    private WebServiceConnection webServiceConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        prf = getContext().getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
        webServiceConnection = new WebServiceConnection();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialogfragment_piechart_dating, container, false);
        Log.v(TAG, "onCreateView");

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //editview 不會自動跳出鍵盤 in DialogFragment
        initView();
        initListView();

        return rootView;
    }

    private void initView() {
        //        dialogAddNewEventBtn = (ImageButton) rootView.findViewById(R.id.dialog_addnewevent_btn);
        //        //  取得日期
        //        dialogDateText = (TextView) rootView.findViewById(R.id.dialog_show_date);
        //        dialogDateText.setText(String.format("%s", getArguments().getInt("DayEventMonth") + "月" + getArguments().getInt("DayEventDate") + "日") + " " + dialogDateText.getText());

        dating_moisture = (TextView) rootView.findViewById(R.id.dating_moisture);
        dating_moisture_number = (TextView) rootView.findViewById(R.id.dating_moisture_number);
        dating_sugar = (TextView) rootView.findViewById(R.id.dating_sugar);
        dating_protein = (TextView) rootView.findViewById(R.id.dating_protein);
        dating_fat = (TextView) rootView.findViewById(R.id.dating_fat);

        pieChart = (PieChart) rootView.findViewById(R.id.dating_piechart);
    }

    private void initListView() {
        FoodDefinitionDAO foodDefinitionDAO = new FoodDefinitionDAO(getContext());
        final List<FoodDefinition> searchDayEventList = foodDefinitionDAO.getfood_type();

        if (getArguments().getString("dating_moisture") != null) {
            dating_moisture.setText(getArguments().getString("dating_moisture"));
            dating_moisture_number.setText(getArguments().getString("dating_moisture_number"));
            dating_sugar.setText("醣類：" + getArguments().getString("Sugarsum") + " g");
            dating_protein.setText("蛋白質：" + getArguments().getString("Proteinsum") + " g");
            dating_fat.setText("脂肪：" + getArguments().getString("Fatsum") + " g");

            sugar = getArguments().getString("dating_sugar");
            protein = getArguments().getString("dating_protein");
            fat = getArguments().getString("dating_fat");

        }

        PieData pieData = getPieData(3, 100);
        showChart(pieChart, pieData);

    }

    private void showChart(PieChart pieChart, PieData pieData) {
        pieChart.setHoleColorTransparent(true);
        pieChart.setHoleRadius(60f);  //半径
        pieChart.setTransparentCircleRadius(64f); // 半透明圈
        //pieChart.setHoleRadius(0)  //实心圆
        pieChart.setDescription("三大營養成份佔比");
        pieChart.setDescriptionTextSize(20f);
        // mChart.setDrawYValues(true);
        pieChart.setDrawCenterText(false);  //饼状图中间可以添加文字
        pieChart.setCenterText("Quarterly Revenue");  //饼状图中间的文字
        pieChart.setDrawHoleEnabled(true);
        pieChart.setRotationAngle(90); // 初始旋转角度

        // draws the corresponding description value into the slice
        // mChart.setDrawXValues(true);

        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true); // 可以手动旋转
        // display percentage values
        pieChart.setUsePercentValues(true);  //显示成百分比
        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener
        //      mChart.setOnChartValueSelectedListener(this);
        // mChart.setTouchEnabled(false);

        //      mChart.setOnAnimationListener(this);


        //设置数据
        pieChart.setData(pieData);

        // undo all highlights
        //      pieChart.highlightValues(null);
        //      pieChart.invalidate();

        Legend mLegend = pieChart.getLegend();  //设置比例图
        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);  //最右边显示
        //      mLegend.setForm(LegendForm.LINE);  //设置比例图的形状，默认是方形
        mLegend.setXEntrySpace(10f);
        mLegend.setYEntrySpace(10f);

        pieChart.animateXY(1000, 1000);  //设置动画
        // mChart.spin(2000, 0, 360);
    }


    /**
     * @param count 分成几部分
     * @param range
     */
    private PieData getPieData(int count, float range) {

        ArrayList<String> xValues = new ArrayList<String>();  //xVals用来表示每个饼块上的内容

        //        for (int i = 0; i < count; i++) {
        //            xValues.add("Quarterly" + (i + 1));  //饼块上显示成Quarterly1, Quarterly2, Quarterly3, Quarterly4
        //        }

        xValues.add(sugar);
        xValues.add(protein);
        xValues.add(fat);

        ArrayList<Entry> yValues = new ArrayList<Entry>();  //yVals用来表示封装每个饼块的实际数据

        // 饼图数据
        /**
         * 将一个饼形图分成四部分， 四部分的数值比例为14:14:34:38
         * 所以 14代表的百分比就是14%
         */
        float sugar_float = Float.parseFloat(sugar.substring(3, sugar.length() - 2));
        Log.v(TAG, String.valueOf(sugar_float));
        float protein_float = Float.parseFloat(protein.substring(4, protein.length() - 2));
        Log.v(TAG, String.valueOf(protein_float));
        float fat_float = Float.parseFloat(fat.substring(3, fat.length() - 2));
        Log.v(TAG, String.valueOf(fat_float));


        yValues.add(new Entry(sugar_float, 0));
        yValues.add(new Entry(protein_float, 1));
        yValues.add(new Entry(fat_float, 2));

        //y轴的集合
        PieDataSet pieDataSet = new PieDataSet(yValues, ""/*显示在比例图上*/);
        pieDataSet.setSliceSpace(0f); //设置个饼状图之间的距离

        ArrayList<Integer> colors = new ArrayList<Integer>();

        // 饼图颜色
        colors.add(Color.rgb(79, 142, 189));
        colors.add(Color.rgb(192, 80, 77));
        colors.add(Color.rgb(155, 187, 89));

        pieDataSet.setColors(colors);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = 5 * (metrics.densityDpi / 160f);
        pieDataSet.setSelectionShift(px); // 选中态多出的长度

        PieData pieData = new PieData(xValues, pieDataSet);

        return pieData;
    }
}