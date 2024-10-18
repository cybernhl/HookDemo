package com.hyb.hookdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hyb.hookdemo.model.Article;
import com.hyb.hookdemo.model.NewsResponse;

import timber.log.Timber;

/**
 * @author Mr.HuaYunBin
 */
public class MainActivity extends AppCompatActivity {
    private CounterViewModel counterViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        counterViewModel = new ViewModelProvider(this).get(CounterViewModel.class);
        Button button = findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, toastMsg(), Toast.LENGTH_LONG).show();
            }
        });
        TextView countTextView = findViewById(R.id.text_count);
        Button incrementButton = findViewById(R.id.button_increment);
        Button decrementButton = findViewById(R.id.button_decrement);
        counterViewModel.getCount().observe(this, count -> countTextView.setText(String.valueOf(count)));
        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counterViewModel.increment();
            }
        });

        decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counterViewModel.decrement();
            }
        });

        // Fetch news headlines
        counterViewModel.fetchTopHeadlines("us", "f53a2c1085bd4e19859f4f3b07f2babf");

        // 觀察數據
        counterViewModel.getTopHeadlines().observe(this, new Observer<NewsResponse>() {
            @Override
            public void onChanged(NewsResponse newsResponse) {
                if (newsResponse != null && newsResponse.getArticles() != null) {
                    StringBuilder news = new StringBuilder();
                    for (Article article : newsResponse.getArticles()) {
                        news.append(article.getTitle()).append("\n\n");
                    }
//                    Timber.e("Show me result : " + news);
                }
            }
        });

//        WebView webview = findViewById(R.id.webview);
//        webview.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                Log.e("WebViewError", "Error: shouldOverrideUrlLoading  ");
//                view.loadUrl(url);
//                return true;
//            }
//            @Override
//            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                Log.e("WebViewError", "Error: " + description + ", URL: " + failingUrl);
//            }
//        });
//        webview.getSettings().setJavaScriptEnabled(true);
//        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        webview.setWebContentsDebuggingEnabled(true);
////      webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
////      webview.getSettings().setDomStorageEnabled(true);
//        webview.setWebChromeClient(new WebChromeClient() {
//            // 設置加載進度
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                Timber.e("Show me onProgressChanged : " + newProgress);
//
//            }
//
//            // 設置活動的標題為網頁標題
//            @Override
//            public void onReceivedTitle(WebView view, String title) {
////                setTitle(title);
//                Timber.e("Show me onReceivedTitle : " + title);
//            }
//        });
//        webview.loadUrl("http://reg4545.98kbus.top/direct/login/MTcyOTE1NDU1N18zMDBfMzQ5NzY3Xzg3RkMwQg==?d=5&l=2");
    }

    //TODO "toastMsg" --> HookUtils --> handleLoadPackage --> XposedHelpers.findAndHookMethod
    private String toastMsg() {
        return "我未被劫持";
    }
}
