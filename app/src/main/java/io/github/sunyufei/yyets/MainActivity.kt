package io.github.sunyufei.yyets

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    companion object {
        private const val INDEX_URL: String = "http://m.zimuzu.tv/index.html"
        private const val VERSION_URL: String = "https://gitee.com/sunovo/YYeTs_H5/raw/master/VERSION.txt"
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
    }

    override fun onBackPressed() {
        when (webView.canGoBack()) {
            true -> webView.goBack()
            false -> super.onBackPressed()
        }
    }
}
