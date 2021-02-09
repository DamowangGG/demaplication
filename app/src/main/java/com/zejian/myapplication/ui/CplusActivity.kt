package com.zejian.myapplication.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zejian.myapplication.ConstManager
import com.zejian.myapplication.R
import kotlinx.android.synthetic.main.activity_cplus.*

class CplusActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cplus)

        button.text = ConstManager.getDeviceId()

    }


    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }


}