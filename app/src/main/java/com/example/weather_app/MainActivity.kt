package com.example.weather_app

import android.Manifest
import android.content.pm.PackageManager
import com.example.weather_app.Home.view.HomeFragment
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.weather_app.FavoriteWeather.view.FavoritesFragment
import com.example.weather_app.alert.view.AlertsFragment
import com.example.weather_app.utils.SettingsManager
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)
        val fragmentName = intent.getStringExtra("fragment")
        val fragment = Class.forName(fragmentName).newInstance() as Fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        val navView = findViewById<NavigationView>(R.id.navView)
        navView.setItemIconTintList(null)

        toggle = ActionBarDrawerToggle(
            this,
            findViewById(R.id.drawerLayout),
            R.string.open_nav,
            R.string.close_nav
        )
        findViewById<DrawerLayout>(R.id.drawerLayout).addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment()).commit()
                    findViewById<DrawerLayout>(R.id.drawerLayout).closeDrawer(GravityCompat.START)
                    true
                }

                R.id.nav_Favourites -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, FavoritesFragment()).commit()
                    findViewById<DrawerLayout>(R.id.drawerLayout).closeDrawer(GravityCompat.START)
                    true
                }

                R.id.nav_Alerts -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, AlertsFragment()).commit()
                    findViewById<DrawerLayout>(R.id.drawerLayout).closeDrawer(GravityCompat.START)
                    true
                }

                R.id.nav_Settings -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, SettingsFragment()).commit()
                    findViewById<DrawerLayout>(R.id.drawerLayout).closeDrawer(GravityCompat.START)
                    true
                }

                else -> false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
