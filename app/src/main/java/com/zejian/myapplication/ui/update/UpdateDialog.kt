package com.zejian.myapplication.ui.update

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ToastUtils
import com.zejian.myapplication.R
import kotlinx.android.synthetic.main.dialog_app_update.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class UpdateDialog : DialogFragment() {

    private var isForce = false
    var updateBean:UpdateBean? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_app_update, container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppUpdateTheme)
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            it.setCanceledOnTouchOutside(false)
            it.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    false
                } else false
            }
            dialog?.window?.let { wd->
                wd.setGravity(Gravity.CENTER)
                val lp = wd.attributes
                lp.width = ScreenUtils.getScreenWidth()
                wd.attributes = lp
            }
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (updateBean == null) {
            dismiss()
            return
        }
        updateBean?.let {
            if(it.isForce){
                isForce = true
                isCancelable = false
                btCancel.visibility = View.GONE
                dialog?.setCanceledOnTouchOutside(false)
            }
            btCancel.text = "稍后再说"
            tvVersion.text = "v${it.serverVersionName}"
            tvUpdateContent.text = Html.fromHtml(it.updateMsg)
            tvTitle.text = "v${it.serverVersionName}"
            val file = UpdateManager.getInstallApkPath(requireContext())
            if (file != null && file.exists()) {
                btOk.text = "开始安装"
            }
        }

        btOk.setOnClickListener {
            context?.let {
                val apkFile = UpdateManager.getInstallApkPath(it)
                if (apkFile != null && apkFile.exists()) {
                    val res = UpdateManager.installApp(this, apkFile)
                    if (res) {
                        if (!isForce) {
                            dismiss()
                        }
                    } else {
                        ToastUtils.showShort("安装失败")
                    }
                } else {
                    val intent = Intent(it, UpdateService::class.java)
                    it.startService(intent)
                    btOk.visibility = View.GONE
                    layoutProgress.visibility = View.VISIBLE
                }
            }

        }

        btCancel.setOnClickListener {
            dismiss()
        }
        EventBus.getDefault().register(this)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }


    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onProgressChange(downLoadProgress: DownLoadProgress) {
        if (downLoadProgress.state == DownLoadProgress.STATE_DOWNLOAD_ERROR) {
            progressBar.progress = 0
            tvProgress.text = "0%"
            layoutProgress.visibility = View.GONE
            btOk.visibility = View.VISIBLE
            if (isForce) {
                btCancel.visibility = View.GONE
            } else {
                btCancel.visibility = View.VISIBLE
            }
            btOk.text = "重试"
        } else if (downLoadProgress.state == DownLoadProgress.STATE_NORMAL) {
            progressBar.progress = downLoadProgress.progress
            tvProgress.text = downLoadProgress.progress.toString() + "%"
        } else if (downLoadProgress.state == DownLoadProgress.STATE_FINISH) {
            progressBar.progress = 0
            tvProgress.text = "0%"
            layoutProgress.visibility = View.GONE
            btOk.visibility = View.VISIBLE
            btOk.text = "安装"
            if (isForce) {
                btCancel.visibility = View.GONE
            } else {
                btCancel.visibility = View.VISIBLE
            }
        }
    }



    @SuppressLint("ObsoleteSdkInt")
    override fun show(manager: FragmentManager, tag: String?) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            if (manager.isDestroyed) {
                return
            }
        }
        try {
            super.show(manager, tag)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



}
