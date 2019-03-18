package com.lf.leafline.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.lf.leafline.PhotoViewActivity;

import android.util.Log;
import android.widget.Toast;

public class HttpUtil {
	private HttpClient httpClient = new DefaultHttpClient();
	private static final String TAG = "uploadFile";

	private static final int TIME_OUT = 10 * 1000; // ��ʱʱ��

	private static final String CHARSET = "utf-8"; // ���ñ���

	public String HttpGetStr(String path, String str) {
		String result = null;
		HttpGet httpGet = new HttpGet(path + "?" + str);
		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(httpResponse.getEntity());
			}
		} catch (ClientProtocolException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		return result;
	}

	public String UploadFile(File file, String RequestURL) {
		String result = null;
		String BOUNDARY = UUID.randomUUID().toString(); // �߽��ʶ �������
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // ��������

		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // ����������
			conn.setDoOutput(true); // ���������
			conn.setUseCaches(false); // ������ʹ�û���
			conn.setRequestMethod("POST"); // ����ʽ
			conn.setRequestProperty("Charset", CHARSET); // ���ñ���
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
					+ BOUNDARY);

			if (file != null) {
				/**
				 * ���ļ���Ϊ�գ����ļ���װ�����ϴ�
				 */
				DataOutputStream dos = new DataOutputStream(
						conn.getOutputStream());
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				/**
				 * �����ص�ע�⣺ name�����ֵΪ�������Ҫkey ֻ�����key �ſ��Եõ���Ӧ���ļ�
				 * filename���ļ������֣�������׺���� ����:abc.png
				 */

				sb.append("Content-Disposition: form-data; name=\"uploadfile\"; filename=\""
						+ file.getName() + "\"" + LINE_END);
				sb.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINE_END);
				sb.append(LINE_END);
				dos.write(sb.toString().getBytes());
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
				}
				is.close();
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
						.getBytes();
				dos.write(end_data);
				dos.flush();
				/**
				 * ��ȡ��Ӧ�� 200=�ɹ� ����Ӧ�ɹ�����ȡ��Ӧ����
				 */
				int res = conn.getResponseCode();
				Log.e(TAG, "response code:" + res);
				// if(res==200)
				// {
				Log.e(TAG, "request success");
				InputStream input = conn.getInputStream();
				StringBuffer sb1 = new StringBuffer();
				int ss;
				while ((ss = input.read()) != -1) {
					sb1.append((char) ss);
				}
				result = sb1.toString();
				Log.e(TAG, "result : " + result);
				// }
				// else{
				// Log.e(TAG, "request error");
				// }
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;

	}

	/**
	 * ͨ��ƴ�ӵķ�ʽ�����������ݣ�ʵ�ֲ��������Լ��ļ�����
	 * 
	 * @param url
	 *            Service net address
	 * @param params
	 *            text content
	 * @param files
	 *            pictures
	 * @return String result of Service response
	 * @throws IOException
	 */
	public static String post(String url, Map<String, String> params,
			Map<String, File> files) throws IOException {
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";

		URL uri = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		conn.setReadTimeout(10 * 1000); // ������ʱ��
		conn.setDoInput(true);// ��������
		conn.setDoOutput(true);// �������
		conn.setUseCaches(false); // ������ʹ�û���
		conn.setRequestMethod("POST");
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charsert", "UTF-8");
		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
				+ ";boundary=" + BOUNDARY);

		// ������ƴ�ı����͵Ĳ���
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINEND);
			sb.append("Content-Disposition: form-data; name=\""
					+ entry.getKey() + "\"" + LINEND);
			sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
			sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
			sb.append(LINEND);
			sb.append(entry.getValue());
			sb.append(LINEND);
		}

		DataOutputStream outStream = new DataOutputStream(
				conn.getOutputStream());
		outStream.write(sb.toString().getBytes());
		// �����ļ�����
		if (files != null)
			for (Map.Entry<String, File> file : files.entrySet()) {
				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(LINEND);
				sb1.append("Content-Disposition: form-data; name=\"uploadfile\"; filename=\""
						+ file.getValue().getName() + "\"" + LINEND);
				sb1.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINEND);
				sb1.append(LINEND);
				outStream.write(sb1.toString().getBytes());

				InputStream is = new FileInputStream(file.getValue());
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}

				is.close();
				outStream.write(LINEND.getBytes());
			}

		// ���������־
		byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
		outStream.write(end_data);
		outStream.flush();
		// �õ���Ӧ��
		int res = conn.getResponseCode();
		InputStream in = conn.getInputStream();
		StringBuilder sb2 = new StringBuilder();
		if (res == 200) {
			int ch;
			while ((ch = in.read()) != -1) {
				sb2.append((char) ch);
			}
		}
		outStream.close();
		conn.disconnect();
		return sb2.toString();
	}

	public String HttpPost1(String uriAPI, List<NameValuePair> params) {
		// String uriAPI = "http://192.168.137.208:58031/ttttt.asp";
		/* ����HTTP Post���� */
		String strResult = null;
		HttpPost httpRequest = new HttpPost(uriAPI);
		// Post�������ͱ���������NameValuePair[]���д���
		// ������ ����˻�ȡ�ķ���Ϊrequest.getParameter("name")
		// List<NameValuePair> params = new ArrayList<NameValuePair>();
		// params.add(new BasicNameValuePair("name", "this is post"));
		try {

			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			// ȡ��HTTP response
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);

			// ��״̬��Ϊ200 ok
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// ȡ����Ӧ�ִ�
				HttpEntity httpentity = httpResponse.getEntity();
				// strResult = EntityUtils.toString(httpResponse.getEntity());
				strResult = EntityUtils.toString(httpentity, "utf-8");

			} else {
				strResult = "Error Response";
				// + httpResponse.getStatusLine().toString();
			}
		} catch (ClientProtocolException e) {
			strResult = "Error Response";
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			strResult = "Error Response";
			e.printStackTrace();
		} catch (IOException e) {
			strResult = "Error Response";
			e.printStackTrace();
		}
		return strResult;
	}

	public String uploadFile(String uriAPI, String filepath) {
		FileInputStream fileInputStream;
		String sendfile = null;

		int len;
		try {
			fileInputStream = new FileInputStream(filepath);
			Scanner scanner = new Scanner(fileInputStream, "UTF-8");
			sendfile = scanner.useDelimiter("\\A").next();
		} catch (FileNotFoundException e1) {
			// TODO �Զ����ɵ� catch ��
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}

		String strResult = null;
		HttpPost httpRequest = new HttpPost(uriAPI);
		// Post�������ͱ���������NameValuePair[]���д���
		// ������ ����˻�ȡ�ķ���Ϊrequest.getParameter("name")
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("name", sendfile));
		try {

			// ����HTTP request
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			// ȡ��HTTP response
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);

			// ��״̬��Ϊ200 ok
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// ȡ����Ӧ�ִ�
				strResult = EntityUtils.toString(httpResponse.getEntity());

			} else {
				strResult = "NoResponse";
			}
		} catch (ClientProtocolException e) {
			strResult = e.getMessage().toString();
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			strResult = e.getMessage().toString();
			e.printStackTrace();
		} catch (IOException e) {
			strResult = e.getMessage().toString();
			e.printStackTrace();
		}
		return strResult;

	}

	private String ChangeInputStream(InputStream inputStream) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		String result = "";
		if (inputStream != null) {
			try {
				while ((len = inputStream.read(data)) != -1) {
					outputStream.write(data, 0, len);
				}
				result = new String(outputStream.toByteArray(), "UTF-8");
			} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
		}
		return result;
	}
}
