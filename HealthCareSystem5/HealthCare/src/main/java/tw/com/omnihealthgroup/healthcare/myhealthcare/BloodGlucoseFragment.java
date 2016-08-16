package tw.com.omnihealthgroup.healthcare.myhealthcare;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import tw.com.omnihealthgroup.drawerframework.DrawerFrameworkMainFragment;
import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.util.ShowMEProgressDiaLog;
import tw.com.omnihealthgroup.healthcare.WebServiceConnection;
import tw.com.omnihealthgroup.healthcare.gcm.GcmUtil;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.BioData;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.BioDataAdapter;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.DeviceMapping;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.DeviceMappingAdapter;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.User;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.UserAdapter;


public class BloodGlucoseFragment extends DrawerFrameworkMainFragment {
    private static final String TAG = "BloodGlucoseFragment";
    private View rootView;
    private String authCode = null;
    private LineChart chart;

    private ArrayList<String> recordtime = new ArrayList<>();
    private ArrayList<String> recordvalues_BSAC = new ArrayList<>();
    private ArrayList<String> recordvalues_BSPC = new ArrayList<>();
    private ArrayList<String> recordvalues_BSNM = new ArrayList<>();

    private ArrayList<String> time_bsac = new ArrayList<>();
    private ArrayList<String> time_bspc = new ArrayList<>();
    private ArrayList<String> time_bsnm = new ArrayList<>();

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
        rootView = inflater.inflate(R.layout.fragment_tabpage_ca, container, false);
        Log.v(TAG, "onCreateView");

        chart = (LineChart) rootView.findViewById(R.id.chart);
        webServiceConnection.setLineChart(chart);
        setleftAxis(chart);

        checkView();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");

        initchart();
        // checkView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v(TAG, "onDestroyView");

