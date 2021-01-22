package com.zejian.myapplication

import android.Manifest
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.SnackbarUtils
import com.zejian.myapplication.anima.Techniques
import com.zejian.myapplication.anima.YoYo
import com.zejian.myapplication.base.BaseActivity
import com.zejian.myapplication.emoji.EmojiActivity
import com.zejian.myapplication.permission.PermissionHelper
import com.zejian.myapplication.swipecard.TanTanActivity
import com.zejian.myapplication.taluo.VP2Activity
import com.zejian.myapplication.ui.GoogleRocketActivity
import com.zejian.myapplication.ui.ScrollingActivity
import com.zejian.myapplication.ui.loading.Loading
import com.zejian.myapplication.widget.SvgaPlayer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    val mHandler = Handler()
    val mPlayer:SvgaPlayer by lazy { SvgaPlayer(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        NotificationChannelUtils.initChannel(this)
        NotificationUtil.getInstance().initUserConfig(this)

        val bitmap1 = BitmapFactory.decodeResource(resources,R.drawable.jack)
        val bitmap2 = BitmapFactory.decodeResource(resources,R.drawable.a)
        val bitmap3 = BitmapFactory.decodeResource(resources,R.drawable.h)

        button1.setOnClickListener {
            NotificationUtil.getInstance().send("1234","Andy",bitmap1)
            YoYo.with(Techniques.Shake)
                .duration(600).playOn(button1)
        }

        button2.setOnClickListener {
//            NotificationUtil.getInstance().send("1235","peter",bitmap2)
            Loading(this).setMessage("加载中").show()
        }

        button3.setOnClickListener {
            NotificationUtil.getInstance().send("1236","jack",bitmap3)
            startActivity(Intent(this,Main2Activity::class.java))
        }
        button4.setOnClickListener {
            startActivity(Intent(this,EmojiActivity::class.java))
        }
        button5.setOnClickListener {
            startActivity(Intent(this,VP2Activity::class.java))
        }
        button6.setOnClickListener { startActivity(Intent(this,TanTanActivity::class.java)) }
        button7.setOnClickListener {
            mPlayer.setImageView(animateView)
            repeat(6){
                mPlayer.play("http://s-y.oss-cn-hangzhou.aliyuncs.com/s-y/gift/91b9afc2-c62e-45bf-8db3-9aacbc413962.svga")
            }
        }
        loading.setOnClickListener {
            startActivity(Intent(this,ScrollingActivity::class.java))
        }
        success.setOnClickListener {
            val dialog = Loading(this)
            dialog.showLoading()
            mHandler.postDelayed({
                dialog.showSuccess("上传成功")
            },3000)
        }
        motionLayout.setOnClickListener {
            startActivity(Intent(this,MotionActivity::class.java))
        }
        googleRocket.setOnClickListener {
            startActivity(Intent(this,GoogleRocketActivity::class.java))
        }
        permissionFun()

    }

    private fun permissionFun() {
        rationale.setOnClickListener {
            PermissionUtils.permissionGroup(PermissionConstants.MICROPHONE)
                .rationale { activity, shouldRequest -> PermissionHelper.showRationaleDialog(activity, shouldRequest) }
                .callback(object : PermissionUtils.FullCallback {
                    override fun onGranted(permissionsGranted: List<String>) {
                        LogUtils.d(permissionsGranted)
                        showSnackbar(true, "Microphone is granted")
                    }

                    override fun onDenied(permissionsDeniedForever: List<String>,
                                          permissionsDenied: List<String>) {
                        LogUtils.d(permissionsDeniedForever, permissionsDenied)
                        if (permissionsDeniedForever.isNotEmpty()) {
                            showSnackbar(false, "Microphone is denied forever")
                        } else {
                            showSnackbar(false, "Microphone is denied")
                        }
                    }
                })
                .request()
        }
        explain.setOnClickListener {

        }
        WriteSettings.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PermissionUtils.requestWriteSettings(object : PermissionUtils.SimpleCallback {
                    override fun onGranted() {
                        showSnackbar(true, "Write Settings is granted")
                    }

                    override fun onDenied() {
                        showSnackbar(false, "Write Settings is denied")
                    }
                })
            }
        }
        singlePermission.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PermissionUtils.requestDrawOverlays(object : PermissionUtils.SimpleCallback {
                    override fun onGranted() {
                        showSnackbar(true, "Draw Overlays is granted")
                    }

                    override fun onDenied() {
                        showSnackbar(false, "Draw Overlays is denied")
                    }
                })
            }
        }
        doublePermission.setOnClickListener {
            PermissionUtils.permission(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                //before request
                .explain { activity, denied, shouldRequest -> PermissionHelper.showExplainDialog(activity, denied, shouldRequest) }
                .callback { isAllGranted, granted, deniedForever, denied ->
                    LogUtils.d(granted, deniedForever, denied)
                    if (isAllGranted) {
                        showSnackbar(true, "Calendar and Microphone are granted")
                        return@callback
                    }
                    if (deniedForever.isNotEmpty()) {
                        showSnackbar(false, "Calendar or Microphone is denied forever")
                    } else {
                        showSnackbar(false, "Calendar or Microphone is denied")
                    }
                }
                .request()
        }
    }


    private fun showSnackbar(isSuccess: Boolean, msg: String) {
        SnackbarUtils.with(l1)
            .setDuration(SnackbarUtils.LENGTH_LONG)
            .setMessage(msg)
            .apply {
                if (isSuccess) {
                    showSuccess()
                } else {
                    showError()
                }
            }
    }

}
