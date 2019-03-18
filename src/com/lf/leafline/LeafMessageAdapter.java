package com.lf.leafline;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class LeafMessageAdapter extends BaseAdapter {
	private List<LeafMessage> list = new ArrayList<LeafMessage>();
	private Context context;
	private int flag = 0;

	public LeafMessageAdapter(Context context, int f) {
		this.flag = f;
		this.context = context;

	}

	public void setflag(int f) {
		flag = f;
	}

	public int getflag() {
		return flag;
	}

	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return list.size();
	}

	@Override
	public LeafMessage getItem(int position) {
		// TODO 自动生成的方法存根
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO 自动生成的方法存根
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO 自动生成的方法存根
		convertView = LayoutInflater.from(context).inflate(
				R.layout.guangchang_item, null);
		ImageView image = (ImageView) convertView
				.findViewById(R.id.user_touxiang);
		TextView name = (TextView) convertView.findViewById(R.id.user_name);
		TextView time = (TextView) convertView.findViewById(R.id.user_time);
		TextView main_context = (TextView) convertView
				.findViewById(R.id.text_context);
		ImageView image_main = (ImageView) convertView
				.findViewById(R.id.image_main);
		LeafMessage msg = list.get(position);
		name.setText(msg.getusername());
		time.setText(msg.getusertime());
		main_context.setText(msg.getcontext());
		// if (msg.getimage() == null) {
		//
		// }
		if (FileExist(msg.getimage())) {
			Bitmap bp = BitmapFactory.decodeFile(msg.getimage());
			image_main.setImageBitmap(bp);
		}

		AssetManager assetManager = context.getAssets();
		try {
			InputStream in = assetManager.open("img/" + msg.getusertouxiang()
					+ ".png");
			Bitmap bmp = BitmapFactory.decodeStream(in);
			image.setImageBitmap(bmp);
			// InputStream inn=new InputStrea

		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		return convertView;
	}

	public void addList(LeafMessage msg) {
		list.add(msg);
	}

	public void RemoveList(int position) {
		list.remove(position);
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
