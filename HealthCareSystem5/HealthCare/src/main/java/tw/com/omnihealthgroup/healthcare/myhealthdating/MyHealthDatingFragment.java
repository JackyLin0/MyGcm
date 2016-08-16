package tw.com.omnihealthgroup.healthcare.myhealthdating;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
import android.widget.DatePicker;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import tw.com.omnihealthgroup.drawerframework.DrawerFrameworkMainFragment;
import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.WebServiceConnection;
import tw.com.omnihealthgroup.healthcare.myhealthcare.adapter.LazyViewPager;
import tw.com.omnihealthgroup.healthcare.myhealthcare.adapter.NoScrollViewPager;
import tw.com.omnihealthgroup.healthcare.myhealthcare.adapter.ViewPagerAdapter;
import tw.com.omnihealthgroup.healthcare.myhealthdating.dbo.FoodDefinitionDAO;
import tw.com.omnihealthgroup.healthcare.myhealthdating.object.FoodDefinition;
import tw.com.omnihealthgroup.healthcare.util.ShowMEProgressDiaLog;

/**
 * Created by Administrator on 2016/5/23.
 */
public class MyHealthDatingFragment extends DrawerFrameworkMainFragment {
    private static final String TAG = "MyHealthDatingFragment";
    private View rootView;
    private WebView myBrowser = null;
    private String authCode = null;

    private NoScrollViewPager mainViewPager;
    private static ViewPagerAdapter pagerAdapter;

    private RadioGroup tabButtonGroup;
    private HorizontalScrollView tabScrollBar;
    private ArrayList<String> titleChannel = new ArrayList<>();
    private ArrayList<Fragment> fragmentList = new ArrayList<>();
    private static String[] FRAGMENT_NAME_ARRAY = new String[]{
            BreakFastFragment.class.getName()
            , DessertFragment.class.getName()
            , LunchFragment.class.getName()
            , AfternoonTeaFragment.class.getName()
            , DinnerFragment.class.getName()
            , SupperFragment.class.getName()};

    private final String DATABASE_PATH = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/HealthCare";
    private final String NO_SDCARD_DATABASE_PATH = "/data/data/com.omnihealthgroup/databases/";
    private final String DATABASE_FILENAME = "FoodDefinition.txt";
    private String dating_tpye = "BreakFastFragment";
    private static TextView dateview = null;
    private LinearLayout layout_list, layout_chart;


    private static SharedPreferences prf;
    private WebServiceConnection webServiceConnection;

    /**
     * 載入共用ToolBar
     *
     * @param toolbar toolbar
     */
    @Override
    protected void onSetToolbar(Toolbar toolbar) {
        super.onSetToolbar(toolbar);
        toolbar.setTitle(getString(R.string.fragment_myhealthdating));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prf = getActivity().getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
        webServiceConnection = new WebServiceConnection();
        Log.v(TAG, "onCreate");

        if (prf.getString("day_dating", "").getBytes().length < 1) {
            initFileData();
        }

        String timeStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        prf.edit().putString("day_dating", timeStr).commit();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_myhealthdating, container, false);
        Log.v(TAG, "onCreateView");

        getTitleChannel();
        getFragmentsList();

        initTabButton();
        initView();

