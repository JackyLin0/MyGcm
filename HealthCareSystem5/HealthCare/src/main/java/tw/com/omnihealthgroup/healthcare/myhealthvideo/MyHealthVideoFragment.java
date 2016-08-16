package tw.com.omnihealthgroup.healthcare.myhealthvideo;

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
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import tw.com.omnihealthgroup.drawerframework.DrawerFrameworkMainFragment;
import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.util.ShowMEProgressDiaLog;
import tw.com.omnihealthgroup.healthcare.WebServiceConnection;
import tw.com.omnihealthgroup.healthcare.myhealthvideo.adapter.VideoAdapter;

/**
 * Created by Administrator on 2016/5/23.
 */
public class MyHealthVideoFragment extends DrawerFrameworkMainFragment {
    private static final String TAG = "MyHealthVideoFragment";
    private View rootView;
    private WebView myBrowser = null;
    private String authCode = null;
    private RecyclerView rv;

    private SharedPreferences prf;
    private WebServiceConnection webServiceConnection;

    @Override
    protected void onSetToolbar(Toolbar toolbar) {
        super.onSetToolbar(toolbar);
        toolbar.setTitle(getString(R.string.fragment_myhealthvideo));
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_myhealthvideo, container, false);
        Log.v(TAG, "onCreateView");

        initView();
        //        checkView();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //        Toast.makeText(getActivity(), "onResume", Toast.LENGTH_SHORT).show();
        Log.v(TAG, "onResume");

        checkView();

        //        if (flag == true) {
        //            Log.v(TAG, "onResume");
        //            return;
        //        } else {
        //            Log.v(TAG, "onResume_Load");
        //            checkView();
        //            flag = true;
        //        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v(TAG, "onDestroyView");

        new TokenExp().cancel(true);
        new TokenGet().cancel(true);
    }


    private void initView() {
        rv = (RecyclerView) rootView.findViewById(R.id.recycle_view_video);

        rootView.findViewById(R.id.event_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new VideoListFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_myhealthvideo, fragment)
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

        //创建默认的线性LayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(linearLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        rv.setHasFixedSize(true);

        //        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        //        rv.addItemDecoration(itemDecoration);

        //        rv.setItemAnimator(new SlideInLeftAnimator());

        final ArrayList<String> mDataset = new ArrayList<>();
        mDataset.add("http://th-pp006.24drs.com/test_flt/t1.mp4");
        mDataset.add("http://th-pp006.24drs.com/test_flt/t2_live.mp4");
        mDataset.add("http://th-pp006.24drs.com/test_flt/t1.mp4");
        mDataset.add("http://th-pp006.24drs.com/test_flt/t2_live.mp4");
        mDataset.add("http://th-pp006.24drs.com/test_flt/t1.mp4");
        mDataset.add("http://th-pp006.24drs.com/test_flt/t2_live.mp4");
        mDataset.add("http://th-pp006.24drs.com/test_flt/t1.mp4");
        mDataset.add("http://th-pp006.24drs.com/test_flt/t2_live.mp4");
        mDataset.add("http://th-pp006.24drs.com/test_flt/t1.mp4");
        mDataset.add("http://th-pp006.24drs.com/test_flt/t2_live.mp4");
        mDataset.add("http://th-pp006.24drs.com/test_flt/t1.mp4");
        mDataset.add("http://th-pp006.24drs.com/test_flt/t2_live.mp4");
        Log.v("mDataset", mDataset.toString());

        ArrayList<String> mPictureset = new ArrayList<>();
        mPictureset.add("http://i.imgur.com/SDFoB2v.png");
        mPictureset.add("http://i.imgur.com/r9C5344.png");
        mPictureset.add("http://i.imgur.com/SDFoB2v.png");
        mPictureset.add("http://i.imgur.com/r9C5344.png");
        mPictureset.add("http://i.imgur.com/SDFoB2v.png");
        mPictureset.add("http://i.imgur.com/r9C5344.png");
        mPictureset.add("http://i.imgur.com/SDFoB2v.png");
        mPictureset.add("http://i.imgur.com/r9C5344.png");
        mPictureset.add("http://i.imgur.com/SDFoB2v.png");
        mPictureset.add("http://i.imgur.com/r9C5344.png");
        mPictureset.add("http://i.imgur.com/SDFoB2v.png");
        mPictureset.add("http://i.imgur.com/r9C5344.png");

        VideoAdapter videoAdapter = new VideoAdapter(getContext(), mDataset, mPictureset);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(videoAdapter);

        pb.dismiss();
        rv.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));
        // ScaleInAnimationAdapter ,SlideInBottomAnimationAdapter ,SlideInRightAnimationAdapter ,SlideInLeftAnimationAdapter

        rv.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.d(TAG, "onItemClick : postion " + position);
                        Intent intent = new Intent();
                        intent.putExtra("data", mDataset.get(position));
                        intent.setClass(rootView.getContext(), VideoEventActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongClick(View view, int posotion) {
                        Log.d(TAG, "onLongClick position : " + posotion);
                    }
                }));
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
