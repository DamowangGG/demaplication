package com.zejian.myapplication

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.recyclerview.widget.*
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType


/**
 * Description: RecyclerView扩展
 */

/**
 * 设置分割线
 * @param color 分割线的颜色，默认是#DEDEDE
 * @param size 分割线的大小，默认是1px
 * @param isReplace 是否覆盖之前的ItemDecoration，默认是true
 *
 */
fun RecyclerView.divider(color: Int = Color.parseColor("#DEDEDE"), size: Int = 1, isReplace: Boolean = true): RecyclerView {
    val decoration = DividerItemDecoration(context, orientation)
    decoration.setDrawable(GradientDrawable().apply {
        setColor(color)
        shape = GradientDrawable.RECTANGLE
        setSize(size, size)
    })
    if(isReplace && itemDecorationCount>0){
        removeItemDecorationAt(0)
    }
    addItemDecoration(decoration)
    return this
}


fun RecyclerView.vertical(spanCount: Int = 0, isStaggered: Boolean = false): RecyclerView {
    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    if (spanCount != 0) {
        layoutManager = GridLayoutManager(context, spanCount)
    }
    if (isStaggered) {
        layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
    }
    return this
}

fun RecyclerView.horizontal(spanCount: Int = 0, isStaggered: Boolean = false): RecyclerView {
    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    if (spanCount != 0) {
        layoutManager = GridLayoutManager(context, spanCount, GridLayoutManager.HORIZONTAL, false)
    }
    if (isStaggered) {
        layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.HORIZONTAL)
    }
    return this
}

fun RecyclerView.fixedSize(fix:Boolean = false):RecyclerView{
    setHasFixedSize(fix)
    return this
}

inline val RecyclerView.data
    get() = (adapter as BaseQuickAdapter<*,*>).data

inline val RecyclerView.orientation
    get() = if (layoutManager == null) -1 else layoutManager.run {
        when (this) {
            is LinearLayoutManager -> orientation
            is GridLayoutManager -> orientation
            is StaggeredGridLayoutManager -> orientation
            else -> -1
        }
    }


fun <T> RecyclerView.bindData(data: MutableList<T>, layoutId: Int,
                  bind: (holder: BaseViewHolder, t: T) -> Unit,
                  click: (adapter: BaseQuickAdapter<Any,BaseViewHolder>,view:View,position:Int) -> Unit): BaseQuickAdapter<T, BaseViewHolder> {
    val baseAdapter = object : BaseQuickAdapter<T,BaseViewHolder>(layoutId,data) {
        override fun convert(holder: BaseViewHolder, item: T) {
            bind(holder,item)
        }
    }
    baseAdapter.setOnItemChildClickListener { adapter, view, position ->
        click(adapter as BaseQuickAdapter<Any, BaseViewHolder>, view, position)
    }
    adapter = baseAdapter
    return baseAdapter
}

/**
 * 必须在bindData之后调用，并且需要hasHeaderOrFooter为true才起作用
 */
fun RecyclerView.addHeader(headerView: View): RecyclerView {
    adapter?.apply {
        (this as BaseQuickAdapter<*,*>).addHeaderView(headerView)
    }
    return this
}

/**
 * 必须在bindData之后调用，并且需要hasHeaderOrFooter为true才起作用
 */
fun RecyclerView.addFooter(footerView: View): RecyclerView {
    adapter?.apply {
        (this as BaseQuickAdapter<*,*>).addFooterView(footerView)
    }
    return this
}


fun RecyclerView.removeRefreshAnimation(): RecyclerView{
    itemAnimator = DefaultItemAnimator()
    itemAnimator?.changeDuration = 0
    return this
}


fun <T> RecyclerView.itemClick(listener: (adapter:BaseQuickAdapter<*, BaseViewHolder>, view:View, position:Int) -> Unit): RecyclerView {
    adapter?.apply {
        (adapter as BaseQuickAdapter<*,*>).setOnItemChildClickListener { adapter, view, position ->
            listener(adapter, view, position)
        }
    }
    return this
}







