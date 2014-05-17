package com.snid.safeway;

import java.util.Date;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService
{
    public static final int NOTIFICATION_ID = 1;
    public static int NOTIFICATION_COUNT;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

   
    public static final String TAG = GcmIntentService.class.getSimpleName();

    public GcmIntentService()
    {
        super("GcmIntentService");
        
    }

	@Override
	protected void onHandleIntent(Intent intent)
	{
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty())
        {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType))
            {
//                sendNotification("Send error: " + extras.toString());
                Log.i(TAG, "MESSAGE_TYPE_SEND_ERROR: " + extras.toString());
            }
            else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType))
            {
//                sendNotification("Deleted messages on server: " + extras.toString());
                Log.i(TAG, "MESSAGE_TYPE_DELETED: " + extras.toString());
            }
            else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
            {
                String message = extras.getString("message");
                
                Log.i(TAG, "Received: " + extras.toString());
                
                /*
                DBAdapter db = new DBAdapter(this);
                
                // add new message
                db.open();
                
                Date now = new Date();
                MessageItem new_msg = new MessageItem();
                new_msg.setPhoneNumber(MainApplication.getPhoneNumber());
                new_msg.setReceiveTime(now);
                new_msg.setMessage(message);
                
                db.insertMessage(new_msg);
                
                db.close();
				*/
                
                /*
                 * 메세지 창이 떠있으면 리스트에 메세지 추가, 아니면 노티바에 표시
                 */
                if (MessagesActivity.IsActive)
                {
                	//MessagesActivity.AddNewMessage(new_msg);
                	MessagesActivity.UpdateMessages();
                	
                	try
                	{
//                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//                        r.play();
                		MediaPlayer mp = MediaPlayer.create(this, R.raw.snid_noti_sound);
                		mp.start();
                    } catch (Exception e)
                    {
                    	
                    }
                }
                else
                {
                    sendNotification(message);
                }

                
            }
        }
        
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg)
    {
        mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

		// show notification item
		Intent notiIntent = new Intent(getApplicationContext(), MessagesActivity.class);
		notiIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notiIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
        mBuilder.setSmallIcon(R.drawable.push_icon);
        mBuilder.setContentTitle(getResources().getString(R.string.gcm_title));
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(msg));
        mBuilder.setContentText(msg);
        
        //mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        mBuilder.setSound(Uri.parse("android.resource://com.snid.safeway/raw/snid_noti_sound"));

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID + NOTIFICATION_COUNT++, mBuilder.build());
    }
}
