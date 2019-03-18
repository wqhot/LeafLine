package com.lf.leafline;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ViewMapActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_map_activity);
		WebView myWebView = (WebView) findViewById(R.id.map_webView1);
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.requestFocus();
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		String url = bundle.getString("str");
		myWebView.loadUrl("http://" + url);
		myWebView.setWebViewClient(new WebViewClient());
	}

	@Override
	public void onBackPressed() {
		// TODO 自动生成的方法存根
		Intent intent1 = new Intent(ViewMapActivity.this, MainActivity.class);
		startActivity(intent1);
		this.finish();
		super.onBackPressed();

	}
}
