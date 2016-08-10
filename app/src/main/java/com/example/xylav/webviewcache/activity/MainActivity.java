package com.example.xylav.webviewcache.activity;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.xylav.webviewcache.R;

public class MainActivity extends Activity {

    private WebView mWebView;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String APP_CACHE_DIRNAME = "/webcache"; // web缓存目录
    private String url; // 网页url

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = (WebView) this.findViewById(R.id.webview);

        // webView.loadUrl("http://www.baidu.com");

        WebSettings settings = mWebView.getSettings();
        // 设置javaScript可用
        settings.setJavaScriptEnabled(true);
        url = "http://ols.sandboxol.com/vip/4216789";
//        url = "http://www.17173.com";
        findView();
    }

    public void findView() {
        initWebView();
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onLoadResource(WebView view, String url) {
                Log.i(TAG, "onLoadResource url=" + url);

                super.onLoadResource(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, "intercept url=" + url);
                view.loadUrl(url);
                return true;
            }

            // 页面开始时调用
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.e(TAG, "onPageStarted");
                super.onPageStarted(view, url, favicon);
            }

            // 页面加载完成调用
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG)
                        .show();
            }
        });

        mWebView.loadUrl(url);
    }

    public void initWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setRenderPriority(RenderPriority.HIGH);
        // 建议缓存策略为，判断是否有网络，有的话，使用LOAD_DEFAULT,无网络时，使用LOAD_CACHE_ELSE_NETWORK

        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); // 设置缓存模式
        // 开启DOM storage API 功能
        mWebView.getSettings().setDomStorageEnabled(true);
        // 开启database storage API功能
        mWebView.getSettings().setDatabaseEnabled(true);
        String cacheDirPath = getFilesDir().getAbsolutePath()
                + APP_CACHE_DIRNAME;
        Log.i("cachePath", cacheDirPath);
        // 设置数据库缓存路径
        mWebView.getSettings().setDatabasePath(cacheDirPath); // API 19 deprecated
        // 设置Application caches缓存目录
        mWebView.getSettings().setAppCachePath(cacheDirPath);
        // 开启Application Cache功能
        mWebView.getSettings().setAppCacheEnabled(true);

        Log.i("databasepath", mWebView.getSettings().getDatabasePath());

    }

    public void clearWebViewCache() {
        // 清理WebView缓存数据库
        try {
            deleteDatabase("webview.db");
            deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // WebView缓存文件
        File appCacheDir = new File(getFilesDir().getAbsolutePath()
                + APP_CACHE_DIRNAME);
        Log.e(TAG, "appCacheDir path=" + appCacheDir.getAbsolutePath());

        File webviewCacheDir = new File(getCacheDir().getAbsolutePath()
                + "/webviewCache");
        Log.e(TAG, "appCacheDir path=" + webviewCacheDir.getAbsolutePath());

        // 删除webView缓存目录
        if (webviewCacheDir.exists()) {
            deleteFile(webviewCacheDir);
        }
        // 删除webView缓存，缓存目录
        if (appCacheDir.exists()) {
            deleteFile(appCacheDir);
        }
    }

    public void deleteFile(File file) {
        Log.i(TAG, "delete file path=" + file.getAbsolutePath());
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        } else {
            Log.e(TAG, "delete file no exists " + file.getAbsolutePath());
        }
    }

    /**
     * 用于控制页面导航
     *
     * @author wwj_748
     */
    private class MyWebViewClient extends WebViewClient {
        /**
         * 当用于点击链接，系统调用这个方法
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals("www.baidu.com")) {
                // 这个是我的网页，所以不要覆盖，让我的WebView来加载页面
                return false;
            }
            // 否则，这个链接不是我的网站页面，因此启用浏览器来处理urls
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 检查是否为返回事件，如果有网页历史记录
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        // 如果不是返回键或没有网页浏览历史，保持默认
        // 系统行为（可能会退出该活动）
        return super.onKeyDown(keyCode, event);
    }

}