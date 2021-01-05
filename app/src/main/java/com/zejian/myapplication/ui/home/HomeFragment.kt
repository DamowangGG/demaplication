package com.zejian.myapplication.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import cn.mtjsoft.www.gridviewpager_recycleview.GridViewPager
import cn.mtjsoft.www.gridviewpager_recycleview.GridViewPager.ImageTextLoaderInterface
import cn.mtjsoft.www.gridviewpager_recycleview.transformer.TopOrDownPageTransformer
import com.zejian.myapplication.R
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
    private val titles = arrayOf(
        "美食", "电影", "酒店住宿", "休闲娱乐", "外卖", "自助餐", "KTV", "机票/火车票", "周边游", "美甲美睫",
        "火锅", "生日蛋糕", "甜品饮品", "水上乐园", "汽车服务", "美发", "丽人", "景点", "足疗按摩", "运动健身", "健身", "超市", "买菜",
        "今日新单", "小吃快餐", "面膜", "洗浴/汗蒸", "母婴亲子", "生活服务", "婚纱摄影", "学习培训", "家装", "结婚", "全部分配"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gridviewpager
            .setDataAllCount(titles.size) // 设置内置的覆盖翻页效果
            .setCoverPageTransformer() // 设置内置的上下进入效果
//                .setTopOrDownPageTransformer(TopOrDownPageTransformer.ModeType.MODE_DOWN)
                // 设置内置的画廊效果
                 .setGalleryPageTransformer()
                // 数据绑定
                 .setImageTextLoaderInterface(ImageTextLoaderInterface { imageView, textView, position ->
                // 自己进行数据的绑定，灵活度更高，不受任何限制
                imageView.setImageResource(R.drawable.a)
                textView.setText(titles.get(position).split("_").toTypedArray().get(0))
            }) // Item点击
            .setGridItemClickListener(GridViewPager.GridItemClickListener { position ->

            }) // 设置Item长按
            .setGridItemLongClickListener(GridViewPager.GridItemLongClickListener { position ->

            })
            .show()

    }

}
