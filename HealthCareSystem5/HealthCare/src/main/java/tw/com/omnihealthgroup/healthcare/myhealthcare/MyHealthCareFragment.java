package tw.com.omnihealthgroup.healthcare.myhealthcare;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import tw.com.omnihealthgroup.drawerframework.DrawerFrameworkMainFragment;
import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.util.ShowMEProgressDiaLog;
import tw.com.omnihealthgroup.healthcare.WebServiceConnection;
import tw.com.omnihealthgroup.healthcare.myhealthcare.adapter.LazyViewPager;
import tw.com.omnihealthgroup.healthcare.myhealthcare.adapter.NoScrollViewPager;
import tw.com.omnihealthgroup.healthcare.myhealthcare.adapter.ViewPagerAdapter;
import tw.com.omnihealthgroup.healthcare.myhealthcare.service.BluetoothLeService;
import tw.com.omnihealthgroup.healthcare.myhealthcare.service.GetBlueToothDeviceDataService;
import tw.com.omnihealthgroup.healthcare.myhealthcare.service.MainService;

/**
 * Created by Administrator on 2016/5/23.
 */
public class MyHealthCareFragment extends DrawerFrameworkMainFragment {
    private static final String TAG = "MyHealthCareFragment";
    private View rootView;
    private WebView myBrowser = null;
    private String authCode = null;

    private SharedPreferences prf;
    private WebServiceConnection webServiceConnection;

    //    private ViewPager mainViewPager;
    private NoScrollViewPager mainViewPager;
    private ViewPagerAdapter pagerAdapter;
    private BluetoothAdapter mainBluetoothAdapter = null;
    private static final int REQUEST_ENABLE_BT = 2;

    private RadioGroup tabButtonGroup;
    private HorizontalScrollView tabScrollBar;
    private ArrayList<String> titleChannel = new ArrayList<>();
    private ArrayList<Fragment> fragmentList = new ArrayList<>();
    private static String[] FRAGMENT_NAME_ARRAY = new String[]{
            BloodPressureFragment.class.getName()
            , HeardPulseFragment.class.getName()
            , BloodGlucoseFragment.class.getName()
            , BodyHeightFragment.class.getName()
            , BMIMeasureFragment.class.getName()
            , BodyTemperatureFragment.class.getName()
            , BodyHeadCircumferenceFragment.class.getName()
            , BodyArmCircumferenceFragment.class.getName()
            , BodyWaistlineFragment.class.getName()
            , HipsFragment.class.getName()};

    /**
     * 載入共用ToolBar
     *
     * @param toolbar toolbar
     */
    @Override
    protected void onSetToolbar(Toolbar toolbar) {
        super.onSetToolbar(toolbar);
        toolbar.setTitle(getString(R.string.fragment_myhealthcare));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        prf = getActivity().getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
        webServiceConnection = new WebServiceConnection();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_myhealthcare, container, false);
        Log.v(TAG, "onCreateView");

        getTitleChannel();
        getFragmentsList();

        initTabButton();
        initView();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");

        checkView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v(TAG, "onDestroyView");

        new TokenExp().cancel(true);
        new TokenGet().cancel(true);

