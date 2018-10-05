package com.example.ano.articledaily;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.example.ano.articledaily.Bean.Book;

public class Bookshow extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookshow);

        Intent intent=getIntent();
        Book book=(Book)intent.getSerializableExtra("Books");
        WebView webView=(WebView)findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.loadUrl(book.bookUrl);
    }
}
