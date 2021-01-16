package com.zejian.myapplication.widget.pyq

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.lxj.xpopup.XPopup
import com.zejian.myapplication.R
import com.zejian.myapplication.widget.XPopImageLoader
import java.util.*

class MomentView(context: Context, attrs: AttributeSet?) : FrameLayout(context,attrs) {

    private var imageSizeSingle: Float = 0.48f
    //已经展示的图片集合
    private var currentImages: List<MomentImage>? = null
    //即将展示的原图集合
    private val originalImages = ArrayList<Any>()
    private var imageGridView: ImageGridView? = null
    private var singleImage: ImageView? = null


    fun setImages(imgList: List<MomentImage>?) {
        if (imgList.isNullOrEmpty()) {
            return
        }
        originalImages.clear()
        for (info in imgList) {
            info.url?.let { originalImages.add(it) }
        }
        if (imgList.size == 1) {
            showSingleImage(imgList)
        } else {
            showMultiImage(imgList)
        }
    }


    /**
     * 展示多张图片
     * */
    private fun showMultiImage(imgList: List<MomentImage>) {
        currentImages = imgList
        singleImage?.visibility = View.GONE
        if (imageGridView == null) {
            imageGridView =
                ImageGridView(context)
            //                nineGridView.setMatchParent(isMatchParent());
            imageGridView?.setSpace(SizeUtils.dp2px( 6.0f))
            imageGridView?.setAdapter(ImageGridView.NineImageAdapter(context, RequestOptions(), imgList))
            imageGridView?.setOnImageClickListener(object : ImageGridView.OnImageClickListener {
                override fun onImageClick(position: Int, view: View?) {
                    val imageView: ImageView = if (position < imageGridView!!.getMaxSize()) {
                        imageGridView?.getChildAt(position) as ImageView
                    } else { //如果图片的数量大于显示的数量，则超过部分的返回动画统一退回到最后一个图片的位置
                        imageGridView?.getChildAt(imageGridView!!.getMaxSize() - 1) as ImageView
                    }
                    XPopup.Builder(context).asImageViewer(
                        imageView, position, originalImages, { popupView, pos ->
                            popupView.updateSrcView(imageGridView!!.getChildAt(pos) as ImageView)
                        }, XPopImageLoader()
                    ).isShowSaveButton(false).show()
                }
            })
        } else {
            imageGridView?.setAdapter(ImageGridView.NineImageAdapter(context, RequestOptions(), imgList))
        }
        removeAllViews()
        addView(imageGridView)
        val total: Int = ScreenUtils.getAppScreenWidth() - SizeUtils.dp2px(28f)
        val size: Int
        val layoutParams = imageGridView?.layoutParams as LayoutParams
        if (imgList.size < 5) {
            size = total
            layoutParams.width = size
            layoutParams.height = size
        } else {
            layoutParams.width = total
            layoutParams.height = LayoutParams.WRAP_CONTENT
        }
        imageGridView?.layoutParams = layoutParams
        imageGridView?.visibility = View.VISIBLE
    }


    /**
     * 展示单张图片
     * */
    private fun showSingleImage(imgList: List<MomentImage>) {
        //检测是否同一张
        currentImages?.let {
            if (it.size == 1 && it[0].url == imgList[0].url) {
                return
            }
        }
        currentImages = imgList
        imageGridView?.visibility = View.GONE
        if (singleImage == null) {
            singleImage = ImageView(context)
            singleImage?.scaleType = ImageView.ScaleType.FIT_START
        }
        removeAllViews()
        singleImage?.let { singleImageView ->
            singleImageView.setImageBitmap(null)
            val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            addView(singleImageView, layoutParams)
            val imgUrl: String? = imgList[0].thumbnail
            Glide.with(context).asBitmap().load(imgUrl).placeholder(R.color.picture_color_eb)
                .transforms(CenterCrop(), RoundedCorners(MomentImage.RADIUS))
                .into(object : SimpleTarget<Bitmap?>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                        val width = resource.width
                        val height = resource.height
                        val lParams = singleImageView.layoutParams as LayoutParams
                        val max: Int = (imageSizeSingle * ScreenUtils.getAppScreenWidth()).toInt()
                        if (width >= height) {
                            lParams.height = max * height / width
                            lParams.width = max
                        } else {
                            lParams.height = max
                            lParams.width = max * width / height
                        }
                        singleImageView.layoutParams = layoutParams
                        singleImageView.visibility = View.VISIBLE
                        singleImageView.setImageBitmap(resource)
                    }
                })
            singleImageView.setOnClickListener {
                XPopup.Builder(context)
                    .asImageViewer(singleImageView, imgList[0], XPopImageLoader())
                    .isShowSaveButton(false)
                    .show()
            }
        }
    }



    fun setSingleSize(percent: Float) {
        imageSizeSingle = percent
    }

    fun getImgList(): List<MomentImage>? {
        return currentImages
    }
}