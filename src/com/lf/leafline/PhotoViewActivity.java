package com.lf.leafline;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.*;

public class PhotoViewActivity extends Activity {
	private String fileuri;
	private int finish;
	private ImageView imageview;
	private Button mbtn;
	private Button mbtn_setting;
	private Button mbtn_back;
	private ProgressDialog waituploading;
	private ProgressDialog waitdelete;
	public String actionUrl = "198.11.175.110";
	public String httpurl = "198.11.175.110";
	private int Port = 8508;
	private final int IS_FINISH = 1;
	private int nameid;
	private CodetoName codetoname = new CodetoName();
	private String ttt = null;
	private String ttt1 = null;
	private String ttt2 = null;
	private String ttt3 = null;
	private String ttt4 = null;
	private String ttt5 = null;
	private String ttt6 = null;
	private String name = "";
	private int port2 = 80;
	private TextView mtext2;
	private ProgressDialog waitupload;
	private int leafid = 0;
	private Bitmap bbb = null;
	private TextView LocationResult;
	private EditText porttext2;
	private EditText uritext;
	private EditText porttext;
	private EditText textport2;
	private static final String WX_APP_ID = "wxba5a02027cd5b0aa";
	private IWXAPI wx_api;

	private void regToWX() {
		wx_api = WXAPIFactory.createWXAPI(this, WX_APP_ID, true);
		wx_api.registerApp(WX_APP_ID);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photoviewlayout);

		imageview = (ImageView) findViewById(R.id.photoview);
		mbtn = (Button) findViewById(R.id.upload);
		LocationResult = (TextView) this.findViewById(R.id.location);
		mbtn_setting = (Button) findViewById(R.id.setting);
		mbtn_back = (Button) findViewById(R.id.back);
		SharedPreferences sharedata = getSharedPreferences("data",
				Context.MODE_PRIVATE);

		actionUrl = sharedata.getString("ip1", actionUrl);
		httpurl = sharedata.getString("ip2", httpurl);
		Port = sharedata.getInt("port1", Port);
		port2 = sharedata.getInt("port2", port2);

