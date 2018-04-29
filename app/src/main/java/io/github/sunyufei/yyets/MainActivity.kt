package io.github.sunyufei.yyets

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.tencent.smtt.sdk.*
import org.jetbrains.anko.toast


class MainActivity : AppCompatActivity() {

    companion object {
        private const val INDEX_URL: String = "http://m.zimuzu.tv/index.html"
        /* private const val VERSION_URL: String = "https://gitee.com/sunovo/YYeTs_H5/raw/master/VERSION.json"
        private const val APK_URL: String = "https://gitee.com/sunovo/YYeTs_H5/raw/master/app/release/YYeTs_Latest.apk"
        private const val DOWNLOAD_PATH: String = "/Download/"
        private const val APK_NAME: String = "yyets_h5.apk" */
    }

    private lateinit var webView: WebView

    // private lateinit var broadcastReceiver: BroadcastReceiver

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

        /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permission = ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val storage = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@MainActivity, storage, 1)
            }
        } */

        webView = findViewById(R.id.WebView)

        webView.run {

            settings.run {
                allowFileAccess = true
                cacheMode = WebSettings.LOAD_DEFAULT
                defaultTextEncodingName = "UTF-8"
                domStorageEnabled = true
                javaScriptEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
                loadWithOverviewMode = true
                loadsImagesAutomatically = true
                useWideViewPort = true
            }

            loadUrl(INDEX_URL)

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    if (webView.url == INDEX_URL && url!!.indexOf("m.zimuzu.tv") < 0)
                        toast("广告页面，不会跳转")
                    else
                        webView.loadUrl(url)
                    return true
                }
            }

            webChromeClient = object : WebChromeClient() {}
        }

        // checkUpdate()
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
                        toast("再按一次退出")
                    }
                }
            }
        }
    }
    /*
    private fun checkUpdate() {
        var currentVCode = 0
        var currentVName = ""
        var latestVCode: Int
        var latestVName: String
        var content: String

        try {
            currentVCode = this@MainActivity.packageManager.getPackageInfo(this@MainActivity.packageName, 0).versionCode
            currentVName = this@MainActivity.packageManager.getPackageInfo(this@MainActivity.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        doAsync {
            val latestString = URL(VERSION_URL).readText()
            val jsonObject = JSONObject(latestString)

            uiThread {
                latestVCode = jsonObject.optInt("versionCode")
                latestVName = jsonObject.optString("versionName")
                content = jsonObject.optString("content")
                if (latestVCode > currentVCode) {
                    var updateMessage = "最新版本："
                    updateMessage += latestVName
                    updateMessage += "\n当前版本："
                    updateMessage += currentVName
                    updateMessage += "\n\n更新内容：\n"
                    updateMessage += content

                    alert {
                        title = "发现新版本"
                        message = updateMessage
                        positiveButton("更新") {
                            val request = DownloadManager.Request(Uri.parse(APK_URL)).run {
                                setDestinationInExternalPublicDir(DOWNLOAD_PATH, APK_NAME)
                                title = "人人影视H5"
                                setDescription("正在下载新版本")
                            }
                            val downloadManager = this@MainActivity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                            val id = downloadManager.enqueue(request)

                            val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                            broadcastReceiver = object : BroadcastReceiver() {
                                override fun onReceive(context: Context?, intent: Intent?) {
                                    val intentID = intent!!.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                                    if (intentID == id) {
                                        val uri = Uri.parse("file://" + Environment.getExternalStorageDirectory().toString() + DOWNLOAD_PATH + APK_NAME)
                                        val intentAPK = Intent(Intent.ACTION_VIEW).run {
                                            flags = when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                true -> Intent.FLAG_GRANT_READ_URI_PERMISSION
                                                false -> Intent.FLAG_ACTIVITY_NEW_TASK
                                            }
                                            setDataAndType(uri, "application/vnd.android.package-archive")
                                        }
                                        startActivity(intentAPK)
                                    }
                                }
                            }
                            this@MainActivity.registerReceiver(broadcastReceiver, intentFilter)
                        }
                        negativeButton("取消") {}
                    }.show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(broadcastReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/
}
