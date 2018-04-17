package io.github.sunyufei.yyets

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    companion object {
        private const val INDEX_URL: String = "http://m.zimuzu.tv/index.html"
    }

    private lateinit var webView: WebView

    private var backPressed: Boolean = false

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
                if (webView.url == INDEX_URL && url!!.indexOf("m.zimuzu.tv") < 0)
                    Toast.makeText(this@MainActivity, "广告页面，不会跳转", Toast.LENGTH_SHORT).show()
                else
                    webView.loadUrl(url)
                return true
            }
        }

        webView.webChromeClient = object : WebChromeClient() {}
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
}
