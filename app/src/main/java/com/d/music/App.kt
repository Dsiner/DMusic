package com.d.music

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.multidex.MultiDex
import com.d.lib.aster.Aster
import com.d.lib.common.component.network.NetworkCompat
import com.d.lib.common.component.quickclick.QuickClick
import com.d.lib.common.util.AppUtils
import com.d.lib.common.util.log.ULog
import com.d.lib.permissioncompat.support.PermissionSupport
import com.d.lib.permissioncompat.support.threadpool.ThreadPool
import com.d.lib.taskscheduler.TaskScheduler
import com.d.music.component.aster.AppAsterModule
import com.d.music.component.cache.Cache
import com.d.music.component.media.controler.MediaControl
import com.d.music.component.service.MusicService
import com.d.music.component.skin.SkinUtil
import com.d.music.data.Constants
import com.d.music.data.database.greendao.DBManager
import com.d.music.data.preferences.Preferences
import com.d.music.play.activity.PlayActivity
import com.d.music.setting.activity.ModeActivity
import com.d.music.transfer.manager.TransferManager
import com.d.music.widget.dialog.NewListDialog
import java.util.concurrent.Executors

/**
 * Application
 * Created by D on 2017/4/28.
 */
class App : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        // Debug switch
        ULog.setDebug(true, "ULog")
        // Asynchronous initialization
        initAsync()
        // Initialize the database
        DBManager.getInstance(context)
        // Anti-double-click interval setting
        QuickClick.setSpanTime(350)
        // Loading skin
        SkinUtil.initSkin(context)
        // Network monitoring
        NetworkCompat.init(context)
        // Network request
        Aster.init(context, AppAsterModule())
        // Runtime permission
        initPermission()
        // Cache
        initCache()
    }

    private fun initAsync() {
        TaskScheduler.executeTask {
            TransferManager.getInstance()
            if (Preferences.getInstance(context).isFirst) {
                NewListDialog.insertNewList(context,
                        context.resources.getString(R.string.module_common_default_list), false)
            }
        }
    }

    private fun initPermission() {
        PermissionSupport.setLevel(PermissionSupport.SUPPORT_LEVEL_M)
        PermissionSupport.setThreadPool(object : ThreadPool() {
            override fun executeMain(r: Runnable) {
                TaskScheduler.executeMain(r)
            }

            override fun executeTask(r: Runnable) {
                TaskScheduler.executeTask(r)
            }

            override fun executeNew(r: Runnable) {
                TaskScheduler.executeNew(r)
            }
        })
    }

    private fun initCache() {
        Cache.setThreadPool(object : com.d.music.component.cache.utils.threadpool.ThreadPool() {
            /**
             * Cache download queue limit
             */
            private val DOWNLOAD_LIMIT = 3
            private val downloadThreadPool = Executors.newFixedThreadPool(DOWNLOAD_LIMIT)
            override fun executeMain(r: Runnable) {
                TaskScheduler.executeMain(r)
            }

            override fun executeTask(r: Runnable) {
                TaskScheduler.executeTask(r)
            }

            override fun executeDownload(r: Runnable) {
                downloadThreadPool.execute(r)
            }

            override fun executeNew(r: Runnable) {
                TaskScheduler.executeNew(r)
            }
        })
    }

    companion object {
        const val TAG_EXIT = "tag_exit"

        /**
         * The context of the single, global Application object
         */
        private var INSTANCE: Application? = null

        /**
         * Return the context of the single, global Application object of the
         * current process.  This generally should only be used if you need a
         * Context whose lifecycle is separate from the current context, that is
         * tied to the lifetime of the process rather than the current component.
         */
        @JvmStatic
        val context: Context
            get() = INSTANCE!!

        /**
         * Exit the app
         */
        fun exit() {
            val context = context
            MusicService.timing(context, false, 0)
            Preferences.getInstance(context).putSleepType(0)
            // Save current playback position
            Preferences.getInstance(context).putLastPlayPosition(MediaControl.getInstance(context).position)
            // Stop music playback
            MediaControl.getInstance(context).onDestroy()
            // Stop the service
            context.stopService(Intent(context, MusicService::class.java))
            AppUtils.exit(context, 1)
        }

        @JvmStatic
        fun toFinish(intent: Intent?): Boolean {
            if (intent == null) {
                return false
            }
            val bundle = intent.extras
            return bundle != null && bundle.getBoolean(TAG_EXIT, false)
        }

        private fun exit(context: Context) {
            if (Constants.PlayerMode.sPlayerMode == Constants.PlayerMode.PLAYER_MODE_NORMAL) {
                exit(context, PlayActivity::class.java)
                exit(context, MainActivity::class.java)
            } else if (Constants.PlayerMode.sPlayerMode == Constants.PlayerMode.PLAYER_MODE_MINIMALIST) {
                exit(context, PlayActivity::class.java)
            } else {
                exit(context, ModeActivity::class.java)
            }
        }

        private fun exit(context: Context, cls: Class<*>) {
            val intent = Intent(context, cls)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val bundle = Bundle()
            bundle.putBoolean(TAG_EXIT, true)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }
}