package com.haidit.intersvyaztestovoe.utils

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.haidit.intersvyaztestovoe.R

const val notificationID = 0
const val reminderID = 1
const val channelID = "channel1"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"

class Notification : BroadcastReceiver() {

    override fun onReceive(p0: Context, p1: Intent) {

        val notification = NotificationCompat.Builder(p0, channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(p1.getStringExtra(titleExtra))
            .setContentText(p1.getStringExtra(messageExtra)).build()

        val manager = p0.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)
    }
}