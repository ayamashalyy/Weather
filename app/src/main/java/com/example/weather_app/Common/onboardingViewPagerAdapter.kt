package com.example.weather_app.Common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.example.weather_app.R

class OnboardingViewPagerAdapter(
    private val context: Context, private val mylistscreen: List<ScreenItemsOnboarding>
) : PagerAdapter() {

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layoutScreen = inflater.inflate(R.layout.layout_screen, null)
        val imgSlide = layoutScreen.findViewById<ImageView>(R.id.img)
        imgSlide.setImageResource(mylistscreen[position].screenImg)

        container.addView(layoutScreen)
        return layoutScreen
    }

    override fun getCount(): Int {
        return mylistscreen.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }
}