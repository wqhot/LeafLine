package com.lf.leafline;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.lf.leafline.LoginActivity.RegThread;
import com.lf.leafline.PhotoViewActivity.DelDir;
import com.lf.leafline.utils.DataJiexi;
import com.lf.leafline.utils.HttpUtil;
import com.lf.leafline.utils.MD5;
import com.lf.leafline.view.RefreshableView;
import com.lf.leafline.view.XListView;
import com.lf.leafline.view.XListView.IXListViewListener;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	private int fregmentflag = 0;
	private ProgressDialog waitdelete;
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		Button btnsend = (Button) findViewById(R.id.btn_fayan);
		Button btnsetting = (Button) findViewById(R.id.btn_setting_main);
		Button btntkpt = (Button) findViewById(R.id.takephoto);
		btnsend.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						FayanActivity.class);
				// intent.putExtra("filename", DataList.get(position));
				startActivity(intent);

			}
		});
		btntkpt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, takephoto.class);
				// intent.putExtra("filename", DataList.get(position));
				startActivity(intent);
				MainActivity.this.finish();

			}
		});
		btnsetting.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				settting();
			}
		});

	}

	private void settting() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		SharedPreferences sharedata = getSharedPreferences("data",
				Context.MODE_PRIVATE);

		String actionUrl = sharedata.getString("ip1", "198.11.175.110");
		String httpurl = sharedata.getString("ip2", "198.11.175.110");
		int Port = sharedata.getInt("port1", 8508);
		int port2 = sharedata.getInt("port2", 80);
		Context mContext = MainActivity.this;
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.settings, null);
		// View layout = inflater.inflate(R.layout.settings, null);
		final EditText textport2 = (EditText) layout
				.findViewById(R.id.editTextPort2);
		final Button btndel = (Button) layout.findViewById(R.id.btn_delete_dir);
		final EditText uritext = (EditText) layout
				.findViewById(R.id.editTextIP);

		final EditText porttext = (EditText) layout
				.findViewById(R.id.editTextPort);
		final EditText porttext2 = (EditText) layout
				.findViewById(R.id.editTextIP2);
		builder.setTitle(getString(R.string.action_settings));
		// EditText textip=new EditText(MainActivity.this);
		textport2.setText(String.valueOf(port2));
		uritext.setText(actionUrl);
		porttext.setText(String.valueOf(Port));
		porttext2.setText(httpurl);
		btndel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				waitdelete = new ProgressDialog(MainActivity.this);
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

						int port2 = Integer.parseInt(textport2.getText()
								.toString());
						String httpurl = porttext2.getText().toString();
						String actionUrl = uritext.getText().toString();
						int Port = Integer.parseInt(porttext.getText()
								.toString());
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

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 5) {
				waitdelete.dismiss();
				Toast.makeText(MainActivity.this,
						getString(R.string.delete_finish), Toast.LENGTH_LONG)
						.show();
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			settting();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			return PlaceholderFragment.newInstance(position + 1);
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);

			}
			return null;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements
			IXListViewListener {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		RefreshableView refreshableView;
		ListView listView;
		private XListView mListView;
		private LeafMessage lf;
		private Handler mHandler;
		private int start = 0;
		private int page = 1;
		private TextView text;
		private Boolean islogin;
		private static int refreshCnt = 0;
		private String point = "";
		LeafMessageAdapter leafadapter;
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView;
			SharedPreferences sharedata = getActivity().getSharedPreferences(
					"login", Activity.MODE_PRIVATE);
			islogin = sharedata.getBoolean("IsLogined", false);
			if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
				rootView = inflater.inflate(R.layout.fragment_main, container,
						false);
				// listView = (ListView) rootView
				// .findViewById(R.id.list_view_guangchang);
				leafadapter = new LeafMessageAdapter(getActivity(), 0);
				mListView = (XListView) rootView.findViewById(R.id.xListView);

				mListView.setPullLoadEnable(true);
				mListView.setXListViewListener(this);

				// refreshableView = (RefreshableView) rootView
				// .findViewById(R.id.refreshable_view);
				new Thread(new getMessage()).start();
				mListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						// FragmentManager fm =
						// getActivity().getFragmentManager();
						// fm.beginTransaction()
						// .replace(android.R.id.content,
						// PlaceholderFragment.newInstance(3))
						// .commit();
						LeafMessage leaf;
						leaf = leafadapter.getItem(position - 1);
						ArrayList<String> alist = new ArrayList<String>();
						alist.add(leaf.getid() + "");
						alist.add(leaf.getusername());
						alist.add(leaf.getusertime());
						alist.add(leaf.getusertouxiang());
						alist.add(leaf.getimage());
						alist.add(leaf.getcontext());
						Intent intent = new Intent(getActivity(),
								ViewMsgActivity.class);

						intent.putStringArrayListExtra("ListString", alist);
						startActivity(intent);
					}
				});
				// listView.setAdapter(adapter);
				// refreshableView.setOnRefreshListener(
				// new PullToRefreshListener() {
				// @Override
				// public void onRefresh() {
				// try {
				// Thread.sleep(3000);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }
				// refreshableView.finishRefreshing();
				// }
				// }, 0);

			} else {
				rootView = inflater.inflate(R.layout.fragment_main2, container,
						false);
				text = (TextView) rootView.findViewById(R.id.userpoints);
				// text.setText(point);
				ListView list = (ListView) rootView
						.findViewById(R.id.list_mine);
				String[] strs = new String[] { getString(R.string.login),
						getString(R.string.view_map),
						getString(R.string.usepoint),
						getString(R.string.mylog),
						getString(R.string.mymessage),
						getString(R.string.logout) };
				SharedPreferences read = getActivity().getSharedPreferences(
						"login", MODE_PRIVATE);
				String logname = read.getString("username", "");
				String logpswd = read.getString("password", "");
				if (!logname.equals("")) {
					strs[0] = logname;
				}
				if (islogin) {
					new Thread(new RegThread()).start();
				}

				list.setAdapter(new ArrayAdapter<String>(getActivity(),
						android.R.layout.simple_list_item_1, strs));
				list.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						switch (position) {
						case 0:
							Intent intent = new Intent(getActivity(),
									LoginActivity.class);
							startActivity(intent);
							break;
						case 1:
							Intent intent1 = new Intent(getActivity(),
									ViewMapActivity.class);
							SharedPreferences sharedata = getActivity()
									.getSharedPreferences("data",
											Context.MODE_PRIVATE);
							String httpurl = sharedata.getString("ip2",
									"198.11.175.110");
							int port2 = sharedata.getInt("port2", 80);
							intent1.putExtra("str", httpurl + ":" + port2
									+ "/map.asp");
							startActivity(intent1);

						}
					}
				});

			}

			return rootView;
		}

		public class RegThread implements Runnable {

			@Override
			public void run() {
				SharedPreferences sharedata1 = getActivity()
						.getSharedPreferences("data", Context.MODE_PRIVATE);

				String httpurl = sharedata1.getString("ip2", "192.168.137.1");
				int port = sharedata1.getInt("port2", 58031);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				SharedPreferences read = getActivity().getSharedPreferences(
						"login", MODE_PRIVATE);
				String logname = read.getString("username", "");
				String logpswd = read.getString("password", "");
				params.add(new BasicNameValuePair("userid", logname));
				String uriAPI = "http://" + httpurl + ":" + port
						+ "/getpoint.asp";
				HttpUtil sss = new HttpUtil();
				String ss = sss.HttpPost1(uriAPI, params);
				Message msg = Message.obtain();
				msg.obj = ss;
				msg.what = 2;
				handler.sendMessage(msg);

			}

		}

		public class getMessage implements Runnable {

			@Override
			public void run() {
				SharedPreferences sharedata = getActivity()
						.getSharedPreferences("data", Context.MODE_PRIVATE);

				String httpurl = sharedata.getString("ip2", "198.11.175.110");
				int port = sharedata.getInt("port2", 80);
				String uriAPI = "http://" + httpurl + ":" + port
						+ "/viewtable.asp";
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("page", "" + page));
				HttpUtil sss = new HttpUtil();
				String str = sss.HttpPost1(uriAPI, params);
				if (!str.equals("Error Response")) {

					// String str =
					// "{iphone@2011-08-12 15:45:52@此留言板收费的还是免费的？怎么有网络上说是收费的？}{关注者@2011-08-12 10:18:49@XYCMS建站系统有哪些呢？价钱怎么样？}{网络用户@2012-06-07 08:45:24@在网络上下载的留言板有技术支持吗？}{最新用户@2011-12-16 15:08:10@XYCMS教育培训系统商业版多少钱？想买套，不知道价钱如何}{jacker@2011-06-28 10:49:02@现在最新企业建站系统版本是？支持生成静态HTML吗？}{站长@2010-12-31 15:50:04@测试是否审核留言测试是否审核留言测试是否审核留言测试是否审核留言}";
					List<LeafMessage> returnlist = new ArrayList<LeafMessage>();
					DataJiexi datajiexi = new DataJiexi();
					returnlist = datajiexi.SquareJiexi(str);
					for (int i = 0; i < returnlist.size(); i++) {
						lf = returnlist.get(i);
						// lf.setid(i - 1);
						String down = lf.getimage();
						File file = Environment.getExternalStorageDirectory();
						String ssss = Environment.getExternalStorageDirectory()
								.getAbsolutePath() + "/leaf/cache";
						File dir = new File(ssss);
						if (!dir.exists()) {
							dir.mkdirs();
						}
						String sFileFullPath = Environment
								.getExternalStorageDirectory()
								.getAbsolutePath()
								+ "/leaf/cache/" + MD5.getMD5(down);
						if (FileExist(sFileFullPath)) {
							lf.setimage(sFileFullPath);
						} else {
							HttpClient httpclient = new DefaultHttpClient();
							HttpPost httpPost = new HttpPost(down);
							HttpResponse response = null;
							OutputStream outputStream = null;
							try {
								response = httpclient.execute(httpPost);
								if (response.getStatusLine().getStatusCode() == 200) {
									byte[] result = EntityUtils
											.toByteArray(response.getEntity());
									lf.setimage(sFileFullPath);
									File new_file = new File(sFileFullPath);
									outputStream = new FileOutputStream(
											new_file);
									outputStream
											.write(result, 0, result.length);

								}
							} catch (ClientProtocolException e) {
								ToastError("ClientProtocolException");
								e.printStackTrace();
							} catch (IOException e) {
								ToastError("IOException");
								e.printStackTrace();
							} finally {
								if (outputStream != null) {
									try {
										outputStream.close();
									} catch (IOException e) {
										ToastError("IOException");
										e.printStackTrace();
									}
								}
								if (httpclient != null) {
									httpclient.getConnectionManager()
											.shutdown();
								}
							}
						}
						leafadapter.addList(lf);
					}
					Message message = Message.obtain();
					message.what = 1;
					handler.sendMessage(message);
				} else {
					Message message = Message.obtain();
					message.what = 3;
					handler.sendMessage(message);
				}
			}
		}

		private Handler handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (msg.what == 1) {
					// listView.setAdapter(leafadapter);
					mListView.setAdapter(leafadapter);
					mHandler = new Handler();
					onLoad();
				} else if (msg.what == 2) {
					String ss = msg.obj.toString();
					if (ss.equals("NoResponse")) {
						ToastError(getString(R.string.connect_error));
					} else if (!ss.equals("none")) {
						point = ss;
						ToastError("Loaded");
						getpoint();
					}
				} else if (msg.what == 3) {
					ToastError("Connected error");
				}
			}
		};

		private void ToastError(String str) {
			Toast.makeText(this.getActivity(), str, Toast.LENGTH_LONG).show();
		}

		private void getpoint() {
			text.setText(point);
		}

		private void onLoad() {
			mListView.stopRefresh();
			mListView.stopLoadMore();
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());
			String str = formatter.format(curDate);
			mListView.setRefreshTime(str);
		}

		@Override
		public void onRefresh() {
			start = ++refreshCnt;
			new Thread(new getMessage()).start();
		}

		@Override
		public void onLoadMore() {
			start = ++refreshCnt;
			page = page + 1;
			new Thread(new getMessage()).start();
			leafadapter.notifyDataSetChanged();
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

}
