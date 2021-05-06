package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.databinding.DataBindingUtil

import com.udacity.databinding.ActivityMainBinding
import com.udacity.util.Constants.GLIDE_URL
import com.udacity.util.Constants.LOADAPP_URL
import com.udacity.util.Constants.RETROFIT_URL


class MainActivity : AppCompatActivity() {

    private var downloadID = 0L
    private var selectedRepo = ""

    private lateinit var binding: ActivityMainBinding
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var loadingButton: LoadingButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)

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
        }
    }

    private fun download() {
        if (selectedRepo.isNotEmpty()) {
            loadingButton.setLoadingState(ButtonState.Loading)
            val request =
                DownloadManager.Request(Uri.parse(selectedRepo))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
                    // Required for api 29 and up
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        "/${getString(R.string.app_name)}"
                    )

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.

        } else {
            showToast(getString(R.string.noRepoSelectedText))
        }
    }

    fun onRadioButtonClicked(view: View) {
        when (view.id) {
            R.id.rb_glide ->
                selectedRepo = GLIDE_URL
            R.id.rb_loadApp ->
                selectedRepo = LOADAPP_URL
            R.id.rb_retrofit ->
                selectedRepo = RETROFIT_URL
        }
    }

    private fun showToast(text: String) {
        val toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        toast.show()
    }
}
