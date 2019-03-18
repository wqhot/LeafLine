package com.lf.leafline;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.lf.leafline.utils.HttpUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class LoginActivity extends Activity {
	private Button btn_login;
	private Button btn_reg;
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
		btn_reg = (Button) findViewById(R.id.btn_reg);
		SharedPreferences sharedata = getSharedPreferences("login",
				Context.MODE_PRIVATE);

		String user = sharedata.getString("username", "");
		mUsername.setText(user);
		String pass = sharedata.getString("password", "");
		mPassword.setText(pass);
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
					editor.commit();
				}
				new Thread(new MyThread()).start();
			}
		});
		btn_reg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (chbx_rem.isChecked()) {
					SharedPreferences sharedata = getSharedPreferences("login",
							Activity.MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedata.edit();
					editor.putString("username", mUsername.getText().toString());
					editor.putString("password", mPassword.getText().toString());
					editor.commit();
				}
				new Thread(new RegThread()).start();
			}
		});

	}

	public class RegThread implements Runnable {

		@Override
		public void run() {
			SharedPreferences sharedata = getSharedPreferences("data",
					Context.MODE_PRIVATE);

			String httpurl = sharedata.getString("ip2", "192.168.137.1");
			int port = sharedata.getInt("port2", 58031);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userid", mUsername.getText()
					.toString()));
			params.add(new BasicNameValuePair("password", mPassword.getText()
					.toString()));
			String uriAPI = "http://" + httpurl + ":" + port
					+ "/addnewuser.asp";
			HttpUtil sss = new HttpUtil();

			String ss = sss.HttpPost1(uriAPI, params);
			Message msg = Message.obtain();
			msg.obj = ss;
			msg.what = 2;
			handler.sendMessage(msg);
		}

	}

	public class MyThread implements Runnable {

		@Override
		public void run() {
			SharedPreferences sharedata = getSharedPreferences("data",
					Context.MODE_PRIVATE);

			String httpurl = sharedata.getString("ip2", "192.168.137.1");
			int port = sharedata.getInt("port2", 58031);

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userid", mUsername.getText()
					.toString()));
			params.add(new BasicNameValuePair("password", mPassword.getText()
					.toString()));
			String uriAPI = "http://" + httpurl + ":" + port + "/userlogin.asp";
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
					Toast.makeText(LoginActivity.this,
							getString(R.string.login_success),
							Toast.LENGTH_LONG).show();
					SharedPreferences sharedata = getSharedPreferences("login",
							Activity.MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedata.edit();
					editor.putBoolean("IsLogined", true);
					editor.commit();
					Intent intent = new Intent(LoginActivity.this,
							MainActivity.class);
					startActivity(intent);
					LoginActivity.this.finish();
				} else if (ss.equals("none")) {
					Toast.makeText(LoginActivity.this,
							getString(R.string.login_none), Toast.LENGTH_LONG)
							.show();
					SharedPreferences sharedata = getSharedPreferences("login",
							Activity.MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedata.edit();
					editor.putBoolean("IsLogined", false);
					editor.commit();

				} else if (ss.equals("error")) {
					Toast.makeText(LoginActivity.this,
							getString(R.string.login_error), Toast.LENGTH_LONG)
							.show();
					SharedPreferences sharedata = getSharedPreferences("login",
							Activity.MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedata.edit();
					editor.putBoolean("IsLogined", false);
					editor.commit();
				} else if (ss.equals("NoResponse")) {
					Toast.makeText(LoginActivity.this,
							getString(R.string.connect_error),
							Toast.LENGTH_LONG).show();
					SharedPreferences sharedata = getSharedPreferences("login",
							Activity.MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedata.edit();
					editor.putBoolean("IsLogined", false);
					editor.commit();
				}
			} else if (msg.what == 2) {
				String ss = msg.obj.toString();
				if (ss.equals("ok")) {
					Toast.makeText(LoginActivity.this,
							getString(R.string.reg_success), Toast.LENGTH_LONG)
							.show();
					SharedPreferences sharedata = getSharedPreferences("login",
							Activity.MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedata.edit();
					editor.putBoolean("IsLogined", true);
					editor.commit();
				} else if (ss.equals("existed")) {
					Toast.makeText(LoginActivity.this,
							getString(R.string.reg_error), Toast.LENGTH_LONG)
							.show();
					SharedPreferences sharedata = getSharedPreferences("login",
							Activity.MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedata.edit();
					editor.putBoolean("IsLogined", false);
					editor.commit();
				}
			}
		}
	};
}
