package com.example.prac10;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {
    private WebView mWebView;

    private void createWebPrintJob(WebView webView) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);

            PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter("doc");

            String jobName = getString(R.string.app_name) + " Print Test";

            printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
        }
    }

    private void doPrint() {
        PrintManager printManager = (PrintManager)this.getSystemService(Context.PRINT_SERVICE);

        String jobName = this.getString(R.string.app_name) + " Document";

        printManager.print(jobName, new MyPrintDocumentAdapter(this), new PrintAttributes.Builder().build());
    }

    private void doWebViewPrint() {
        WebView webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                createWebPrintJob(view);
                mWebView = null;
            }
        });

        String htmlDocument = "<html><body><h1>Test Content</h1><p>Testing, " +
                "testing, testing...</p></body></html>";
        webView.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null);

        mWebView = webView;
    }

    private void doWebViewPrintPdf() {
        WebView webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                createWebPrintJob(view);
                mWebView = null;
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);

        String pdf = "http://www.africau.edu/images/default/sample.pdf";
        webView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=" + pdf);

        mWebView = webView;
    }

    public void onClickHtml(View view) {
        this.doWebViewPrint();
    }

    public void onClickPdf(View view) {
        this.doWebViewPrintPdf();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}