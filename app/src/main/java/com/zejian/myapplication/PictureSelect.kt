package com.zejian.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PointF
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.coroutineScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnImageCompleteCallback
import com.luck.picture.lib.tools.MediaUtils
import com.luck.picture.lib.widget.longimage.ImageSource
import com.luck.picture.lib.widget.longimage.ImageViewState
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PictureSelect(val lifecycle: Lifecycle) :ImageEngine{

    init {
        lifecycle.addObserver(SelectorObserver())
    }

    var checkQrCode = false
    var checkSexy = false

    fun select(activity:Activity,
               maxCount:Int = 9,
               selectList:MutableList<LocalMedia>? = null,
               minimumCompressSize:Int = 60,
               cutOutQuality:Int = 90,
               takePhotoEnable:Boolean = true){
        selectStart(PictureSelector.create(activity),maxCount,selectList,minimumCompressSize,cutOutQuality,takePhotoEnable)
    }

    fun select(fragment: Fragment,
               maxCount:Int = 9,
               selectList:MutableList<LocalMedia>? = null,
               minimumCompressSize:Int = 60,
               cutOutQuality:Int = 90,
               takePhotoEnable:Boolean = true
    ){
        selectStart(PictureSelector.create(fragment),maxCount,selectList,minimumCompressSize,cutOutQuality,takePhotoEnable)
    }


    private fun selectStart(pictureSelector: PictureSelector,
                            maxCount:Int = 9,
                            selectList:MutableList<LocalMedia>? = null,
                            minimumCompressSize:Int = 60,
                            cutOutQuality:Int = 90,
                            takePhotoEnable:Boolean = true
                            ){
        pictureSelector
            .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
            .imageEngine(this)
            .isWeChatStyle(true)// 是否开启微信图片选择风格
            .maxSelectNum(maxCount)// 最大图片选择数量
            .minSelectNum(1)// 最小选择数量
            .imageSpanCount(3)// 每行显示个数
            .selectionMode(
                PictureConfig.MULTIPLE
            )// 多选 or 单选  PictureConfig.SINGLE
            .isPreviewImage(true)// 是否可预览图片
            .isPreviewVideo(false)// 是否可预览视频
            .isCamera(takePhotoEnable)// 是否显示拍照按钮
            .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
            .isCompress(true)// 是否压缩
            .synOrAsy(true)//同步true或异步false 压缩 默认同步
            .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示
            .isGif(false)// 是否显示gif图片
            .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
            .circleDimmedLayer(false)// 是否圆形裁剪
            .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
            .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
            .selectionData(selectList)// 是否传入已选图片
            .cutOutQuality(cutOutQuality)// 裁剪压缩质量 默认100
            .minimumCompressSize(minimumCompressSize)// 小于100kb的图片不压缩
            .rotateEnabled(true) // 裁剪是否可旋转图片
            .scaleEnabled(true)// 裁剪是否可放大缩小图片
            .withAspectRatio(4,5)
            .forResult(PictureConfig.CHOOSE_REQUEST)//结果回调onActivityResult code
    }



    private var mListener:SelectListener? = null
    interface SelectListener{
        fun onSelectResult(selectList:MutableList<LocalMedia>)
    }


    inner class SelectorObserver:LifecycleObserver{
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy(){
            mListener = null
        }
    }



    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) {
            return
        }
        if (resultCode != AppCompatActivity.RESULT_OK) {
            mListener?.onSelectResult(mutableListOf())
        }
        // 图片选择结果回调
        val list = PictureSelector.obtainMultipleResult(data)
        if(!checkQrCode && !checkSexy){
            mListener?.onSelectResult(list)
        }

        lifecycle.coroutineScope.launch(Dispatchers.IO){
            repeat(list.size) {

            }
            mListener?.onSelectResult(list)
        }
        // 例如 LocalMedia 里面返回三种path
        // 1.media.getPath(); 为原图path
        // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
        // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
        // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的

    }






    /**
     * 加载图片
     *
     * @param context
     * @param url
     * @param imageView
     */
    override fun loadImage(context: Context, url: String, imageView: ImageView) {
        // * other https://www.jianshu.com/p/28f5bcee409f
        val drawableCrossFadeFactory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
        Glide.with(context)
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade(drawableCrossFadeFactory))
            .into(imageView)
    }

    override fun loadImage(
        context: Context,
        url: String,
        imageView: ImageView,
        longImageView: SubsamplingScaleImageView?,
        callback: OnImageCompleteCallback?
    ) {
// * other https://www.jianshu.com/p/28f5bcee409f
        val drawableCrossFadeFactory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
        Glide.with(context)
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade(drawableCrossFadeFactory))
            .into(imageView)
    }

    /**
     * 加载网络图片适配长图方案
     * # 注意：此方法只有加载网络图片才会回调
     *
     * @param context
     * @param url
     * @param imageView
     * @param longImageView
     */
    override fun loadImage(context: Context, url: String, imageView: ImageView, longImageView: SubsamplingScaleImageView?) {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .into(object : ImageViewTarget<Bitmap?>(imageView) {
                override fun setResource(resource: Bitmap?) {
                    if (resource != null) {
                        val eqLongImage: Boolean = MediaUtils.isLongImg(resource.width, resource.height)
                        longImageView?.visibility = if (eqLongImage) View.VISIBLE else View.GONE
                        imageView.visibility = if (eqLongImage) View.GONE else View.VISIBLE
                        if (eqLongImage) { // 加载长图
                            longImageView?.isQuickScaleEnabled = true
                            longImageView?.isZoomEnabled = true
                            longImageView?.isPanEnabled = true
                            longImageView?.setDoubleTapZoomDuration(100)
                            longImageView?.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
                            longImageView?.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER)
                            longImageView?.setImage(
                                ImageSource.bitmap(resource),
                                ImageViewState(0F, PointF(0F, 0F), 0)
                            )
                        } else { // 普通图片
                            imageView.setImageBitmap(resource)
                        }
                    }
                }
            })
    }

    /**
     * 加载gif
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    override fun loadAsGifImage(context: Context, url: String, imageView: ImageView) {
        Glide.with(context)
            .asGif()
            .load(url)
            .into(imageView)
    }

    /**
     * 加载图片列表图片
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    override fun loadGridImage(context: Context, url: String, imageView: ImageView) {
        // * other https://www.jianshu.com/p/28f5bcee409f
        val drawableCrossFadeFactory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
        Glide.with(context)
            .load(url)
            .override(200, 200)
            .centerCrop()
            .apply(RequestOptions().placeholder(R.drawable.picture_image_placeholder))
            .transition(DrawableTransitionOptions.withCrossFade(drawableCrossFadeFactory))
            .into(imageView)
    }

    /**
     * 加载相册目录
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    override fun loadFolderImage(context: Context, url: String, imageView: ImageView) {
        val drawableCrossFadeFactory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
        Glide.with(context)
            .asBitmap()
            .load(url)
            .override(180, 180)
            .centerCrop()
            .sizeMultiplier(0.5f)
            .apply(RequestOptions().placeholder(R.drawable.picture_image_placeholder))
            .transition(BitmapTransitionOptions.withCrossFade(drawableCrossFadeFactory))
            .into(object : BitmapImageViewTarget(imageView) {
                override fun setResource(resource: Bitmap?) {
                    val circularBitmapDrawable: RoundedBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource)
                    circularBitmapDrawable.cornerRadius = 8F
                    imageView.setImageDrawable(circularBitmapDrawable)
                }
            })
    }

}