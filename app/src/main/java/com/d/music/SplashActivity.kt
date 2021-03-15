package com.d.music

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.Window
import com.d.lib.common.component.statusbarcompat.StatusBarCompat
import com.d.music.component.service.MusicService
import com.d.music.data.Constants
import com.d.music.data.preferences.Preferences
import com.d.music.play.activity.PlayActivity
import java.lang.ref.WeakReference

/**
 * SplashActivity
 * Created by D on 2017/4/28.
 */
class SplashActivity : Activity() {
    // Splash时间
    private val mDelayTime = 2500
    private var mIsBackPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 沉浸式状态栏
        StatusBarCompat.setStatusBarColor(this@SplashActivity, Color.parseColor("#ececec"))
        val preferences = Preferences.getInstance(applicationContext)
        val handler = WeakHandler(this)
        if (preferences.isFirst) {
            // 首次安装启动
            Constants.PlayerMode.sPlayerMode = Constants.PlayerMode.PLAYER_MODE_NORMAL
            startActivity(Intent(this@SplashActivity, WelcomeActivity::class.java))
            finish()
            return
        }
        if (!MusicService.isRunning()) {
            // 第一次启动
            Constants.PlayerMode.sPlayerMode = preferences.playerMode
        }
        when (Constants.PlayerMode.sPlayerMode) {
            Constants.PlayerMode.PLAYER_MODE_MINIMALIST -> {
                MusicService.startService(applicationContext)
                PlayActivity.openActivity(this@SplashActivity)
                finish()
            }
            Constants.PlayerMode.PLAYER_MODE_NOTIFICATION -> {
                MusicService.startService(applicationContext)
                finish()
            }
            else -> if (!MusicService.isRunning()) {
                // 第一次启动
                initView()
                handler.sendEmptyMessageDelayed(1, mDelayTime.toLong())
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.module_common_activity_splash)
        findViewById<View>(R.id.iv_splash).visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        mIsBackPressed = true
        super.onBackPressed()
    }

    internal class WeakHandler(activity: SplashActivity) : Handler() {
        var ref: WeakReference<SplashActivity>
        override fun handleMessage(msg: Message) {
            val theActivity = ref.get()
            if (theActivity != null && !theActivity.isFinishing && !theActivity.mIsBackPressed) {
                when (msg.what) {
                    1 -> {
                        MusicService.startService(theActivity.applicationContext)
                        theActivity.startActivity(Intent(theActivity, MainActivity::class.java))
                        theActivity.finish()
                    }
                }
            }
            super.handleMessage(msg)
        }

        init {
            ref = WeakReference(activity)
        }
    }
}