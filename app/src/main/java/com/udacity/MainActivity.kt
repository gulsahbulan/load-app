package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.udacity.databinding.ActivityMainBinding
import com.udacity.util.Constants.CHANNEL_ID
import com.udacity.util.Constants.CHANNEL_NAME
import com.udacity.util.Constants.GLIDE_URL
import com.udacity.util.Constants.LOADAPP_URL
import com.udacity.util.Constants.RETROFIT_URL
import com.udacity.util.Constants.TOAST_POSITION_Y_DP
import com.udacity.util.DownloadStatus
import com.udacity.util.sendNotification


class MainActivity : AppCompatActivity() {

    private var downloadID = 0L
    private var selectedRepo = ""
    private var fileName = ""

    private lateinit var binding: ActivityMainBinding
    private lateinit var notificationManager: NotificationManager
    private lateinit var loadingButton: LoadingButton
    private lateinit var downloadManager: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)
        notificationManager = getSystemService(NotificationManager::class.java)
        createNotificationChannel()
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        loadingButton = binding.contentMain.loadingButton
        loadingButton.setOnClickListener {
            download()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            loadingButton.setLoadingState(ButtonState.Completed)
            if (id == downloadID) {
                val cursor = downloadManager.query(DownloadManager.Query().setFilterById(id))
                if (cursor.moveToFirst()) {
                    if (cursor.count > 0) {
                        val title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
                        val downloadStatus =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                        val status = if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {
                            DownloadStatus.SUCCESS
                        } else {
                            DownloadStatus.FAILURE
                        }
                        notificationManager.sendNotification(
                            applicationContext,
                            title,
                            status.status
                        )
                    }
                    cursor.close()
                }
            }
        }
    }

    private fun download() {
        if (selectedRepo.isNotEmpty()) {
            loadingButton.setLoadingState(ButtonState.Loading)
            val request =
                DownloadManager.Request(Uri.parse(selectedRepo))
                    .setTitle(fileName)
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)

            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        } else {
            showToast(getString(R.string.noRepoSelectedText))
        }
    }

    fun onRadioButtonClicked(view: View) {
        when (view.id) {
            R.id.rb_glide -> {
                selectedRepo = GLIDE_URL
                fileName = getString(R.string.glide)
            }
            R.id.rb_loadApp -> {
                selectedRepo = LOADAPP_URL
                fileName = getString(R.string.loadApp)
            }
            R.id.rb_retrofit -> {
                selectedRepo = RETROFIT_URL
                fileName = getString(R.string.retrofit)
            }
        }
    }

    private fun showToast(text: String) {
        val toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        toast.show()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description =
                applicationContext.getString(R.string.notification_description)
            notificationChannel.setShowBadge(false)

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}