        boolean isStartMainService = webServiceConnection.isServiceRunning(getActivity(), MainService.class.getName());
        if (isStartMainService) {
            //  關閉BT3.0的Service
            Intent btServiceIntent = new Intent(getActivity(), GetBlueToothDeviceDataService.class);
            getActivity().stopService(btServiceIntent);
            //  關閉解析BT3.0的Service
            Intent MainServiceIntent = new Intent(getActivity(), MainService.class);
            getActivity().stopService(MainServiceIntent);
        }
    }

    /**
     * 初始化TabButton 動態新增TabButton
     */
    private void initTabButton() {
        tabButtonGroup = (RadioGroup) rootView.findViewById(R.id.tab_btn_group);
        tabScrollBar = (HorizontalScrollView) rootView.findViewById(R.id.tab_scrollbar);

        for (int i = 0; i < titleChannel.size(); i++) {
            RadioButton addTabButton = (RadioButton) LayoutInflater
                    .from(getActivity())
                    .inflate(R.layout.tab_button_custom, null);
            addTabButton.setId(i);
            addTabButton.setText(titleChannel.get(i));
            RadioGroup.LayoutParams params = new RadioGroup
                    .LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT
                    , RadioGroup.LayoutParams.MATCH_PARENT);
            tabButtonGroup.addView(addTabButton, params);
        }
        ((RadioButton) tabButtonGroup.getChildAt(0)).setTextColor(0xFFFFFFFF);
        tabButtonGroup.check(0);
        initViewPager(); //初始化ViewPager
    }

    /**
     * 初始化ViewPager
     */
    private void initViewPager() {
        //        mainViewPager = (ViewPager) rootView.findViewById(R.id.main_viewpager);
        mainViewPager = (NoScrollViewPager) rootView.findViewById(R.id.main_viewpager);
        pagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), fragmentList);
        mainViewPager.setAdapter(pagerAdapter);
        mainViewPager.setCurrentItem(0);

        //初始化監聽
        initHandler();
    }

    /**
     * 初始化TabButton、ViewPager的監聽器
     */
    private void initHandler() {

        //TabButton
        tabButtonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mainViewPager.setCurrentItem(checkedId);
            }
        });

        //ViewPager
        //        mainViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        mainViewPager.setOnPageChangeListener(new LazyViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTabScroll(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 設置TabButton滑動變化
     *
     * @param position 選中的Tab參數
     */
    private void setTabScroll(int position) {
        RadioButton setTabButton = (RadioButton) tabButtonGroup.getChildAt(position);
        for (int i = 0; i < tabButtonGroup.getChildCount(); i++) {
            // 判斷是否為選中的button，選中的文字為白色未選中的為灰色
            if (i == position) {
                setTabButton.setTextColor(0xFFFFFFFF);  //白色
            } else {
                ((RadioButton) tabButtonGroup.getChildAt(i)).setTextColor(0xFFACACAC);  //灰色
            }
        }

        setTabButton.setChecked(true);
        int left = setTabButton.getLeft();
        int width = setTabButton.getMeasuredWidth();
        DisplayMetrics metrics = new DisplayMetrics();
        super.getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int len = left + width / 2 - screenWidth / 2;
        tabScrollBar.smoothScrollTo(len, 0);    //滑動ScrollView
    }

    /**
     * 取得各項目的頁面清單
     *
     * @return fragmentList
     */
    private List<Fragment> getFragmentsList() {
        for (int i = 0; i < FRAGMENT_NAME_ARRAY.length; i++) {
            fragmentList.add(Fragment.instantiate(getActivity(), FRAGMENT_NAME_ARRAY[i]));
        }
        return fragmentList;
    }

    /**
     * 取得各項目的標題清單
     *
     * @return
     */
    private ArrayList<String> getTitleChannel() {
        titleChannel.add(getString(R.string.tabpage_1));
        titleChannel.add(getString(R.string.tabpage_3));
        titleChannel.add(getString(R.string.tabpage_2));
        titleChannel.add(getString(R.string.tabpage_4));
        titleChannel.add(getString(R.string.tabpage_5));
        titleChannel.add(getString(R.string.tabpage_6));
        titleChannel.add(getString(R.string.tabpage_7));
        titleChannel.add(getString(R.string.tabpage_8));
        titleChannel.add(getString(R.string.tabpage_9));
        titleChannel.add(getString(R.string.tabpage_10));
        return titleChannel;
    }

    /**
     * 建立Tab相關物件實體
     */
    private void initView() {
        //監聽Button
        //        final FloatingActionButton event_btn = (FloatingActionButton) rootView.findViewById(R.id.event_btn);
        final ImageView event_btn = (ImageView) rootView.findViewById(R.id.event_btn);

        switch (prf.getInt("day_count", 0)) {
            case 3:
                Log.v(TAG + "day_count", String.valueOf(prf.getInt("day_count", 0)));
                //                ColorStateList lists_3 = getResources().getColorStateList(R.color.event_dot_color);
                //                event_btn.setBackgroundTintList(lists_3);
                event_btn.setImageResource(R.drawable.circleshadow_event_blue);
                break;
            case 7:
                Log.v(TAG + "day_count", String.valueOf(prf.getInt("day_count", 0)));
                //                ColorStateList lists_7 = getResources().getColorStateList(R.color.event_type_gracegreen);
                //                event_btn.setBackgroundTintList(lists_7);
                event_btn.setImageResource(R.drawable.circleshadow_event_gracegreen);
                break;
            case 30:
                Log.v(TAG + "day_count", String.valueOf(prf.getInt("day_count", 0)));
                //                ColorStateList lists_30 = getResources().getColorStateList(R.color.event_type_yellow);
                //                event_btn.setBackgroundTintList(lists_30);
                event_btn.setImageResource(R.drawable.circleshadow_event_yellow_color);
                break;
            case 180:
                Log.v(TAG + "day_count", String.valueOf(prf.getInt("day_count", 0)));
                //                ColorStateList lists_180 = getResources().getColorStateList(R.color.c_reminder_bg);
                //                event_btn.setBackgroundTintList(lists_180);
                event_btn.setImageResource(R.drawable.circleshadow_event_graceblue);
                break;
            case 365:
                Log.v(TAG + "day_count", String.valueOf(prf.getInt("day_count", 0)));
                //                ColorStateList lists_365 = getResources().getColorStateList(R.color.yellow_color);
                //                event_btn.setBackgroundTintList(lists_365);
                event_btn.setImageResource(R.drawable.circleshadow_event_yellow);
                break;
        }

        event_btn.setOnClickListener(new View.OnClickListener() {
            String[] eventType = getResources().getStringArray(R.array.count_event);
            ArrayList<String> textArrayList = new ArrayList<>(Arrays.asList(eventType));

            @Override
            public void onClick(View v) {
                new android.support.v7.app.AlertDialog.Builder(getContext())
                        .setItems(textArrayList.toArray(new String[textArrayList.size()]), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = textArrayList.get(which);
                                switch (name) {
                                    case "三天":
                                        prf.edit().putInt("day_count", 3).commit();
                                        Log.v(TAG + "day_count", String.valueOf(prf.getInt("day_count", 0)));
                                        //                                        ColorStateList lists_3 = getResources().getColorStateList(R.color.event_dot_color);
                                        //                                        event_btn.setBackgroundTintList(lists_3);
                                        event_btn.setImageResource(R.drawable.circleshadow_event_blue);

                                        //                                        fragmentList.clear();
                                        //                                        getFragmentsList();
                                        pagerAdapter.notifyDataSetChanged();

                                        break;

                                    case "一週":
                                        prf.edit().putInt("day_count", 7).commit();
                                        Log.v(TAG + "day_count", String.valueOf(prf.getInt("day_count", 0)));
                                        //                                        ColorStateList lists_7 = getResources().getColorStateList(R.color.event_type_gracegreen);
                                        //                                        event_btn.setBackgroundTintList(lists_7);
                                        event_btn.setImageResource(R.drawable.circleshadow_event_gracegreen);

                                        //                                        fragmentList.clear();
                                        //                                        getFragmentsList();
                                        pagerAdapter.notifyDataSetChanged();

                                        break;

                                    case "一個月":
                                        prf.edit().putInt("day_count", 30).commit();
                                        Log.v(TAG + "day_count", String.valueOf(prf.getInt("day_count", 0)));
                                        //                                        ColorStateList lists_30 = getResources().getColorStateList(R.color.event_type_yellow);
                                        //                                        event_btn.setBackgroundTintList(lists_30);
                                        event_btn.setImageResource(R.drawable.circleshadow_event_yellow_color);

                                        //                                        fragmentList.clear();
                                        //                                        getFragmentsList();
                                        pagerAdapter.notifyDataSetChanged();

                                        break;

                                    case "三個月":
                                        prf.edit().putInt("day_count", 180).commit();
                                        Log.v(TAG + "day_count", String.valueOf(prf.getInt("day_count", 0)));
                                        //                                        ColorStateList lists_180 = getResources().getColorStateList(R.color.c_reminder_bg);
                                        //                                        event_btn.setBackgroundTintList(lists_180);
                                        event_btn.setImageResource(R.drawable.circleshadow_event_graceblue);

                                        //                                        fragmentList.clear();
                                        //                                        getFragmentsList();
                                        pagerAdapter.notifyDataSetChanged();

                                        break;

                                    case "一年":
                                        prf.edit().putInt("day_count", 365).commit();
                                        Log.v(TAG + "day_count", String.valueOf(prf.getInt("day_count", 0)));
                                        //                                        ColorStateList lists_365 = getResources().getColorStateList(R.color.yellow_color);
                                        //                                        event_btn.setBackgroundTintList(lists_365);
                                        event_btn.setImageResource(R.drawable.circleshadow_event_yellow);

                                        //                                        fragmentList.clear();
                                        //                                        getFragmentsList();
                                        pagerAdapter.notifyDataSetChanged();

                                        break;
                                }
                            }
                        }).show();
            }
        });

        rootView.findViewById(R.id.btn_pressuredata).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(rootView.getContext(), AddMeasurePressureActivity.class);
                startActivity(intent);
            }
        });

        rootView.findViewById(R.id.btn_glucosedata).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(rootView.getContext(), AddMeasureGlucoseActivity.class);
                startActivity(intent);
            }
        });

        rootView.findViewById(R.id.btn_body).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(rootView.getContext(), AddMeasureBodyActivity.class);
                startActivity(intent);
            }
        });

        rootView.findViewById(R.id.btn_BLE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBluetoothDevice();
            }
        });

        /////////////

        rootView.findViewById(R.id.btn_orth1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new AdminPostFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_myhealthcare, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        rootView.findViewById(R.id.btn_orth2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new NewMedicineFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_myhealthcare, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        rootView.findViewById(R.id.btn_orth3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new OperationFocusFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_myhealthcare, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        rootView.findViewById(R.id.btn_orth4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new SecretaryReminderFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_myhealthcare, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void checkView() {
        String timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String checkLogin = webServiceConnection.DateCompare(timeStr, prf.getString("take_time", ""));

        if (webServiceConnection.isOnline(getContext())) {
            if (checkLogin.equals("true")) {
                loadPageView();
            } else if (checkLogin.equals("exToke")) {
                new TokenExp().execute();
            } else {
                new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.message_title))
                        .setMessage(getString(R.string.msg_retokenget))
                        .setPositiveButton(getString(R.string.msg_confirm),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        try {
                                            getAuthRequest(getContext(), R.layout.auth_dialog, R.id.webv);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).show();
            }
        } else {
            new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.msg_connect_faild_title))
                    .setMessage(getString(R.string.msg_connect_faild))
                    .setPositiveButton(getString(R.string.msg_confirm),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                                    getContext().startActivity(intent);
                                }
                            }).show();
        }
    }

    private void loadPageView() {
        final ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(getContext()
                , getString(R.string.webview_loading_title)
                , getString(R.string.msg_tokenget), false, true);
        pb.show();

        Log.v(TAG, TAG + "Start");

        pb.dismiss();
    }

    /**
     * 1. 確認系統版本是否支援BLE（API 18+）
     * 2. 確認裝置是否支援藍芽4.0
     * 3. 確認藍芽是否開啟
     */
    private void checkBluetoothDevice() {
        //初始化藍芽適配器，並確認android版本
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //初始化BLEService
            mainBluetoothAdapter = BluetoothLeService.BLEService.initBlueTooth(getActivity(), true);

            //檢查BLE是否支援此裝置，並選擇性禁用BLE的相關功能
            if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                webServiceConnection.getMessageDialog(getString(R.string.webview_loading_title), getString(R.string.auto_upload_ble_not_supported), getActivity()).show();

            } else if (!BluetoothLeService.BLEService.checkBluetoothOpen()) {
                //確認藍芽在裝置上可以開啟
                getBluetoothDialog(getString(R.string.webview_loading_title), getString(R.string.auto_upload_ble_open)).show();


            } else if (webServiceConnection.isOnline(getActivity())) {
                //搜尋裝置的進度條
                BluetoothLeService.BLEService.postSearchingProgressDialog(getActivity());
                //開始掃描BT3.0裝置
                boolean isStartBlueTooth = WebServiceConnection.isServiceRunning(getActivity(), GetBlueToothDeviceDataService.class.getName());
                Log.i(TAG, " isStartBlueTooth : " + isStartBlueTooth);
                if (!isStartBlueTooth) {
                    Intent inetnt = new Intent(getActivity(), GetBlueToothDeviceDataService.class);
                    getActivity().startService(inetnt);
                }
                boolean isStartMainService = WebServiceConnection.isServiceRunning(getActivity(), MainService.class.getName());
                if (!isStartMainService) {
                    startMainService();
                }
                //開始掃描BLE裝置
                BluetoothLeService.BLEService.searchBLEDevice(true);
            } else {
                webServiceConnection.getMessageDialog(getString(R.string.webview_loading_title), getString(R.string.auto_upload_nonetwork_error), getActivity()).show();

            }
        } else {
            //SDK version < JELLY_BEAN_MR2(Android4.3, Api 18)
            webServiceConnection.getMessageDialog(getString(R.string.auto_upload_old_version_title), getString(R.string.auto_upload_old_vertion), getActivity()).show();
            mainBluetoothAdapter = BluetoothLeService.BLEService.initBlueTooth(getActivity(), false);
        }
    }

    //YMU modify
    private void startMainService() {
        Intent i = new Intent(getActivity(), MainService.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        ComponentName CN = getActivity().startService(i);
        Log.e(TAG, "ComponentName CN:" + CN);
        if (CN == null) {
            Log.e(TAG, "Start Bluetooth Server Failed");
        } else {
            Log.e(TAG, "Start Bluetooth Server Succeed");
        }
    }


    /**
     * 判斷藍芽是否開啟的AlertDialog
     *
     * @param title   標題
     * @param message 內文訊息
     * @return 實作BluetoothDialog
     */
    private AlertDialog getBluetoothDialog(String title, String message) {
        //產生一個Builder物件
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //設定Dialog的標題
        // builder.setIcon(R.drawable.alert_icon);
        builder.setTitle(title);
        //設定Dialog的內容
        builder.setMessage(message);
        //設定Positive按鈕資料
        builder.setPositiveButton(getString(R.string.msg_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //按下按鈕時顯示快顯
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        });
        //利用Builder物件建立AlertDialog
        return builder.create();
    }

    /**
     * GetAuthCode
     */
    protected void getAuthRequest(final Context context, int dialogid, int webid) {
        final Dialog auth_dialog = new Dialog(context);
        final ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(context, getString(R.string.message_title), getString(R.string.msg_tokenget), true, false);
        pb.show();

        auth_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //去除Dialog標題
        auth_dialog.setContentView(dialogid);
        myBrowser = (WebView) auth_dialog.findViewById(webid);
        webServiceConnection.setBrowserProperty(myBrowser);
        webServiceConnection.setWebviewProperty(myBrowser, getContext());

        String webSiteStr = webServiceConnection.AuthServer + "/" + webServiceConnection.AuthRequest
                + "?response_type=code"
                + "&client_id=" + webServiceConnection.CLIENT_ID
                //                + "&secret_key=" + webServiceConnection.CLIENT_SECRET
                + "&redirect_url=" + webServiceConnection.REDIRECT_URI
                + "&display=page"
                + "&scope=user_profile"
                + "&scope=measure_data"
                + "&scope=health_report"
                + "&state=Oauth_Call"
                + "&access_type=online"
                + "&prompt=none"
                + "&login_hint=email";

        Log.v(TAG + "webSiteStr", webSiteStr);
        myBrowser.loadUrl(webSiteStr);

        myBrowser.setWebViewClient(new WebViewClient() {
            boolean authComplete = false;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pb.dismiss();
                if (url.contains("?code=") && authComplete != true) {
                    Uri uri = Uri.parse(url);
                    authCode = uri.getQueryParameter("code");
                    Log.i("", "CODE : " + authCode);
                    authComplete = true;
                    //                    Toast.makeText(context, "Authorization Code is: " + authCode, Toast.LENGTH_SHORT).show();
                    pb.dismiss();
                    auth_dialog.dismiss();
                    new TokenGet().execute();

                } else if (url.contains("error=access_denied")) {
                    Log.i("", "ACCESS_DENIED_HERE");
                    authComplete = true;
                    Toast.makeText(context, getString(R.string.msg_net_faild), Toast.LENGTH_SHORT).show();
                    auth_dialog.dismiss();
                }
            }
        });
        auth_dialog.show();
        auth_dialog.setCancelable(true);
    }

    protected class TokenGet extends AsyncTask<String, String, JSONObject> {
        ShowMEProgressDiaLog pb;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb = new ShowMEProgressDiaLog(getContext(), getString(R.string.webview_loading_title), getString(R.string.msg_tokenget), true, false);
            //            pDialog = new ProgressDialog(getContext());
            //            pDialog.setMessage("連接資料庫中，請稍後...");
            //            pDialog.setIndeterminate(false);
            //            pDialog.setCancelable(true);
            //            Code = pref.getString("Code", "");
            pb.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject response = null;
            if (!isCancelled()) {
                try {
                    response = webServiceConnection.getTokenRequest(authCode, webServiceConnection.CLIENT_ID, webServiceConnection.CLIENT_SECRET);
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
                    if (response.getString("message").equals("Success")) {
                        String access_token = response.getJSONObject("result").getString("access_token");
                        String refresh_token = response.getJSONObject("result").getString("refresh_token");
                        String take_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        Log.d(TAG + "access_token", access_token);
                        Log.d(TAG + "refresh_token", refresh_token);
                        Log.d(TAG + "take_time", take_time);

                        prf.edit().putString("access_token", access_token)
                                .putString("refresh_token", refresh_token)
                                .putString("take_time", take_time)
                                .commit();

                        pb.dismiss();
                        loadPageView();

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

    protected class TokenExp extends AsyncTask<String, String, JSONObject> {
        ShowMEProgressDiaLog pb;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb = new ShowMEProgressDiaLog(getContext(), getString(R.string.webview_loading_title), getString(R.string.msg_tokenexp), true, false);
            //            pDialog = new ProgressDialog(getContext());
            //            pDialog.setMessage("連接資料庫中，請稍後...");
            //            pDialog.setIndeterminate(false);
            //            pDialog.setCancelable(true);
            //            Code = pref.getString("Code", "");
            pb.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject response = null;
            if (!isCancelled()) {
                try {
                    response = webServiceConnection.getTokenExRequest(prf.getString("access_token", ""), prf.getString("refresh_token", ""));
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
                    if (response.getString("message").equals("Success")) {
                        String access_token = response.getJSONObject("result").getString("access_token");
                        String refresh_token = response.getJSONObject("result").getString("refresh_token");
                        String take_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        Log.d(TAG + "access_token", access_token);
                        Log.d(TAG + "refresh_token", refresh_token);
                        Log.d(TAG + "take_time", take_time);

                        prf.edit().putString("access_token", access_token)
                                .putString("refresh_token", refresh_token)
                                .putString("take_time", take_time)
                                .commit();

                        pb.dismiss();
                        loadPageView();

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


