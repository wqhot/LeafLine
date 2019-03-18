package com.lf.leafline;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.lf.leafline.FayanActivity.MyThread;
import com.lf.leafline.utils.HttpUtil;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class RegActivity extends Activity {
	private Button btn_login;
	private EditText mUsername;
	private EditText mPassword;
	private CheckBox chbx_rem;
	private String username;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		initView();
		initEvent();
	}

	private void initView() {
		btn_login = (Button) findViewById(R.id.btn_login);
		mUsername = (EditText) findViewById(R.id.et_name);
		mPassword = (EditText) findViewById(R.id.et_pass);
		chbx_rem = (CheckBox) findViewById(R.id.chbx_rem);
	}

	private void initEvent() {
		SharedPreferences sharedata = getSharedPreferences("login",
				Context.MODE_PRIVATE);
		username = sharedata.getString("username", "");
		password = sharedata.getString("password", "");
		if (!username.equals("")) {
			mUsername.setText(username);
		}
		if (!password.equals("")) {
			mPassword.setText(password);
		}

		btn_login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (chbx_rem.isChecked()) {
					SharedPreferences sharedata = getSharedPreferences("login",
							Context.MODE_PRIVATE);
					Editor editor = sharedata.edit();
					editor.putString("username", mUsername.getText().toString());
					editor.putString("password", mPassword.getText().toString());
				}
				new Thread(new MyThread()).start();
			}
		});
	}

	public class MyThread implements Runnable {

		@Override
		public void run() {
			SharedPreferences sharedata = getSharedPreferences("data",
					Context.MODE_PRIVATE);

			String httpurl = sharedata.getString("ip2", "192.168.137.1");
			int port = sharedata.getInt("port2", 58031);

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userid", "this is post"));
			params.add(new BasicNameValuePair("password", "sss"));
			String uriAPI = httpurl + port + "/userlogin.asp";
			HttpUtil sss = new HttpUtil();

			String ss = sss.HttpPost1(uriAPI, params);
			Message msg = Message.obtain();
			msg.obj = ss;
			msg.what = 1;
			handler.sendMessage(msg);
		}

	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				String ss = msg.obj.toString();
				if (ss.equals("ok")) {
					Toast.makeText(RegActivity.this,
							getString(R.string.login_success),
							Toast.LENGTH_LONG).show();
				} else if (ss.equals("none")) {
					Toast.makeText(RegActivity.this,
							getString(R.string.login_none), Toast.LENGTH_LONG)
							.show();
				} else if (ss.equals("error")) {
					Toast.makeText(RegActivity.this,
							getString(R.string.login_error), Toast.LENGTH_LONG)
							.show();
				}
			}
		}
	};
}
