package com.lf.leafline;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lf.leafline.ListImageDirPopupWindow.OnImageDirSelected;
import com.lf.leafline.bean.ImageFloder;

public class CopyOftakephoto extends Activity implements OnImageDirSelected {
	private ProgressDialog mProgressDialog;

	/**
	 * 存储文件夹中的图片数量
	 */
	private int mPicsSize;
	/**
	 * 图片数量最多的文件夹
	 */
	private File mImgDir;
	/**
	 * 所有的图片
	 */
	private List<String> mImgs;
	private List<String> mSelectedImage;

	private GridView mGirdView;
	private MyAdapter mAdapter;
	/**
	 * 临时的辅助类，用于防止同一个文件夹的多次扫描
	 */
	private HashSet<String> mDirPaths = new HashSet<String>();

	/**
	 * 扫描拿到所有的图片文件夹
	 */
	private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();

	private RelativeLayout mBottomLy;
	private int fffff;
	private TextView mChooseDir;
	private TextView mImageCount;
	private Button mbtn;
	int totalCount = 0;

	private int mScreenHeight;

	private ListImageDirPopupWindow mListImageDirPopupWindow;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mProgressDialog.dismiss();
			// 为View绑定数据
			data2View();
			// 初始化展示文件夹的popupWindw
			initListDirPopupWindw();
		}
	};

	/**
	 * 为View绑定数据
	 */
	private void data2View() {
		if (mImgDir == null) {
			Toast.makeText(getApplicationContext(),
					getString(R.string.none_pictures), Toast.LENGTH_SHORT)
					.show();
			return;
		}
		String[] temp1 = { "takephoto" };
		String[] temp = mImgDir.list();
		String[] temp2 = new String[temp1.length + temp.length];
		for (int i = 0; i < temp1.length; i++) {
			temp2[i] = temp1[i];
		}
		for (int i = 0; i < temp.length; i++) {
			temp2[temp1.length + i] = temp[i];
		}
		mImgs = Arrays.asList(temp2);

		// mImgs
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter = new MyAdapter(getApplicationContext(), mImgs,
				R.layout.grid_item, mImgDir.getAbsolutePath());
		mGirdView.setAdapter(mAdapter);
		mImageCount.setText(totalCount + "" /* + "张" */);
	};

	/**
	 * 初始化展示文件夹的popupWindw
	 */
	private void initListDirPopupWindw() {
		mListImageDirPopupWindow = new ListImageDirPopupWindow(
				LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
				mImageFloders, LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.list_dir, null));

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		// 设置选择文件夹的回调
		mListImageDirPopupWindow.setOnImageDirSelected(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.takephoto);

		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;
		if (getIntent().getStringExtra("lll") == null) {
			fffff = 0;
		} else {
			fffff = 1;
		}
		initView();
		getImages();
		initEvent();
		initQueding();
		// initPaizhao();
		// mGirdView.setOnItemClickListener(this);

	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, getString(R.string.no_external_storage),
					Toast.LENGTH_SHORT).show();
			return;
		}
		// 显示进度条
		mProgressDialog = ProgressDialog.show(this, null,
				getString(R.string.loading));

		new Thread(new Runnable() {
			@Override
			public void run() {

				String firstImage = null;

				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = CopyOftakephoto.this
						.getContentResolver();

				// 只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" },
						MediaStore.Images.Media.DATE_MODIFIED);

				Log.e("TAG", mCursor.getCount() + "");
				while (mCursor.moveToNext()) {
					// 获取图片的路径
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));

					Log.e("TAG", path);
					// 拿到第一张图片的路径
					if (firstImage == null)
						firstImage = path;
					// 获取该图片的父路径名
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();
					ImageFloder imageFloder = null;
					// 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
					if (mDirPaths.contains(dirPath)) {
						continue;
					} else {
						mDirPaths.add(dirPath);
						// 初始化imageFloder
						imageFloder = new ImageFloder();
						imageFloder.setDir(dirPath);
						imageFloder.setFirstImagePath(path);
					}

					int picSize = parentFile.list(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String filename) {
							if (filename.endsWith(".jpg")
									|| filename.endsWith(".png")
									|| filename.endsWith(".jpeg"))
								return true;
							return false;
						}
					}).length;
					totalCount += picSize;

					imageFloder.setCount(picSize);
					mImageFloders.add(imageFloder);

					if (picSize > mPicsSize) {
						mPicsSize = picSize;
						mImgDir = parentFile;
					}
				}
				mCursor.close();

				// 扫描完成，辅助的HashSet也就可以释放内存了
				mDirPaths = null;

				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(0x110);

			}
		}).start();

	}

	/**
	 * 初始化View
	 */
	private void initView() {
		mGirdView = (GridView) findViewById(R.id.id_gridView);
		mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
		mImageCount = (TextView) findViewById(R.id.id_total_count);
		mbtn = (Button) findViewById(R.id.btn_queding);

		mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);

	}

	private void initQueding() {
		mbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mAdapter.mSelectedImage.size() == 0) {
					Toast.makeText(getApplicationContext(),
							getString(R.string.select_null_picture),
							Toast.LENGTH_SHORT).show();
				} else {
					mSelectedImage = mAdapter.mSelectedImage;
					if (fffff == 1) {
						Intent intent = new Intent(CopyOftakephoto.this,
								FayanActivity.class);
						intent.putExtra("ListString", mSelectedImage.get(0));
						startActivity(intent);
						CopyOftakephoto.this.finish();
					} else if (mSelectedImage.get(0).equals("takephoto")) {
						Intent intent = new Intent(CopyOftakephoto.this,
								FayanActivity.class);
						intent.putExtra("ListString", "takephoto");
						startActivity(intent);
						CopyOftakephoto.this.finish();
					} else {
						Intent intent = new Intent(CopyOftakephoto.this,
								FayanActivity.class);
						intent.putExtra("ListString", mSelectedImage.get(0));
						startActivity(intent);
						CopyOftakephoto.this.finish();
					}
					// Toast.makeText(getApplicationContext(),
					// mSelectedImage.get(0), Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void initEvent() {
		/**
		 * 为底部的布局设置点击事件，弹出popupWindow
		 */
		mBottomLy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListImageDirPopupWindow
						.setAnimationStyle(R.style.anim_popup_dir);
				mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = .3f;
				getWindow().setAttributes(lp);
			}
		});
	}

	@Override
	public void selected(ImageFloder floder) {

		mImgDir = new File(floder.getDir());
		String[] temp1 = { "takephoto" };
		String[] temp = mImgDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".jpg") || filename.endsWith(".png")
						|| filename.endsWith(".jpeg"))
					return true;
				return false;
			}
		});
		String[] temp2 = new String[temp1.length + temp.length];
		for (int i = 0; i < temp1.length; i++) {
			temp2[i] = temp1[i];
		}
		for (int i = 0; i < temp.length; i++) {
			temp2[temp1.length + i] = temp[i];
		}
		mImgs = Arrays.asList(temp2);

		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter = new MyAdapter(getApplicationContext(), mImgs,
				R.layout.grid_item, mImgDir.getAbsolutePath());
		mGirdView.setAdapter(mAdapter);
		// mAdapter.notifyDataSetChanged();
		mImageCount.setText(floder.getCount() + "" /* + "张" */);
		mChooseDir.setText(floder.getName());
		mListImageDirPopupWindow.dismiss();

	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(CopyOftakephoto.this, MainActivity.class);
		startActivity(intent);
		CopyOftakephoto.this.finish();
	}

}
