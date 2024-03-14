package com.example.weather_app.Common
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.weather_app.MainActivity
import com.example.weather_app.R

class OnboardingScreen : AppCompatActivity() {
    private lateinit var screenPager: ViewPager
    private lateinit var adapter: OnboardingViewPagerAdapter
    private lateinit var btnNext: Button
    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        if (restorePrefData()) {
            val mainActivity = Intent(applicationContext, SplashScreen::class.java)
            startActivity(mainActivity)
            finish()
        }

        setContentView(R.layout.activity_onboarding_screen)
        supportActionBar?.hide()
        btnNext = findViewById(R.id.btn_next)

        val mList: MutableList<ScreenItemsOnboarding> = ArrayList()
        mList.add(ScreenItemsOnboarding(R.drawable.image5))
        mList.add(ScreenItemsOnboarding(R.drawable.imag))
        mList.add(ScreenItemsOnboarding(R.drawable.image4))
        screenPager = findViewById(R.id.screen_viewPager)
        adapter = OnboardingViewPagerAdapter(this, mList)
        screenPager.adapter = adapter
        btnNext.setOnClickListener {
            position = screenPager.currentItem
            if (position < mList.size) {
                position++
                screenPager.currentItem = position
            }
            if (position == mList.size - 1) {
                loadLastScreen()
            }
            val mainActivity = Intent(
                applicationContext,
                SplashScreen::class.java
            )
            startActivity(mainActivity)
            savePrefsData()
            finish()
        }
    }

    private fun restorePrefData(): Boolean {
        val pref = applicationContext.getSharedPreferences("myPrefs", MODE_PRIVATE)
        return pref.getBoolean("isOnboardingOpened", false)
    }

    private fun savePrefsData() {
        val pref = applicationContext.getSharedPreferences("myPrefs", MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean("isOnboardingOpened", true)
        editor.apply()
    }

    private fun loadLastScreen() {
        btnNext.visibility = View.INVISIBLE
    }
}
