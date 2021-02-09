package com.zejian.myapplication.ui

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.SeekBar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.larswerkman.lobsterpicker.OnColorListener
import com.larswerkman.lobsterpicker.sliders.LobsterShadeSlider
import com.zejian.myapplication.R
import com.zejian.myapplication.base.BaseActivity
import com.zejian.myapplication.base.dp2px
import com.zejian.myapplication.view.multiimage.MultiImageView
import kotlinx.android.synthetic.main.activity_circularmage.*
import xyz.schwaab.avvylib.BadgePosition


class CircularActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_circularmage)

        seekBarBorderWidth.onProgressChanged { circularImageView.borderWidth = it.toDp() }
        seekBarShadowRadius.onProgressChanged { circularImageView.shadowRadius = it.toDp() }

        shadeSlider.onColorChange{
            circularImageView.borderColor = it
            circularImageView.shadowColor = it
        }


        //multiImage
        multiImage.shape = MultiImageView.Shape.CIRCLE
        multiImage.rectCorners = dp2px(10f).toInt()
        multiImage.addImage(BitmapFactory.decodeResource(resources, R.mipmap.pic3))
        multiImage.addImage(BitmapFactory.decodeResource(resources, R.mipmap.pic4))
        multiImage.addImage(BitmapFactory.decodeResource(resources, R.mipmap.pic5))
        multiImage.addImage(BitmapFactory.decodeResource(resources, R.mipmap.pic6))

        //avatarView
        avatarView.apply {
            isAnimating = true
            borderThickness = 18
            highlightBorderColor = Color.GREEN
            highlightBorderColorEnd = Color.CYAN
            numberOfArches = 0
            totalArchesDegreeArea = 80f
            text = "Avatar View"
            showBadge = true
            badgePosition = BadgePosition.TOP_LEFT
        }


        Glide.with(image).load("https://y-m.oss-cn-shanghai.aliyuncs.com/user/123009/16126860683952").listener(object : RequestListener<Drawable?> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any,
                target: Target<Drawable?>,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any,
                target: Target<Drawable?>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {

                return false
            }
        }).into(image)


    }

    //region Extensions
    private fun Int.toDp(): Float =
        this * resources.displayMetrics.density

    private fun SeekBar.onProgressChanged(onProgressChanged: (Int) -> Unit) {
        setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                onProgressChanged(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Nothing
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Nothing
            }
        })
    }

    fun LobsterShadeSlider.onColorChange(onColorChanged: (Int) -> Unit) {
        addOnColorListener(object : OnColorListener {
            override fun onColorChanged(color: Int) {
                onColorChanged(color)
            }

            override fun onColorSelected(color: Int) {
                // Nothing
            }
        })
    }
    //endregion

}
