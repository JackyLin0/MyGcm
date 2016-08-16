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
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.BioData;
import tw.com.omnihealthgroup.healthcare.myhealthcare.dbo.BioDataAdapter;


public class BodyArmCircumferenceFragment extends DrawerFrameworkMainFragment {
    private static final String TAG = "BodyArmCircumferenceFragment";
    private View rootView;
    private String authCode = null;
    private LineChart chart;

    private ArrayList<String> recordtime = new ArrayList<>();
    private ArrayList<String> recordvalues_ArmcirCumference = new ArrayList<>();

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

        //        new queryMeasureResource().cancel(true);
        //        new TokenExp().cancel(true);
        //        new TokenGet().cancel(true);
    }

    private void checkView() {
        //        String timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        //        String checkLogin = webServiceConnection.DateCompare(timeStr, prf.getString("take_time", ""));

        //        if (webServiceConnection.isOnline(getContext())) {
        //            if (checkLogin.equals("true")) {
        //        new queryMeasureResource().execute();
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
        List<BioData> listData = bioDataAdapter.getBodyArmCircumference_withTime(timeStr, formatter.format(new Date()));
        if (listData.size() > 0) {
            recordtime.clear();
            recordvalues_ArmcirCumference.clear();

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

                    recordvalues_ArmcirCumference.add(bioData.getArmcircumference());
                }
            }

            Log.v(TAG + "recordtime", recordtime.toString());
            Log.v(TAG + "recordvalues_ArmcirCumference", recordvalues_ArmcirCumference.toString());

            pb.dismiss();
            drawchart(recordtime, recordvalues_ArmcirCumference);
        } else {
            ArrayList<String> defArray = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                defArray.add("0");
            }
            pb.dismiss();
            drawchart(defArray, defArray);
        }
    }

    private void drawchart(ArrayList<String> recordtime, ArrayList<String> recordvalues_ArmcirCumference) {
        ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(getContext()
                , getString(R.string.webview_loading_title)
                , getString(R.string.msg_tokenget), false, true);
        pb.show();

        List<String> xVals = new ArrayList<>();
        Log.v(TAG + "recordtime.size()", String.valueOf(recordtime.size()));
        for (int i = 0; i < recordtime.size(); i++) {
            xVals.add(recordtime.get(i));
        }

        String dataset_label1 = "上臂圍數值";
        ArrayList<Entry> yVals1 = new ArrayList<>();

        int cont = 0;
        for (int i = 0; i < recordvalues_ArmcirCumference.size(); i++) {
            if (Integer.parseInt(recordvalues_ArmcirCumference.get(i)) > 100) {
                chart.getAxisLeft().resetAxisMaxValue();
            }
            yVals1.add(new Entry(Float.parseFloat(recordvalues_ArmcirCumference.get(i)), cont));
            cont++;
        }

        LineDataSet dataSet1 = new LineDataSet(yVals1, dataset_label1);
        dataSet1.setColors(new int[]{R.color.p9e78d4}, getContext());
        dataSet1.setLineWidth(5f);
        dataSet1.setCircleSize(5f);
        dataSet1.setValueTextSize(12f);

        List<LineDataSet> dataSetList = new ArrayList<>();
        dataSetList.add(dataSet1);

        LineData data = new LineData(xVals, dataSetList);
        chart.setData(data);
        chart.invalidate();

        pb.dismiss();
    }

    private void setleftAxis(LineChart chart) {
        // 获得左侧侧坐标轴
        YAxis leftAxis = chart.getAxisLeft();

        // 设置左侧的LimitLine
        LimitLine ll1 = new LimitLine(100f, "標準上臂圍數值上限");
        ll1.setLineWidth(2f);
        ll1.setLineColor(0xFFB6CEE7);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(10f, "標準上臂圍數值下限");
        ll2.setLineWidth(2f);
        ll2.setLineColor(0xFFB6CEE7);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);

        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);

        chart.getAxisLeft().setAxisMaxValue(110f);
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
                    response = webServiceConnection.queryMeasureResource(prf.getString("access_token", ""), "ArmCircumference", timeStr, formatter.format(new Date()));
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
                        //                            recordvalues_HP.clear();
                        //                        }

                        //                        UserAdapter userAdapter = new UserAdapter(getContext());
                        //                        List<User> userList = userAdapter.getAllUser();
                        //                        String UserName = null;
                        //                        for (User user : userList) {
                        //                            Log.v("userName", user.getName().toString());
                        //                            UserName = user.getName();
                        //                        }
                        //
                        //                        DeviceMappingAdapter deviceMappingAdapter = new DeviceMappingAdapter(getContext());
                        //                        ArrayList<DeviceMapping> listDevice = deviceMappingAdapter.getAllDeviceData();
                        //                        String DeviceID = null;
                        //                        for (DeviceMapping deviceMapping : listDevice) {
                        //                            Log.v("DeviceID", deviceMapping.getDeviceId().toString());
                        //                            DeviceID = deviceMapping.getDeviceId();
                        //                        }

                        //                        GcmUtil gcmUtil = new GcmUtil();
                        //
                        //                        BioDataAdapter bioDataAdapter = new BioDataAdapter(getContext());
                        //                        bioDataAdapter.deleteBioData_device_type(6);
                        //
                        //                        for (int i = 0; i < recordArray.length(); i++) {
                        //                            String recordtypestr = recordArray.getJSONObject(i).get("type").toString();
                        //                            String recorddeviceIdstr = recordArray.getJSONObject(i).get("deviceId").toString();
                        //                            String recordtimestr = recordArray.getJSONObject(i).get("time").toString();
                        //                            String recordmarkstr = recordArray.getJSONObject(i).get("mark").toString();
                        //                            String recordvaluesstr = recordArray.getJSONObject(i).get("values").toString();
                        //
                        //                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        //                            Date date = formatter.parse(recordtimestr);
                        //                            String timeStr = formatter.format(date);
                        //
                        //                            recordvaluesstr = recordvaluesstr.replace("[", "");
                        //                            recordvaluesstr = recordvaluesstr.replace("]", "");
                        //                            String[] bpData = recordvaluesstr.split(",");
                        //
                        //                            BioData bioData = new BioData();
                        //
                        //                            bioData.set_id(timeStr + gcmUtil.getDeviceSerail(getContext()));
                        //                            bioData.setUserId(UserName);
                        //                            bioData.setDeviceTime(timeStr);
                        //                            bioData.setDeviceType(webServiceConnection.BIODATA_DEVICE_TYPE_BLOOD_PRESSURE);
                        //                            bioData.setBhp(bpData[0]);
                        //                            bioData.setBlp(bpData[1]);
                        //                            bioData.setPulse(bpData[2]);
                        //                            if (recorddeviceIdstr.length() > 10) {
                        //                                bioData.setDeviceMac(recorddeviceIdstr);
                        //                                bioData.setInputType(webServiceConnection.UPLOAD_INPUT_TYPE_DEVICE);
                        //                                bioData.setDeviceId(DeviceID);
                        //                            } else {
                        //                                bioData.setInputType(webServiceConnection.UPLOAD_INPUT_TYPE_MANUAL);
                        //                            }
                        //
                        //                            bioData.setUploaded(webServiceConnection.DATA_ALREADY_UPLOAD);
                        //
                        //                            bioDataAdapter.createBioData(bioData);
                        //
                        //
                        //                            //                            recordtime.add(timeStr);
                        //                            //                            recordvalues_HP.add(bpData[2]);
                        //                        }

                        pb.dismiss();
                        //                        initchart();
                        //                        drawchart(recordtime, recordvalues_HP);

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