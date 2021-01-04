package com.zejian.myapplication.taluo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zejian.myapplication.R
import com.zejian.myapplication.base.BaseActivity
import kotlinx.android.synthetic.main.activity_v_p2.*

class VP2Activity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_v_p2)

        val adapter = object : BaseQuickAdapter<String, BaseViewHolder>(R.layout.listitem_vp2) {
            override fun convert(holder: BaseViewHolder, item: String) {

            }

        }
        adapter.setOnItemClickListener { adapter, view, position ->

        }
        val list = mutableListOf<String>()
        for(i in 1..10){  list.add(i.toString()) }
        adapter.addData(list)
        vp.offscreenPageLimit = 4
        vp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        val pageTransformer = CompositePageTransformer();
        pageTransformer.addTransformer(MarginPageTransformer(ScreenUtils.dp2Px(this,30f).toInt()))
        pageTransformer.addTransformer(ScaleInTransformer());
        vp.setPageTransformer(pageTransformer)
        vp.adapter = adapter
    }
}
