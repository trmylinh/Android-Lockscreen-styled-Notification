package com.example.lockscreen_stylednotification.notification

import android.Manifest
import android.app.KeyguardManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.PowerManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.lockscreen_stylednotification.MainActivity
import com.example.lockscreen_stylednotification.R
import com.example.lockscreen_stylednotification.lockscreen.LockScreenPopupActivity
import com.example.lockscreen_stylednotification.notification.LockscreenManager.LOCKSCREEN_CHANNEL_ID


class LockscreenReceiver: BroadcastReceiver() {

    var DEFAULT_LOCKSCREEN_ID = 1000
    override fun onReceive(context: Context, intent: Intent) {
        /*0. Start repeating notification if the device was shut down and then reboot*/
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            LockscreenManager.setupDailyLockscreenNotification(context)
        }


       Log.e("Notification", "BroadcastReceiver: Send lockscreen-styled notification !")

        val powerManager = context.getSystemService(PowerManager::class.java)
        val keyguardManager = context.getSystemService(KeyguardManager::class.java)

        if (!powerManager.isInteractive || keyguardManager.isKeyguardLocked) {
            Log.e( "Notification", "BroadcastReceiver: popupLockscreenNotification !")

            popupLockscreenNotification(context)
        } else {
            Log.e( "Notification", "BroadcastReceiver: popupNormalNotification ! !")

            popupNormalNotification(context)
        }


        //4. Set again this alarm manager
        LockscreenManager.setupDailyLockscreenNotification(context)
    }

    /**
     * send a lockscreen-styled notification
     */
    private fun popupLockscreenNotification(context: Context) {
        //1. Create pending
        val lockscreenIntent = Intent(context, LockScreenPopupActivity::class.java)
        lockscreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val lockscreenPendingIntent = PendingIntent.getActivity(context, 0, lockscreenIntent, PendingIntent.FLAG_IMMUTABLE)


        //2. Setup notification builder
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, LOCKSCREEN_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setFullScreenIntent(lockscreenPendingIntent, true)


        //3. Show notification with notificationId which is a unique int for each notification that you must define
        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        notificationManager.cancel(DEFAULT_LOCKSCREEN_ID)
        notificationManager.notify(DEFAULT_LOCKSCREEN_ID, builder.build())
    }


    /**
     * send a normal notification instead of lockscreen-styled notification
     * */
    private fun popupNormalNotification(context: Context) {
        //2. Create an explicit intent for an Activity in your app
        val destinationIntent = Intent(context, MainActivity::class.java)
        destinationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(
            context,
            1896,
            destinationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )


        //3. define notification builder
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, LOCKSCREEN_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.app_name))
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(context.getString(R.string.app_name))
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)


        //4. Show notification with notificationId which is a unique int for each notification that you must define
        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        notificationManager.notify(DEFAULT_LOCKSCREEN_ID, builder.build())
    }
}