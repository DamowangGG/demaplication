package com.zejian.myapplication.ui.update

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.CollectionUtils
import com.blankj.utilcode.util.Utils
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

object UpdateManager {

    val apkDir = Utils.getApp().filesDir.path + "/download/apk/"
    val INSTALL_PACKAGES_REQUESTCODE = 666
    var updateBean:UpdateBean? = null

    fun installApp(context: Context, appFile: File): Boolean {
        try {
            val intent = getInstallAppIntent(context, appFile)
            if (context.packageManager.queryIntentActivities(intent, 0).size > 0) {
                context.startActivity(intent)
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }


    fun installApp(activity: Activity?, appFile: File): Boolean {
        try {
            val intent = getInstallAppIntent(activity, appFile)
            if (activity!!.packageManager.queryIntentActivities(intent, 0).size > 0) {
                activity.startActivityForResult(intent, INSTALL_PACKAGES_REQUESTCODE)
            }
            return true
        } catch (e: Exception) {
        }
        return false
    }


    fun installApp(fragment: Fragment, appFile: File): Boolean {
        return installApp(fragment.activity, appFile)
    }


    fun getInstallAppIntent(context: Context?, appFile: File): Intent? {
        try {
            val intent =
                Intent(Intent.ACTION_VIEW)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //区别于 FLAG_GRANT_READ_URI_PERMISSION 跟 FLAG_GRANT_WRITE_URI_PERMISSION，
                // URI权限会持久存在即使重启，直到明确的用 revokeUriPermission(Uri, int) 撤销。
                // 这个flag只提供可能持久授权。但是接收的应用必须调用ContentResolver的takePersistableUriPermission(Uri, int)方法实现
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                val fileUri = FileProvider.getUriForFile(
                    context!!,
                    context.packageName + ".provider", appFile
                )
                intent.setDataAndType(fileUri, "application/vnd.android.package-archive")
            } else {
                try {
                    Runtime.getRuntime().exec("chmod 777 " + appFile.canonicalPath)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                val uri = Uri.fromFile(appFile)
                intent.setDataAndType(uri, "application/vnd.android.package-archive")
            }
            return intent
        } catch (e: Exception) {
        }
        return null
    }


    @Throws(FileNotFoundException::class)
    fun installApk(context: Context) {
        val file = getInstallApkPath(context)
        if (file == null || !file.exists()) throw FileNotFoundException()
        val intent = Intent(Intent.ACTION_VIEW)
        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= 24) {
            //provider authorities
            val apkUri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
            //Granting Temporary Permissions to a URI
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setDataAndType(
                Uri.fromFile(file),
                "application/vnd.android.package-archive"
            )
        }
        context.startActivity(intent)
    }


    fun getInstallApkPath(context: Context): File? {
        val remoteVersion = getNewApkVersion()
        return if (!TextUtils.isEmpty(remoteVersion)) {
            val temp = File(context.externalCacheDir!!.absolutePath)
            if (!temp.exists()) temp.mkdirs()
            File(temp.path, "app_$remoteVersion.apk")
        } else {
            null
        }
    }

    fun getTempApkPath(context: Context): File? {
        val remoteVersion = getNewApkVersion()
        return if (!TextUtils.isEmpty(remoteVersion)) {
            val temp = File(context.externalCacheDir!!.absolutePath)
            if (!temp.exists()) temp.mkdirs()
            File(temp.path, "app_" + remoteVersion + "_temp.apk")
        } else {
            null
        }
    }

    fun getNewApkVersion(): String {
        updateBean?.let {
            return it.serverVersionName
        }
        return ""
    }

    fun getUpdateAddress(): String? {
        updateBean?.let {
            return it.apkUrl
        }
        return null
    }


    /**
     * 对比版本号
     * @return 新版本返回true ,否则返回false
     */
    fun isNewVersion(version: String): Boolean {
        if (TextUtils.isEmpty(version)) {
            return false
        }
        val versionPart = getVersionName().split("\\.".toRegex()).toTypedArray()
        val newVersionPart =
            version.split("\\.".toRegex()).toTypedArray()
        if (versionPart.isNullOrEmpty() || newVersionPart.isNullOrEmpty()) {
            return versionPart.size != newVersionPart.size
        }
        for (i in versionPart.indices) {
            val newPart = parseInt(newVersionPart[i])
            val oldPart = parseInt(versionPart[i])
            if (newPart > oldPart) {
                return true
            } else if (newPart < oldPart) {
                break
            }
        }
        return false
    }


    fun getVersionCode(): Int {
        val context = Utils.getApp().applicationContext
        var versionCode = 0
        try {
            versionCode = context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versionCode
    }


    /**
     * 获取版本号名称
     */
    fun getVersionName(): String {
        var verName: String = ""
        try {
            val context = Utils.getApp()
            verName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return verName
    }


    private fun parseInt(number: String): Int {
        try {
            return if (TextUtils.isEmpty(number)) {
                0
            } else number.toInt()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        return 0
    }

}