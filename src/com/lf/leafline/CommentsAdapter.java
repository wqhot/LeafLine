package com.lf.leafline;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentsAdapter extends BaseAdapter {
	private List<CommentsMessage> comments = new ArrayList<CommentsMessage>();
	private Context context;

	public CommentsAdapter(Context c) {
		this.context = c;
	}

	@Override
	public int getCount() {
		// TODO �Զ����ɵķ������
		return comments.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO �Զ����ɵķ������
		return comments.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO �Զ����ɵķ������
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO �Զ����ɵķ������
		convertView = LayoutInflater.from(context).inflate(
				R.layout.comments_item, null);
		ImageView image = (ImageView) convertView.findViewById(R.id.c_image);
		TextView name = (TextView) convertView.findViewById(R.id.c_name);
		TextView time = (TextView) convertView.findViewById(R.id.c_time);
		TextView main_context = (TextView) convertView
				.findViewById(R.id.c_comment);
		CommentsMessage msg = comments.get(position);
		name.setText(msg.get().get(0));
		time.setText(msg.get().get(1));
		main_context.setText(msg.get().get(2));
		AssetManager assetManager = context.getAssets();
		try {
			InputStream in = assetManager
					.open("img/" + msg.getImage() + ".png");
			Bitmap bmp = BitmapFactory.decodeStream(in);
			image.setImageBitmap(bmp);
			// InputStream inn=new InputStrea

		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		return convertView;
	}

	public void addList(CommentsMessage msg) {
		comments.add(msg);
	}

	public void RemoveList(int position) {
		comments.remove(position);
	}

}
