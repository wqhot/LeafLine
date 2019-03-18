package com.lf.leafline;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lf.leafline.PhotoViewActivity.BitmapWorkTask;
import com.lf.leafline.PhotoViewActivity.BitmapWorkerTask;
import com.lf.leafline.utils.HttpUtil;

public class FayanActivity extends Activity {
	private EditText comment;
	private ImageView image;
	private Button btn;
	private String str;
	private String fileuri;
	private int finish = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upnewimage);
		SharedPreferences sharedata = getSharedPreferences("login",
				Activity.MODE_PRIVATE);
		Boolean islogin = sharedata.getBoolean("IsLogined", false);
		if (!islogin) {
			Toast.makeText(FayanActivity.this, getString(R.string.login_first),
					Toast.LENGTH_LONG).show();
			Intent intent = new Intent(FayanActivity.this, LoginActivity.class);
			startActivity(intent);
			FayanActivity.this.finish();
		}
		image = (ImageView) findViewById(R.id.image_selected);
		comment = (EditText) findViewById(R.id.context_input);
		btn = (Button) findViewById(R.id.btn_ok);

		if (getIntent().getStringExtra("ListString") == null) {

		} else {
			if (getIntent().getStringExtra("ListString").equals("takephoto")) {
				String ssss = Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/leaf";
				File dir = new File(ssss);
				if (!dir.exists()) {
					dir.mkdirs();

				}
				SimpleDateFormat timenow = new SimpleDateFormat(
						"yyyyMMddHHmmss");
				Date curDate = new Date(System.currentTimeMillis());
				String sss = timenow.format(curDate);

				String sFileFullPath = Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ "/leaf/leaf" + sss + ".jpg";
				// uploadname = "test" + sss + ".jpg";
				fileuri = sFileFullPath;
				File file = new File(sFileFullPath);
				finish = 0;
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
				startActivityForResult(intent, 1);
			} else {
				fileuri = getIntent().getStringExtra("ListString");
				BitmapWorkerTask asyncTask = new BitmapWorkerTask(image);
				asyncTask.execute(fileuri);
			}
		}
		initEvent();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				BitmapWorkTask asyncTask = new BitmapWorkTask(image);
				asyncTask.execute(fileuri);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
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
			if (bitmap.getHeight() > bitmap.getWidth()) {
				newbitmap = Bitmap.createScaledBitmap(bitmap, 600, 800, true);
			} else {
				newbitmap = Bitmap.createScaledBitmap(bitmap, 800, 600, true);
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
			File f = new File(fileuri);
			try {
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(bitmapdata);

			} catch (FileNotFoundException e) {
				Intent intent = new Intent(FayanActivity.this, takephoto.class);
				startActivity(intent);
				FayanActivity.this.finish();
				e.printStackTrace();
			} catch (IOException e) {
				Intent intent = new Intent(FayanActivity.this, takephoto.class);
				startActivity(intent);
				FayanActivity.this.finish();
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

		public BitmapWorkerTask(ImageView imageView) {
			// Use a WeakReference to ensure the ImageView can be garbage
			// collected
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			data = params[0];
			Bitmap map = decodeSampledBitmapFromFile(data, 600, 800);
			if (map.getHeight() < map.getWidth()) {
				Matrix m = new Matrix();
				int width = map.getWidth();
				int height = map.getHeight();
				m.setRotate(90);
				map = Bitmap.createBitmap(map, 0, 0, width, height, m, true);
			}
			return map;
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

	private void initEvent() {
		image.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FayanActivity.this,
						CopyOftakephoto.class);
				intent.putExtra("lll", 1);
				startActivity(intent);
			}
		});
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new Thread(new MyThread()).start();
			}
		});

	}

	public class MyThread implements Runnable {

		@Override
		public void run() {
			SharedPreferences sharedata = getSharedPreferences("data",
					Context.MODE_PRIVATE);
			String httpurl = sharedata.getString("ip2", "198.11.175.110");
			int port = sharedata.getInt("port2", 80);
			SharedPreferences read = getSharedPreferences("login", MODE_PRIVATE);
			String logname = read.getString("username", "");
			String logpswd = read.getString("password", "");
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userid", logname));
			params.add(new BasicNameValuePair("password", logpswd));
			params.add(new BasicNameValuePair("context", comment.getText()
					.toString()));
			String uriAPI = "http://" + httpurl + ":" + port
					+ "/addnewtable.asp";
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
				if (ss.equals("NoResponse")) {
					Toast.makeText(FayanActivity.this,
							getString(R.string.connect_error),
							Toast.LENGTH_LONG).show();
				} else if (ss.equals("error")) {
					Toast.makeText(FayanActivity.this,
							getString(R.string.login_error), Toast.LENGTH_LONG)
							.show();
				} else {
					Toast.makeText(FayanActivity.this,
							getString(R.string.com_success), Toast.LENGTH_LONG)
							.show();
					Intent intent = new Intent(FayanActivity.this,
							MainActivity.class);
					startActivity(intent);
					FayanActivity.this.finish();
				}
			}
		}
	};

}
