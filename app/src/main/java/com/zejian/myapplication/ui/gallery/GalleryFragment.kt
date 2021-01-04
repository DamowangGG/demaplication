package com.zejian.myapplication.ui.gallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zejian.myapplication.R
import com.zejian.myapplication.gridviewpager.GridItemClickListener
import com.zejian.myapplication.gridviewpager.GridItemLongClickListener
import com.zejian.myapplication.gridviewpager.Model
import kotlinx.android.synthetic.main.fragment_gallery.*





class GalleryFragment : Fragment() {

    private val titles = arrayOf(
        "美食", "电影", "酒店住宿", "休闲娱乐", "外卖", "自助餐", "KTV", "机票/火车票", "周边游", "美甲美睫",
        "火锅", "生日蛋糕", "甜品饮品", "水上乐园", "汽车服务", "美发", "丽人", "景点", "足疗按摩", "运动健身", "健身", "超市", "买菜",
        "今日新单", "小吃快餐", "面膜", "洗浴/汗蒸", "母婴亲子", "生活服务", "婚纱摄影", "学习培训", "家装", "结婚", "全部分配"
    )
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //使用builder模式设计初始化
        mGridViewPager
            .setPageSize(8)
            .setGridItemClickListener(GridItemClickListener { pos, position, str ->
                Log.d("123", "$pos/$str")
            })
            .setGridItemLongClickListener(GridItemLongClickListener { pos, position, str ->
                Log.d("456", "$pos/$str")
            }) //传入String的List 必须作为最后一步
            .init(initData())
    }


    override fun onDestroy() {
        super.onDestroy()
    }


    /**
     * 初始化数据源
     */
    private fun initData(): List<Model>? {
        val mData: MutableList<Model> = ArrayList()
        for (i in titles.indices) { //动态获取资源ID，第一个参数是资源名，第二个参数是资源类型例如drawable，string等，第三个参数包名
            val imageId = R.drawable.a
            mData.add(Model(titles[i], imageId))
        }
        return mData
    }

}
