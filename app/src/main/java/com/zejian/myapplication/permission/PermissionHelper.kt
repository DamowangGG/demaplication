package com.zejian.myapplication.permission

import android.content.Context
import android.view.View
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.*
import com.zejian.myapplication.R

object PermissionHelper {

    fun request(context: Context, callback: PermissionUtils.SimpleCallback,
                @PermissionConstants.PermissionGroup vararg permissions: String) {
        PermissionUtils.permission(*permissions)
                .rationale { activity, shouldRequest -> showRationaleDialog(activity, shouldRequest) }
                .callback(object : PermissionUtils.SingleCallback {
                    override fun callback(isAllGranted: Boolean, granted: MutableList<String>,
                                          deniedForever: MutableList<String>, denied: MutableList<String>) {
                        LogUtils.d(isAllGranted, granted, deniedForever, denied)
                        if (isAllGranted) {
                            callback.onGranted()
                            return
                        }
                        if (deniedForever.isNotEmpty()) {
                            showOpenAppSettingDialog(context)
                            return
                        }
                        val activity = ActivityUtils.getActivityByContext(context)
                        if (activity != null) {
                            SnackbarUtils.with(activity.findViewById(android.R.id.content))
                                    .setMessage("Permission denied: ${permissions2String(denied)}")
                                    .showError(true)
                        }
                        callback.onDenied()
                    }

                    fun permissions2String(permissions: MutableList<String>): String {
                        if (permissions.isEmpty()) return "[]"
                        val sb: StringBuilder = StringBuilder()
                        for (permission in permissions) {
                            sb.append(", " + permission.substring(permission.lastIndexOf('.') + 1))
                        }
                        return "[${sb.substring(2)}]"
                    }
                })
                .request()
    }

    fun showRationaleDialog(context: Context, shouldRequest: PermissionUtils.OnRationaleListener.ShouldRequest) {
        CommonDialogContent().init(context,
                StringUtils.getString(android.R.string.dialog_alert_title),
                StringUtils.getString(R.string.permission_rationale_message),
                android.util.Pair(StringUtils.getString(android.R.string.ok), View.OnClickListener {
                    shouldRequest.again(true)
                }),
            android.util.Pair(StringUtils.getString(android.R.string.cancel), View.OnClickListener {
                    shouldRequest.again(false)
                }))
                .show()
    }

    fun showExplainDialog(context: Context, denied: List<String>, shouldRequest: PermissionUtils.OnExplainListener.ShouldRequest) {
        CommonDialogContent().init(context,
                StringUtils.getString(android.R.string.dialog_alert_title),
                "We needs the permissions of $denied to test the utils of permission.",
                android.util.Pair(StringUtils.getString(android.R.string.ok), View.OnClickListener {
                    shouldRequest.start(true)
                }),
                android.util.Pair(StringUtils.getString(android.R.string.cancel), View.OnClickListener {
                    ToastUtils.showShort("request failed.")
                    shouldRequest.start(false)
                }))
                .show()
    }

    fun showOpenAppSettingDialog(context: Context) {
        CommonDialogContent().init(context,
                StringUtils.getString(android.R.string.dialog_alert_title),
                StringUtils.getString(R.string.permission_denied_forever_message),
                android.util.Pair(StringUtils.getString(android.R.string.ok), View.OnClickListener {
                    PermissionUtils.launchAppDetailsSettings()
                }),
                android.util.Pair(StringUtils.getString(android.R.string.cancel), View.OnClickListener {
                }))
                .show()
    }
}