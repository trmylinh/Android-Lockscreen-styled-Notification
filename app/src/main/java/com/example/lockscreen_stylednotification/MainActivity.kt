package com.example.lockscreen_stylednotification

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.lockscreen_stylednotification.databinding.ActivityMainBinding
import com.example.lockscreen_stylednotification.notification.LockscreenManager
import com.example.lockscreen_stylednotification.ultil.PermissionUtil

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initOnBackPressed()

        binding.btnShowNoti.setOnClickListener {
            setupNotification()
        }
    }

    private fun initOnBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun setupNotification() {
        //1. Request POST NOTIFICATION permission if device has Android OS from 13
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isAccessed: Boolean = PermissionUtil.isNotiEnabled(this)
            if (!isAccessed) {
                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }


        //2. Create lockscreen-styled notification and send it every day
        LockscreenManager.createNotificationChannel(this)
        LockscreenManager.setupDailyLockscreenNotification(this)
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private val permissionLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isAccessed ->
        if (isAccessed) {
            LockscreenManager.setupDailyLockscreenNotification(this)
        } else {
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                showRationaleDialog()
            } else {
                showSettingsDialog()
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private fun showRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Yêu cầu quyền thông báo")
            .setMessage("Ứng dụng cần quyền này để hiển thị thông báo trên màn hình khóa.")
            .setPositiveButton("OK") { _, _ ->
                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cấp quyền trong Cài đặt")
            .setMessage("Bạn đã từ chối quyền thông báo. Hãy vào Cài đặt để cấp lại quyền.")
            .setPositiveButton("Mở Cài đặt") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}