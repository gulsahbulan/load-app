package com.udacity.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import com.udacity.DetailActivity
import com.udacity.R
import com.udacity.util.Constants.CHANNEL_ID
import com.udacity.util.Constants.EXTRA_DOWNLOAD_STATUS
import com.udacity.util.Constants.EXTRA_FILE_NAME
import com.udacity.util.Constants.NOTIFICATION_ID


enum class DownloadStatus(val status: String) {
    SUCCESS("Success"),
    FAILURE("Fail")
}

fun NotificationManager.sendNotification(
    applicationContext: Context,
    fileName: String,
    downloadStatus: String
) {

    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra(EXTRA_FILE_NAME, fileName)
    contentIntent.putExtra(EXTRA_DOWNLOAD_STATUS, downloadStatus)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val bitmap = AppCompatResources
        .getDrawable(
            applicationContext,
            R.drawable.ic_assistant_black_24dp
        )
        ?.toBitmap()

    val notificationTitle: Spannable = SpannableString(applicationContext.getString(R.string.notification_title))
    notificationTitle.setSpan(StyleSpan(Typeface.BOLD), 0, notificationTitle.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

    val builder = NotificationCompat.Builder(
        applicationContext,
        CHANNEL_ID
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(notificationTitle)
        .setContentText(applicationContext.getString(R.string.notification_description))
        .setLargeIcon(bitmap)
        .addAction(
            0,
            applicationContext.getString(R.string.notification_button),
            contentPendingIntent
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}