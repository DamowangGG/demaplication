package com.zejian.myapplication.widget

import android.content.Context
import com.opensource.svgaplayer.SVGACallback
import com.opensource.svgaplayer.SVGAImageView
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import java.net.URL
import java.util.concurrent.ConcurrentLinkedQueue

class SvgaPlayer  constructor(context: Context) {

    private var parser: SVGAParser = SVGAParser(context)
    private var svgaView:SVGAImageView? = null
    private var prepareOrPlaying = false
    private var playList: ConcurrentLinkedQueue<String> = ConcurrentLinkedQueue()

    fun setImageView(svgaView: SVGAImageView){
        this.svgaView = svgaView
        svgaView.callback = object :SVGACallback{
            override fun onFinished() {
                prepareOrPlaying = false
                svgaView.let {
                    val url = playList.poll();
                    url?.let {
                        play(url)
                    }
                }
            }

            override fun onPause() {

            }

            override fun onRepeat() {

            }

            override fun onStep(frame: Int, percentage: Double) {

            }

        }
    }


    fun play(url:String){
        svgaView?.let {
            if(prepareOrPlaying){
                playList.add(url)
            }else{
                prepareOrPlaying = true
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    parser.decodeFromURL(URL(url), parseCompletion)
                } else {
                    parser.decodeFromAssets(url, parseCompletion)
                }
            }
        }
    }


    private val parseCompletion = object : SVGAParser.ParseCompletion {
        override fun onComplete(videoItem: SVGAVideoEntity) {
            svgaView?.loops = 1
            svgaView?.setVideoItem(videoItem)
            svgaView?.startAnimation()
        }
        override fun onError() {
            prepareOrPlaying = false
            svgaView?.let {
                val url = playList.poll();
                url?.let {
                    play(url)
                }
            }
        }
    }


    fun stopAnimation(){
        svgaView?.let {
            playList.clear()
            it.stopAnimation(true)
        }
    }


    public fun cancel(){
        stopAnimation()
    }


    companion object{

    }

}
