package io.github.sunyufei.yyets

import android.content.DialogInterface
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import java.net.URL
import kotlin.math.min


class MainActivity : AppCompatActivity() {

    companion object {
        private const val INDEX_URL: String = "http://m.zimuzu.tv/index.html"
        private const val VERSION_URL: String = "https://gitee.com/sunovo/YYeTs_H5/raw/master/VERSION.json"
    }

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar!!.hide()

        webView = findViewById(R.id.WebView)

        val settings = webView.settings
        settings.allowFileAccess = true
        settings.defaultTextEncodingName = "UTF-8"
        settings.domStorageEnabled = true
        settings.javaScriptEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.loadWithOverviewMode = true
        settings.loadsImagesAutomatically = true
        settings.useWideViewPort = true

        webView.loadUrl(INDEX_URL)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url!!.indexOf("m.zimuzu.tv") > 0)
                    webView.loadUrl(url)
                else
                    Toast.makeText(this@MainActivity, "广告页面，不会跳转", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        checkUpdate()
    }

    override fun onBackPressed() {
        when (webView.canGoBack()) {
            true -> webView.goBack()
            false -> super.onBackPressed()
        }
    }

    private fun checkUpdate() {
        var currentVersion = "0"
        val packageManager = this@MainActivity.packageManager
        try {
            val packageInfo = packageManager.getPackageInfo(this.packageName, 0)
            currentVersion = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        val thread = object : Thread() {
            override fun run() {
                val url = URL(VERSION_URL)
                val latestVersion = url.readText()
                if (canUpdate(currentVersion, latestVersion)) {
                    val builder = AlertDialog.Builder(this@MainActivity)
                    builder.setMessage("检测到新版本，是否下载安装？")
                    builder.setTitle("更新提示")
                    builder.setPositiveButton("更新", null)
                    builder.setNeutralButton("取消", null)
                    builder.create()
                    builder.show()
                }
            }

            private fun canUpdate(current: String, latest: String): Boolean {
                if (current == "0" || latest == "0")
                    return false
                else {
                    val currentList = current.split(".")
                    val latestList = latest.split(".")
                    val index = min(currentList.size, latestList.size) - 1
                    for (i in 0..index) {
                        if (latestList[i] > currentList[i])
                            return true
                        else if (latestList[i] < currentList[i])
                            return false
                    }
                    if (latestList.size > index)
                        return true
                    return false
                }
            }
        }
        thread.start()
    }
}
