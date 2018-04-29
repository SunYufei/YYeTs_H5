package io.github.sunyufei.yyets

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.net.URL

class Update(private val context: Context,
             private val versionUrl: String,
             private val apkUrl: String,
             private val downloadPath: String,
             private val apkName: String) {

    private var currentVCode = 0
    private var currentVName = ""
    private var latestVCode = 0
    private var latestVName = ""
    private var content = ""

    private lateinit var broadcastReceiver: BroadcastReceiver

    init {
        checkUpdate()
    }


    private fun checkUpdate() {
        try {
            currentVCode = context.packageManager.getPackageInfo(context.packageName, 0).versionCode
            currentVName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        doAsync {
            val latestString = URL(versionUrl).readText()
            val jsonObject = JSONObject(latestString)

            uiThread {
                latestVCode = jsonObject.optInt("versionCode")
                latestVName = jsonObject.optString("versionName")
                content = jsonObject.optString("content")
                if (latestVCode > currentVCode)
                    update()
            }
        }
    }

    private fun update() {
        var updateMessage = "最新版本："
        updateMessage += latestVName
        updateMessage += "\n当前版本"
        updateMessage += currentVName
        updateMessage += "\n\n更新内容：\n"
        updateMessage += content

        val builder = AlertDialog.Builder(context)
        builder.run {
            setTitle("发现新版本")
            setMessage(updateMessage)
            setPositiveButton("更新") { dialog: DialogInterface?, _ ->
                val request = DownloadManager.Request(Uri.parse(apkUrl))
                request.setDestinationInExternalPublicDir(downloadPath, apkName)
                // request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
                request.setTitle(context.packageName)
                request.setDescription("正在下载新版本")
                val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val id = downloadManager.enqueue(request)

                val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                broadcastReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        val intentID = intent!!.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                        if (intentID == id) {
                            val intentAPK = Intent(Intent.ACTION_VIEW)
                            intentAPK.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            val uri = Uri.parse("file://" + Environment.getExternalStorageDirectory().toString() + downloadPath + apkName)
                            intentAPK.setDataAndType(uri, "application/vnd.android.package-archive")
                            context!!.startActivity(intentAPK)
                        }
                    }
                }
                context.registerReceiver(broadcastReceiver, intentFilter)
                dialog!!.dismiss()
            }
            setNegativeButton("取消", null)
            show()
        }
    }
}