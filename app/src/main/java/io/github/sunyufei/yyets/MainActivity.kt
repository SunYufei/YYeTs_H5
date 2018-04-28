package io.github.sunyufei.yyets

import android.app.DownloadManager
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.tencent.smtt.sdk.*
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.net.URL


class MainActivity : AppCompatActivity() {

    companion object {
        private const val INDEX_URL: String = "http://m.zimuzu.tv/index.html"
        private const val VERSION_URL: String = "https://gitee.com/sunovo/YYeTs_H5/raw/master/VERSION.json"
        private const val APK_URL: String = "https://gitee.com/sunovo/YYeTs_H5/raw/master/app/release/YYeTs_Latest.apk"
        private const val DOWNLOAD_PATH: String = "/Download/"
        private const val APK_NAME: String = "yyets_h5.apk"
    }

    private lateinit var webView: WebView
    private lateinit var broadcastReceiver: BroadcastReceiver

    private var backPressed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cb = object : QbSdk.PreInitCallback {
            override fun onCoreInitFinished() {}
            override fun onViewInitFinished(p0: Boolean) {}
        }
        QbSdk.initX5Environment(applicationContext, cb)

        setContentView(R.layout.activity_main)

        supportActionBar!!.hide()

        webView = findViewById(R.id.WebView)

        val settings = webView.settings
        settings.allowFileAccess = true
        settings.defaultTextEncodingName = "UTF-8"
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.domStorageEnabled = true
        settings.javaScriptEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.loadWithOverviewMode = true
        settings.loadsImagesAutomatically = true
        settings.useWideViewPort = true

        webView.loadUrl(INDEX_URL)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (webView.url == INDEX_URL && url!!.indexOf("m.zimuzu.tv") < 0)
                    Toast.makeText(this@MainActivity, "广告页面，不会跳转", Toast.LENGTH_SHORT).show()
                else
                    webView.loadUrl(url)
                return true
            }
        }

        webView.webChromeClient = object : WebChromeClient() {}

        checkUpdate()
    }

    override fun onBackPressed() {
        when (webView.canGoBack()) {
            true -> {
                webView.goBack()
                backPressed = false
            }
            false -> {
                when (backPressed) {
                    true -> super.onBackPressed()
                    false -> {
                        backPressed = true
                        Toast.makeText(this@MainActivity, "再按一次退出", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun checkUpdate() {
        var currentVCode = 0
        var currentVName = ""
        try {
            currentVCode = this@MainActivity.packageManager.getPackageInfo(this@MainActivity.packageName, 0).versionCode
            currentVName = this@MainActivity.packageManager.getPackageInfo(this@MainActivity.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        var latestVCode: Int
        var latestVName: String
        var content: String
        async {
            val latestString = URL(VERSION_URL).readText()
            val jsonObject = JSONObject(latestString)

            uiThread {
                latestVCode = jsonObject.optInt("versionCode")
                latestVName = jsonObject.optString("versionName")
                if (latestVCode > currentVCode) {
                    content = jsonObject.optString("content")
                    val builder = AlertDialog.Builder(this@MainActivity)
                    builder.setTitle("发现新版本")
                    builder.setMessage("最新版本：" + latestVName + "\n当前版本：" + currentVName + "\n\n更新内容：\n" + content)
                    builder.setPositiveButton("更新", DialogInterface.OnClickListener { _, _ ->
                        val request = DownloadManager.Request(Uri.parse(APK_URL))
                        request.setDestinationInExternalPublicDir(DOWNLOAD_PATH, APK_NAME)
                        request.setTitle("人人影视H5")
                        request.setDescription("正在下载新版本")
                        val downloadManager = this@MainActivity.getSystemService(android.content.Context.DOWNLOAD_SERVICE) as DownloadManager
                        val id = downloadManager.enqueue(request)

                        val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                        broadcastReceiver = object : BroadcastReceiver() {
                            override fun onReceive(context: Context?, intent: Intent?) {
                                val intentID = intent!!.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                                if (intentID == id) {
                                    val intentAPK = Intent(Intent.ACTION_VIEW)
                                    intentAPK.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    val uri = Uri.parse("file://" + Environment.getExternalStorageDirectory().toString() + DOWNLOAD_PATH + APK_NAME)
                                    intentAPK.setDataAndType(uri, "application/vnd.android.package-archive")
                                    this@MainActivity.startActivity(intentAPK)
                                }
                            }
                        }
                        registerReceiver(broadcastReceiver, intentFilter)
                    })
                    builder.setNegativeButton("取消", null)
                    builder.show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(broadcastReceiver)
        } catch (e: UninitializedPropertyAccessException) {
            e.printStackTrace()
        }
    }
}
