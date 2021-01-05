package com.zejian.myapplication.ui.loading

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.blankj.utilcode.util.ScreenUtils
import com.zejian.myapplication.R

class Loading( context: Context) : Dialog(context,R.style.LoadingDialog) {
    private val TAG = "Loading"
    private var tvContent: TextView? = null
    private var seconds = 12
    private var timer: CountDownTimer? = null


    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null)
        tvContent = view.findViewById(R.id.tvContent) as TextView
        setCanceledOnTouchOutside(false)
        setContentView(view)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设置自适应的方法：
        val dialogParams = window?.attributes;
        dialogParams?.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogParams?.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //设置底部显示
        dialogParams?.gravity = Gravity.BOTTOM;
        window?.attributes = dialogParams;
    }


    override fun show() {
        super.show()
        if (timer != null) {
            timer?.cancel()
        }
        timer = object : CountDownTimer((seconds * 1000).toLong(), 5000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                timer = null
                dismiss()
            }
        }
        timer?.start()
    }

    override fun onDetachedFromWindow() {
        timer?.cancel()
        timer = null
        super.onDetachedFromWindow()
    }

    override fun dismiss() {
        timer?.cancel()
        super.dismiss()
    }

    fun setMessage(message: String?):Loading {
        tvContent?.let {
            if (TextUtils.isEmpty(message)) {
                it.visibility = View.GONE
            } else {
                it.text = message
            }
        }
        return this
    }

    fun setSeconds(seconds: Int) {
        this.seconds = seconds
        if (seconds <= 0 && timer != null) {
            timer!!.cancel()
        }
    }



}