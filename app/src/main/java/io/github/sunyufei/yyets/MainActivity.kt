package io.github.sunyufei.yyets

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.tencent.smtt.sdk.*


class MainActivity : AppCompatActivity() {

    companion object {
        private const val INDEX_URL: String = "http://m.zimuzu.tv/index.html"
        private const val VERSION_URL: String = "https://gitee.com/sunovo/YYeTs_H5/raw/master/VERSION.json"
        private const val APK_URL: String = "https://gitee.com/sunovo/YYeTs_H5/raw/master/app/release/YYeTs_Latest.apk"
        private const val DOWNLOAD_PATH: String = "/Download/"
        private const val APK_NAME: String = "yyets_h5.apk"
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

        webView.settings.run {
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

        Update(this@MainActivity, VERSION_URL, APK_URL, DOWNLOAD_PATH, APK_NAME)
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