		initEvent();
		regToWX();
		// mSelectedImage = getIntent().getStringArrayListExtra("ListString");
		if (getIntent().getStringExtra("ListString") == null) {
			String ssss = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/leaf";
			File dir = new File(ssss);
			if (!dir.exists()) {
				dir.mkdirs();

			}
			SimpleDateFormat timenow = new SimpleDateFormat("yyyyMMddHHmmss");
			Date curDate = new Date(System.currentTimeMillis());
			String sss = timenow.format(curDate);

			String sFileFullPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/leaf/leaf" + sss + ".jpg";
			// uploadname = "test" + sss + ".jpg";
			fileuri = sFileFullPath;
			File file = new File(sFileFullPath);
			finish = 0;
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			startActivityForResult(intent, 1);
		} else {
			fileuri = getIntent().getStringExtra("ListString");
			BitmapWorkerTask asyncTask = new BitmapWorkerTask(imageview);
			asyncTask.execute(fileuri);
		}
	}

	class BitmapWorkTask extends AsyncTask<String, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private String data = null;

		public BitmapWorkTask(ImageView imageView) {
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			data = params[0];
			Bitmap bitmap = BitmapFactory.decodeFile(data);
			Bitmap newbitmap = null;
			// int bwidth = bitmap.getWidth();

			if (bitmap.getHeight() > bitmap.getWidth()) {
				// float ff = 300 / bwidth;
				newbitmap = Bitmap.createScaledBitmap(bitmap, 300, 400, true);
			} else {
				// float ff = 400 / bwidth;
				newbitmap = Bitmap.createScaledBitmap(bitmap, 400, 300, true);
				Matrix m = new Matrix();
				int width = newbitmap.getWidth();
				int height = newbitmap.getHeight();
				m.setRotate(90);
				newbitmap = Bitmap.createBitmap(newbitmap, 0, 0, width, height,
						m, true);

			}
			bitmap = newbitmap;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, 100, bos);
			byte[] bitmapdata = bos.toByteArray();
			fileuri = fileuri + ".jpg";
			File f = new File(fileuri);
			try {
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(bitmapdata);

			} catch (FileNotFoundException e) {
				Intent intent = new Intent(PhotoViewActivity.this,
						takephoto.class);
				startActivity(intent);
				PhotoViewActivity.this.finish();
				e.printStackTrace();
			} catch (IOException e) {
				Intent intent = new Intent(PhotoViewActivity.this,
						takephoto.class);
				startActivity(intent);
				PhotoViewActivity.this.finish();
				e.printStackTrace();
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {

			if (imageViewReference != null && result != null) {
				final ImageView imageView = imageViewReference.get();
				if (imageView != null) {
					imageView.setImageBitmap(result);
				}
			}

			finish = 1;
			super.onPostExecute(result);
		}

	}

	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

		private final WeakReference<ImageView> imageViewReference;
		private String data = null;
		private Bitmap map;

		public BitmapWorkerTask(ImageView imageView) {
			// Use a WeakReference to ensure the ImageView can be garbage
			// collected
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			data = params[0];
			Bitmap newbitmap;
			Bitmap map0 = BitmapFactory.decodeFile(data, null);
			int bwidth = map0.getWidth();
			float ff = 300 / (float) (bwidth);
			newbitmap = Bitmap.createScaledBitmap(map0, (int) (bwidth * ff),
					(int) (map0.getHeight() * ff), true);

			if (newbitmap.getHeight() < newbitmap.getWidth()) {
				Matrix m = new Matrix();
				int width = newbitmap.getWidth();
				int height = newbitmap.getHeight();
				m.setRotate(90);
				newbitmap = Bitmap.createBitmap(newbitmap, 0, 0, width, height,
						m, true);
			}

			// Bitmap bitmap0 = BitmapFactory.decodeFile(data);
			Bitmap bitmap = BitmapFactory.decodeFile(data);

			bitmap = newbitmap;

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, 100, bos);
			byte[] bitmapdata = bos.toByteArray();
			fileuri = fileuri + ".jpg";
			File f = new File(fileuri);
			try {
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(bitmapdata);

			} catch (FileNotFoundException e) {
				Intent intent = new Intent(PhotoViewActivity.this,
						takephoto.class);
				startActivity(intent);
				PhotoViewActivity.this.finish();
				e.printStackTrace();
			} catch (IOException e) {
				Intent intent = new Intent(PhotoViewActivity.this,
						takephoto.class);
				startActivity(intent);
				PhotoViewActivity.this.finish();
				e.printStackTrace();
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (imageViewReference != null && bitmap != null) {
				final ImageView imageView = imageViewReference.get();
				if (imageView != null) {
					imageView.setImageBitmap(bitmap);
				}
			}

			finish = 1;
			super.onPostExecute(bitmap);
		}

	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromFile(String filename,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filename, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filename, options);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				BitmapWorkTask asyncTask = new BitmapWorkTask(imageview);
				asyncTask.execute(fileuri);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void initEvent() {
		mbtn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PhotoViewActivity.this,
						takephoto.class);
				startActivity(intent);
				PhotoViewActivity.this.finish();

			}
		});
		mbtn_setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				settting();
			}
		});
		mbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (fileuri != null && finish == 1) {
					waituploading = new ProgressDialog(PhotoViewActivity.this);
					waituploading.setTitle(getString(R.string.uploadtitle));
					waituploading.setMessage(getString(R.string.uploadtext));
					// waituploading.setCancelable(false);
					String result = null;
					new Thread(new MyThread()).start();
					waituploading.show();
					waituploading.setCanceledOnTouchOutside(false);

				} else {
					Toast.makeText(PhotoViewActivity.this,
							getString(R.string.nullpicture), Toast.LENGTH_LONG)
							.show();
				}
			}
		});
	}

	public class MyThread implements Runnable {
		public void run() {
			String result = null;

			FileInputStream fileInputStream;
			DataInputStream netInputStream;
			DataOutputStream netOutputStream;
			Socket sc;
			int fileLength = 0;
			byte[] buffer = new byte[1023];
			byte[] readLen = new byte[10];
			byte[] readResult = new byte[4];
			byte[] ress = { '1', '2', '7' };
			int len;
			int result_count = 0;

			File f = new File(fileuri);
			if (f.exists()) {
				fileLength = (int) f.length();
			} else {
				System.out.println("No such file");
				Toast.makeText(PhotoViewActivity.this,
						getString(R.string.nullfile), Toast.LENGTH_LONG).show();

			}

			try {
				fileInputStream = new FileInputStream(fileuri);
				sc = new Socket(actionUrl, Port);
				netInputStream = new DataInputStream(sc.getInputStream());
				netOutputStream = new DataOutputStream(sc.getOutputStream());

				// ///////////////////1.send file length//////////////////////
				netOutputStream.write(Integer.toString(fileLength).getBytes());

				// ///////////////////2. send file///////////////////////////
				while ((len = fileInputStream.read(buffer)) > 0) {
					netOutputStream.write(buffer, 0, len);
					Thread.sleep(40);

				}

				netInputStream.read(readResult);
				ress = Arrays.copyOf(readResult, 3);

				fileInputStream.close();
				netInputStream.close();
				netOutputStream.close();
				sc.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
			Message message = Message.obtain();
			message.obj = ress;
			message.what = IS_FINISH;
			handler.sendMessage(message);

		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == IS_FINISH) {
				byte[] result = (byte[]) msg.obj;
				nameid = codetoname.CodetoName(result);
				leafid = nameid;
				new Thread(new getname()).start();
			} else if (msg.what == 4) {
				new Thread(new uploc()).start();

				waituploading.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						PhotoViewActivity.this);
				Context mContext = PhotoViewActivity.this;
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				View layout = inflater.inflate(R.layout.result, null);
				mtext2 = (TextView) layout.findViewById(R.id.textView1);
				builder.setTitle(getString(R.string.resulttitle));
				// mtext2.setText(name);
				mtext2.setText(name);
				builder.setView(layout);
				builder.setPositiveButton(getString(R.string.sharebutton),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								WXTextObject textobj = new WXTextObject();
								textobj.text = getString(R.string.share_text);
								WXMediaMessage wxmsg = new WXMediaMessage();
								wxmsg.mediaObject = textobj;
								wxmsg.description = textobj.text;
								SendMessageToWX.Req req = new SendMessageToWX.Req();
								req.message = wxmsg;
								wx_api.sendReq(req);

							}
						});
				builder.setNegativeButton(getString(R.string.view_map),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// ClipboardManager cmb = (ClipboardManager)
								// PhotoViewActivity.this
								// .getSystemService(Context.CLIPBOARD_SERVICE);
								//
								// cmb.setText(name.trim());
								// Toast.makeText(PhotoViewActivity.this,
								// getString(R.string.copy_text),
								// Toast.LENGTH_LONG).show();
								Intent intent1 = new Intent(
										PhotoViewActivity.this,
										ViewMapActivity.class);
								SharedPreferences sharedata = getSharedPreferences(
										"data", Context.MODE_PRIVATE);
								String httpurl = sharedata.getString("ip2",
										"198.11.175.110");
								int port2 = sharedata.getInt("port2", 80);
								intent1.putExtra("str", httpurl + ":" + port2
										+ "/map.asp?id=" + nameid);
								startActivity(intent1);
							}
						});
				builder.setNeutralButton(getString(R.string.detail_t),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								waitupload = new ProgressDialog(
										PhotoViewActivity.this);
								waitupload
										.setTitle(getString(R.string.detail_tittle));
								waitupload
										.setMessage(getString(R.string.detail_text));
								// waituploading.setCancelable(false);
								new Thread(new details()).start();
								waitupload.show();
							}
						});

				builder.create();
				builder.show();
			} else if (msg.what == 2) {
				try {
					String result = (String) msg.obj;
					FileInputStream fis = null;
					fis = new FileInputStream(result);
					Bitmap bitmap = null;
					bitmap = BitmapFactory.decodeStream(fis);
					imageview.setImageBitmap(bitmap);
				} catch (FileNotFoundException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}

			} else if (msg.what == 3) {
				waitupload.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						PhotoViewActivity.this);
				Context mContext = PhotoViewActivity.this;
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				View layout = inflater.inflate(R.layout.details, null);
				TextView textname = (TextView) layout
						.findViewById(R.id.textname);
				TextView textinfo = (TextView) layout
						.findViewById(R.id.textinfo);
				ImageView imagebbbb = (ImageView) layout
						.findViewById(R.id.imagebbbbb);
				TextView nameling = (TextView) layout
						.findViewById(R.id.textnamelatin);
				TextView ke = (TextView) layout.findViewById(R.id.textfamily);
				TextView keling = (TextView) layout
						.findViewById(R.id.textfamilylatin);
				TextView shu = (TextView) layout.findViewById(R.id.textgenus);
				TextView shuling = (TextView) layout
						.findViewById(R.id.textgenuslatin);
				builder.setTitle(getString(R.string.introduction_tittle));
				textname.setText(ttt);
				textinfo.setText(ttt1);
				nameling.setText(ttt2);
				ke.setText(ttt3);
				keling.setText(ttt4);
				shu.setText(ttt5);
				shuling.setText(ttt6);

				imagebbbb.setImageBitmap(bbb);

				builder.setView(layout);
				builder.setPositiveButton(
						getString(R.string.introduction_back),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}

						});
				builder.setNegativeButton(getString(R.string.wikipedia),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent();
								intent.setAction(Intent.ACTION_VIEW);
								intent.setData(Uri
										.parse("http://zh.wikipedia.org/"
												+ name));

								startActivity(intent);
							}
						});

				builder.show();
			} else if (msg.what == 5) {
				waitdelete.dismiss();
				Toast.makeText(PhotoViewActivity.this,
						getString(R.string.delete_finish), Toast.LENGTH_LONG)
						.show();
			}
		}
	};

	public class getname implements Runnable {

		@Override
		public void run() {
			int id = nameid - 1;
			HttpClient httpclient = new DefaultHttpClient();
			String path = "http://" + httpurl + ":" + port2 + "/abi.asp?ID="
					+ id;
			HttpGet httpget = new HttpGet(path);
			try {
				HttpResponse httpresponse = httpclient.execute(httpget);
				if (httpresponse.getStatusLine().getStatusCode() == 200) {
					HttpEntity httpentity = httpresponse.getEntity();
					ttt = EntityUtils.toString(httpentity, "utf-8");
					name = ttt;

				}
			} catch (ClientProtocolException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			Message message = Message.obtain();
			message.what = 4;
			handler.sendMessage(message);

		}

	}

	public class details implements Runnable {

		@Override
		public void run() {
			int id = leafid - 1;
			// int id = 2;
			HttpClient httpclient = new DefaultHttpClient();

			String path = "http://" + httpurl + ":" + port2 + "/abi.asp?ID="
					+ id;
			String path2 = "http://" + httpurl + ":" + port2 + "/abi1.asp?ID="
					+ id;
			String path3 = "http://" + httpurl + ":" + port2 + "/abi2.asp?ID="
					+ id;
			String path4 = "http://" + httpurl + ":" + port2 + "/abi3.asp?ID="
					+ id;
			String path5 = "http://" + httpurl + ":" + port2 + "/abi4.asp?ID="
					+ id;
			String path6 = "http://" + httpurl + ":" + port2 + "/abi5.asp?ID="
					+ id;
			String path7 = "http://" + httpurl + ":" + port2 + "/abi6.asp?ID="
					+ id;
			String path8 = "http://" + httpurl + ":" + port2 + "/abi7.asp?ID="
					+ id;

			HttpGet httpget = new HttpGet(path);
			HttpGet httpget1 = new HttpGet(path2);
			HttpGet httpget2 = new HttpGet(path3);
			HttpGet httpget3 = new HttpGet(path4);
			HttpGet httpget4 = new HttpGet(path5);
			HttpGet httpget5 = new HttpGet(path6);
			HttpGet httpget6 = new HttpGet(path7);
			HttpGet httpget7 = new HttpGet(path8);

			try {
				HttpResponse httpresponse = httpclient.execute(httpget);

				if (httpresponse.getStatusLine().getStatusCode() == 200) {
					HttpEntity httpentity = httpresponse.getEntity();
					ttt = EntityUtils.toString(httpentity, "utf-8");
					HttpResponse httpresponse1 = httpclient.execute(httpget1);
					HttpResponse httpresponse2 = httpclient.execute(httpget2);
					HttpResponse httpresponse3 = httpclient.execute(httpget3);
					HttpResponse httpresponse4 = httpclient.execute(httpget4);
					HttpResponse httpresponse5 = httpclient.execute(httpget5);
					HttpResponse httpresponse6 = httpclient.execute(httpget6);
					HttpResponse httpresponse7 = httpclient.execute(httpget7);

					if (httpresponse1.getStatusLine().getStatusCode() == 200) {
						HttpEntity httpentity1 = httpresponse1.getEntity();
						ttt1 = EntityUtils.toString(httpentity1, "utf-8");
					}
					if (httpresponse3.getStatusLine().getStatusCode() == 200) {
						HttpEntity httpentity3 = httpresponse3.getEntity();
						ttt2 = EntityUtils.toString(httpentity3, "utf-8");
					}
					if (httpresponse4.getStatusLine().getStatusCode() == 200) {
						HttpEntity httpentity4 = httpresponse4.getEntity();
						ttt4 = EntityUtils.toString(httpentity4, "utf-8");
					}
					if (httpresponse5.getStatusLine().getStatusCode() == 200) {
						HttpEntity httpentity5 = httpresponse5.getEntity();
						ttt3 = EntityUtils.toString(httpentity5, "utf-8");
					}
					if (httpresponse6.getStatusLine().getStatusCode() == 200) {
						HttpEntity httpentity6 = httpresponse6.getEntity();
						ttt5 = EntityUtils.toString(httpentity6, "utf-8");
					}
					if (httpresponse7.getStatusLine().getStatusCode() == 200) {
						HttpEntity httpentity7 = httpresponse7.getEntity();
						ttt6 = EntityUtils.toString(httpentity7, "utf-8");
					}
					if (httpresponse2.getStatusLine().getStatusCode() == 200) {
						HttpEntity httpentity2 = httpresponse2.getEntity();
						byte[] data = EntityUtils.toByteArray(httpentity2);
						bbb = BitmapFactory.decodeByteArray(data, 0,
								data.length);
					}
				}

			} catch (ClientProtocolException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			Message message = Message.obtain();
			message.what = 3;
			handler.sendMessage(message);

		}
	}

	public class uploc implements Runnable {
		@Override
		public void run() {
			/**
			 ** 发送位置信息
			 **
			 **/
			String loc = LocationResult.getText().toString();
			HttpClient httpclient = new DefaultHttpClient();
			int leafidd = leafid + 1;
			String path = "http://" + httpurl + ":" + port2 + "/loc.asp?text="
					+ loc + "," + leafidd;
			HttpGet httpget = new HttpGet(path);
			HttpResponse httpresponse;
			try {
				httpresponse = httpclient.execute(httpget);
				if (httpresponse.getStatusLine().getStatusCode() == 200) {
					HttpEntity httpentity1 = httpresponse.getEntity();
					String ttttt = EntityUtils.toString(httpentity1, "utf-8");
				}
			} catch (ClientProtocolException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}

			// teeet=httpresponse.getStatusLine().getStatusCode()+"";

		}
	}

	public class DelDir implements Runnable {

		@Override
		public void run() {
			String ssss = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/leaf";
			File dir = new File(ssss);
			if (dir == null || !dir.exists() || !dir.isDirectory())
				return;
			for (File file : dir.listFiles()) {
				if (file.isFile())
					file.delete(); // 删除所有文件
				else if (file.isDirectory())
					;

			}
			Message message = Message.obtain();

			message.what = 5;
			handler.sendMessage(message);

		}
	}

	private void settting() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				PhotoViewActivity.this);
		Context mContext = PhotoViewActivity.this;
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.settings, null);
		// View layout = inflater.inflate(R.layout.settings, null);
		textport2 = (EditText) layout.findViewById(R.id.editTextPort2);
		uritext = (EditText) layout.findViewById(R.id.editTextIP);
		final Button btndel = (Button) layout.findViewById(R.id.btn_delete_dir);
		porttext = (EditText) layout.findViewById(R.id.editTextPort);
		porttext2 = (EditText) layout.findViewById(R.id.editTextIP2);
		builder.setTitle(getString(R.string.action_settings));
		// EditText textip=new EditText(MainActivity.this);
		textport2.setText(String.valueOf(port2));
		uritext.setText(actionUrl);
		porttext.setText(String.valueOf(Port));
		porttext2.setText(httpurl);
		btndel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				waitdelete = new ProgressDialog(PhotoViewActivity.this);
				waitdelete.setTitle(getString(R.string.loading));
				waitdelete.setMessage(getString(R.string.deleting));
				waitdelete.show();
				waitdelete.setCanceledOnTouchOutside(false);
				new Thread(new DelDir()).start();
			}
		});
		// mtext.setText(fileuri);
		builder.setView(layout);
		builder.setPositiveButton(getString(R.string.ok_button),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						port2 = Integer
								.parseInt(textport2.getText().toString());
						httpurl = porttext2.getText().toString();
						actionUrl = uritext.getText().toString();
						Port = Integer.parseInt(porttext.getText().toString());
						SharedPreferences sharedata = getSharedPreferences(
								"data", Context.MODE_PRIVATE);
						Editor editor = sharedata.edit();
						editor.putString("ip1", actionUrl);
						editor.putString("ip2", httpurl);
						editor.putInt("port1", Port);
						editor.putInt("port2", port2);
						editor.commit();
						// mtext.setText(actionUrl);
					}
				});
		builder.setNegativeButton(getString(R.string.cancel_button),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		builder.create();
		builder.show();
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(PhotoViewActivity.this, MainActivity.class);
		startActivity(intent);
		PhotoViewActivity.this.finish();
	}
}
