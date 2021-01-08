package com.zejian.myapplication.ui.loading

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.*
import android.widget.TextView
import com.zejian.myapplication.R
import com.zejian.myapplication.ui.loading.animation.SpinKitView
import kotlinx.android.synthetic.main.dialog_loading.*

class Loading( context: Context) : Dialog(context,R.style.LoadingDialog) {
    private val TAG = "Loading"
    private var tvContent: TextView? = null
    private var loadView: SpinKitView? = null
    private var seconds = 12000L
    private var successView:RightDiaView? = null
    private var failView:WrongDiaView? = null


    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null)
        tvContent = view.findViewById(R.id.tvContent) as TextView
        loadView = view.findViewById(R.id.spin_kit)
        successView = view.findViewById(R.id.successView);
        val color = Color.parseColor("#555555")
        successView?.setDrawColor(color)
        failView = view.findViewById(R.id.wrongView);
        failView?.setDrawColor(color)
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



    private val mHandler:Handler = @SuppressLint("HandlerLeak") object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
                MSG_DISMISS->{
                    dismiss()
                }
                MSG_LOADING->{

                }
            }
        }
    }


    fun showLoading(loadingText:String = TEXT_LOADING,cancelable:Boolean = false,timeOut:Long = seconds){
        setCanceledOnTouchOutside(false)
        setCancelable(cancelable)
        wrongView?.visibility = View.GONE
        successView?.visibility = View.GONE
        loadView?.visibility = View.VISIBLE
        tvContent?.text = loadingText
        if(!isShowing){
            mHandler.removeCallbacksAndMessages(null)
            show()
            mHandler.sendEmptyMessageDelayed(MSG_DISMISS,timeOut)
        }
    }


    fun showSuccess(successText:String = "ok",animate:Boolean = true,dismissDelay:Long = 1500){
        mHandler.removeMessages(MSG_DISMISS)
        tvContent?.text = successText
        loadView?.visibility = View.GONE
        successView?.visibility = View.VISIBLE
        successView?.setSpeed(10)
        successView?.setDrawDynamic(animate)
        successView?.visibility = View.VISIBLE
        mHandler.sendEmptyMessageDelayed(MSG_DISMISS,dismissDelay)
    }


    fun showError(errorText:String = "error",animate:Boolean = true,dismissDelay:Long = 1500){
        mHandler.removeMessages(MSG_DISMISS)
        tvContent?.text = errorText
        loadView?.visibility = View.GONE
        successView?.visibility = View.GONE
        wrongView?.visibility = View.VISIBLE
        wrongView?.setSpeed(10)
        wrongView?.setDrawDynamic(animate)
        wrongView?.visibility = View.VISIBLE
        mHandler.sendEmptyMessageDelayed(MSG_DISMISS,dismissDelay)
    }


    override fun onDetachedFromWindow() {
        mHandler.removeCallbacksAndMessages(null)
        super.onDetachedFromWindow()
    }

    override fun dismiss() {
        mHandler.removeCallbacksAndMessages(null)
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


    companion object{
        const val MSG_LOADING = 1
        const val MSG_SUCCESS = 2
        const val MSG_FAIL = 3
        const val MSG_DISMISS = 10
        const val TEXT_LOADING = "加载中..."
    }


}