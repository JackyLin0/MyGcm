/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tw.com.omnihealthgroup.healthcare.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import tw.com.omnihealthgroup.healthcare.R;

public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";
    private String title = null, body = null, kindCode = null, msgPk = null;

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.i(TAG, "From: " + from);
        Log.d(TAG, "data: " + data);

        Bundle notification = data.getBundle("notification");
        Log.d(TAG, "notification: " + notification);

        if (notification != null) {
            title = notification.getString("title");
            body = notification.getString("body");
            kindCode = notification.getString("kindcode");
            msgPk = notification.getString("msgpk");
        } else {
            title = data.getString("title");
            body = data.getString("body");
            kindCode = data.getString("kindcode");
            msgPk = data.getString("msgpk");
        }

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(title, body, kindCode, msgPk);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param title GCM message received.
     */
    private void sendNotification(String title, String body, String kindCode, String msgPk) {
        Context thisContext = getApplicationContext();
        Intent intent = new Intent(this, tw.com.omnihealthgroup.healthcare.MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("kindcode", kindCode);
        intent.putExtra("msgpk", msgPk);
        // Flags:
        // ONE_SHOT：PendingIntent只使用一次
        // CANCEL_CURRENT：PendingIntent執行前會先結束掉之前的
        // NO_CREATE：沿用先前的PendingIntent，不建立新的PendingIntent
        // UPDATE_CURRENT：更新先前PendingIntent所帶的額外資料，並繼續沿用
        int flags = PendingIntent.FLAG_CANCEL_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getActivity(
                thisContext,
                0,
                intent,
                flags
        );

        //Build Notification
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //設定推播通知可以下拉展開及樣式
        Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
        bigTextStyle.setBigContentTitle(title);
        bigTextStyle.bigText(body);

        Notification.Builder notificationBuilder =
                new Notification
                        .Builder(thisContext)
                        .setSmallIcon(R.mipmap.ic_launcher2)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setStyle(bigTextStyle)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);
        Log.i(TAG, "Title: " + title);
        Log.i(TAG, "text: " + body);
        Log.i(TAG, "KindCode: " + kindCode);
        Log.i(TAG, "MsgPk: " + msgPk);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = notificationBuilder.build();
        notificationManager.notify(0, notification);
    }
}
