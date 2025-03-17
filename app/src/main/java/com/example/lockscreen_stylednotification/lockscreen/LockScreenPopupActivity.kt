package com.example.lockscreen_stylednotification.lockscreen

import android.app.KeyguardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.lockscreen_stylednotification.MainActivity
import com.example.lockscreen_stylednotification.databinding.ActivityLockScreenPopupBinding
import com.example.lockscreen_stylednotification.lockscreen.digital_clock.ClockViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class LockScreenPopupActivity : AppCompatActivity() {
    private val viewModel: ClockViewModel by viewModels()
    private lateinit var binding: ActivityLockScreenPopupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLockScreenPopupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableLockscreen()

        lifecycleScope.launch {
            viewModel.time.collectLatest {
                binding.digitalClock.tvTime.text = it
            }
        }

        lifecycleScope.launch {
            viewModel.date.collectLatest {
                binding.digitalClock.tvDate.text = it
            }
        }

//        binding.llRoot.setOnClickListener {
//            startMainActivity(this)
//        }

        binding.lockscreenView.ivClose.setOnClickListener {
            this.finish()
        }

        binding.lockscreenView.actionButton.setOnClickListener {
            // 🛠️ Mở khóa màn hình nếu có thể
            val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
            if (keyguardManager.isKeyguardLocked) {
                keyguardManager.requestDismissKeyguard(this, object : KeyguardManager.KeyguardDismissCallback() {
                    override fun onDismissSucceeded() {
                        super.onDismissSucceeded()
                        openMainActivity() // Sau khi mở khóa, vào MainActivity
                    }
                })
            }
        }
    }

    private fun enableLockscreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
        } else {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON)
        }
    }

    private fun openMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}