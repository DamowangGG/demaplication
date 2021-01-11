package com.zejian.myapplication.widget

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.Uri
import android.net.http.HttpResponseCache
import android.view.*
import android.widget.FrameLayout
import com.blankj.utilcode.util.ActivityUtils
import com.opensource.svgaplayer.SVGACallback
import com.opensource.svgaplayer.SVGAImageView
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import com.zejian.myapplication.R
import java.io.File
import java.net.URL
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean

object SvgaUtils {

//    private var isAdded = false
//    private lateinit var appContext: Application
//    private val rootId = View.generateViewId()
//    private val root: View by lazy { inflate() }
//    private val svga: SVGAImageView by lazy { root.findViewById<SVGAImageView>(R.id.SVGAImageView) }
//    private var parser: SVGAParser? = null
//    private val queue: ConcurrentLinkedQueue<String> by lazy { ConcurrentLinkedQueue<String>() }
//    private val isPlaying: AtomicBoolean by lazy { AtomicBoolean() }
//
//    fun init(context: Application) {
//        appContext = context
//    }
//
//
//    public fun checkInit(){
//        if(parser == null) {
//            parser = SVGAParser(appContext)
//            val cacheDir = File(appContext.cacheDir, "http")
//            HttpResponseCache.install(cacheDir, 1024 * 1024 * 128)
//        }
//    }
//
//    fun playAnim(context: Context?, url: String?) {
//        checkInit()
//        val activity = ActivityUtils.getActivity(context)
//        if (activity != null) {
//            playAnim(activity.window, url)
//        }
//    }
//
//    fun playAnim(window: Window?, url: String?, isNestedCall: Boolean = false) {
//        checkInit()
//        if (window == null) {
//            return
//        }
//        if (url.isNullOrEmpty()) {
//            return
//        }
//        try {
//            val uri = Uri.parse(url)
//            val last = uri.lastPathSegment
//            if (last.isNullOrEmpty() || !last.endsWith(".svga")) {
//                return
//            }
//        } catch (e: Exception) {
//            return
//        }
//
//        addView(window)
//        svga.callback = object : PlaySvgaCallback() {
//            override fun onRepeat() {
//                // 播完一次 结束当前动画
//                svga.stopAnimation(true)
//            }
//
//            override fun onFinished() {
//                // 当前动画播放完毕，播下一个
//                val nextUrl = queue.poll()
//                if (!nextUrl.isNullOrEmpty()) {
//                    playAnim(window, nextUrl, true)
//                } else {
//                    // 队列结束 停止动画
//                    removeView(window)
//                    svga.callback = null
//                    root.setOnClickListener(null)
//                    root.setOnKeyListener(null)
//                    isPlaying.set(false)
//                }
//            }
//        }
//        if (isPlaying.get() && !isNestedCall) {
//            queue.offer(url)
//        } else {
//            isPlaying.set(true)
//            parser?.decodeFromURL(URL(url), object : SVGAParser.ParseCompletion {
//                override fun onComplete(videoItem: SVGAVideoEntity) {
//                    if(isAdded) {
//                        svga.setVideoItem(videoItem)
//                        svga.startAnimation()
//                    }
//                }
//
//                override fun onError() {
//                    isPlaying.set(false)
//                }
//            })
//        }
//    }
//
//    private fun addView(window: Window) {
//        val decorView = window.decorView as FrameLayout
//        val tempRoot = decorView.findViewById<View>(rootId)
//        if (tempRoot == null) {
//            val lp = FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.MATCH_PARENT,
//                FrameLayout.LayoutParams.MATCH_PARENT
//            )
//            decorView.addView(root, lp)
//            root.setOnClickListener {}
//            root.setOnKeyListener { v, keyCode, event ->
//                if (keyCode == KeyEvent.KEYCODE_BACK && isAdded) {
//                    svga.stopAnimation(true)
//                    removeView(window)
//                    true
//                } else {
//                    false
//                }
//            }
//            root.isFocusable = true
//            root.isFocusableInTouchMode = true
//            root.requestFocus()
//            isAdded = true
//        }
//    }
//
//    private fun removeView(window: Window) {
//        if (root.parent != null) {
//            val decorView = window.decorView as FrameLayout
//            decorView.removeView(root)
//            isAdded = false
//            isPlaying.set(false)
//        }
//    }
//
//
//    public fun release(){
//        queue.clear()
//        svga.stopAnimation(true)
//        root.parent?.let {
//            try {
//                (it as ViewGroup).removeView(root)
//            }catch (e:Exception){ e.printStackTrace() }
//
//        }
//    }
//
//
//    @SuppressLint("InflateParams")
//    private fun inflate(): View {
//        val view = LayoutInflater.from(appContext).inflate(R.layout.layout_svga_player, null)
//        view.id = rootId
//        return view
//    }
//
//    open class PlaySvgaCallback : SVGACallback {
//        override fun onFinished() {
//        }
//
//        override fun onPause() {
//        }
//
//        override fun onRepeat() {
//        }
//
//        override fun onStep(frame: Int, percentage: Double) {
//        }
//    }
}