        //        checkView();

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
    }

    private void initView() {
        dateview = (TextView) rootView.findViewById(R.id.dateview);
        dateview.setText(prf.getString("day_dating", ""));

        //        rootView.findViewById(R.id.layout_list).setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                MyHealthDatingFragment myHealthDatingFragment = new MyHealthDatingFragment();
        //                getChildFragmentManager().beginTransaction()
        //                        .replace(R.id.fragment_datingchart, myHealthDatingFragment)
        //                        .addToBackStack(null)
        //                        .commit();
        //            }
        //        });

        rootView.findViewById(R.id.layout_chart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatingChartFragment datingChartFragment = new DatingChartFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_myhealthdating, datingChartFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        dateview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTruitonDatePickerDialog(v);
            }
        });

        rootView.findViewById(R.id.add_new_dating_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("DayEventYear", Integer.parseInt(prf.getString("day_dating", "").substring(0, 4)));
                bundle.putInt("DayEventMonth", Integer.parseInt(prf.getString("day_dating", "").substring(5, 7)));
                bundle.putInt("DayEventDate", Integer.parseInt(prf.getString("day_dating", "").substring(8, 10)));
                bundle.putString("dating_tpye", dating_tpye);

                DialogFragment dialogFragment = new DatingShowFragment();
                dialogFragment.setArguments(bundle);
                dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.PageTransparent);
                dialogFragment.show(getActivity().getSupportFragmentManager(), "DatingShowFragment");
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
                new android.app.AlertDialog.Builder(getContext())
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
            new android.app.AlertDialog.Builder(getContext())
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

                switch (position) {
                    case 0:
                        dating_tpye = "BreakFastFragment";
                        Log.v(TAG + "position", "BreakFastFragment");
                        break;
                    case 1:
                        dating_tpye = "DessertFragment";
                        Log.v(TAG + "position", "DessertFragment");
                        break;
                    case 2:
                        dating_tpye = "LunchFragment";
                        Log.v(TAG + "position", "LunchFragment");
                        break;
                    case 3:
                        dating_tpye = "AfternoonTeaFragment";
                        Log.v(TAG + "position", "AfternoonTeaFragment");
                        break;
                    case 4:
                        dating_tpye = "DinnerFragment";
                        Log.v(TAG + "position", "DinnerFragment");
                        break;
                    case 5:
                        dating_tpye = "SupperFragment";
                        Log.v(TAG + "position", "SupperFragment");
                        break;
                }

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
        titleChannel.add(getString(R.string.tabpage_11));
        titleChannel.add(getString(R.string.tabpage_12));
        titleChannel.add(getString(R.string.tabpage_13));
        titleChannel.add(getString(R.string.tabpage_14));
        titleChannel.add(getString(R.string.tabpage_15));
        titleChannel.add(getString(R.string.tabpage_16));

        return titleChannel;
    }

    public void showTruitonDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user(顯示在EditView中Month要加一)
            dateview.setText(year + "-" + (month + 1) + "-" + day);
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = formatter.parse(dateview.getText().toString());
                String timeStr = formatter.format(date);
                dateview.setText(timeStr);
                prf.edit().putString("day_dating", timeStr).commit();

                pagerAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initFileData() {
        File txtFile = null;
        String txtStr = null;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            Log.v(TAG, "Has SDCard");
            try {
                boolean b = false;
                //取得資料庫的完整路徑
                String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
                //Log.i(TAG, "databaseFilename : " + databaseFilename);
                //將資料庫文件從資源文件放到合適地方（資源文件也就是資料庫文件放在項目的res下的raw目錄中）
                //將資料庫文件複製到SD卡中
                File dir = new File(DATABASE_PATH);
                if (!dir.exists()) {
                    //Log.i(TAG,"MakeDir=" + dir.getAbsolutePath());
                    b = dir.mkdir();
                }
                //判斷是否存在該文件
                if (!(new File(databaseFilename)).exists()) {
                    //Log.i(TAG,"Database file=" + databaseFilename);
                    //若不存在則取得資料庫輸入串流對象
                    InputStream is = getContext().getResources().openRawResource(R.raw.fooddefinition);
                    //新建輸出串流
                    FileOutputStream fos = new FileOutputStream(databaseFilename);
                    //將資料輸出
                    byte[] buffer = new byte[8192];
                    int count = 0;
                    while ((count = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, count);
                    }
                    // 關閉資源
                    fos.close();
                    is.close();
                }

                txtFile = new File(dir, "FoodDefinition.txt");
                txtStr = webServiceConnection.readFromFile(txtFile);
                Log.v(TAG, txtStr);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            Log.v(TAG, "No SDCard");
            try {
                boolean b = false;
                //取得資料庫的完整路徑
                String databaseFilename = NO_SDCARD_DATABASE_PATH + DATABASE_FILENAME;
                //Log.i(TAG, "databaseFilename : " + databaseFilename);
                //將資料庫文件從資源文件放到合適地方（資源文件也就是資料庫文件放在項目的res下的raw目錄中）
                //將資料庫文件複製到手機裡
                File dir = new File(NO_SDCARD_DATABASE_PATH);
                if (!dir.exists()) {
                    //Log.i(TAG,"MakeDir=" + dir.getAbsolutePath());
                    b = dir.mkdir();
                }
                //判斷是否存在該文件
                if (!(new File(databaseFilename)).exists()) {
                    //Log.i(TAG,"Database file=" + databaseFilename);
                    //若不存在則取得資料庫輸入串流對象
                    InputStream is = getContext().getResources().openRawResource(R.raw.fooddefinition);
                    //新建輸出串流
                    FileOutputStream fos = new FileOutputStream(databaseFilename);
                    //將資料輸出
                    byte[] buffer = new byte[8192];
                    int count;
                    while ((count = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, count);
                    }
                    //關閉資源
                    fos.close();
                    is.close();

                    txtFile = new File(dir, "FoodDefinition.txt");
                    txtStr = webServiceConnection.readFromFile(txtFile);
                    Log.v(TAG, txtStr);
                }
                //取得SQLDatabase對象
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        if (txtStr != null) {
            try {
                FoodDefinitionDAO foodDefinitionDAO = new FoodDefinitionDAO(getContext());

                JSONObject jsonObject = new JSONObject(txtStr);
                JSONArray jsonArray = jsonObject.getJSONArray("工作表1");
                for (int i = 0; i < jsonArray.length(); i++) {
                    String timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                    FoodDefinition foodDefinition = new FoodDefinition();
                    foodDefinition.setType(jsonArray.getJSONObject(i).getString("食品分類"));
                    foodDefinition.setName(jsonArray.getJSONObject(i).getString("樣品名稱"));
                    foodDefinition.setContent(jsonArray.getJSONObject(i).getString("內容物描述"));
                    foodDefinition.setUnit(jsonArray.getJSONObject(i).getString("單位"));
                    //                    foodDefinition.setAmount(jsonArray.getJSONObject(i).getString("食品分類"));
                    foodDefinition.setAmountunit(jsonArray.getJSONObject(i).getString("每食物單位(g/ml)"));
                    foodDefinition.setRefimgsn(jsonArray.getJSONObject(i).getString("每食物單位參考圖"));
                    foodDefinition.setMoisture(jsonArray.getJSONObject(i).getString("每單位熱量(kcal)"));
                    foodDefinition.setProtein(jsonArray.getJSONObject(i).getString("每單位蛋白質(g)"));
                    foodDefinition.setFat(jsonArray.getJSONObject(i).getString("每單位脂肪(g)"));
                    foodDefinition.setSugar(jsonArray.getJSONObject(i).getString("每單位碳水化合物(g)"));
                    foodDefinition.setNote(jsonArray.getJSONObject(i).getString("食物種類"));
                    foodDefinition.setStatus("1");
                    foodDefinition.setCrTime(timeStr);
                    //                    foodDefinition.setMdTime(jsonArray.getJSONObject(i).getString("食品分類"));

                    foodDefinitionDAO.insert(foodDefinition);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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


