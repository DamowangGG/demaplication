package com.zejian.myapplication.base

import android.content.res.Configuration
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity: AppCompatActivity() {

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val currentNightMode =
            newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                Log.d("currentNightMode", "UI_MODE_NIGHT_NO$currentNightMode")
                // Night mode is not active, we're using the light theme
                recreate()
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                Log.d("currentNightMode", "UI_MODE_NIGHT_YES$currentNightMode")
                // Night mode is active, we're using dark theme
                recreate()
            }
        }
    }

}