package io.github.sunyufei.yyets

import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
        private const val APK_URL: String = ""
    }

    private lateinit var webView: WebView

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
        var currentVersion = 0
        try {
            currentVersion = this@MainActivity.packageManager.getPackageInfo(this@MainActivity.packageName, 0).versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        var latestVersion: Int
        var content: String
        async {
            val latestString = URL(VERSION_URL).readText()
            val jsonObject = JSONObject(latestString)

            uiThread {
                latestVersion = jsonObject.optInt("versionCode")
                if (latestVersion > currentVersion) {
                    content = jsonObject.optString("content")
                    val builder = AlertDialog.Builder(this@MainActivity)
                    builder.setTitle("发现新版本")
                    builder.setMessage(content)
                    builder.setPositiveButton("更新", null)
                    builder.setNegativeButton("取消", null)
                    builder.show()
                }
            }
        }
    }
}
