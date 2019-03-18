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

public class takephoto extends Activity implements OnImageDirSelected {
	private ProgressDialog mProgressDialog;

	/**
	 * �洢�ļ����е�ͼƬ����
	 */
	private int mPicsSize;
	/**
	 * ͼƬ���������ļ���
	 */
	private File mImgDir;
	/**
	 * ���е�ͼƬ
	 */
	private List<String> mImgs;
	private List<String> mSelectedImage;

	private GridView mGirdView;
	private MyAdapter mAdapter;
	/**
	 * ��ʱ�ĸ����࣬���ڷ�ֹͬһ���ļ��еĶ��ɨ��
	 */
	private HashSet<String> mDirPaths = new HashSet<String>();

	/**
	 * ɨ���õ����е�ͼƬ�ļ���
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
			// ΪView������
			data2View();
			// ��ʼ��չʾ�ļ��е�popupWindw
			initListDirPopupWindw();
		}
	};

	/**
	 * ΪView������
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
		 * ���Կ����ļ��е�·����ͼƬ��·���ֿ����棬����ļ������ڴ�����ģ�
		 */
		mAdapter = new MyAdapter(getApplicationContext(), mImgs,
				R.layout.grid_item, mImgDir.getAbsolutePath());
		mGirdView.setAdapter(mAdapter);
		mImageCount.setText(totalCount + "" /* + "��" */);
	};

	/**
	 * ��ʼ��չʾ�ļ��е�popupWindw
	 */
	private void initListDirPopupWindw() {
		mListImageDirPopupWindow = new ListImageDirPopupWindow(
				LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
				mImageFloders, LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.list_dir, null));

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// ���ñ�����ɫ�䰵
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		// ����ѡ���ļ��еĻص�
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
	 * ����ContentProviderɨ���ֻ��е�ͼƬ���˷��������������߳��� ���ͼƬ��ɨ�裬���ջ��jpg�����Ǹ��ļ���
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, getString(R.string.no_external_storage),
					Toast.LENGTH_SHORT).show();
			return;
		}
		// ��ʾ������
		mProgressDialog = ProgressDialog.show(this, null,
				getString(R.string.loading));

		new Thread(new Runnable() {
			@Override
			public void run() {

				String firstImage = null;

				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = takephoto.this
						.getContentResolver();

				// ֻ��ѯjpeg��png��ͼƬ
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" },
						MediaStore.Images.Media.DATE_MODIFIED);

				Log.e("TAG", mCursor.getCount() + "");
				while (mCursor.moveToNext()) {
					// ��ȡͼƬ��·��
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));

					Log.e("TAG", path);
					// �õ���һ��ͼƬ��·��
					if (firstImage == null)
						firstImage = path;
					// ��ȡ��ͼƬ�ĸ�·����
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();
					ImageFloder imageFloder = null;
					// ����һ��HashSet��ֹ���ɨ��ͬһ���ļ��У���������жϣ�ͼƬ�����������൱�ֲ���~~��
					if (mDirPaths.contains(dirPath)) {
						continue;
					} else {
						mDirPaths.add(dirPath);
						// ��ʼ��imageFloder
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

				// ɨ����ɣ�������HashSetҲ�Ϳ����ͷ��ڴ���
				mDirPaths = null;

				// ֪ͨHandlerɨ��ͼƬ���
				mHandler.sendEmptyMessage(0x110);

			}
		}).start();

	}

	/**
	 * ��ʼ��View
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
						Intent intent = new Intent(takephoto.this,
								FayanActivity.class);
						intent.putExtra("ListString", mSelectedImage.get(0));
						startActivity(intent);
						takephoto.this.finish();
					} else if (mSelectedImage.get(0).equals("takephoto")) {
						Intent intent = new Intent(takephoto.this,
								PhotoViewActivity.class);
						startActivity(intent);
						takephoto.this.finish();
					} else {
						Intent intent = new Intent(takephoto.this,
								PhotoViewActivity.class);
						intent.putExtra("ListString", mSelectedImage.get(0));
						startActivity(intent);
						takephoto.this.finish();
					}
					// Toast.makeText(getApplicationContext(),
					// mSelectedImage.get(0), Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void initEvent() {
		/**
		 * Ϊ�ײ��Ĳ������õ���¼�������popupWindow
		 */
		mBottomLy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListImageDirPopupWindow
						.setAnimationStyle(R.style.anim_popup_dir);
				mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

				// ���ñ�����ɫ�䰵
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
		 * ���Կ����ļ��е�·����ͼƬ��·���ֿ����棬����ļ������ڴ�����ģ�
		 */
		mAdapter = new MyAdapter(getApplicationContext(), mImgs,
				R.layout.grid_item, mImgDir.getAbsolutePath());
		mGirdView.setAdapter(mAdapter);
		// mAdapter.notifyDataSetChanged();
		mImageCount.setText(floder.getCount() + "" /* + "��" */);
		mChooseDir.setText(floder.getName());
		mListImageDirPopupWindow.dismiss();

	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(takephoto.this, MainActivity.class);
		startActivity(intent);
		takephoto.this.finish();
	}

}
