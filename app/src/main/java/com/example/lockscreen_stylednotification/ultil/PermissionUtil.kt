package com.example.lockscreen_stylednotification.ultil

import android.content.Context
import androidx.core.app.NotificationManagerCompat

object PermissionUtil {
    /**
     * Checks if the app has permission to post notifications.
     *
     * @param context The application or activity context.
     * @return True if notifications are enabled, false otherwise.
     */
    fun isNotiEnabled(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
}