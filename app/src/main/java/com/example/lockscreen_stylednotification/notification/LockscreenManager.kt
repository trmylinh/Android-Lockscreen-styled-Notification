package com.example.lockscreen_stylednotification.notification

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.example.lockscreen_stylednotification.R
import java.util.Calendar

object LockscreenManager {

    var LOCKSCREEN_CHANNEL_ID = "lockscreen_id"
    /**
     * Note: Create the Notification-Channel, but only on API 26+ because the NotificationChannel
     * class is new and not in the support library
     */
    @SuppressLint("NewApi")
    fun createNotificationChannel(context: Context) {
        //1. define variable
        val name: CharSequence = context.getString(R.string.app_name)
        val description = context.getString(R.string.app_name)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(LOCKSCREEN_CHANNEL_ID, name, importance)
        channel.description = description
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC


        //2. Register the channel with the system; you can't change the importance or other notification behaviors after this
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    fun setupDailyLockscreenNotification(context: Context) {
        //1. alarm manager sends a lockscreen-styled notification at 4 AM everyday
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmTime = Calendar.getInstance()
        val now = Calendar.getInstance()
        alarmTime.timeInMillis = System.currentTimeMillis()
        alarmTime[Calendar.HOUR] = 11
        alarmTime[Calendar.MINUTE] = 43
        alarmTime[Calendar.SECOND] = 0
        if (now.after(alarmTime)) {
            alarmTime.add(Calendar.DATE, 1)
        }


        //2. set up notification at specific time
        val intent = Intent(context, LockscreenReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 100, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime.timeInMillis, pendingIntent)
    }



    fun cancelNotification(context: Context, notificationId: Int) {
        with(NotificationManagerCompat.from(context)) {
            cancel(notificationId)
        }
    }
}