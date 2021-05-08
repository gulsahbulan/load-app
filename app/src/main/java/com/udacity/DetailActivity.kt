package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding
import com.udacity.util.Constants.EXTRA_DOWNLOAD_STATUS
import com.udacity.util.Constants.EXTRA_FILE_NAME
import com.udacity.util.cancelNotifications

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)

        notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancelNotifications()

        val bundleExtra = intent?.extras

        bundleExtra?.let {
            val fileName = it.getString(EXTRA_FILE_NAME)
            val downloadStatus = it.getString(EXTRA_DOWNLOAD_STATUS)

            if (downloadStatus.equals(getString(R.string.success))) {
                binding.contentDetail.tvDownloadStatus.setTextColor(getColor(R.color.colorPrimaryDark))
            } else {
                binding.contentDetail.tvDownloadStatus.setTextColor(Color.RED)
            }

            binding.contentDetail.tvFileName.text = fileName
            binding.contentDetail.tvDownloadStatus.text = downloadStatus
        }

        binding.contentDetail.btnOk.setOnClickListener {
            finish()
        }
    }
}
