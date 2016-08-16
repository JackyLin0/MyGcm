package tw.com.omnihealthgroup.healthcare.myhealthvideo;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import tw.com.omnihealthgroup.healthcare.R;
import tw.com.omnihealthgroup.healthcare.util.ShowMEProgressDiaLog;
import tw.com.omnihealthgroup.healthcare.WebServiceConnection;

public class VideoEventActivity extends AppCompatActivity {
    private static final String TAG = "VideoEventActivity";

    private WebServiceConnection webServiceConnection;
    private SharedPreferences prf;

    private VideoView myVideo = null;
    private int position = 0;
    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Log.v(TAG, "onCreate");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        prf = getApplicationContext().getSharedPreferences("AuthServer", Context.MODE_PRIVATE);
        webServiceConnection = new WebServiceConnection();

        initView();
        loadPageView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");

        //        loadPageView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");

        position = myVideo.getCurrentPosition();
        Log.v(TAG + "position", String.valueOf(position));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");

        myVideo.stopPlayback();
    }

    private void initView() {
        myVideo = (VideoView) findViewById(R.id.pageVideoView);
        findViewById(R.id.event_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // When you change direction of phone, this method will be called.
    // It store the state of video (Current position)
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.v(TAG, "onSaveInstanceState");

        // Store current position.
        savedInstanceState.putInt("CurrentPosition", myVideo.getCurrentPosition());
        myVideo.pause();
    }

    // After rotating the phone. This method is called.
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.v(TAG, "onRestoreInstanceState");
        final ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(this, getString(R.string.webview_loading_title), getString(R.string.msg_tokenget), false, true);
        pb.show();

        // Get saved position.
        position = savedInstanceState.getInt("CurrentPosition");
        Log.v(TAG + "position", String.valueOf(position));
        myVideo.seekTo(position);
        pb.dismiss();
        myVideo.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.v(TAG, "onBackPressed");

        //        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(VideoEventActivity.this);
        //        dialogBuilder
        //                .setTitle(getResources().getString(R.string.new_measure_cancel_title))
        //                .setMessage(getResources().getString(R.string.new_event_cancel_message))
        //                .setPositiveButton(
        //                        getResources().getString(R.string.msg_confirm),
        //                        new DialogInterface.OnClickListener() {
        //                            @Override
        //                            public void onClick(DialogInterface dialog, int which) {
        //                                //取消新增私人行程回到行事曆檢視畫面
        //                                finish();
        //                            }
        //                        })
        //                .setNeutralButton(
        //                        getResources().getString(R.string.msg_cancel),
        //                        new DialogInterface.OnClickListener() {
        //                            @Override
        //                            public void onClick(DialogInterface dialog, int which) {
        //                                //關閉提醒視窗回到新增私人行程
        //                            }
        //                        });
        //        AlertDialog alertDialog = dialogBuilder.show();
        //        TextView alertMessageText = (TextView) alertDialog.findViewById(android.R.id.message);
        //        alertMessageText.setGravity(Gravity.CENTER);
        //        alertDialog.show();

        myVideo.stopPlayback();

        finish();
    }

    /**
     * 初始化完成按鈕與取消按鈕的監聽器
     */
    private void loadPageView() {
        final ShowMEProgressDiaLog pb = new ShowMEProgressDiaLog(this, getString(R.string.webview_loading_title), getString(R.string.msg_tokenget), false, true);
        pb.show();

        String data = getIntent().getStringExtra("data");

        // Set the media controller buttons
        if (mediaController == null) {
            mediaController = new MediaController(this);

            // Set the videoView that acts as the anchor for the MediaController.
            mediaController.setAnchorView(myVideo);

            // Set MediaController for VideoView
            myVideo.setMediaController(mediaController);
        }

        try {
            // ID of video file.
            myVideo.setVideoURI(Uri.parse(data));
        } catch (Exception e) {
            e.printStackTrace();
        }

        myVideo.requestFocus();

        // When the video file ready for playback.
        myVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {
                if (position == 0) {
                    pb.dismiss();
                    myVideo.start();
                } else {
                    myVideo.seekTo(position);
                    pb.dismiss();
                    myVideo.start();
                }

                // When video Screen change size.
                mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

                        // Re-Set the videoView that acts as the anchor for the MediaController
                        mediaController.setAnchorView(myVideo);
                    }
                });
            }
        });
    }

}
