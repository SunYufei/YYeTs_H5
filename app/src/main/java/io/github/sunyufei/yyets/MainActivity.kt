package io.github.sunyufei.yyets

import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


class MainActivity : AppCompatActivity() {

    companion object {
        private val URL: String = "http://m.zimuzu.tv/index.html"
        private val VERSION_URL: String = "https://gitee.com/sunovo/YYeTs_H5/raw/master/VERSION.xml"
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

        webView.loadUrl(URL)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url!!.indexOf("m.zimuzu.tv") > 0)
                    webView.loadUrl(url)
                else
                    Toast.makeText(this@MainActivity, "广告页面，不会打开", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        getLatestVersion()
    }

    override fun onBackPressed() {
        when (webView.canGoBack()) {
            true -> webView.goBack()
            false -> super.onBackPressed()
        }
    }

    private fun getVersionName(): String {
        val packageManager = this@MainActivity.packageManager
        var versionName = ""
        try {
            val packageInfo = packageManager.getPackageInfo(this.packageName, 0)
            versionName = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versionName
    }

    private fun getLatestVersion(): String {
        // val doc: Document = Jsoup.connect(VERSION_URL).get()
        return ""
    }

    private fun canUpdate(): Boolean {
        val currentVersion = getVersionName()
        val latestVersion = getLatestVersion()
        return false
    }
}
