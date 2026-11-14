package com.resilientbite.app;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.webkit.*;
import android.widget.*;

public class MainActivity extends Activity {
    private WebView web;
    private ValueCallback<Uri[]> upload;
    private String server;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        server = BuildConfig.SERVER_URL;
        if (server.isEmpty()) {
            server = getPreferences(0).getString("server", "");
            showConnection();
        } else {
            showPlanner();
        }
    }

    private void showConnection() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 56, 32, 24);
        layout.setBackgroundColor(Color.rgb(244, 247, 241));
        TextView title = new TextView(this);
        title.setText("ResilientBite");
        title.setTextSize(28);
        title.setTextColor(Color.rgb(35, 78, 53));
        layout.addView(title);
        TextView help = new TextView(this);
        help.setText("Connect to your forecasting server\n\nRun the Python app on your computer. Connect this phone to the same Wi-Fi and enter the computer’s address, for example http://192.168.1.10:8501\n");
        layout.addView(help);
        EditText address = new EditText(this);
        address.setSingleLine(true);
        address.setInputType(17);
        address.setHint("Server address");
        address.setText(server);
        layout.addView(address);
        Button connect = new Button(this);
        connect.setText("Open meal planner");
        layout.addView(connect);
        connect.setOnClickListener(view -> {
            String value = address.getText().toString().trim();
            Uri uri = Uri.parse(value);
            if (uri.getHost() == null || !("http".equals(uri.getScheme()) || "https".equals(uri.getScheme()))) {
                address.setError("Enter a complete http:// or https:// address");
                return;
            }
            server = value;
            getPreferences(0).edit().putString("server", server).apply();
            showPlanner();
        });
        setContentView(layout);
    }

    private void showPlanner() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        Button settings = new Button(this);
        settings.setText(BuildConfig.SERVER_URL.isEmpty() ? "Change server" : "Reload meal planner");
        settings.setOnClickListener(view -> {
            if (BuildConfig.SERVER_URL.isEmpty()) {
                web.destroy();
                web = null;
                showConnection();
            } else {
                web.reload();
            }
        });
        layout.addView(settings);
        web = new WebView(this);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.setWebViewClient(new WebViewClient() {
            @Override public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request.isForMainFrame()) Toast.makeText(MainActivity.this,
                    "Could not connect. Check your internet connection and try Reload.", Toast.LENGTH_LONG).show();
            }
        });
        web.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if (upload != null) upload.onReceiveValue(null);
                upload = callback;
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
                return true;
            }
        });
        layout.addView(web, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(layout);
        web.loadUrl(server);
    }

    @Override protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        if (request == 1 && upload != null) {
            upload.onReceiveValue(result == RESULT_OK && data != null ? new Uri[]{data.getData()} : null);
            upload = null;
        }
    }

    @Override public void onBackPressed() {
        if (web != null && web.canGoBack()) web.goBack();
        else if (web != null && BuildConfig.SERVER_URL.isEmpty()) { web.destroy(); web = null; showConnection(); }
        else super.onBackPressed();
    }
}