        new queryMeasureResource().cancel(true);
        //        new TokenExp().cancel(true);
        //        new TokenGet().cancel(true);
    }

    private void checkView() {
        //        String timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        //        String checkLogin = webServiceConnection.DateCompare(timeStr, prf.getString("take_time", ""));

        //        if (webServiceConnection.isOnline(getContext())) {
        //            if (checkLogin.equals("true")) {
        new queryMeasureResource().execute();
        //            } else if (checkLogin.equals("exToke")) {
        //                new TokenExp().execute();
        //            } else {
        //                new AlertDialog.Builder(getContext())
        //                        .setTitle(getString(R.string.message_title))
        //                        .setMessage(getString(R.string.msg_retokenget))
        //                        .setPositiveButton(getString(R.string.msg_confirm),
        //                                new DialogInterface.OnClickListener() {
        //                                    public void onClick(DialogInterface dialog, int whichButton) {
        //                                        try {
        //                                            getAuthRequest(getContext(), R.layout.auth_dialog, R.id.webv);
        //
        //                                        } catch (Exception e) {
        //                                            e.printStackTrace();
        //                                        }
        //                                    }
        //                                }).show();
        //            }
        //        } else {
        //            new android.app.AlertDialog.Builder(getContext())
        //                    .setTitle(getString(R.string.msg_connect_faild_title))
        //                    .setMessage(getString(R.string.msg_connect_faild))
        //                    .setPositiveButton(getString(R.string.msg_confirm),
        //                            new DialogInterface.OnClickListener() {
        //                                public void onClick(DialogInterface dialog,
        //                                                    int whichButton) {
        //                                    Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
        //                                    getContext().startActivity(intent);
        //                                }
        //                            }).show();
        //        }
    }

    private void initchart() {
        ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(getContext()
                , getString(R.string.webview_loading_title)
                , getString(R.string.msg_tokenget), false, true);
        pb.show();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH) - (prf.getInt("day_count", 0));
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        int millisecond = c.get(Calendar.MILLISECOND);

        String timeStr = null;
        try {
            Date date = formatter.parse(String.valueOf(year + "-" + (month + 1) + "-" + day + " " + hour + ":" + minute + ":" + second + "." + millisecond));
            timeStr = formatter.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        BioDataAdapter bioDataAdapter = new BioDataAdapter(getContext());
        List<BioData> listData = bioDataAdapter.getBloodGlucose_withTime(timeStr, formatter.format(new Date()));
        if (listData.size() > 0) {
            recordtime.clear();
            recordvalues_BSAC.clear();
            recordvalues_BSPC.clear();
            recordvalues_BSNM.clear();

            time_bsac.clear();
            time_bspc.clear();
            time_bsnm.clear();

            int count = 0;
            for (BioData bioData : listData) {
                if (!bioData.getDeviceTime().equals("2000-00-00 00:00:00")) {
                    try {
                        SimpleDateFormat formatter_chat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String getDeviceTime = bioData.getDeviceTime().toString().replaceAll("/", "-");
                        Date date_chat = formatter_chat.parse(getDeviceTime);
                        String timeStr_chat = formatter_chat.format(date_chat);
                        recordtime.add(timeStr_chat);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (bioData.getAc() != null) {
                        recordvalues_BSAC.add(bioData.getAc());
                        time_bsac.add(String.valueOf(count));
                    } else if (bioData.getPc() != null) {
                        recordvalues_BSPC.add(bioData.getPc());
                        time_bspc.add(String.valueOf(count));
                    } else if (bioData.getNm() != null) {
                        recordvalues_BSNM.add(bioData.getNm());
                        time_bsnm.add(String.valueOf(count));
                    }
                    count++;
                }
            }

            Log.v(TAG + "recordtime", recordtime.toString());
            Log.v(TAG + "recordvalues_BSAC", recordvalues_BSAC.toString());
            Log.v(TAG + "recordvalues_BSPC", recordvalues_BSPC.toString());
            Log.v(TAG + "recordvalues_BSNM", recordvalues_BSNM.toString());

            Log.v(TAG + "time_bsac", time_bsac.toString());
            Log.v(TAG + "time_bspc", time_bspc.toString());
            Log.v(TAG + "time_bsnm", time_bsnm.toString());

            pb.dismiss();
            drawchart(recordtime, recordvalues_BSAC, recordvalues_BSPC, recordvalues_BSNM, time_bsac, time_bspc, time_bsnm);
        } else {
            ArrayList<String> defArray = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                defArray.add("0");
            }
            pb.dismiss();
            drawchart(defArray, defArray, defArray, defArray, defArray, defArray, defArray);
        }
    }

    private void drawchart(ArrayList<String> recordtime
            , ArrayList<String> recordvalues_BSAC, ArrayList<String> recordvalues_BSPC, ArrayList<String> recordvalues_BSNM
            , ArrayList<String> time_bsac, ArrayList<String> time_bspc, ArrayList<String> time_bsnm) {
        ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(getContext()
                , getString(R.string.webview_loading_title)
                , getString(R.string.msg_tokenget), false, true);
        pb.show();

        List<String> xVals = new ArrayList<>();
        Log.v(TAG + "recordtime.size()", String.valueOf(recordtime.size()));
        for (int i = 0; i < recordtime.size(); i++) {
            xVals.add(recordtime.get(i));
        }

        String dataset_label1 = "飯前血糖";
        ArrayList<Entry> yVals1 = new ArrayList<>();

        for (int i = 0; i < recordvalues_BSAC.size(); i++) {
            if (Integer.parseInt(recordvalues_BSAC.get(i)) > 200) {
                chart.getAxisLeft().resetAxisMaxValue();
            }
            yVals1.add(new Entry(Float.parseFloat(recordvalues_BSAC.get(i)), Integer.parseInt(time_bsac.get(i))));
        }

        String dataset_label2 = "飯後血糖";
        ArrayList<Entry> yVals2 = new ArrayList<>();

        for (int i = 0; i < recordvalues_BSPC.size(); i++) {
            if (Integer.parseInt(recordvalues_BSPC.get(i)) > 200) {
                chart.getAxisLeft().resetAxisMaxValue();
            }
            yVals2.add(new Entry(Float.parseFloat(recordvalues_BSPC.get(i)), Integer.parseInt(time_bspc.get(i))));
        }

        String dataset_label3 = "隨機血糖";
        ArrayList<Entry> yVals3 = new ArrayList<>();

        for (int i = 0; i < recordvalues_BSNM.size(); i++) {
            if (Integer.parseInt(recordvalues_BSNM.get(i)) > 200) {
                chart.getAxisLeft().resetAxisMaxValue();
            }
            yVals3.add(new Entry(Float.parseFloat(recordvalues_BSNM.get(i)), Integer.parseInt(time_bsnm.get(i))));
        }

        LineDataSet dataSet1 = new LineDataSet(yVals1, dataset_label1);
        dataSet1.setColors(new int[]{R.color.C8A152}, getContext());
        dataSet1.setLineWidth(5f);
        dataSet1.setCircleSize(5f);
        dataSet1.setValueTextSize(12f);

        LineDataSet dataSet2 = new LineDataSet(yVals2, dataset_label2);
        dataSet2.setColors(new int[]{R.color.A74A3D}, getContext());
        dataSet2.setLineWidth(5f);
        dataSet2.setCircleSize(5f);
        dataSet2.setValueTextSize(12f);

        LineDataSet dataSet3 = new LineDataSet(yVals3, dataset_label3);
        dataSet3.setColors(new int[]{R.color.b799AB3}, getContext());
        dataSet3.setLineWidth(5f);
        dataSet3.setCircleSize(5f);
        dataSet3.setValueTextSize(12f);

        List<LineDataSet> dataSetList = new ArrayList<>();
        dataSetList.add(dataSet1);
        dataSetList.add(dataSet2);
        dataSetList.add(dataSet3);

        LineData data = new LineData(xVals, dataSetList);
        chart.setData(data);
        chart.invalidate();

        pb.dismiss();
    }

    private void setleftAxis(LineChart chart) {
        // 获得左侧侧坐标轴
        YAxis leftAxis = chart.getAxisLeft();

        // 设置左侧的LimitLine
        LimitLine ll1 = new LimitLine(140f, "標準飯前血糖數值上限");
        ll1.setLineWidth(2f);
        ll1.setLineColor(0xFFE8D7B5);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(70f, "標準飯前血糖數值下限");
        ll2.setLineWidth(2f);
        ll2.setLineColor(0xFFE8D7B5);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);

        LimitLine ll3 = new LimitLine(200f, "標準飯後血糖數值上限");
        ll3.setLineWidth(2f);
        ll3.setLineColor(0xFFE2B8B1);
        ll3.enableDashedLine(10f, 10f, 0f);
        ll3.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll3.setTextSize(10f);

        LimitLine ll4 = new LimitLine(90f, "飯後血糖數值下限");
        ll4.setLineWidth(2f);
        ll4.setLineColor(0xFFE2B8B1);
        ll4.enableDashedLine(10f, 10f, 0f);
        ll4.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll4.setTextSize(10f);

        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.addLimitLine(ll3);
        leftAxis.addLimitLine(ll4);

        chart.getAxisLeft().setAxisMaxValue(210f);
    }

    protected class queryMeasureResource extends AsyncTask<String, String, JSONObject> {
        ShowMEProgressDiaLog pb;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH) - (prf.getInt("day_count", 0));
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        int millisecond = c.get(Calendar.MILLISECOND);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb = new ShowMEProgressDiaLog(getContext(), getString(R.string.webview_loading_title), getString(R.string.msg_tokenget), false, true);
            //            pDialog = new ProgressDialog(getContext());
            //            pDialog.setMessage("連接資料庫中，請稍後...");
            //            pDialog.setIndeterminate(false);
            //            pDialog.setCancelable(true);
            //            Code = pref.getString("Code", "");
            pb.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            String timeStr = null;
            JSONObject response = null;
            try {
                Date date = formatter.parse(String.valueOf(year + "-" + (month + 1) + "-" + day + " " + hour + ":" + minute + ":" + second + "." + millisecond));
                timeStr = formatter.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!isCancelled()) {
                try {
                    response = webServiceConnection.queryMeasureResource(prf.getString("access_token", ""), "BS", timeStr, formatter.format(new Date()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            try {
                if (response != null) {
                    Log.v(TAG + "response", response.toString());
                    if (response.getString("message").equals("Success.")) {
                        JSONArray recordArray = response.getJSONArray("result");
                        Log.v(TAG + "jsonArray", recordArray.toString());

                        //                        if (recordtime.size() > 0) {
                        //                            recordtime.clear();
                        //                            recordvalues_BSAC.clear();
                        //                            recordvalues_BSPC.clear();
                        //                            recordvalues_BSNM.clear();
                        //
                        //                            time_bsac.clear();
                        //                            time_bspc.clear();
                        //                            time_bsnm.clear();
                        //                        }

                        UserAdapter userAdapter = new UserAdapter(getContext());
                        List<User> userList = userAdapter.getAllUser();
                        String UserName = null;
                        for (User user : userList) {
                            Log.v(TAG + "userName", user.getName().toString());
                            UserName = user.getName();
                        }

                        DeviceMappingAdapter deviceMappingAdapter = new DeviceMappingAdapter(getContext());
                        ArrayList<DeviceMapping> listDevice = deviceMappingAdapter.getAllDeviceData();
                        String DeviceID = null;
                        for (DeviceMapping deviceMapping : listDevice) {
                            Log.v(TAG + "DeviceID", deviceMapping.getDeviceId().toString());
                            DeviceID = deviceMapping.getDeviceId();
                        }

                        GcmUtil gcmUtil = new GcmUtil();

                        BioDataAdapter bioDataAdapter = new BioDataAdapter(getContext());
                        bioDataAdapter.deleteBioData_device_type(2);

                        //                        int count = 0;
                        for (int i = 0; i < recordArray.length(); i++) {
                            String recordtypestr = recordArray.getJSONObject(i).get("type").toString();
                            String recorddeviceIdstr = recordArray.getJSONObject(i).get("deviceId").toString();
                            String recordtimestr = recordArray.getJSONObject(i).get("time").toString();
                            String recordmarkstr = recordArray.getJSONObject(i).get("mark").toString();
                            String recordvaluesstr = recordArray.getJSONObject(i).get("values").toString();

                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = formatter.parse(recordtimestr);
                            String timeStr = formatter.format(date);

                            recordvaluesstr = recordvaluesstr.replace("[", "");
                            recordvaluesstr = recordvaluesstr.replace("]", "");

                            BioData bioData = new BioData();

                            //                            bioData.set_id(timeStr + gcmUtil.getDeviceSerail(getContext()));
                            bioData.setUserId(UserName);
                            bioData.setDeviceTime(timeStr);
                            bioData.setDeviceType(webServiceConnection.BIODATA_DEVICE_TYPE_BLOOD_GLUCOSE);

                            if (recorddeviceIdstr.length() > 10) {
                                bioData.setDeviceMac(recorddeviceIdstr);
                                bioData.setInputType(webServiceConnection.UPLOAD_INPUT_TYPE_DEVICE);
                                bioData.setDeviceId(DeviceID);
                            } else {
                                bioData.setInputType(webServiceConnection.UPLOAD_INPUT_TYPE_MANUAL);
                            }

                            if (recordmarkstr.equals("AC")) {
                                bioData.setAc(recordvaluesstr);
                            } else if (recordmarkstr.equals("PC")) {
                                bioData.setPc(recordvaluesstr);
                            } else if (recordmarkstr.equals("NM")) {
                                bioData.setNm(recordvaluesstr);
                            }

                            bioData.setUploaded(webServiceConnection.DATA_ALREADY_UPLOAD);
                            bioDataAdapter.createGlucose(bioData);

                            //                            recordtime.add(timeStr);

                            //                            if (recordmarkstr.equals("AC")) {
                            //                                recordvalues_BSAC.add(recordvaluesstr);
                            //                                time_bsac.add(String.valueOf(count));
                            //                            } else if (recordmarkstr.equals("PC")) {
                            //                                recordvalues_BSPC.add(recordvaluesstr);
                            //                                time_bspc.add(String.valueOf(count));
                            //                            } else if (recordmarkstr.equals("NM")) {
                            //                                recordvalues_BSNM.add(recordvaluesstr);
                            //                                time_bsnm.add(String.valueOf(count));
                            //                            }
                            //                            count++;
                        }

                        pb.dismiss();
                        initchart();
                        //                        drawchart(recordtime, recordvalues_BSAC, recordvalues_BSPC, recordvalues_BSNM, time_bsac, time_bspc, time_bsnm);

                    } else {
                        Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(getContext(), getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
            } finally {
                pb.dismiss();
            }
        }
    }

}