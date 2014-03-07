package com.asdzheng.analyseapp.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.asdzheng.analyseapp.model.AppInfo;
import com.asdzheng.analyseapp.tool.SendDatas;
import com.google.gson.Gson;

public class MainActivity extends Activity {
	private List<Map<String, Object>> data;
	private ListView listView = null;
	private ArrayList<AppInfo> res;
	Map<String, Object> item;
	ArrayList<AppInfo> systemApps, customApps;
	Thread thread;
	Handler handler;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		listView = new ListView(this);
		data = new ArrayList<Map<String, Object>>();
	
		SimpleAdapter adapter = new SimpleAdapter(this, data,
				android.R.layout.simple_list_item_2, new String[] { "appname",
						"pname" }, new int[] { android.R.id.text1,
						android.R.id.text2, });
		listView.setAdapter(adapter);
		
		handler = new Handler() {
			@Override
			public void handleMessage(android.os.Message msg) {
				Log.i("response code : ", msg.arg1 + "");
			};
		};
		
		thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message msg = new Message();
				msg.arg1 = getSendDatasResponseCode();
				handler.sendMessage(msg);
			}
		});
			
		setPackagesToDatas();
		setContentView(listView);
	}

	/**
	 * 列出所有的APP信息
	 */
	private void setPackagesToDatas() {
		ArrayList<AppInfo> apps = getInstalledApps(false);
		final int max = apps.size();
		for (int i = 0; i < max; i++) {
			item = new HashMap<String, Object>();
			item.put("appname", apps.get(i).getAppname());
			item.put("pname", apps.get(i).getPname());
			data.add(item);
		}
	}

	/**
	 * 获取系统所有已安装的APP信息
	 * 
	 * @param getSysPackages
	 * @return
	 */
	private ArrayList<AppInfo> getInstalledApps(boolean getSysPackages) {
	    res = new ArrayList<AppInfo>();
		List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
		for (int i = 0; i < packs.size(); i++) {
			PackageInfo p = packs.get(i);

			if (!filterApp(p.applicationInfo)) {
				continue;
			}

			if ((!getSysPackages) && (p.versionName == null)) {
				continue;
			}

			AppInfo newInfo = new AppInfo();
			newInfo.setAppname(p.applicationInfo.loadLabel(getPackageManager())
					.toString());
			newInfo.setPname(p.packageName);
			newInfo.setVersionName(p.versionName);
			newInfo.setVersionCode(p.versionCode);
			res.add(newInfo);
		}
		thread.start();
		return res;
	}

	public boolean filterApp(ApplicationInfo info) {
		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			// 代表的是系统的应用,但是被用户升级了. 用户应用
			return true;
		}

		else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
			// 代表的用户的应用
			return true;
		}
		return false;
	}

	public int getSendDatasResponseCode() {
		Gson gson = new Gson();
		return SendDatas.sendAppDatas(gson.toJson(res));
	}
	
	
}
