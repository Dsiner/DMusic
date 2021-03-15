package com.d.music

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import com.d.lib.common.component.mvp.MvpBasePresenter
import com.d.lib.common.component.mvp.MvpView
import com.d.lib.common.component.mvp.app.BaseActivity
import com.d.lib.common.component.quickclick.QuickClick
import com.d.music.component.service.MusicService
import java.util.*

/**
 * WelcomeActivity
 * Created by D on 2017/6/16.
 */
class WelcomeActivity : BaseActivity<MvpBasePresenter<*>>(), MvpView, View.OnClickListener {
    var vp_page: ViewPager? = null
    var iv_dot0: ImageView? = null
    var iv_dot1: ImageView? = null
    var iv_dot2: ImageView? = null

    override fun onClick(v: View) {
        if (QuickClick.isQuickClick()) {
            return
        }
        when (v.id) {
            R.id.btn_start -> {
                // 启动音乐主界面
                gotoMain()
            }
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.module_common_activity_welcome
    }

    override fun getPresenter(): MvpBasePresenter<MvpView> {
        return MvpBasePresenter(application)
    }

    override fun getMvpView(): MvpView {
        return this
    }

    override fun bindView() {
        super.bindView()
        vp_page = findViewById(R.id.vp_page)
        iv_dot0 = findViewById(R.id.iv_dot0)
        iv_dot1 = findViewById(R.id.iv_dot1)
        iv_dot2 = findViewById(R.id.iv_dot2)
    }

    override fun init() {
        val inflater = LayoutInflater.from(this)
        val page0 = inflater.inflate(R.layout.module_common_welcome_page0, null)
        val page1 = inflater.inflate(R.layout.module_common_welcome_page1, null)
        val page2 = inflater.inflate(R.layout.module_common_welcome_page2, null)
        page2.findViewById<View>(R.id.btn_start).setOnClickListener(this)
        val pages: MutableList<View> = ArrayList()
        pages.add(page0)
        pages.add(page1)
        pages.add(page2)
        val pagerAdapter: PagerAdapter = object : PagerAdapter() {
            override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
                return arg0 === arg1
            }

            override fun getCount(): Int {
                return pages.size
            }

            override fun destroyItem(container: View, position: Int, `object`: Any) {
                (container as ViewPager).removeView(pages[position])
            }

            override fun instantiateItem(container: View, position: Int): Any {
                (container as ViewPager).addView(pages[position])
                return pages[position]
            }
        }
        vp_page!!.adapter = pagerAdapter
        vp_page!!.addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                invalidateDots(position)
            }
        })
    }

    /**
     * 刷新指示器
     */
    private fun invalidateDots(position: Int) {
        when (position) {
            0 -> {
                iv_dot1!!.setImageDrawable(resources.getDrawable(R.drawable.module_common_dot_wel))
                iv_dot0!!.setImageDrawable(resources.getDrawable(R.drawable.module_common_dot_wel_cover))
            }
            1 -> {
                iv_dot0!!.setImageDrawable(resources.getDrawable(R.drawable.module_common_dot_wel))
                iv_dot2!!.setImageDrawable(resources.getDrawable(R.drawable.module_common_dot_wel))
                iv_dot1!!.setImageDrawable(resources.getDrawable(R.drawable.module_common_dot_wel_cover))
            }
            2 -> {
                iv_dot1!!.setImageDrawable(resources.getDrawable(R.drawable.module_common_dot_wel))
                iv_dot2!!.setImageDrawable(resources.getDrawable(R.drawable.module_common_dot_wel_cover))
            }
        }
    }

    private fun gotoMain() {
        // 开启Service服务
        MusicService.startService(applicationContext)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}