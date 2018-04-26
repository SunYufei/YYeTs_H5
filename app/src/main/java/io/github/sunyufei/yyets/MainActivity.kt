package io.github.sunyufei.yyets

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.sdk.*


class MainActivity : AppCompatActivity() {

    companion object {
        private const val INDEX_URL: String = "http://m.zimuzu.tv/index.html"
    }

    private lateinit var webView: WebView

    private var backPressed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cb = object : QbSdk.PreInitCallback {
            override fun onViewInitFinished(p0: Boolean) {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun onCoreInitFinished() {
                //To change body of created functions use File | Settings | File Templates.
            }
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
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                if (webView.url == INDEX_URL && request!!.url.toString().indexOf("m.zimuzu.tv") < 0)
                    Toast.makeText(this@MainActivity, "广告页面，不会跳转", Toast.LENGTH_SHORT).show()
                else
                    webView.loadUrl(request!!.url.toString())
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
