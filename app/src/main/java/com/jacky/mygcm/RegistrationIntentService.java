package com.jacky.mygcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;


/**
 * Created by lhm05 on 2016/08/01.
 */
public class RegistrationIntentService extends IntentService{
    private static final String[] TOPICS = {"global"}; ;
    private static String TAG="TAG";



    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);

        try
        {
            // 1.get Instance Id
            // 2.get Token
            // 3.sendRegistrationToServer(token);
            // 4.Subscribe to topic channels
            // 5.update SharePreference
            // 6 Send Broadcast to mRegistrationBroadcastReceiver

            InstanceID instanceID= InstanceID.getInstance(this);
            String token=instanceID.getToken(getString(R.string.gcm_defaultSenderId)
                    , GoogleCloudMessaging.INSTANCE_ID_SCOPE,null);

            _(token);
            //sendRegistrationToServer(token);
            // Subscribe to topic channels
            subscribeTopics(token);

            sp.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();


        }catch (Exception e) {
            sp.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();

        }
        finally {

        }
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);

    }


    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }


    }


    public void _(String mesg)
    {
        Log.i(TAG,mesg);
    }
}
