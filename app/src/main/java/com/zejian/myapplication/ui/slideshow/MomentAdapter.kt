package com.zejian.myapplication.ui.slideshow

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zejian.myapplication.R

class MomentAdapter(data: MutableList<MomentBean>?) :
    BaseQuickAdapter<MomentBean, BaseViewHolder>(R.layout.list_moment, data) {

    override fun convert(holder: BaseViewHolder, item: MomentBean) {

    }

}