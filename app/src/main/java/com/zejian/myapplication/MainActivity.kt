package com.zejian.myapplication

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import com.zejian.myapplication.base.BaseActivity
import com.zejian.myapplication.emoji.EmojiActivity
import com.zejian.myapplication.taluo.VP2Activity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        NotificationChannelUtils.initChannel(this)
        NotificationUtil.getInstance().initUserConfig(this)

        val bitmap1 = BitmapFactory.decodeResource(resources,R.drawable.jack)
        val bitmap2 = BitmapFactory.decodeResource(resources,R.drawable.a)
        val bitmap3 = BitmapFactory.decodeResource(resources,R.drawable.h)

        button1.setOnClickListener {
            NotificationUtil.getInstance().send("1234","Andy",bitmap1)
        }

        button2.setOnClickListener {
            NotificationUtil.getInstance().send("1235","peter",bitmap2)
        }

        button3.setOnClickListener {
            NotificationUtil.getInstance().send("1236","jack",bitmap3)
            startActivity(Intent(this,Main2Activity::class.java))
        }
        button4.setOnClickListener {
            startActivity(Intent(this,EmojiActivity::class.java))
        }
        button5.setOnClickListener {
            startActivity(Intent(this,VP2Activity::class.java))
        }
    }
}
