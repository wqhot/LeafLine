package com.lf.leafline;

import java.util.LinkedList;
import java.util.List;

import com.lf.leafline.utils.CommonAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class MyAdapter extends CommonAdapter<String> {

	/**
	 * 用户选择的图片，存储为图片的完整路径
	 */
	public static List<String> mSelectedImage = new LinkedList<String>();

	/**
	 * 文件夹路径
	 */
	private String mDirPath;

	public MyAdapter(Context context, List<String> mDatas, int itemLayoutId,
			String dirPath) {
		super(context, mDatas, itemLayoutId);
		mSelectedImage.removeAll(mSelectedImage);
		this.mDirPath = dirPath;
	}

	@Override
	public void convert(final com.lf.leafline.utils.ViewHolder helper, final String item) {
		if (item.equals("takephoto")) {
			helper.setImageResource(R.id.id_item_image, R.drawable.pic_photo);
			helper.setImageResource(R.id.id_item_select,
					R.drawable.picture_unselected);
			final ImageView mImageView = helper.getView(R.id.id_item_image);
			final ImageView mSelect = helper.getView(R.id.id_item_select);
			mImageView.setColorFilter(null);
			mImageView.setOnClickListener(new OnClickListener() {
				// 选择，则将图片变暗，反之则反之
				@Override
				public void onClick(View v) {

					mImageView.setColorFilter(Color.parseColor("#77000000"));
					mSelect.setImageResource(R.drawable.pictures_selected);
					if (mSelectedImage.contains("takephoto")) {
						mSelectedImage.remove("takephoto");
						mSelect.setImageResource(R.drawable.picture_unselected);
						mImageView.setColorFilter(null);
					} else {
						if (mSelectedImage.size() == 0) {

							mSelectedImage.add("takephoto");
							mSelect.setImageResource(R.drawable.pictures_selected);
							mImageView.setColorFilter(Color
									.parseColor("#77000000"));

						}
					}
				}
			});

		} else {

			// 设置no_pic
			helper.setImageResource(R.id.id_item_image, R.drawable.pictures_no);
			// 设置no_selected
			helper.setImageResource(R.id.id_item_select,
					R.drawable.picture_unselected);

			// 设置图片
			helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);

			final ImageView mImageView = helper.getView(R.id.id_item_image);
			final ImageView mSelect = helper.getView(R.id.id_item_select);

			mImageView.setColorFilter(null);
			// 设置ImageView的点击事件
			mImageView.setOnClickListener(new OnClickListener() {
				// 选择，则将图片变暗，反之则反之
				@Override
				public void onClick(View v) {

					// 已经选择过该图片
					if (mSelectedImage.contains(mDirPath + "/" + item)) {
						mSelectedImage.remove(mDirPath + "/" + item);
						mSelect.setImageResource(R.drawable.picture_unselected);
						mImageView.setColorFilter(null);
					} else
					// 未选择该图片
					{
						if (mSelectedImage.size() == 0) {

							mSelectedImage.add(mDirPath + "/" + item);
							mSelect.setImageResource(R.drawable.pictures_selected);
							mImageView.setColorFilter(Color
									.parseColor("#77000000"));

						}
					}

				}
			});

			/**
			 * 已经选择过的图片，显示出选择过的效果
			 */
			if (mSelectedImage.contains(mDirPath + "/" + item)) {
				mSelect.setImageResource(R.drawable.pictures_selected);
				mImageView.setColorFilter(Color.parseColor("#77000000"));
			}
		}

	}
}
