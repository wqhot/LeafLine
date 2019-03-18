package com.lf.leafline;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.lf.leafline.MainActivity.PlaceholderFragment.getMessage;
import com.lf.leafline.utils.DataJiexi;
import com.lf.leafline.utils.HttpUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewMsgActivity extends Activity {
	private ImageView image;
	private TextView name;
	private TextView time;
	private TextView main_context;
	private ImageView image_main;
	private LeafMessage leafmessage;
	private CommentsMessage comments;
	private CommentsAdapter com_adapter;
	private Button mbtn_yes;
	private Button mbtn_no;
	private Button mbtn_send;
	private ListView mListView;
	private EditText mContext;
	private ArrayList<String> alist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_msg);

		image = (ImageView) findViewById(R.id.v_user_touxiang);
		name = (TextView) findViewById(R.id.v_user_name);
		time = (TextView) findViewById(R.id.v_user_time);
		main_context = (TextView) findViewById(R.id.v_text_context);
		image_main = (ImageView) findViewById(R.id.v_image_main);
		mListView = (ListView) findViewById(R.id.v_pinglun);
		mbtn_yes = (Button) findViewById(R.id.btn_yes);
		mbtn_no = (Button) findViewById(R.id.btn_no);
		mbtn_send = (Button) findViewById(R.id.v_btn_send);
		mContext = (EditText) findViewById(R.id.v_edit_neirong);
		com_adapter = new CommentsAdapter(this);
		ArrayList list = getIntent().getStringArrayListExtra("ListString");
		leafmessage = new LeafMessage();
		leafmessage.setid(Integer.parseInt(list.get(0).toString()));
		leafmessage.setusername(list.get(1).toString());
		leafmessage.setusertime(list.get(2).toString());
		leafmessage.setusertouxiang(list.get(3).toString());
		leafmessage.setimage(list.get(4).toString());
		leafmessage.setcontext(list.get(5).toString());
		name.setText(leafmessage.getusername());
		time.setText(leafmessage.getusertime());
		main_context.setText(leafmessage.getcontext());
		AssetManager assetManager = getAssets();
		try {
			InputStream in = assetManager.open("img/"
					+ leafmessage.getusertouxiang() + ".png");
			Bitmap bmp = BitmapFactory.decodeStream(in);
			image.setImageBitmap(bmp);
			// InputStream inn=new InputStrea

		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		initEvent();
		new Thread(new setView()).start();

	}

	public class setView implements Runnable {

		@Override
		public void run() {
			// download form Internet

			// int num = 5;// 评论数
			SharedPreferences sharedata = getSharedPreferences("data",
					Context.MODE_PRIVATE);

			String httpurl = sharedata.getString("ip2", "198.11.175.110");
			int port = sharedata.getInt("port2", 80);
			String uriAPI = "http://" + httpurl + ":" + port
					+ "/viewleafmessage.asp";
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id", "" + leafmessage.getid()));
			HttpUtil sss = new HttpUtil();
			String str = sss.HttpPost1(uriAPI, params);
			if (str.length() != 0) {
				List<CommentsMessage> returnlist = new ArrayList<CommentsMessage>();
				DataJiexi datajiexi = new DataJiexi();
				returnlist = datajiexi.MessageJiexi(str);
				for (int i = 0; i < returnlist.size(); i++) {
					com_adapter.addList(returnlist.get(i));
				}
			}
			// download form Internet

			if (FileExist(leafmessage.getimage())) {
				Bitmap bp = BitmapFactory.decodeFile(leafmessage.getimage());
				image_main.setImageBitmap(bp);
			}

			Message message = Message.obtain();
			message.what = 1;
			handler.sendMessage(message);
			new Thread(new setView()).start();
		}

	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {

				mListView.setAdapter(com_adapter);

			} else if (msg.what == 2) {
				String ss = msg.obj.toString();
				if (ss.equals("NoResponse")) {
					Toast.makeText(ViewMsgActivity.this,
							getString(R.string.connect_error),
							Toast.LENGTH_LONG).show();
				} else if (ss.equals("error")) {
					Toast.makeText(ViewMsgActivity.this,
							getString(R.string.login_error), Toast.LENGTH_LONG)
							.show();
				} else {
					Toast.makeText(ViewMsgActivity.this,
							getString(R.string.com_success), Toast.LENGTH_LONG)
							.show();

				}
			}
		}
	};

	private void initEvent() {
		mbtn_yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences sharedata = getSharedPreferences("login",
						Activity.MODE_PRIVATE);
				Boolean islogin = sharedata.getBoolean("IsLogined", false);
				if (!islogin) {
					Toast.makeText(ViewMsgActivity.this,
							getString(R.string.login_first), Toast.LENGTH_LONG)
							.show();
					Intent intent = new Intent(ViewMsgActivity.this,
							LoginActivity.class);
					startActivity(intent);
					ViewMsgActivity.this.finish();
				}
			}
		});
		mbtn_no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences sharedata = getSharedPreferences("login",
						Activity.MODE_PRIVATE);
				Boolean islogin = sharedata.getBoolean("IsLogined", false);
				if (!islogin) {
					Toast.makeText(ViewMsgActivity.this,
							getString(R.string.login_first), Toast.LENGTH_LONG)
							.show();
					Intent intent = new Intent(ViewMsgActivity.this,
							LoginActivity.class);
					startActivity(intent);
					ViewMsgActivity.this.finish();
				}
			}
		});
		mbtn_send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences sharedata = getSharedPreferences("login",
						Activity.MODE_PRIVATE);
				Boolean islogin = sharedata.getBoolean("IsLogined", false);
				if (!islogin) {
					Toast.makeText(ViewMsgActivity.this,
							getString(R.string.login_first), Toast.LENGTH_LONG)
							.show();
					Intent intent = new Intent(ViewMsgActivity.this,
							LoginActivity.class);
					startActivity(intent);
					ViewMsgActivity.this.finish();
				} else {
					new Thread(new MyThread()).start();
				}
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
			SharedPreferences read = getSharedPreferences("login", MODE_PRIVATE);
			String logname = read.getString("username", "");
			String logpswd = read.getString("password", "");
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userid", logname));
			params.add(new BasicNameValuePair("password", logpswd));
			params.add(new BasicNameValuePair("context", mContext.getText()
					.toString()));
			params.add(new BasicNameValuePair("comms", leafmessage.getid() + ""));
			String uriAPI = "http://" + httpurl + ":" + port
					+ "/addnewcomms.asp";
			HttpUtil sss = new HttpUtil();

			String ss = sss.HttpPost1(uriAPI, params);
			Message msg = Message.obtain();
			msg.obj = ss;
			msg.what = 2;
			handler.sendMessage(msg);
		}
	}

	private boolean FileExist(String str) {
		try {
			File f = new File(str);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;

	}
}
