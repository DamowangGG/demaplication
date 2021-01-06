package com.zejian.myapplication.swipecard

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.PermissionUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zejian.myapplication.R
import kotlinx.android.synthetic.main.activity_tan_tan.*

class TanTanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tan_tan)

        recyclerView.layoutManager = OverLayCardLayoutManager()
        val adapter = Adapter()
        recyclerView.adapter = adapter

        CardConfig.initConfig(this)

        val callback = TanTanCallback(recyclerView, recyclerView.adapter,adapter.data)
        //测试竖直滑动是否已经不会被移除屏幕
        //callback.setHorizontalDeviation(Integer.MAX_VALUE);
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }


    class Adapter:BaseQuickAdapter<SwipeCardBean,BaseViewHolder>(R.layout.item_swipe_card,SwipeCardBean.initDatas()){
        @SuppressLint("CheckResult")
        override fun convert(holder: BaseViewHolder, item: SwipeCardBean) {
            holder.setText(R.id.tvName, item.name)
            holder.setText(R.id.tvPrecent, item.postition.toString() + " /" + data.size)
            val imageView = holder.getView<ImageView>(R.id.iv)
            Glide.with(imageView).load(item.url)
        }
    }


    override fun onResume() {
        super.onResume()
        Handler().postDelayed({
            PermissionUtils.permission(Manifest.permission.ACCESS_FINE_LOCATION).rationale { activity, shouldRequest ->

            }.callback { isAllGranted, granted, deniedForever, denied ->

            }.request()
        },3000)
    }

}
