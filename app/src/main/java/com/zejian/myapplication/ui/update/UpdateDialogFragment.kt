package me.yinmai.yidui.main.update

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.ToastUtils
import com.zejian.myapplication.R
import com.zejian.myapplication.ui.update.DownLoadProgress
import com.zejian.myapplication.ui.update.UpdateManager
import com.zejian.myapplication.ui.update.UpdateService
import kotlinx.android.synthetic.main.dialog_update.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * A simple [Fragment] subclass.
 */
class UpdateDialogFragment : DialogFragment() {

    private var isConstraint = false
    private var updateMsg: String? = null
    private var remoteVersion = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_update, container)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.UpdateAppDialog)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val versionBean = AppConfig.getVersionInfo()
        if (versionBean == null) {
            dismiss()
            return
        }
        updateMsg = versionBean.message
        if (versionBean.must == 1) { //强制更新
            isConstraint = true
        } else {
            isConstraint = false
            dialog!!.setCanceledOnTouchOutside(false)
        }

        btCancel.text = "暂不升级"
        tvVersion.text = "v" + versionBean.versions
        tvUpdateContent.text = Html.fromHtml(updateMsg)
        //标题
        tvTitle.text = "V" + versionBean.versions + "更新内容"
        //强制更新
        if (isConstraint) {
            btCancel.visibility = View.GONE
        }

        val file = UpdateManager.getInstallApkPath(context)
        if (file != null && file.exists()) {
            btOk.text = "开始安装"
        }

        btOk.click {
            context?.let {
                val apkFile = UpdateManager.getInstallApkPath(it)
                if (apkFile != null && apkFile.exists()) {
                    val res = UpdateManager.installApp(this, apkFile)
                    if (res) {
                        if (!isConstraint) {
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
            AppConfig.INSTANCE.lastRemindVersion = remoteVersion
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
            if (isConstraint) {
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
            if (isConstraint) {
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


    override fun onStart() {
        super.onStart()
        //点击window外的区域 是否消失
        context?.let { mContext->
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

    }


}
