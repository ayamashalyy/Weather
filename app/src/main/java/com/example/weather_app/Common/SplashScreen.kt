package com.example.weather_app.Common
import com.example.weather_app.Home.view.HomeFragment
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.weather_app.MainActivity
import com.example.weather_app.R

class SplashScreen : AppCompatActivity() {
    private lateinit var lottieAnimationView: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        lottieAnimationView = findViewById(R.id.lottie)
        lottieAnimationView.animate().translationX(2000f).setDuration(2000).setStartDelay(2900)
        Handler().postDelayed({
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra("fragment", HomeFragment::class.java.name)
            startActivity(intent)
            finish()
        }, 5000)
    }
}
