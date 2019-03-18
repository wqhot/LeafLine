package com.lf.leafline;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class CodetoName {
	private final String TAG = "CodetoName";
	public int re = 0;

	public int code(byte[] result) {
		String res = new String(result);
		int re = Integer.parseInt(res);
		return re;
	}

	public int CodetoName(byte[] result) {
		Log.i(TAG, new String(result));
		re = 0;
		String res = new String(result);
		int re = Integer.parseInt(res);

		return re;

	}

}
